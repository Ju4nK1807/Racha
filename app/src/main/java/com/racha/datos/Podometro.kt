package com.racha.datos

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Contador de pasos de hardware del reloj. Da los pasos acumulados desde que se encendió el
 * reloj y sigue contando aunque la app esté cerrada; los pasos de hoy se calculan contra una base.
 */
class Podometro(context: Context) {

    private val gestor = context.getSystemService(SensorManager::class.java)
    private val sensor: Sensor? = gestor?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    val disponible: Boolean get() = sensor != null

    /** Emite el contador cada vez que cambia. Sin sensor, termina sin emitir. */
    fun contador(): Flow<Long> = callbackFlow {
        val sensor = sensor
        if (gestor == null || sensor == null) {
            close()
            return@callbackFlow
        }
        val oyente = object : SensorEventListener {
            override fun onSensorChanged(evento: SensorEvent) {
                trySend(evento.values[0].toLong())
            }

            override fun onAccuracyChanged(sensor: Sensor?, precision: Int) = Unit
        }
        // Latencia 0: cada paso llega al momento, sin agruparse en lotes.
        gestor.registerListener(oyente, sensor, SensorManager.SENSOR_DELAY_UI, 0)
        awaitClose { gestor.unregisterListener(oyente) }
    }
}
