package ru.octol1ttle.flightassistant.api.autoflight.heading

import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.api.autoflight.ControlInput

interface HeadingController {
    fun getHeadingInput(computers: ComputerAccess): ControlInput?
}
