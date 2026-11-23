package com.racha.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.MaterialTheme

object ColoresRacha {
    val fondo = Color(0xFF000000)
    val superficie = Color(0xFF1C1B1F)
    val primario = Color(0xFF7BE0AD)
    val secundario = Color(0xFFFFB77D)
    val acento = Color(0xFF9FC9FF)
    val texto = Color(0xFFE6E1E5)
    val textoSuave = Color(0xFFA8A3A8)
    val exito = Color(0xFF7BE0AD)
    val atenuado = Color(0xFF49454F)
}

private val paleta = Colors(
    primary = ColoresRacha.primario,
    primaryVariant = ColoresRacha.primario,
    secondary = ColoresRacha.secundario,
    secondaryVariant = ColoresRacha.secundario,
    background = ColoresRacha.fondo,
    surface = ColoresRacha.superficie,
    onPrimary = ColoresRacha.fondo,
    onSecondary = ColoresRacha.fondo,
    onBackground = ColoresRacha.texto,
    onSurface = ColoresRacha.texto,
    onSurfaceVariant = ColoresRacha.textoSuave,
)

/** Usa la tipografía por defecto de Wear: ya está calibrada para leerse en la muñeca. */
@Composable
fun TemaRacha(contenido: @Composable () -> Unit) {
    MaterialTheme(colors = paleta, content = contenido)
}
