package ru.octol1ttle.flightassistant.impl.computer.autoflight

import kotlin.math.abs
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.autoflight.pitch.PitchController
import ru.octol1ttle.flightassistant.api.autoflight.pitch.PitchControllerRegistrationCallback
import ru.octol1ttle.flightassistant.api.autoflight.pitch.PitchLimiter
import ru.octol1ttle.flightassistant.api.autoflight.pitch.PitchLimiterRegistrationCallback
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerView
import ru.octol1ttle.flightassistant.api.util.FATickCounter
import ru.octol1ttle.flightassistant.api.util.event.ChangeLookDirectionEvents
import ru.octol1ttle.flightassistant.api.util.extensions.filterNonFaulted
import ru.octol1ttle.flightassistant.api.util.extensions.getActiveHighestPriority
import ru.octol1ttle.flightassistant.api.util.requireIn

class PitchComputer(computers: ComputerView) : Computer(computers), PitchController {
    private val limiters: MutableList<PitchLimiter> = ArrayList()
    private val controllers: MutableList<PitchController> = ArrayList()
    private var automationsAllowed: Boolean = false
    internal var manualOverride: Boolean = false
    var minimumPitch: ControlInput? = null
        private set
    var maximumPitch: ControlInput? = null
        private set
    var activeInput: ControlInput? = null
        private set

    override fun subscribeToEvents() {
        PitchControllerRegistrationCallback.EVENT.register { it.accept(this) }
        ChangeLookDirectionEvents.PITCH.register { mcPitchDelta, output ->
            if (!this.manualOverride && this.automationsAllowed) {
                val pitchDelta: Float = -mcPitchDelta

                val oldPitch: Float = computers.data.pitch
                val newPitch: Float = oldPitch + pitchDelta

                val min: ControlInput? = this.minimumPitch
                val max: ControlInput? = this.maximumPitch
                if (max != null && max.active && pitchDelta > 0.0f && newPitch > max.target) {
                    output.add(ControlInput(-(max.target - oldPitch).coerceAtLeast(0.0f), max.priority))
                } else if (min != null && min.active && pitchDelta < 0.0f && newPitch < min.target) {
                    output.add(ControlInput(-(min.target - oldPitch).coerceAtMost(0.0f), min.priority))
                }
            }
        }
    }

    override fun invokeEvents() {
        PitchLimiterRegistrationCallback.EVENT.invoker().register(limiters::add)
        PitchControllerRegistrationCallback.EVENT.invoker().register(controllers::add)
    }

    override fun tick() {
        automationsAllowed = !manualOverride && !computers.protections.protectionsLost && computers.data.automationsAllowed()

        updateSafePitches()

        val inputs: List<ControlInput> = controllers.filterNonFaulted().mapNotNull { it.getPitchInput() }.sortedBy { it.priority.value }
        if (inputs.isEmpty()) {
            activeInput = null
            return
        }

        val pitch: Float = computers.data.pitch
        val finalInput: ControlInput? = inputs.getActiveHighestPriority().maxByOrNull { it.target }
        if (finalInput == null) {
            activeInput = null
            return
        }

        activeInput = finalInput
        if (automationsAllowed && finalInput.active) {
            var target: Float = finalInput.target
            if (!finalInput.priority.isHigherOrSame(minimumPitch?.priority)) {
                target = target.coerceAtLeast(minimumPitch!!.target)
            }
            if (!finalInput.priority.isHigherOrSame(maximumPitch?.priority)) {
                target = target.coerceAtMost(maximumPitch!!.target)
            }
            smoothSetPitch(computers.data.player, pitch, target.requireIn(-90.0f..90.0f), finalInput.deltaTimeMultiplier.requireIn(0.001f..Float.MAX_VALUE))
        }
    }

    private fun updateSafePitches() {
        val maximums: List<ControlInput> = limiters.filterNonFaulted().mapNotNull { it.getMaximumPitch() }.sortedBy { it.priority.value }
        maximumPitch = maximums.getActiveHighestPriority().minByOrNull { it.target }
        val max: ControlInput? = maximumPitch
        if (max != null) {
            max.target.requireIn(-90.0f..90.0f)
            max.deltaTimeMultiplier.requireIn(0.001f..Float.MAX_VALUE)
        }

        val minimums: List<ControlInput> = limiters.filterNonFaulted().mapNotNull { it.getMinimumPitch() }.sortedBy { it.priority.value }
        minimumPitch = minimums.getActiveHighestPriority().maxByOrNull { it.target }
        val min: ControlInput? = minimumPitch
        if (min != null) {
            min.target.requireIn(-90.0f..90.0f)
            min.deltaTimeMultiplier.requireIn(0.001f..Float.MAX_VALUE)
        }

        if (max != null && min != null && max.priority.isHigherOrSame(min.priority)) {
            minimumPitch = min.copy(target = min.target.coerceAtMost(max.target))
        }
    }

    override fun getPitchInput(): ControlInput? {
        val max: ControlInput? = maximumPitch
        if (max != null && computers.data.pitch > max.target) {
            return max
        } else {
            val min: ControlInput? = minimumPitch
            if (min != null && computers.data.pitch < min.target) {
                return min
            }
        }

        return null
    }

    private fun smoothSetPitch(player: PlayerEntity, current: Float, target: Float, deltaTimeMultiplier: Float) {
        val diff: Float = target - current

        val closeDistanceMultiplier: Float =
            if (diff == 0.0f) 1.0f
            else (1.0f / abs(diff)).coerceAtLeast(1.0f)

        val delta: Float = diff * (FATickCounter.timePassed * deltaTimeMultiplier * closeDistanceMultiplier).coerceIn(0.0f..1.0f)
        player.pitch -= delta
        player.prevPitch -= delta
    }

    override fun reset() {
        automationsAllowed = false
        manualOverride = true
        minimumPitch = null
        maximumPitch = null
        activeInput = null
    }

    companion object {
        val ID: Identifier = FlightAssistant.id("pitch")
    }
}
