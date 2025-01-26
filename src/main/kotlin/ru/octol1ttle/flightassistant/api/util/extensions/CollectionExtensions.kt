package ru.octol1ttle.flightassistant.api.util.extensions

import ru.octol1ttle.flightassistant.api.alert.AlertData
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.autoflight.ControlInput

fun List<ControlInput>.getActiveHighestPriority(): List<ControlInput> {
    val activeInput: List<ControlInput> = this.filter { it.active && it.priority.value == this[0].priority.value }
    if (activeInput.any()) {
        return activeInput
    }

    return this.filter { it.priority.value == this[0].priority.value }
}

fun List<AlertData>.getHighestPriority(): List<AlertData> {
    return this.filter { it.priority == this[0].priority }
}

fun <T> List<T>.filterNonFaulted(): List<T> {
    return this.filter { it !is Computer || !it.faulted}
}
