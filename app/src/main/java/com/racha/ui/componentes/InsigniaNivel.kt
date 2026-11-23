package com.racha.ui.componentes

import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.racha.ui.ColoresRacha

/** Nivel actual como insignia circular. Mantenerla pulsada 2 s abre las opciones de desarrollo. */
@Composable
fun InsigniaNivel(
    nivel: Int,
    alMantener: () -> Unit,
    modifier: Modifier = Modifier,
    tamano: Dp = 40.dp,
) {
    val accion by rememberUpdatedState(alMantener)
    Box(
        modifier = modifier
            .size(tamano)
            .border(2.dp, ColoresRacha.acento, CircleShape)
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    // null solo si se cumplieron los 2 s sin soltar ni arrastrar.
                    val soltado = withTimeoutOrNull(DURACION_PULSACION) {
                        waitForUpOrCancellation()
                        true
                    }
                    if (soltado == null) {
                        accion()
                        waitForUpOrCancellation()
                    }
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("nv", style = MaterialTheme.typography.caption3, color = ColoresRacha.textoSuave)
            Text("$nivel", style = MaterialTheme.typography.title3, color = ColoresRacha.acento)
        }
    }
}

private const val DURACION_PULSACION = 2_000L
