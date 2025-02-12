package ru.octol1ttle.flightassistant.api.alert

import ru.octol1ttle.flightassistant.api.computer.ComputerView

/**
 * A class that represents an alert. Only a single instance of this class is present for each actual alert
 */
abstract class Alert(val computers: ComputerView) {
    /**
     * The data that this alert uses. Impacts the priority of this alert, its sound and category color
     */
    abstract val data: AlertData

    /**
     * An offset for the priority of this alert, relative to the priority defined by the [data].
     * A negative offset means this alert will be more important than others in its category
     */
    open val priorityOffset: Int = 0

    val priority: Int
        get() = data.priority + priorityOffset

    /**
     * @return whether or not this alert should be active (displaying on a screen and/or playing a sound)
     */
    abstract fun shouldActivate(): Boolean
}
