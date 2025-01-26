package ru.octol1ttle.flightassistant

import ru.octol1ttle.flightassistant.api.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.util.extensions.getActiveHighestPriority

fun onEntityChangePitch(inputs: List<ControlInput>): Float? {
    return inputs.getActiveHighestPriority().minByOrNull { it.target }?.target
}

fun onEntityChangeHeading(inputs: List<ControlInput>): Float? {
    return inputs.getActiveHighestPriority().firstOrNull()?.target
}
