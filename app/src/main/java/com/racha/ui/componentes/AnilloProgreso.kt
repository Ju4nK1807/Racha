package com.racha.ui.componentes

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.racha.ui.ColoresRacha

/** Anillo que se llena en sentido horario desde las 12. [progreso] va de 0 a 1. */
@Composable
fun AnilloProgreso(
    progreso: Float,
    diametro: Dp,
    modifier: Modifier = Modifier,
    grosor: Dp = 8.dp,
    color: Color = ColoresRacha.primario,
    contenido: @Composable BoxScope.() -> Unit,
) {
    Box(modifier.size(diametro), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val trazo = grosor.toPx()
            val origen = Offset(trazo / 2, trazo / 2)
            val area = Size(size.width - trazo, size.height - trazo)
            drawArc(
                color = ColoresRacha.atenuado,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = origen,
                size = area,
                style = Stroke(trazo),
            )
            if (progreso > 0f) {
                drawArc(
                    color = color,
                    startAngle = -90f,
                    sweepAngle = 360f * progreso.coerceIn(0f, 1f),
                    useCenter = false,
                    topLeft = origen,
                    size = area,
                    style = Stroke(trazo, cap = StrokeCap.Round),
                )
            }
        }
        contenido()
    }
}
