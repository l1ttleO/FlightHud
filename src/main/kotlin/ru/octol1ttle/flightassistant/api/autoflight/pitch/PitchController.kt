package ru.octol1ttle.flightassistant.api.autoflight.pitch

import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.api.autoflight.ControlInput

interface PitchController {
    fun getPitchInput(computers: ComputerAccess): ControlInput?
}
