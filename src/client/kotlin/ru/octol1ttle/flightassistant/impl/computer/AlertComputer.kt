package ru.octol1ttle.flightassistant.impl.computer

import net.minecraft.client.sound.SoundManager
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.alert.*
import ru.octol1ttle.flightassistant.api.computer.*
import ru.octol1ttle.flightassistant.api.event.AlertCategoryRegistrationCallback
import ru.octol1ttle.flightassistant.impl.alert.elytra.*
import ru.octol1ttle.flightassistant.impl.alert.fault.computer.ComputerFaultAlert
import ru.octol1ttle.flightassistant.impl.alert.fault.display.DisplayFaultAlert
import ru.octol1ttle.flightassistant.impl.display.HudDisplayHost

class AlertComputer(val soundManager: SoundManager) : Computer() {
    val categories: ArrayList<AlertCategory> = ArrayList()

    override fun invokeEvents() {
        registerBuiltin()
        AlertCategoryRegistrationCallback.EVENT.invoker().register(this::register)
    }

    private fun registerBuiltin() {
        register(
            AlertCategory(Text.translatable("alerts.flightassistant.fault.computer"))
                .addAll(ComputerHost.identifiers().map { ComputerFaultAlert(it) })
        )
        register(
            AlertCategory(Text.translatable("alerts.flightassistant.fault.hud"))
                .addAll(HudDisplayHost.identifiers().map { DisplayFaultAlert(it) })
        )
        register(
            AlertCategory(Text.translatable("alerts.flightassistant.elytra"))
                .add(ElytraDurabilityCriticalAlert())
                .add(ElytraDurabilityLowAlert())
        )
    }

    fun register(category: AlertCategory) {
        if (categories.contains(category)) {
            throw IllegalArgumentException("Already registered alert category: ${category.javaClass.name}")
        }

        categories.add(category)
    }

    override fun tick(computers: ComputerAccess) {
        for (category: AlertCategory in categories) {
            category.updateActiveAlerts(computers, soundManager)
        }

        categories.sortBy { it.getHighestPriority()?.priorityValue }

        var interrupt = false
        for (category: AlertCategory in categories) {
            for (alert: Alert in category.activeAlerts) {
                if (interrupt) {
                    if (alert.soundInstance != null) {
                        soundManager.stop(alert.soundInstance)
                    }
                    continue
                }

                if (alert.soundInstance == null) {
                    alert.soundInstance = AlertSoundInstance(alert.priority.soundEvent)
                    soundManager.play(alert.soundInstance)
                }
                if (soundManager.isPlaying(alert.soundInstance)) {
                    interrupt = true
                }
            }
        }
    }

    companion object {
        val ID: Identifier = FlightAssistant.id("alert")
    }
}
