package com.racha.datos

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.racha.logica.ContextoLogros
import com.racha.logica.bonoRacha
import com.racha.logica.logrosNuevos
import com.racha.logica.nivelPara
import com.racha.logica.rachaAlRegistrar
import com.racha.logica.rachaVigente
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import java.io.IOException
import java.time.LocalDate
import java.time.LocalDateTime

private val Context.almacen: DataStore<Preferences> by preferencesDataStore(name = "racha")

data class ResultadoRegistro(
    val actividad: Actividad,
    val puntosBase: Int,
    val bono: Int,
    val metaAlcanzada: Boolean,
    val nivelNuevo: Int?,
    val logros: List<Logro>,
)

class Repositorio(context: Context) {

    private val almacen = context.applicationContext.almacen
    private val json = Json { ignoreUnknownKeys = true }

    val actividades: List<Actividad> = Semilla.actividades
    val logros: List<Logro> = Semilla.logros
    val metaDiaria: Int = Semilla.META_DIARIA

    val estado: Flow<EstadoJuego> = almacen.data
        .catch { error -> if (error is IOException) emit(emptyPreferences()) else throw error }
        .map(::leer)

    /** Registra la actividad y devuelve lo ganado, o null si ya estaba cumplida hoy. */
    suspend fun registrar(actividadId: String, ahora: LocalDateTime = LocalDateTime.now()): ResultadoRegistro? {
        val actividad = actividades.find { it.id == actividadId } ?: return null
        var resultado: ResultadoRegistro? = null
        almacen.edit { preferencias ->
            val estado = leer(preferencias)
            val hoy = ahora.toLocalDate()
            val hoyTexto = hoy.toString()
            val deHoy = estado.registros.filter { it.fecha == hoyTexto }
            if (deHoy.any { it.actividadId == actividadId }) return@edit

            val racha = rachaAlRegistrar(fecha(estado.ultimaFecha), estado.racha, hoy)
            val bono = bonoRacha(actividad.puntos, racha)
            val registro = Registro(actividadId, hoyTexto, ahora.hour, actividad.puntos + bono, bono)
            val experiencia = estado.experiencia + registro.puntos
            val nivelAntes = nivelPara(estado.experiencia)
            val nivelDespues = nivelPara(experiencia)
            val puntosAntes = deHoy.sumOf { it.puntos }

            val contexto = ContextoLogros(
                totalRegistros = estado.registros.size + 1,
                racha = racha,
                nivel = nivelDespues,
                cumplidasHoy = deHoy.size + 1,
                totalActividades = actividades.size,
                horaRegistro = ahora.hour,
            )
            val nuevos = logrosNuevos(logros, estado.logros.keys, contexto)

            guardar(
                preferencias,
                estado.copy(
                    experiencia = experiencia,
                    racha = racha,
                    mejorRacha = maxOf(estado.mejorRacha, racha),
                    ultimaFecha = hoyTexto,
                    registros = recortar(estado.registros + registro, hoy),
                    logros = estado.logros + nuevos.associate { it.id to hoyTexto },
                ),
            )
            resultado = ResultadoRegistro(
                actividad = actividad,
                puntosBase = actividad.puntos,
                bono = bono,
                metaAlcanzada = puntosAntes < metaDiaria && puntosAntes + registro.puntos >= metaDiaria,
                nivelNuevo = nivelDespues.takeIf { it > nivelAntes },
                logros = nuevos,
            )
        }
        return resultado
    }

    /** Opción de desarrollo: suma experiencia sin registrar actividad. Devuelve los logros ganados. */
    suspend fun sumarPuntos(cantidad: Int, hoy: LocalDate = LocalDate.now()): List<Logro> {
        var nuevos = emptyList<Logro>()
        almacen.edit { preferencias ->
            val estado = leer(preferencias)
            val experiencia = estado.experiencia + cantidad
            val contexto = ContextoLogros(
                totalRegistros = estado.registros.size,
                racha = rachaVigente(fecha(estado.ultimaFecha), estado.racha, hoy),
                nivel = nivelPara(experiencia),
                cumplidasHoy = estado.registros.count { it.fecha == hoy.toString() },
                totalActividades = actividades.size,
                horaRegistro = null,
            )
            nuevos = logrosNuevos(logros, estado.logros.keys, contexto)
            guardar(
                preferencias,
                estado.copy(
                    experiencia = experiencia,
                    logros = estado.logros + nuevos.associate { it.id to hoy.toString() },
                ),
            )
        }
        return nuevos
    }

    /** Opción de desarrollo: mueve todo un día hacia atrás, como si hoy fuera mañana. */
    suspend fun simularDiaAnterior() {
        almacen.edit { preferencias ->
            val estado = leer(preferencias)
            guardar(
                preferencias,
                estado.copy(
                    ultimaFecha = estado.ultimaFecha?.let(::diaAnterior),
                    registros = estado.registros.map { it.copy(fecha = diaAnterior(it.fecha)) },
                ),
            )
        }
    }

    suspend fun reiniciar() {
        almacen.edit { it.clear() }
    }

    val pasos: Flow<EstadoPasos> = almacen.data
        .catch { error -> if (error is IOException) emit(emptyPreferences()) else throw error }
        .map(::leerPasos)

    /**
     * Ajusta la base con una lectura del contador. Solo escribe al empezar un día nuevo, al
     * conocer la base por primera vez o si el reloj se reinició (el contador vuelve a cero).
     */
    suspend fun actualizarPasos(contador: Long, pasosPrevios: Int, hoy: LocalDate = LocalDate.now()) {
        almacen.edit { preferencias ->
            val estado = leerPasos(preferencias)
            val hoyTexto = hoy.toString()
            val nuevo = when {
                estado.fecha != hoyTexto -> estado.copy(fecha = hoyTexto, base = contador, acumulado = 0)
                estado.base < 0 -> estado.copy(base = contador)
                contador < estado.base -> estado.copy(base = contador, acumulado = maxOf(estado.acumulado, pasosPrevios))
                else -> return@edit
            }
            guardarPasos(preferencias, nuevo)
        }
    }

    /** Opción de desarrollo: suma pasos sin caminar (sirve en el emulador, que no tiene podómetro). */
    suspend fun sumarPasos(cantidad: Int, hoy: LocalDate = LocalDate.now()) {
        almacen.edit { preferencias ->
            val estado = leerPasos(preferencias)
            val hoyTexto = hoy.toString()
            val nuevo = if (estado.fecha != hoyTexto) {
                estado.copy(fecha = hoyTexto, base = -1, acumulado = cantidad)
            } else {
                estado.copy(acumulado = estado.acumulado + cantidad)
            }
            guardarPasos(preferencias, nuevo)
        }
    }

    suspend fun alternarDemoPasos() {
        almacen.edit { preferencias ->
            val estado = leerPasos(preferencias)
            guardarPasos(preferencias, estado.copy(demo = !estado.demo))
        }
    }

    private fun leerPasos(preferencias: Preferences): EstadoPasos =
        preferencias[CLAVE_PASOS]
            ?.let { texto -> runCatching { json.decodeFromString<EstadoPasos>(texto) }.getOrNull() }
            ?: EstadoPasos()

    private fun guardarPasos(preferencias: MutablePreferences, estado: EstadoPasos) {
        preferencias[CLAVE_PASOS] = json.encodeToString(estado)
    }

    private fun leer(preferencias: Preferences): EstadoJuego =
        preferencias[CLAVE_ESTADO]
            ?.let { texto -> runCatching { json.decodeFromString<EstadoJuego>(texto) }.getOrNull() }
            ?: EstadoJuego()

    private fun guardar(preferencias: MutablePreferences, estado: EstadoJuego) {
        preferencias[CLAVE_ESTADO] = json.encodeToString(estado)
    }

    private fun diaAnterior(texto: String): String = fecha(texto)?.minusDays(1)?.toString() ?: texto

    /** Solo hacen falta los días recientes: la experiencia y los logros se guardan aparte. */
    private fun recortar(registros: List<Registro>, hoy: LocalDate): List<Registro> {
        val limite = hoy.minusDays(DIAS_GUARDADOS)
        return registros.filter { registro -> fecha(registro.fecha)?.isAfter(limite) ?: false }
    }

    private companion object {
        val CLAVE_ESTADO = stringPreferencesKey("estado")
        val CLAVE_PASOS = stringPreferencesKey("pasos")
        const val DIAS_GUARDADOS = 14L
    }
}

internal fun fecha(texto: String?): LocalDate? = texto?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
