package ru.octol1ttle.flightassistant.api.alert

import net.minecraft.text.Text
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.ComputerAccess

class AlertCategory(val categoryText: Text) {
    private val registeredAlerts: ArrayList<Alert> = ArrayList()
    val activeAlerts: ArrayList<Alert> = ArrayList()
    val ignoredAlerts: ArrayList<Alert> = ArrayList()

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

    fun updateActiveAlerts(computers: ComputerAccess) {
        for (alert: Alert in registeredAlerts) {
            try {
                if (alert.shouldActivate(computers)) {
                    if (!activeAlerts.contains(alert) && !ignoredAlerts.contains(alert)) {
                        activeAlerts.add(alert)
                    }
                } else {
                    activeAlerts.remove(alert)
                    ignoredAlerts.remove(alert)
                }
            } catch (t: Throwable) {
                FlightAssistant.logger.atError().setCause(t).log("Exception ticking alert of type: {}", alert.javaClass.name)
            }
        }

        activeAlerts.sortBy { it.data.priority }
    }

    fun getHighestPriority(): AlertData? {
        return if (activeAlerts.isEmpty()) null else activeAlerts[0].data
    }
}
