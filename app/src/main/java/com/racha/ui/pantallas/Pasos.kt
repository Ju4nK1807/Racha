package com.racha.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Text
import com.racha.Movimiento
import com.racha.PasosUi
import com.racha.SensorPasos
import com.racha.ui.ColoresRacha
import com.racha.ui.Iconos
import java.util.Locale

/** Pasos de hoy medidos por el reloj y si estás quieto, caminando o corriendo. */
@Composable
fun Pasos(
    pasos: PasosUi,
    caminarCumplida: Boolean,
    alPedirPermiso: () -> Unit,
) {
    val lista = rememberScalingLazyListState()
    Box(Modifier.fillMaxSize()) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = lista,
        ) {
            when (pasos.sensor) {
                SensorPasos.NO_DISPONIBLE -> item {
                    Aviso("Este reloj no tiene contador de pasos")
                }
                SensorPasos.SIN_PERMISO -> {
                    item { Aviso("Permite el sensor de actividad para contar pasos") }
                    item {
                        Chip(
                            onClick = alPedirPermiso,
                            label = { Text("Permitir", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
                            colors = ChipDefaults.primaryChipColors(),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
                SensorPasos.ACTIVO -> {
                    item { EstadoMovimiento(pasos.movimiento) }
                    item {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = String.format(Locale.forLanguageTag("es"), "%,d", pasos.pasos),
                                style = MaterialTheme.typography.display1,
                                color = ColoresRacha.texto,
                            )
                            Text("pasos hoy", style = MaterialTheme.typography.caption2, color = ColoresRacha.textoSuave)
                        }
                    }
                    item {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Barra(if (caminarCumplida) 1f else pasos.progreso)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = if (caminarCumplida) {
                                    "Caminar cumplida"
                                } else {
                                    "${pasos.pasos} / ${pasos.meta} para Caminar"
                                },
                                style = MaterialTheme.typography.caption3,
                                color = if (caminarCumplida) ColoresRacha.primario else ColoresRacha.textoSuave,
                            )
                        }
                    }
                    item {
                        Text(
                            text = if (pasos.cadencia > 0) "${pasos.cadencia} pasos/min" else "Camina para empezar",
                            style = MaterialTheme.typography.caption2,
                            color = ColoresRacha.acento,
                        )
                    }
                    if (pasos.demo) {
                        item {
                            Text("Modo demo", style = MaterialTheme.typography.caption3, color = ColoresRacha.secundario)
                        }
                    }
                }
            }
        }
        PositionIndicator(scalingLazyListState = lista)
    }
}

@Composable
private fun EstadoMovimiento(movimiento: Movimiento) {
    val (texto, color) = when (movimiento) {
        Movimiento.CORRIENDO -> "Corriendo" to ColoresRacha.secundario
        Movimiento.CAMINANDO -> "Caminando" to ColoresRacha.primario
        Movimiento.QUIETO -> "Quieto" to ColoresRacha.textoSuave
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = Iconos.pie,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(28.dp),
        )
        Text(texto, style = MaterialTheme.typography.title3, color = color)
    }
}

@Composable
private fun Aviso(texto: String) {
    Text(
        text = texto,
        style = MaterialTheme.typography.body2,
        color = ColoresRacha.textoSuave,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 12.dp),
    )
}

@Composable
private fun Barra(progreso: Float, color: Color = ColoresRacha.primario) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .height(8.dp)
            .background(ColoresRacha.atenuado, RoundedCornerShape(4.dp)),
    ) {
        Box(
            Modifier
                .fillMaxHeight()
                .fillMaxWidth(progreso.coerceIn(0f, 1f))
                .background(color, RoundedCornerShape(4.dp)),
        )
    }
}
