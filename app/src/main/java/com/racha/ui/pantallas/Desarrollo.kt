package com.racha.ui.pantallas

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Text
import com.racha.ui.ColoresRacha

/** Opciones para la demostración: se abre manteniendo pulsado el nivel en Hoy. */
@Composable
fun Desarrollo(
    demoPasos: Boolean,
    alSumarPasos: () -> Unit,
    alAlternarDemo: () -> Unit,
    alSumarPuntos: () -> Unit,
    alSimularDia: () -> Unit,
    alReiniciar: () -> Unit,
) {
    val lista = rememberScalingLazyListState()
    Box(Modifier.fillMaxSize()) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = lista,
        ) {
            item {
                ListHeader {
                    Text("Desarrollo", style = MaterialTheme.typography.title3, color = ColoresRacha.acento)
                }
            }
            item {
                Chip(
                    onClick = alSumarPuntos,
                    label = { Text("Sumar 100 puntos") },
                    colors = ChipDefaults.primaryChipColors(),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            item {
                Chip(
                    onClick = alAlternarDemo,
                    label = { Text(if (demoPasos) "Meta pasos: 30" else "Meta pasos: 2000") },
                    secondaryLabel = { Text(if (demoPasos) "Modo demo activo" else "Toca para modo demo") },
                    colors = ChipDefaults.chipColors(
                        backgroundColor = ColoresRacha.superficie,
                        contentColor = ColoresRacha.primario,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            item {
                Chip(
                    onClick = alSumarPasos,
                    label = { Text("Sumar 100 pasos") },
                    colors = ChipDefaults.chipColors(
                        backgroundColor = ColoresRacha.superficie,
                        contentColor = ColoresRacha.acento,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            item {
                Chip(
                    onClick = alSimularDia,
                    label = { Text("Simular día anterior") },
                    colors = ChipDefaults.chipColors(
                        backgroundColor = ColoresRacha.superficie,
                        contentColor = ColoresRacha.secundario,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            item {
                Chip(
                    onClick = alReiniciar,
                    label = { Text("Reiniciar todo") },
                    colors = ChipDefaults.chipColors(
                        backgroundColor = ColoresRacha.superficie,
                        contentColor = ColoresRacha.textoSuave,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        PositionIndicator(scalingLazyListState = lista)
    }
}
