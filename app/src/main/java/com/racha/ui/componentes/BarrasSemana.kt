package com.racha.ui.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.racha.DiaSemana
import com.racha.ui.ColoresRacha

/** Siete barras con altura proporcional a los puntos de cada día; hoy va resaltado. */
@Composable
fun BarrasSemana(
    dias: List<DiaSemana>,
    meta: Int,
    modifier: Modifier = Modifier,
) {
    val altoMaximo = 56.dp
    val escala = maxOf(meta, dias.maxOfOrNull { it.puntos } ?: 0, 1)
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom,
    ) {
        dias.forEach { dia ->
            val color = if (dia.esHoy) ColoresRacha.primario else ColoresRacha.acento.copy(alpha = 0.6f)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(Modifier.height(altoMaximo), contentAlignment = Alignment.BottomCenter) {
                    Box(
                        Modifier
                            .width(10.dp)
                            .height((altoMaximo * dia.puntos / escala).coerceAtLeast(3.dp))
                            .background(
                                if (dia.puntos > 0) color else ColoresRacha.atenuado,
                                RoundedCornerShape(5.dp),
                            ),
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = dia.inicial,
                    style = MaterialTheme.typography.caption3,
                    color = if (dia.esHoy) ColoresRacha.primario else ColoresRacha.textoSuave,
                )
            }
        }
    }
}
