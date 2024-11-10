package ru.octol1ttle.flightassistant.impl.computer.safety

import net.minecraft.client.sound.SoundManager
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.alert.*
import ru.octol1ttle.flightassistant.api.computer.*
import ru.octol1ttle.flightassistant.api.event.AlertCategoryRegistrationCallback
import ru.octol1ttle.flightassistant.api.util.*
import ru.octol1ttle.flightassistant.impl.alert.AlertSoundInstance
import ru.octol1ttle.flightassistant.impl.alert.autoflight.NoThrustSourceAlert
import ru.octol1ttle.flightassistant.impl.alert.autoflight.ThrustLockedAlert
import ru.octol1ttle.flightassistant.impl.alert.elytra.*
import ru.octol1ttle.flightassistant.impl.alert.fault.computer.ComputerFaultAlert
import ru.octol1ttle.flightassistant.impl.alert.fault.display.DisplayFaultAlert
import ru.octol1ttle.flightassistant.impl.alert.navigation.*
import ru.octol1ttle.flightassistant.impl.alert.stall.*
import ru.octol1ttle.flightassistant.impl.computer.*
import ru.octol1ttle.flightassistant.impl.computer.autoflight.*
import ru.octol1ttle.flightassistant.impl.display.HudDisplayHost

class AlertComputer(private val soundManager: SoundManager) : Computer() {
    val categories: ArrayList<AlertCategory> = ArrayList()

    override fun invokeEvents() {
        registerBuiltin()
        AlertCategoryRegistrationCallback.EVENT.invoker().register(this::register)
    }

    private fun registerBuiltin() {
        register(
            AlertCategory(Text.translatable("alerts.flightassistant.fault.hud"))
                .addAll(HudDisplayHost.identifiers().map { DisplayFaultAlert(it) })
        )
        register(
            AlertCategory(Text.translatable("alerts.flightassistant.stall"))
                .add(ComputerFaultAlert(StallComputer.ID, Text.translatable("alerts.flightassistant.stall.detection_fault")))
                .add(ApproachingStallAlert())
                .add(FullStallAlert())
        )
        register(
            AlertCategory(Text.translatable("alerts.flightassistant.autoflight"))
                .add(ComputerFaultAlert(PitchComputer.ID, Text.translatable("alerts.flightassistant.autoflight.pitch_fault")))
        )
        register(
            AlertCategory(Text.translatable("alerts.flightassistant.firework"))
                .add(ComputerFaultAlert(FireworkComputer.ID, Text.translatable("alerts.flightassistant.firework.fault")))
        )
        register(
            AlertCategory(Text.translatable("alerts.flightassistant.flight_controls"))
                .add(ComputerFaultAlert(PitchComputer.ID, Text.translatable("alerts.flightassistant.flight_controls.pitch_fault")))
        )
        register(
            AlertCategory(Text.translatable("alerts.flightassistant.elytra"))
                .add(ComputerFaultAlert(ElytraStatusComputer.ID, Text.translatable("alerts.flightassistant.elytra.fault")))
                .add(ElytraDurabilityLowAlert())
                .add(ElytraDurabilityCriticalAlert())
        )
        register(
            AlertCategory(Text.translatable("alerts.flightassistant.thrust"))
                .add(ComputerFaultAlert(ThrustComputer.ID, Text.translatable("alerts.flightassistant.thrust.fault")))
                .add(ThrustLockedAlert())
                .add(NoThrustSourceAlert())
        )
        register(
            AlertCategory(Text.translatable("alerts.flightassistant.navigation"))
                .add(ComputerFaultAlert(AirDataComputer.ID, Text.translatable("alerts.flightassistant.navigation.air_data_fault")))
                .add(ComputerFaultAlert(VoidProximityComputer.ID, Text.translatable("alerts.flightassistant.navigation.void_proximity_fault")))
                .add(ApproachingVoidDamageAltitudeAlert())
                .add(ReachedVoidDamageAltitudeAlert())
        )
    }

    fun register(category: AlertCategory) {
        if (categories.contains(category)) {
            throw IllegalArgumentException("Already registered alert category: ${category.javaClass.name}")
        }

        categories.add(category)
    }

    // TODO: alert keybindings
    override fun tick(computers: ComputerAccess) {
        for (category: AlertCategory in categories) {
            category.updateActiveAlerts(computers, soundManager)
        }

        categories.sortBy { it.getHighestPriority()?.priority }

        var interrupt: Boolean = !computers.data.flying
        for (category: AlertCategory in categories) {
            for (alert: Alert in category.activeAlerts) {
                val soundInstance: AlertSoundInstance? = alert.soundInstance
                if (interrupt) {
                    if (soundInstance != null) {
                        if (soundInstance.isRepeatable) {
                            soundManager.pause(soundInstance)
                        } else {
                            soundManager.stop(soundInstance)
                        }
                    }
                    continue
                }

                if (soundInstance == null) {
                    alert.soundInstance = AlertSoundInstance(computers.data.player, alert.data)
                    soundManager.play(alert.soundInstance)
                    interrupt = true
                    continue
                }

                if (soundInstance.isDone) {
                    alert.soundInstance = null
                } else if (soundInstance.isRepeatable) {
                    soundManager.resume(soundInstance)
                }

                if (soundManager.isPlaying(soundInstance)) {
                    interrupt = true
                }
            }
        }
    }

    companion object {
        val ID: Identifier = FlightAssistant.id("alert")
    }
}
