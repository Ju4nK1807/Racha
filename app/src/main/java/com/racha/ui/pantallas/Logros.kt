package com.racha.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Text
import com.racha.EstadoUi
import com.racha.LogroVista
import com.racha.ui.ColoresRacha
import com.racha.ui.Iconos

@Composable
fun Logros(estado: EstadoUi) {
    val lista = rememberScalingLazyListState()
    Box(Modifier.fillMaxSize()) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = lista,
        ) {
            item {
                ListHeader {
                    Text(
                        text = "${estado.logrosDesbloqueados} de ${estado.logros.size}",
                        style = MaterialTheme.typography.title2,
                        color = ColoresRacha.secundario,
                    )
                }
            }
            items(estado.logros, key = { it.logro.id }) { logro ->
                FilaLogro(logro)
            }
        }
        PositionIndicator(scalingLazyListState = lista)
    }
}

/** Desbloqueado: a todo color con su fecha. Bloqueado: silueta atenuada y la condición. */
@Composable
private fun FilaLogro(vista: LogroVista) {
    val desbloqueado = vista.desbloqueado
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp)
            .background(ColoresRacha.superficie, RoundedCornerShape(26.dp))
            .padding(horizontal = 14.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Iconos.de(vista.logro.icono),
            contentDescription = null,
            tint = if (desbloqueado) ColoresRacha.secundario else ColoresRacha.atenuado,
            modifier = Modifier.size(24.dp),
        )
        Spacer(Modifier.width(10.dp))
        Column {
            Text(
                text = vista.logro.nombre,
                style = MaterialTheme.typography.button,
                color = if (desbloqueado) ColoresRacha.texto else ColoresRacha.textoSuave,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = vista.fecha ?: vista.logro.textoCondicion,
                style = MaterialTheme.typography.caption3,
                color = if (desbloqueado) ColoresRacha.secundario else ColoresRacha.atenuado,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
