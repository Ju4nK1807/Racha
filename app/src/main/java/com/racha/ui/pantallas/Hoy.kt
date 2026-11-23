package com.racha.ui.pantallas

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.racha.EstadoUi
import com.racha.ui.ColoresRacha
import com.racha.ui.componentes.AnilloProgreso
import com.racha.ui.componentes.InsigniaNivel

@Composable
fun Hoy(
    estado: EstadoUi,
    alRegistrar: () -> Unit,
    alAbrirDesarrollo: () -> Unit,
) {
    // Los valores mostrados por última vez sobreviven a la navegación para animar desde ahí.
    var puntosPrevios by rememberSaveable { mutableIntStateOf(0) }
    var progresoPrevio by rememberSaveable { mutableFloatStateOf(0f) }
    val contador = remember { Animatable(puntosPrevios.toFloat()) }
    val anillo = remember { Animatable(progresoPrevio) }

    LaunchedEffect(estado.puntosHoy) {
        contador.animateTo(estado.puntosHoy.toFloat(), tween(DURACION_CONTADOR))
        puntosPrevios = estado.puntosHoy
    }
    LaunchedEffect(estado.progresoMeta) {
        anillo.animateTo(estado.progresoMeta, tween(DURACION_ANILLO))
        progresoPrevio = estado.progresoMeta
    }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        // 160 dp en pantallas grandes; en las pequeñas se reduce para que el botón quepa.
        val diametro = minOf(160.dp, maxHeight - 100.dp)
        val lateral = (maxWidth - diametro) / 2
        // Más ancho no cabe: en la parte baja de una pantalla redonda las esquinas se cortan.
        val anchoBoton = maxWidth * 0.5f

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 10.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(Modifier.width(lateral), contentAlignment = Alignment.CenterEnd) {
                    InsigniaNivel(
                        nivel = estado.nivel,
                        alMantener = alAbrirDesarrollo,
                        modifier = Modifier.padding(end = 4.dp),
                    )
                }
                AnilloProgreso(progreso = anillo.value, diametro = diametro) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${contador.value.toInt()}",
                            style = MaterialTheme.typography.display1,
                            color = ColoresRacha.texto,
                        )
                        Text(
                            text = "de ${estado.metaDiaria}",
                            style = MaterialTheme.typography.caption2,
                            color = ColoresRacha.textoSuave,
                        )
                    }
                }
                Box(Modifier.width(lateral), contentAlignment = Alignment.CenterStart) {
                    Column(
                        modifier = Modifier.padding(start = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "${estado.racha}",
                            style = MaterialTheme.typography.title2,
                            color = ColoresRacha.secundario,
                        )
                        Text(
                            text = "días",
                            style = MaterialTheme.typography.caption3,
                            color = ColoresRacha.textoSuave,
                        )
                    }
                }
            }
            Spacer(Modifier.height(6.dp))
            Chip(
                onClick = alRegistrar,
                modifier = Modifier.width(anchoBoton),
                colors = ChipDefaults.primaryChipColors(),
                label = {
                    Text(
                        text = "Registrar",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                    )
                },
            )
        }
    }
}

private const val DURACION_CONTADOR = 600
private const val DURACION_ANILLO = 800
