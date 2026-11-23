package com.racha.ui.pantallas

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.racha.datos.ResultadoRegistro
import com.racha.ui.ColoresRacha
import com.racha.ui.Hapticos
import com.racha.ui.Iconos
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Un solo mensaje, sin controles. Vuelve sola a los 1,8 s o al tocar la pantalla. */
@Composable
fun Confirmacion(
    resultado: ResultadoRegistro?,
    hapticos: Hapticos,
    alTerminar: () -> Unit,
) {
    val terminar by rememberUpdatedState(alTerminar)
    var terminado by remember { mutableStateOf(false) }
    val salir = {
        if (!terminado) {
            terminado = true
            terminar()
        }
    }
    val escala = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch {
            escala.animateTo(
                1f,
                spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
            )
        }
        // Se deja respirar el toque del registro para que el patrón de la meta se distinga.
        if (resultado?.metaAlcanzada == true) {
            launch {
                delay(PAUSA_VIBRACION)
                hapticos.meta()
            }
        }
        delay(DURACION)
        salir()
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { salir() },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = maxWidth * 0.1f, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .scale(escala.value)
                    .background(ColoresRacha.primario, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Iconos.palomita,
                    contentDescription = null,
                    tint = ColoresRacha.fondo,
                    modifier = Modifier.size(32.dp),
                )
            }
            if (resultado != null) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = resultado.actividad.nombre,
                    style = MaterialTheme.typography.title3,
                    color = ColoresRacha.texto,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "+${resultado.puntosBase} pts",
                    style = MaterialTheme.typography.display2,
                    color = ColoresRacha.primario,
                )
                if (resultado.bono > 0) {
                    Text(
                        text = "+${resultado.bono} por racha",
                        style = MaterialTheme.typography.body2,
                        color = ColoresRacha.secundario,
                    )
                }
                lineaExtra(resultado)?.let { extra ->
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = extra,
                        style = MaterialTheme.typography.caption1,
                        color = ColoresRacha.acento,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

private fun lineaExtra(resultado: ResultadoRegistro): String? = when {
    resultado.logros.isNotEmpty() -> "Logro: ${resultado.logros.first().nombre}"
    resultado.metaAlcanzada -> "¡Meta diaria cumplida!"
    resultado.nivelNuevo != null -> "¡Nivel ${resultado.nivelNuevo}!"
    else -> null
}

private const val DURACION = 1_800L
private const val PAUSA_VIBRACION = 250L
