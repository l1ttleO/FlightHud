package ru.octol1ttle.flightassistant.api.computer.autoflight.pitch

import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.api.computer.autoflight.ControlInput

interface PitchController {
    fun getPitchInput(computers: ComputerAccess): ControlInput?
}
