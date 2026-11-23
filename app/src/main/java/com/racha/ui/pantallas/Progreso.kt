package com.racha.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Text
import com.racha.EstadoUi
import com.racha.ui.ColoresRacha
import com.racha.ui.componentes.BarrasSemana

@Composable
fun Progreso(estado: EstadoUi) {
    val lista = rememberScalingLazyListState()
    Box(Modifier.fillMaxSize()) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = lista,
        ) {
            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Nivel", style = MaterialTheme.typography.caption2, color = ColoresRacha.textoSuave)
                    Text(
                        text = "${estado.nivel}",
                        style = MaterialTheme.typography.display1,
                        color = ColoresRacha.acento,
                    )
                }
            }
            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    BarraNivel(estado.progresoNivel)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "${estado.experiencia} / ${estado.umbralSiguiente} para el nivel ${estado.nivel + 1}",
                        style = MaterialTheme.typography.caption3,
                        color = ColoresRacha.textoSuave,
                    )
                }
            }
            item {
                Dato(valor = "${estado.experiencia}", etiqueta = "experiencia total", color = ColoresRacha.acento)
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    Dato(valor = "${estado.racha}", etiqueta = "racha", color = ColoresRacha.secundario)
                    Dato(valor = "${estado.mejorRacha}", etiqueta = "mejor racha", color = ColoresRacha.secundario)
                }
            }
            item {
                Text("Últimos 7 días", style = MaterialTheme.typography.caption2, color = ColoresRacha.textoSuave)
            }
            item {
                BarrasSemana(dias = estado.semana, meta = estado.metaDiaria)
            }
        }
        PositionIndicator(scalingLazyListState = lista)
    }
}

@Composable
private fun BarraNivel(progreso: Float) {
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
                .background(ColoresRacha.acento, RoundedCornerShape(4.dp)),
        )
    }
}

@Composable
private fun Dato(valor: String, etiqueta: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(valor, style = MaterialTheme.typography.title1, color = color)
        Text(etiqueta, style = MaterialTheme.typography.caption3, color = ColoresRacha.textoSuave)
    }
}
