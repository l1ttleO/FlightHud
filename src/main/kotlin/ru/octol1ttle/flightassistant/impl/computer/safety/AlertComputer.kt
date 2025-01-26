package ru.octol1ttle.flightassistant.impl.computer.safety

import net.minecraft.client.sound.SoundManager
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.alert.Alert
import ru.octol1ttle.flightassistant.api.alert.AlertCategory
import ru.octol1ttle.flightassistant.api.alert.AlertData
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.api.alert.AlertCategoryRegistrationCallback
import ru.octol1ttle.flightassistant.api.util.*
import ru.octol1ttle.flightassistant.api.util.extensions.*
import ru.octol1ttle.flightassistant.impl.alert.AlertSoundInstance
import ru.octol1ttle.flightassistant.impl.alert.autoflight.AutoThrustOffAlert
import ru.octol1ttle.flightassistant.impl.alert.autoflight.ForceAutopilotOffAlert
import ru.octol1ttle.flightassistant.impl.alert.autoflight.PlayerAutopilotOffAlert
import ru.octol1ttle.flightassistant.impl.alert.elytra.ElytraDurabilityCriticalAlert
import ru.octol1ttle.flightassistant.impl.alert.elytra.ElytraDurabilityLowAlert
import ru.octol1ttle.flightassistant.impl.alert.fault.DisplayFaultAlert
import ru.octol1ttle.flightassistant.impl.alert.fault.computer.AlertComputerFaultAlert
import ru.octol1ttle.flightassistant.impl.alert.fault.computer.ComputerFaultAlert
import ru.octol1ttle.flightassistant.impl.alert.firework.FireworkExplosiveAlert
import ru.octol1ttle.flightassistant.impl.alert.firework.FireworkNoResponseAlert
import ru.octol1ttle.flightassistant.impl.alert.firework.FireworkSlowResponseAlert
import ru.octol1ttle.flightassistant.impl.alert.flight_controls.ProtectionsLostAlert
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
import ru.octol1ttle.flightassistant.impl.computer.autoflight.*
import ru.octol1ttle.flightassistant.impl.display.HudDisplayHost

// TODO: allow new alerts to repeat an already-played active sound
class AlertComputer(private val soundManager: SoundManager) : Computer() {
    internal var alertsFaulted: Boolean = false
    val categories: MutableList<AlertCategory> = ArrayList()
    private val alertLists: HashMap<AlertData, ChangeTrackingArrayList<Alert>> = HashMap()
    private val sounds: HashMap<AlertData, AlertSoundInstance> = HashMap()

    override fun invokeEvents() {
        registerBuiltin()
        AlertCategoryRegistrationCallback.EVENT.invoker().register(this::register)
    }

    private fun registerBuiltin() {
        register(
            AlertCategory(Text.translatable("alerts.flightassistant.alert"))
                .add(AlertComputerFaultAlert())
        )
        register(
            AlertCategory(Text.translatable("alerts.flightassistant.autoflight"))
                .add(ComputerFaultAlert(AutoFlightComputer.ID, Text.translatable("alerts.flightassistant.autoflight.fault")))
                .add(ComputerFaultAlert(HeadingComputer.ID, Text.translatable("alerts.flightassistant.autoflight.heading_fault")))
                .add(ComputerFaultAlert(PitchComputer.ID, Text.translatable("alerts.flightassistant.autoflight.pitch_fault")))
                .add(ForceAutopilotOffAlert())
                .add(PlayerAutopilotOffAlert())
                .add(AutoThrustOffAlert())
        )
        register(
            AlertCategory(Text.translatable("alerts.flightassistant.elytra"))
                .add(ComputerFaultAlert(ElytraStatusComputer.ID, Text.translatable("alerts.flightassistant.elytra.fault")))
                .add(ElytraDurabilityCriticalAlert())
                .add(ElytraDurabilityLowAlert())
        )
        register(
            AlertCategory(Text.translatable("alerts.flightassistant.hud"))
                .addAll(HudDisplayHost.identifiers().map { DisplayFaultAlert(it) })
        )
        register(
            AlertCategory(Text.translatable("alerts.flightassistant.firework"))
                .add(ComputerFaultAlert(FireworkComputer.ID, Text.translatable("alerts.flightassistant.firework.fault")))
                .add(FireworkExplosiveAlert(Hand.MAIN_HAND))
                .add(FireworkExplosiveAlert(Hand.OFF_HAND))
                .add(FireworkNoResponseAlert())
                .add(FireworkSlowResponseAlert())
        )
        register(
            AlertCategory(Text.translatable("alerts.flightassistant.flight_controls"))
                .add(ComputerFaultAlert(PitchComputer.ID, Text.translatable("alerts.flightassistant.flight_controls.pitch_fault"), listOf(
                    Text.translatable("alerts.flightassistant.flight_controls.pitch_fault.use_manual_pitch"),
                )))
                .add(ProtectionsLostAlert())
        )
        register(
            AlertCategory(Text.translatable("alerts.flightassistant.gpws"))
                .add(ComputerFaultAlert(GroundProximityComputer.ID, Text.translatable("alerts.flightassistant.gpws.fault")))
                .add(PullUpAlert())
                .add(SinkRateAlert())
                .add(TerrainAheadAlert())
        )
        register(
            AlertCategory(Text.translatable("alerts.flightassistant.navigation"))
                .add(ComputerFaultAlert(AirDataComputer.ID, Text.translatable("alerts.flightassistant.navigation.air_data_fault"), data = AlertData.MASTER_WARNING))
                .add(ComputerFaultAlert(ChunkStatusComputer.ID, Text.translatable("alerts.flightassistant.navigation.chunk_status_fault")))
                .add(ComputerFaultAlert(VoidProximityComputer.ID, Text.translatable("alerts.flightassistant.navigation.void_proximity_fault")))
                .add(ReachedVoidDamageAltitudeAlert())
                .add(ApproachingVoidDamageAltitudeAlert())
                .add(NoChunksLoadedAlert())
                .add(SlowChunkLoadingAlert())
        )
        register(
            AlertCategory(Text.translatable("alerts.flightassistant.stall"))
                .add(ComputerFaultAlert(StallComputer.ID, Text.translatable("alerts.flightassistant.stall.detection_fault")))
                .add(FullStallAlert())
                .add(ApproachingStallAlert())
        )
        register(
            AlertCategory(Text.translatable("alerts.flightassistant.thrust"))
                .add(ComputerFaultAlert(ThrustComputer.ID, Text.translatable("alerts.flightassistant.thrust.fault")))
                .add(ThrustLockedAlert())
                .add(NoThrustSourceAlert())
                .add(ReverseThrustNotSupportedAlert())
        )
    }

    fun register(category: AlertCategory) {
        if (categories.contains(category)) {
            throw IllegalArgumentException("Already registered alert category: ${category.categoryText.string}")
        }

        categories.add(category)
    }

    fun hideCurrentAlert() {
        for (category: AlertCategory in categories) {
            if (category.activeAlerts.isEmpty()) {
                continue
            }
            category.ignoredAlerts.add(category.activeAlerts.removeAt(0))
            break
        }
    }

    fun showHiddenAlert() {
        for (category: AlertCategory in categories) {
            if (category.ignoredAlerts.isEmpty()) {
                continue
            }
            category.ignoredAlerts.removeAt(0)
            break
        }
    }

    override fun tick(computers: ComputerAccess) {
        updateAlerts(computers)
        if (computers.data.player.isDead || !computers.data.flying) {
            tickSoundsAndStopInactive(true)
            return
        }
        tickSoundsAndStopInactive()
        startNewSounds(computers)
        stopOutPrioritizedAlerts()
    }

    private fun updateAlerts(computers: ComputerAccess) {
        for (category: AlertCategory in categories) {
            category.updateActiveAlerts(computers)
        }

        categories.sortBy { it.getHighestPriority() ?: Int.MAX_VALUE }

        for (list: ChangeTrackingArrayList<Alert> in alertLists.values) {
            list.startTracking()
        }

        for (alert: Alert in categories.flatMap { it.activeAlerts } ) {
            alertLists.computeIfAbsent(alert.data) { return@computeIfAbsent ChangeTrackingArrayList<Alert>() }.add(alert)
        }
    }

    private fun tickSoundsAndStopInactive(force: Boolean = false) {
        val iterator = sounds.entries.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            for (i: Int in 1..FATickCounter.ticksPassed) {
                entry.value.tick()
                soundManager.applyVolume(entry.value)
            }

            if (force || alertLists[entry.key]?.isEmpty() != false) {
                entry.value.setRepeat(false, soundManager)
                if (entry.value.fadeOut(FATickCounter.ticksPassed)) {
                    iterator.remove()
                }
                soundManager.applyVolume(entry.value)
            }
        }
    }

    private fun startNewSounds(computers: ComputerAccess) {
        val activeDatas: List<AlertData> = alertLists.filterValues { it.hasNewElements() }.keys.sortedBy { it.priority }
        val highestPriority: List<AlertData> = activeDatas.getHighestPriority()

        var anyStartedThisTick = false
        for (data: AlertData in highestPriority) {
            val existing: AlertSoundInstance? = sounds[data]
            if (existing == null || (!existing.isRepeatable && existing.age > 60)) {
                soundManager.stop(existing)

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

    override fun reset() {
        categories.forEach { it.activeAlerts.clear() }
        categories.forEach { it.ignoredAlerts.clear() }
        sounds.values.forEach { soundManager.stop(it) }
        sounds.clear()
    }

    companion object {
        val ID: Identifier = FlightAssistant.id("alert")
    }
}
