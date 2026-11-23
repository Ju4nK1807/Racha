package com.racha.datos

import kotlinx.serialization.Serializable

/** Iconos disponibles; el dibujo de cada uno vive en ui/Iconos.kt. */
enum class Icono { GOTA, FIGURA, PIE, LIBRO, MEDITAR, LUNA, PESA, LLAMA, ESTRELLA, TROFEO, SOL }

data class Actividad(
    val id: String,
    val nombre: String,
    val puntos: Int,
    val icono: Icono,
)

/** Una actividad cumplida. [puntos] ya incluye el [bono] de racha. */
@Serializable
data class Registro(
    val actividadId: String,
    val fecha: String,
    val hora: Int,
    val puntos: Int,
    val bono: Int,
)

/** Todo lo que se persiste. Las fechas van en ISO (aaaa-mm-dd). */
@Serializable
data class EstadoJuego(
    val experiencia: Int = 0,
    val racha: Int = 0,
    val mejorRacha: Int = 0,
    val ultimaFecha: String? = null,
    val registros: List<Registro> = emptyList(),
    val logros: Map<String, String> = emptyMap(),
)

/**
 * Pasos del día. [base] es el valor del contador del reloj al empezar a contar hoy (-1 si aún
 * no se conoce); [acumulado] guarda pasos de antes de un reinicio del reloj y los simulados.
 */
@Serializable
data class EstadoPasos(
    val fecha: String? = null,
    val base: Long = -1,
    val acumulado: Int = 0,
    val demo: Boolean = false,
)

sealed interface Condicion {
    data object PrimeraActividad : Condicion
    data class Racha(val dias: Int) : Condicion
    data class Nivel(val nivel: Int) : Condicion
    data object DiaCompleto : Condicion
    data class AntesDe(val hora: Int) : Condicion
}

data class Logro(
    val id: String,
    val nombre: String,
    val descripcion: String,
    val textoCondicion: String,
    val condicion: Condicion,
    val icono: Icono,
)
