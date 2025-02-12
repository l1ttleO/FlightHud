package ru.octol1ttle.flightassistant.api.alert

import net.minecraft.text.Text
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.ComputerView

/**
 * A class that represents a category of alerts.
 */
class AlertCategory(val categoryText: Text) {
    private val registeredAlerts: MutableList<Alert> = ArrayList()
    val activeAlerts: MutableList<Alert> = ArrayList()
    val ignoredAlerts: MutableList<Alert> = ArrayList()

    fun add(alert: Alert): AlertCategory {
        if (registeredAlerts.contains(alert)) {
            throw IllegalArgumentException("Already registered alert: ${alert.javaClass.name}")
        }

        registeredAlerts.add(alert)
        return this
    }

    fun addAll(alerts: Collection<Alert>): AlertCategory {
        for (alert: Alert in alerts) {
            add(alert)
        }
        return this
    }

    fun updateActiveAlerts(computers: ComputerView) {
        for (alert: Alert in registeredAlerts) {
            try {
                if (alert.shouldActivate()) {
                    if (!activeAlerts.contains(alert) && !ignoredAlerts.contains(alert)) {
                        activeAlerts.add(alert)
                    }
                } else {
                    activeAlerts.remove(alert)
                    ignoredAlerts.remove(alert)
                }
            } catch (t: Throwable) {
                computers.alert.alertsFaulted = true
                FlightAssistant.logger.atError().setCause(t).log("Exception ticking alert of type: {}", alert.javaClass.name)
            }
        }

        activeAlerts.sortBy { it.data.priority + it.priorityOffset }
    }

    fun getHighestPriority(): Int? {
        return if (activeAlerts.isEmpty()) null else activeAlerts[0].priority
    }

    fun getFirstData(): AlertData? {
        return if (activeAlerts.isEmpty()) null else activeAlerts[0].data
    }
}
