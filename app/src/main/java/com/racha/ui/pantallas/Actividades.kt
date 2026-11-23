package com.racha.ui.pantallas

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Text
import com.racha.EstadoUi
import com.racha.ui.ColoresRacha
import com.racha.ui.componentes.ChipActividad

@Composable
fun Actividades(
    estado: EstadoUi,
    alCumplir: (String) -> Unit,
) {
    val lista = rememberScalingLazyListState()
    val conBono = estado.porcentajeBono > 0
    Box(Modifier.fillMaxSize()) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = lista,
        ) {
            item {
                ListHeader {
                    Text(
                        text = if (conBono) "Bono +${estado.porcentajeBono}%" else "¿Qué cumpliste?",
                        style = MaterialTheme.typography.title3,
                        color = if (conBono) ColoresRacha.secundario else ColoresRacha.texto,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            items(estado.actividades, key = { it.actividad.id }) { item ->
                ChipActividad(item = item, alTocar = { alCumplir(item.actividad.id) })
            }
        }
        PositionIndicator(scalingLazyListState = lista)
    }
}
