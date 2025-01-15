package ru.octol1ttle.flightassistant.api.alert

import ru.octol1ttle.flightassistant.api.computer.ComputerAccess

abstract class Alert {
    abstract val data: AlertData

    abstract fun shouldActivate(computers: ComputerAccess): Boolean
}
