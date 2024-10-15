package ru.octol1ttle.flightassistant.api.util

import kotlin.math.PI

fun degrees(value: Float): Float {
    return (value * (180 / PI)).toFloat()
}
