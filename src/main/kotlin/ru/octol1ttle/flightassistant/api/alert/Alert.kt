package ru.octol1ttle.flightassistant.api.alert

import ru.octol1ttle.flightassistant.api.computer.ComputerAccess

abstract class Alert {
    abstract val data: AlertData
    open val priorityOffset: Int = 0
    val priority: Int
        get() = data.priority + priorityOffset

    abstract fun shouldActivate(computers: ComputerAccess): Boolean
}
