package com.racha

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.racha.datos.Actividad
import com.racha.datos.EstadoJuego
import com.racha.datos.EstadoPasos
import com.racha.datos.Logro
import com.racha.datos.Podometro
import com.racha.datos.Repositorio
import com.racha.datos.ResultadoRegistro
import com.racha.datos.Semilla
import com.racha.datos.fecha
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import com.racha.logica.nivelPara
import com.racha.logica.porcentajeBono
import com.racha.logica.progresoNivel
import com.racha.logica.rachaVigente
import com.racha.logica.umbralNivel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

data class ActividadDelDia(val actividad: Actividad, val cumplida: Boolean)

data class DiaSemana(val inicial: String, val puntos: Int, val esHoy: Boolean)

data class LogroVista(val logro: Logro, val fecha: String?) {
    val desbloqueado: Boolean get() = fecha != null
}

data class EstadoUi(
    val cargado: Boolean = false,
    val puntosHoy: Int = 0,
    val metaDiaria: Int = 1,
    val nivel: Int = 1,
    val experiencia: Int = 0,
    val umbralSiguiente: Int = 0,
    val progresoNivel: Float = 0f,
    val racha: Int = 0,
    val mejorRacha: Int = 0,
    val porcentajeBono: Int = 0,
    val actividades: List<ActividadDelDia> = emptyList(),
    val semana: List<DiaSemana> = emptyList(),
    val logros: List<LogroVista> = emptyList(),
) {
    val progresoMeta: Float get() = (puntosHoy.toFloat() / metaDiaria).coerceIn(0f, 1f)
    val logrosDesbloqueados: Int get() = logros.count { it.desbloqueado }
}

enum class Movimiento { QUIETO, CAMINANDO, CORRIENDO }

enum class SensorPasos { ACTIVO, SIN_PERMISO, NO_DISPONIBLE }

data class PasosUi(
    val sensor: SensorPasos = SensorPasos.ACTIVO,
    val pasos: Int = 0,
    val meta: Int = Semilla.META_PASOS,
    val cadencia: Int = 0,
    val movimiento: Movimiento = Movimiento.QUIETO,
    val demo: Boolean = false,
) {
    val progreso: Float get() = (pasos.toFloat() / meta).coerceIn(0f, 1f)
}

class VistaModelo(aplicacion: Application) : AndroidViewModel(aplicacion) {

    private val repositorio = Repositorio(aplicacion)
    private val espanol = Locale.forLanguageTag("es")
    private val formatoFecha = DateTimeFormatter.ofPattern("d MMM", espanol)

    // Se reevalúa cada minuto para que el cambio de día se note sin reabrir la app.
    private val hoy = flow {
        while (true) {
            emit(LocalDate.now())
            delay(60_000)
        }
    }.distinctUntilChanged()

    val estado: StateFlow<EstadoUi> = combine(repositorio.estado, hoy, ::construir)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), EstadoUi())

    private val _resultado = MutableStateFlow<ResultadoRegistro?>(null)
    val resultado: StateFlow<ResultadoRegistro?> = _resultado.asStateFlow()

    private val _porCelebrar = MutableStateFlow<List<Logro>>(emptyList())
    val porCelebrar: StateFlow<List<Logro>> = _porCelebrar.asStateFlow()

    private var registrando = false

    fun registrar(actividadId: String, alRegistrar: () -> Unit) {
        if (registrando) return
        registrando = true
        viewModelScope.launch {
            try {
                val resultado = repositorio.registrar(actividadId) ?: return@launch
                _resultado.value = resultado
                _porCelebrar.value = resultado.logros
                alRegistrar()
            } finally {
                registrando = false
            }
        }
    }

    fun celebracionVista() {
        _porCelebrar.value = emptyList()
    }

    fun sumarPuntos(alTerminar: (hayLogros: Boolean) -> Unit) {
        viewModelScope.launch {
            val nuevos = repositorio.sumarPuntos(PUNTOS_DESARROLLO)
            _porCelebrar.value = nuevos
            alTerminar(nuevos.isNotEmpty())
        }
    }

    fun simularDiaAnterior(alTerminar: () -> Unit) {
        viewModelScope.launch {
            repositorio.simularDiaAnterior()
            alTerminar()
        }
    }

    fun reiniciar(alTerminar: () -> Unit) {
        viewModelScope.launch {
            repositorio.reiniciar()
            _resultado.value = null
            _porCelebrar.value = emptyList()
            alTerminar()
        }
    }

    private fun construir(juego: EstadoJuego, hoy: LocalDate): EstadoUi {
        val hoyTexto = hoy.toString()
        val cumplidasHoy = juego.registros.filter { it.fecha == hoyTexto }.map { it.actividadId }.toSet()
        val puntosPorDia = juego.registros.groupBy { it.fecha }.mapValues { (_, lista) -> lista.sumOf { it.puntos } }
        val racha = rachaVigente(fecha(juego.ultimaFecha), juego.racha, hoy)
        val nivel = nivelPara(juego.experiencia)

        return EstadoUi(
            cargado = true,
            puntosHoy = puntosPorDia[hoyTexto] ?: 0,
            metaDiaria = repositorio.metaDiaria,
            nivel = nivel,
            experiencia = juego.experiencia,
            umbralSiguiente = umbralNivel(nivel + 1),
            progresoNivel = progresoNivel(juego.experiencia),
            racha = racha,
            mejorRacha = juego.mejorRacha,
            porcentajeBono = porcentajeBono(racha),
            actividades = repositorio.actividades.map { ActividadDelDia(it, it.id in cumplidasHoy) },
            semana = (6 downTo 0).map { atras ->
                val dia = hoy.minusDays(atras.toLong())
                DiaSemana(
                    inicial = dia.dayOfWeek.getDisplayName(TextStyle.NARROW, espanol).uppercase(espanol),
                    puntos = puntosPorDia[dia.toString()] ?: 0,
                    esHoy = atras == 0,
                )
            },
            logros = repositorio.logros.map { logro ->
                LogroVista(logro, juego.logros[logro.id]?.let(::fecha)?.format(formatoFecha))
            },
        )
    }

    // ---------- Pasos y movimiento ----------

    private val podometro = Podometro(aplicacion)
    private val _permisoPasos = MutableStateFlow(tienePermisoPasos())

    /** Lecturas recientes del contador (tiempo, valor) para calcular el ritmo. */
    private val lecturas = ArrayDeque<Pair<Long, Long>>()
    private var ultimosPasosHoy = 0

    @OptIn(ExperimentalCoroutinesApi::class)
    private val contador: Flow<Long?> = _permisoPasos
        .flatMapLatest { permitido ->
            if (permitido && podometro.disponible) {
                podometro.contador().onEach { valor ->
                    anotarLectura(valor)
                    repositorio.actualizarPasos(valor, ultimosPasosHoy)
                }
            } else {
                flowOf(null)
            }
        }
        .onStart { emit(null) }

    // El ritmo se recalcula cada segundo para que pase a "Quieto" cuando dejas de moverte.
    private val reloj = flow {
        while (true) {
            emit(SystemClock.elapsedRealtime())
            delay(1_000)
        }
    }

    val pasos: StateFlow<PasosUi> = combine(repositorio.pasos, contador, _permisoPasos, reloj, hoy) { guardado, valor, permitido, ahora, dia ->
        val pasosHoy = pasosDeHoy(guardado, valor, dia)
        ultimosPasosHoy = pasosHoy
        val cadencia = cadencia(ahora)
        PasosUi(
            sensor = when {
                !podometro.disponible -> SensorPasos.NO_DISPONIBLE
                !permitido -> SensorPasos.SIN_PERMISO
                else -> SensorPasos.ACTIVO
            },
            pasos = pasosHoy,
            meta = if (guardado.demo) Semilla.META_PASOS_DEMO else Semilla.META_PASOS,
            cadencia = cadencia,
            movimiento = when {
                cadencia >= CADENCIA_CORRER -> Movimiento.CORRIENDO
                cadencia >= CADENCIA_CAMINAR -> Movimiento.CAMINANDO
                else -> Movimiento.QUIETO
            },
            demo = guardado.demo,
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, PasosUi())

    /** Avisa a la navegación cuando "Caminar" se registró sola por los pasos. */
    private val _autoRegistro = Channel<Unit>(Channel.CONFLATED)
    val autoRegistro: Flow<Unit> = _autoRegistro.receiveAsFlow()

    init {
        viewModelScope.launch {
            combine(pasos, estado) { p, e -> p to e }.collect { (p, e) ->
                if (!e.cargado || p.pasos < p.meta) return@collect
                val caminar = e.actividades.find { it.actividad.id == Semilla.ID_CAMINAR } ?: return@collect
                // Si ya está cumplida, el repositorio devuelve null y no se repite.
                if (!caminar.cumplida) registrar(Semilla.ID_CAMINAR) { _autoRegistro.trySend(Unit) }
            }
        }
    }

    fun faltaPermisoPasos(): Boolean = podometro.disponible && !tienePermisoPasos()

    fun permisoPasosActualizado() {
        _permisoPasos.value = tienePermisoPasos()
    }

    fun sumarPasos(alTerminar: () -> Unit) {
        viewModelScope.launch {
            repositorio.sumarPasos(PASOS_DESARROLLO)
            alTerminar()
        }
    }

    fun alternarDemoPasos() {
        viewModelScope.launch { repositorio.alternarDemoPasos() }
    }

    private fun tienePermisoPasos(): Boolean =
        getApplication<Application>().checkSelfPermission(Manifest.permission.ACTIVITY_RECOGNITION) ==
            PackageManager.PERMISSION_GRANTED

    private fun pasosDeHoy(guardado: EstadoPasos, valor: Long?, hoy: LocalDate): Int {
        if (guardado.fecha != hoy.toString()) return 0
        val enVivo = if (valor != null && guardado.base >= 0 && valor >= guardado.base) (valor - guardado.base).toInt() else 0
        return guardado.acumulado + enVivo
    }

    @Synchronized
    private fun anotarLectura(valor: Long) {
        val ahora = SystemClock.elapsedRealtime()
        lecturas.addLast(ahora to valor)
        while (lecturas.isNotEmpty() && ahora - lecturas.first().first > VENTANA_RITMO) lecturas.removeFirst()
    }

    /** Pasos por minuto en los últimos segundos; 0 si llevas un rato sin dar pasos. */
    @Synchronized
    private fun cadencia(ahora: Long): Int {
        while (lecturas.isNotEmpty() && ahora - lecturas.first().first > VENTANA_RITMO) lecturas.removeFirst()
        if (lecturas.size < 2 || ahora - lecturas.last().first > SIN_PASOS) return 0
        val pasosVentana = lecturas.last().second - lecturas.first().second
        val minutos = maxOf(ahora - lecturas.first().first, VENTANA_MINIMA) / 60_000.0
        return (pasosVentana / minutos).toInt()
    }

    private companion object {
        const val PUNTOS_DESARROLLO = 100
        const val PASOS_DESARROLLO = 100

        // Caminar ronda 90–120 pasos/min; trotar o correr, 140–180.
        const val CADENCIA_CAMINAR = 40
        const val CADENCIA_CORRER = 135
        const val VENTANA_RITMO = 12_000L
        const val VENTANA_MINIMA = 4_000L
        const val SIN_PASOS = 4_000L
    }
}
