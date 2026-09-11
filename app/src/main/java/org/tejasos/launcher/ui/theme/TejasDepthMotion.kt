package org.tejasos.launcher.ui.theme

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext

@Composable
fun Modifier.tejasParallax(depthMultiplier: Float = 1.0f): Modifier {
    val context = LocalContext.current
    var roll by remember { mutableFloatStateOf(0f) }
    var pitch by remember { mutableFloatStateOf(0f) }

    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    roll = (roll * 0.8f) + (it.values[0] * 0.2f)
                    pitch = (pitch * 0.8f) + (it.values[1] * 0.2f)
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        if (sensorManager != null && accelerometer != null) {
            sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_UI)
        }
        onDispose { sensorManager?.unregisterListener(listener) }
    }

    return this.graphicsLayer {
        rotationY = (-roll * 1.5f * depthMultiplier).coerceIn(-12f, 12f)
        rotationX = (pitch * 1.5f * depthMultiplier).coerceIn(-12f, 12f)
        cameraDistance = 16 * density
    }
}
