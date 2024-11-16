package ru.octol1ttle.flightassistant.api.computer.autoflight.pitch

import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.api.computer.autoflight.ControlInput

interface PitchLimiter {
    fun getMinimumPitch(computers: ComputerAccess): ControlInput? {
        return null
    }

    fun getMaximumPitch(computers: ComputerAccess): ControlInput? {
        return null
    }
}
