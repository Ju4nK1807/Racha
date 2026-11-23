package com.racha.ui

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

/** Los tres patrones de vibración de la app; cada evento se siente distinto. */
class Hapticos(context: Context) {

    private val vibrador: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        context.getSystemService(VibratorManager::class.java)?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Vibrator::class.java)
    }

    /** Actividad registrada: un toque corto. */
    fun registro() = vibrar(longArrayOf(0, 40), intArrayOf(0, 200))

    /** Meta diaria alcanzada: dos toques firmes. */
    fun meta() = vibrar(longArrayOf(0, 70, 110, 70), intArrayOf(0, 255, 0, 255))

    /** Logro desbloqueado: tres toques que suben de intensidad. */
    fun logro() = vibrar(longArrayOf(0, 60, 90, 80, 90, 120), intArrayOf(0, 80, 0, 160, 0, 255))

    private fun vibrar(tiempos: LongArray, intensidades: IntArray) {
        val vibrador = vibrador?.takeIf { it.hasVibrator() } ?: return
        val efecto = if (vibrador.hasAmplitudeControl()) {
            VibrationEffect.createWaveform(tiempos, intensidades, -1)
        } else {
            VibrationEffect.createWaveform(tiempos, -1)
        }
        vibrador.vibrate(efecto)
    }
}

@Composable
fun rememberHapticos(): Hapticos {
    val context = LocalContext.current
    return remember(context) { Hapticos(context) }
}
