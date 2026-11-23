package com.racha.ui.pantallas

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.racha.datos.Icono
import com.racha.datos.Logro
import com.racha.ui.ColoresRacha
import com.racha.ui.Hapticos
import com.racha.ui.Iconos
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Celebración breve de un logro recién desbloqueado. */
@Composable
fun Celebracion(
    logros: List<Logro>,
    hapticos: Hapticos,
    alTerminar: () -> Unit,
) {
    // Se congela la lista: al terminar se vacía en el ViewModel y no debe parpadear al salir.
    val mostrados = remember { logros }
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
        hapticos.logro()
        launch {
            escala.animateTo(
                1f,
                spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
            )
        }
        delay(DURACION)
        salir()
    }

    val principal = mostrados.firstOrNull()
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { salir() },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = maxWidth * 0.1f, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = Iconos.de(principal?.icono ?: Icono.TROFEO),
                contentDescription = null,
                tint = ColoresRacha.secundario,
                modifier = Modifier
                    .size(48.dp)
                    .scale(escala.value),
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Logro desbloqueado",
                style = MaterialTheme.typography.caption2,
                color = ColoresRacha.textoSuave,
            )
            if (principal != null) {
                Text(
                    text = principal.nombre,
                    style = MaterialTheme.typography.title1,
                    color = ColoresRacha.secundario,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = principal.descripcion,
                    style = MaterialTheme.typography.body2,
                    color = ColoresRacha.texto,
                    textAlign = TextAlign.Center,
                )
            }
            if (mostrados.size > 1) {
                Text(
                    text = "y ${mostrados.size - 1} más",
                    style = MaterialTheme.typography.caption2,
                    color = ColoresRacha.acento,
                )
            }
        }
    }
}

private const val DURACION = 2_400L
