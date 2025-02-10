package ru.octol1ttle.flightassistant.api.autoflight.pitch

import ru.octol1ttle.flightassistant.api.autoflight.ControlInput

interface PitchLimiter {
    fun getMinimumPitch(): ControlInput? {
        return null
    }

    fun getMaximumPitch(): ControlInput? {
        return null
    }
}
