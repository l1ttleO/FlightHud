package ru.octol1ttle.flightassistant.api.util

import ru.octol1ttle.flightassistant.api.computer.autoflight.ControlInput

fun List<ControlInput>.getActiveHighestPriority(): ControlInput? {
    val activeInput: ControlInput? = this.filter { it.active && it.priority.value == this[0].priority.value }.maxByOrNull { it.target }
    if (activeInput != null) {
        return activeInput
    }

    return this.filter { it.priority.value == this[0].priority.value }.maxByOrNull { it.target }
}
