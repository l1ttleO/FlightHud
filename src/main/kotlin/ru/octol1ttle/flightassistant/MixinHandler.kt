package ru.octol1ttle.flightassistant

import ru.octol1ttle.flightassistant.api.computer.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.util.getActiveHighestPriority

fun onEntityChangeLookDirection(inputs: List<ControlInput>): Float? {
    return inputs.getActiveHighestPriority().minByOrNull { it.target }?.target
}
