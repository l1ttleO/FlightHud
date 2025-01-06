package ru.octol1ttle.flightassistant.impl.computer.safety

import net.minecraft.client.sound.SoundManager
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.alert.AlertCategory
import ru.octol1ttle.flightassistant.api.alert.AlertData
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.api.event.AlertCategoryRegistrationCallback
import ru.octol1ttle.flightassistant.api.util.FATickCounter
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
    private val sounds: HashMap<AlertData, AlertSoundInstance> = HashMap()

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
        updateAlerts(computers)
        if (computers.data.player.isDead || !computers.data.flying) {
            stopInactiveAlerts(true)
            return
        }
        stopInactiveAlerts()
        startNewSounds(computers)
        stopOutPrioritizedAlerts()
    }

    private fun updateAlerts(computers: ComputerAccess) {
        for (category: AlertCategory in categories) {
            category.updateActiveAlerts(computers)
        }

        categories.sortBy { it.getHighestPriority()?.priority }
    }

    private fun stopInactiveAlerts(force: Boolean = false) {
        val iterator = sounds.entries.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (!force && categories.any { category -> category.activeAlerts.any { it.data == entry.key } }) {
                continue
            }

            entry.value.setRepeat(false, soundManager)
            if (entry.value.fadeOut(FATickCounter.ticksPassed)) {
                iterator.remove()
            }
        }
    }

    private fun startNewSounds(computers: ComputerAccess) {
        val activeDatas: List<AlertData> = categories.flatMap { it.activeAlerts.map { alert -> alert.data } }.sortedBy { it.priority }

        var anyStartedThisTick = false
        for (data: AlertData in activeDatas) {
            if (!sounds.containsKey(data)) {
                val instance = AlertSoundInstance(computers.data.player, data)
                sounds[data] = instance
                if (!anyStartedThisTick) {
                    soundManager.play(instance)
                } else if (instance.isRepeatable) {
                    soundManager.play(instance)
                    if (instance.isRepeatable) {
                        soundManager.pause(instance)
                    }
                }

                anyStartedThisTick = true
            }
        }
    }

    private fun stopOutPrioritizedAlerts() {
        var interrupt = false
        for (entry in sounds.entries.sortedBy { it.key.priority }) {
            if (!interrupt) {
                interrupt = true
                if (entry.value.isRepeatable) {
                    soundManager.resume(entry.value)
                }
                continue
            }

            if (entry.value.isRepeatable) {
                soundManager.pause(entry.value)
            } else {
                entry.value.fadeOut(FATickCounter.ticksPassed)
            }
        }
    }

    companion object {
        val ID: Identifier = FlightAssistant.id("alert")
    }
}
