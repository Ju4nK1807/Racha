package com.racha.logica

/**
 * Experiencia total necesaria para alcanzar [nivel]: 50 × N × (N + 1) / 2.
 * El nivel 1 es el de partida y no pide nada.
 */
fun umbralNivel(nivel: Int): Int = if (nivel <= 1) 0 else 50 * nivel * (nivel + 1) / 2

fun nivelPara(experiencia: Int): Int {
    var nivel = 1
    while (experiencia >= umbralNivel(nivel + 1)) nivel++
    return nivel
}

/** Fracción entre 0 y 1 recorrida desde el nivel actual hasta el siguiente. */
fun progresoNivel(experiencia: Int): Float {
    val nivel = nivelPara(experiencia)
    val desde = umbralNivel(nivel)
    val hasta = umbralNivel(nivel + 1)
    return ((experiencia - desde).toFloat() / (hasta - desde)).coerceIn(0f, 1f)
}
