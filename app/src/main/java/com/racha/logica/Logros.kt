package com.racha.logica

import com.racha.datos.Condicion
import com.racha.datos.Logro

/** Lo que hace falta saber del jugador para evaluar cualquier condición. */
data class ContextoLogros(
    val totalRegistros: Int,
    val racha: Int,
    val nivel: Int,
    val cumplidasHoy: Int,
    val totalActividades: Int,
    val horaRegistro: Int?,
)

fun cumple(condicion: Condicion, contexto: ContextoLogros): Boolean = when (condicion) {
    Condicion.PrimeraActividad -> contexto.totalRegistros > 0
    is Condicion.Racha -> contexto.racha >= condicion.dias
    is Condicion.Nivel -> contexto.nivel >= condicion.nivel
    Condicion.DiaCompleto -> contexto.totalActividades > 0 && contexto.cumplidasHoy >= contexto.totalActividades
    is Condicion.AntesDe -> contexto.horaRegistro?.let { it < condicion.hora } ?: false
}

fun logrosNuevos(logros: List<Logro>, desbloqueados: Set<String>, contexto: ContextoLogros): List<Logro> =
    logros.filter { it.id !in desbloqueados && cumple(it.condicion, contexto) }
