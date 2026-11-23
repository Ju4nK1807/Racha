package com.racha.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp
import com.racha.datos.Icono

/** Iconos de trazo en una cuadrícula de 24 × 24, escritos como rutas SVG. */
object Iconos {
    val gota = trazo("gota", "M12,3 C12,3 6,10 6,14.5 A6,6 0 0,0 18,14.5 C18,10 12,3 12,3 Z")

    val figura = trazo(
        "figura",
        "M10.2,4.5 a1.8,1.8 0 1,0 3.6,0 a1.8,1.8 0 1,0 -3.6,0",
        "M5,6 L12,9.5 L19,6",
        "M12,8.5 L12,14.5",
        "M12,14.5 L8.5,21",
        "M12,14.5 L15.5,21",
    )

    val pie = trazo(
        "pie",
        "M9.5,21 C6.5,21 6,17.5 6.8,14 C7.6,10.5 11,10 12,12.5 C13,15 12.5,21 9.5,21 Z",
        "M7,7.5 a1.2,1.2 0 1,0 2.4,0 a1.2,1.2 0 1,0 -2.4,0",
        "M10.6,6 a1,1 0 1,0 2,0 a1,1 0 1,0 -2,0",
        "M13.6,7.8 a0.9,0.9 0 1,0 1.8,0 a0.9,0.9 0 1,0 -1.8,0",
        "M15.5,11.5 C17.5,12.5 18.5,15 17.5,17",
    )

    val libro = trazo(
        "libro",
        "M3,5.5 C6,4.5 9,4.5 12,6.5 C15,4.5 18,4.5 21,5.5 L21,19 C18,18 15,18 12,20 C9,18 6,18 3,19 Z",
        "M12,6.5 L12,20",
    )

    val meditar = trazo(
        "meditar",
        "M3,12 a9,9 0 1,0 18,0 a9,9 0 1,0 -18,0",
        "M7.5,12 a4.5,4.5 0 1,0 9,0 a4.5,4.5 0 1,0 -9,0",
        "M11,12 a1,1 0 1,0 2,0 a1,1 0 1,0 -2,0",
    )

    val luna = trazo("luna", "M20,14.5 A8.5,8.5 0 1,1 9.5,4 A7,7 0 0,0 20,14.5 Z")

    val pesa = trazo(
        "pesa",
        "M3,10 L3,14",
        "M6.5,7 L6.5,17",
        "M6.5,12 L17.5,12",
        "M17.5,7 L17.5,17",
        "M21,10 L21,14",
    )

    val llama = trazo(
        "llama",
        "M12,3 C13,7 18,9 18,14.5 A6,6 0 0,1 6,14.5 C6,11.5 8,9.5 9.5,8 C9.5,10.5 10.5,12 12,12 C12,8.5 11,6 12,3 Z",
    )

    val estrella = trazo(
        "estrella",
        "M12,3 L14.6,8.6 L20.6,9.2 L16,13.3 L17.3,19.3 L12,16.2 L6.7,19.3 L8,13.3 L3.4,9.2 L9.4,8.6 Z",
    )

    val trofeo = trazo(
        "trofeo",
        "M7,4 L17,4 L17,9 A5,5 0 0,1 7,9 Z",
        "M7,6 L4,6 C4,9 5.5,10.5 7.5,10.5",
        "M17,6 L20,6 C20,9 18.5,10.5 16.5,10.5",
        "M12,14 L12,18",
        "M8,20 L16,20",
    )

    val sol = trazo(
        "sol",
        "M8,12 a4,4 0 1,0 8,0 a4,4 0 1,0 -8,0",
        "M12,2.5 L12,4.5", "M12,19.5 L12,21.5", "M2.5,12 L4.5,12", "M19.5,12 L21.5,12",
        "M5.3,5.3 L6.7,6.7", "M17.3,17.3 L18.7,18.7", "M5.3,18.7 L6.7,17.3", "M17.3,6.7 L18.7,5.3",
    )

    val palomita = trazo("palomita", "M5,12.5 L10,17.5 L19,7")

    fun de(icono: Icono): ImageVector = when (icono) {
        Icono.GOTA -> gota
        Icono.FIGURA -> figura
        Icono.PIE -> pie
        Icono.LIBRO -> libro
        Icono.MEDITAR -> meditar
        Icono.LUNA -> luna
        Icono.PESA -> pesa
        Icono.LLAMA -> llama
        Icono.ESTRELLA -> estrella
        Icono.TROFEO -> trofeo
        Icono.SOL -> sol
    }

    // El color real lo pone el tinte del Icon; aquí solo importa el trazo.
    private fun trazo(nombre: String, vararg rutas: String): ImageVector =
        ImageVector.Builder(
            name = nombre,
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            rutas.forEach { ruta ->
                addPath(
                    pathData = addPathNodes(ruta),
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 2f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round,
                )
            }
        }.build()
}
