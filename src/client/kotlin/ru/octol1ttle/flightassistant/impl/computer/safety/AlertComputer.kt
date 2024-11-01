package ru.octol1ttle.flightassistant.impl.computer.safety

import net.minecraft.client.sound.SoundManager
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.alert.*
import ru.octol1ttle.flightassistant.api.computer.*
import ru.octol1ttle.flightassistant.api.event.AlertCategoryRegistrationCallback
import ru.octol1ttle.flightassistant.api.util.data
import ru.octol1ttle.flightassistant.impl.alert.AlertSoundInstance
import ru.octol1ttle.flightassistant.impl.alert.elytra.*
import ru.octol1ttle.flightassistant.impl.alert.fault.computer.ComputerFaultAlert
import ru.octol1ttle.flightassistant.impl.alert.fault.display.DisplayFaultAlert
import ru.octol1ttle.flightassistant.impl.alert.navigation.*
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
            AlertCategory(Text.translatable("alerts.flightassistant.autoflight"))
                .add(
                    ComputerFaultAlert(
                        PitchComputer.ID,
                        Text.translatable("alerts.flightassistant.autoflight.pitch_fault")
                    )
                )
                .add(
                    ComputerFaultAlert(
                        ThrustComputer.ID,
                        Text.translatable("alerts.flightassistant.autoflight.thrust_fault")
                    )
                )
        )
        register(
            AlertCategory(Text.translatable("alerts.flightassistant.firework"))
                .add(
                    ComputerFaultAlert(
                        FireworkComputer.ID,
                        Text.translatable("alerts.flightassistant.firework.fault")
                    )
                )
        )
        register(
            AlertCategory(Text.translatable("alerts.flightassistant.flight_controls"))
                .add(
                    ComputerFaultAlert(
                        PitchComputer.ID,
                        Text.translatable("alerts.flightassistant.flight_controls.pitch_fault")
                    )
                )
        )
        register(
            AlertCategory(Text.translatable("alerts.flightassistant.elytra"))
                .add(ComputerFaultAlert(ElytraStatusComputer.ID, Text.translatable("alerts.flightassistant.elytra.fault")))
                .add(ElytraDurabilityLowAlert())
                .add(ElytraDurabilityCriticalAlert())
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

    override fun tick(computers: ComputerAccess) {
        for (category: AlertCategory in categories) {
            category.updateActiveAlerts(computers, soundManager)
        }

        categories.sortBy { it.getHighestPriority()?.priority }

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
                    alert.soundInstance = AlertSoundInstance(computers.data.player, alert.data)
                    soundManager.play(alert.soundInstance)
                }
                if (soundManager.isPlaying(alert.soundInstance)) {
                    interrupt = true
                }
            }
        }
    }

    companion object {
        val ID: Identifier = FlightAssistant.computerId("alert")
    }
}
