package ru.octol1ttle.flightassistant.impl.computer.safety

import net.minecraft.client.sound.SoundManager
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.alert.Alert
import ru.octol1ttle.flightassistant.api.alert.AlertCategory
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.api.event.AlertCategoryRegistrationCallback
import ru.octol1ttle.flightassistant.api.util.data
import ru.octol1ttle.flightassistant.api.util.pause
import ru.octol1ttle.flightassistant.api.util.resume
import ru.octol1ttle.flightassistant.impl.alert.AlertSoundInstance
import ru.octol1ttle.flightassistant.impl.alert.elytra.ElytraDurabilityCriticalAlert
import ru.octol1ttle.flightassistant.impl.alert.elytra.ElytraDurabilityLowAlert
import ru.octol1ttle.flightassistant.impl.alert.fault.computer.ComputerFaultAlert
import ru.octol1ttle.flightassistant.impl.alert.fault.display.DisplayFaultAlert
import ru.octol1ttle.flightassistant.impl.alert.firework.FireworkExplosiveAlert
import ru.octol1ttle.flightassistant.impl.alert.firework.FireworkNoResponseAlert
import ru.octol1ttle.flightassistant.impl.alert.firework.FireworkSlowResponseAlert
import ru.octol1ttle.flightassistant.impl.alert.gpws.PullUpAlert
import ru.octol1ttle.flightassistant.impl.alert.gpws.SinkRateAlert
import ru.octol1ttle.flightassistant.impl.alert.gpws.TerrainAheadAlert
import ru.octol1ttle.flightassistant.impl.alert.navigation.ApproachingVoidDamageAltitudeAlert
import ru.octol1ttle.flightassistant.impl.alert.navigation.NoChunksLoadedAlert
import ru.octol1ttle.flightassistant.impl.alert.navigation.ReachedVoidDamageAltitudeAlert
import ru.octol1ttle.flightassistant.impl.alert.navigation.SlowChunkLoadingAlert
import ru.octol1ttle.flightassistant.impl.alert.stall.ApproachingStallAlert
import ru.octol1ttle.flightassistant.impl.alert.stall.FullStallAlert
import ru.octol1ttle.flightassistant.impl.alert.thrust.NoThrustSourceAlert
import ru.octol1ttle.flightassistant.impl.alert.thrust.ReverseThrustNotSupportedAlert
import ru.octol1ttle.flightassistant.impl.alert.thrust.ThrustLockedAlert
import ru.octol1ttle.flightassistant.impl.computer.AirDataComputer
import ru.octol1ttle.flightassistant.impl.computer.autoflight.FireworkComputer
import ru.octol1ttle.flightassistant.impl.computer.autoflight.PitchComputer
import ru.octol1ttle.flightassistant.impl.computer.autoflight.ThrustComputer
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
                .add(FireworkExplosiveAlert(Hand.MAIN_HAND))
                .add(FireworkExplosiveAlert(Hand.OFF_HAND))
                .add(FireworkSlowResponseAlert())
                .add(FireworkNoResponseAlert())
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
            AlertCategory(Text.translatable("alerts.flightassistant.gpws"))
                .add(ComputerFaultAlert(GroundProximityComputer.ID, Text.translatable("alerts.flightassistant.gpws.fault")))
                .add(PullUpAlert())
                .add(SinkRateAlert())
                .add(TerrainAheadAlert())
        )
        register(
            AlertCategory(Text.translatable("alerts.flightassistant.thrust"))
                .add(ComputerFaultAlert(ThrustComputer.ID, Text.translatable("alerts.flightassistant.thrust.fault")))
                .add(ThrustLockedAlert())
                .add(NoThrustSourceAlert())
                .add(ReverseThrustNotSupportedAlert())
        )
        register(
            AlertCategory(Text.translatable("alerts.flightassistant.navigation"))
                .add(ComputerFaultAlert(AirDataComputer.ID, Text.translatable("alerts.flightassistant.navigation.air_data_fault")))
                .add(ComputerFaultAlert(ChunkStatusComputer.ID, Text.translatable("alerts.flightassistant.navigation.chunk_status_fault")))
                .add(ComputerFaultAlert(VoidProximityComputer.ID, Text.translatable("alerts.flightassistant.navigation.void_proximity_fault")))
                .add(SlowChunkLoadingAlert())
                .add(NoChunksLoadedAlert())
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

        var interrupt: Boolean = computers.data.player.isDead || !computers.data.flying
        for (category: AlertCategory in categories) {
            for (alert: Alert in category.activeAlerts) {
                val soundInstance: AlertSoundInstance? = alert.soundInstance
                if (interrupt) {
                    if (soundInstance?.isRepeatable == true) {
                        soundManager.pause(soundInstance)
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
                    return
                }

                if (soundInstance.isRepeatable) {
                    soundManager.resume(soundInstance)
                }

                if (soundInstance.volume > 0.0f && !soundInstance.fadingOut) {
                    interrupt = true
                }
            }
        }
    }

    companion object {
        val ID: Identifier = FlightAssistant.id("alert")
    }
}
