package com.racha.logica

import java.time.LocalDate

/** Racha resultante de cumplir una actividad [hoy]. */
fun rachaAlRegistrar(ultimaFecha: LocalDate?, rachaActual: Int, hoy: LocalDate): Int = when (ultimaFecha) {
    hoy -> rachaActual.coerceAtLeast(1)
    hoy.minusDays(1) -> rachaActual + 1
    else -> 1
}

/** Racha que se muestra: si ya pasó un día entero sin actividad, está rota. */
fun rachaVigente(ultimaFecha: LocalDate?, racha: Int, hoy: LocalDate): Int =
    if (ultimaFecha == hoy || ultimaFecha == hoy.minusDays(1)) racha else 0

/** Porcentaje extra: 10 % por día de racha a partir del día 3, con tope de 50 %. */
fun porcentajeBono(racha: Int): Int = if (racha < 3) 0 else (racha * 10).coerceAtMost(50)

fun bonoRacha(puntos: Int, racha: Int): Int = puntos * porcentajeBono(racha) / 100
