package com.racha.ui.componentes

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.racha.ActividadDelDia
import com.racha.ui.ColoresRacha
import com.racha.ui.Iconos

/** Las cumplidas quedan atenuadas, con palomita en vez de puntos, y no responden al toque. */
@Composable
fun ChipActividad(
    item: ActividadDelDia,
    alTocar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val actividad = item.actividad
    Chip(
        onClick = alTocar,
        enabled = !item.cumplida,
        modifier = modifier.fillMaxWidth(),
        colors = ChipDefaults.chipColors(
            backgroundColor = ColoresRacha.superficie,
            contentColor = ColoresRacha.texto,
            iconColor = ColoresRacha.primario,
        ),
        icon = {
            Icon(
                imageVector = Iconos.de(actividad.icono),
                contentDescription = null,
                modifier = Modifier.size(ChipDefaults.IconSize),
            )
        },
        label = {
            Text(
                text = actividad.nombre,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f).align(Alignment.CenterVertically),
            )
            Spacer(Modifier.width(6.dp))
            if (item.cumplida) {
                Icon(
                    imageVector = Iconos.palomita,
                    contentDescription = "Cumplida",
                    tint = ColoresRacha.exito,
                    modifier = Modifier.size(20.dp).align(Alignment.CenterVertically),
                )
            } else {
                Text(
                    text = "+${actividad.puntos}",
                    style = MaterialTheme.typography.title3,
                    color = ColoresRacha.primario,
                    modifier = Modifier.align(Alignment.CenterVertically),
                )
            }
        },
    )
}
