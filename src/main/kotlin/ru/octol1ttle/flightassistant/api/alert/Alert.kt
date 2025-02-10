package ru.octol1ttle.flightassistant.api.alert

import ru.octol1ttle.flightassistant.api.computer.ComputerView

abstract class Alert(val computers: ComputerView) {
    abstract val data: AlertData
    open val priorityOffset: Int = 0
    val priority: Int
        get() = data.priority + priorityOffset

    abstract fun shouldActivate(): Boolean
}
