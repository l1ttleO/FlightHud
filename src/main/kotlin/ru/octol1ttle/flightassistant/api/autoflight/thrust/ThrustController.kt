package ru.octol1ttle.flightassistant.api.autoflight.thrust

import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.api.autoflight.ControlInput

interface ThrustController {
    fun getThrustInput(computers: ComputerAccess): ControlInput?
}
