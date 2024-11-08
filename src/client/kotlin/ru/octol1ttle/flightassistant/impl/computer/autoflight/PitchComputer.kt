package ru.octol1ttle.flightassistant.impl.computer.autoflight

import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.Direction
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.*
import ru.octol1ttle.flightassistant.api.computer.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.computer.autoflight.pitch.*
import ru.octol1ttle.flightassistant.api.event.ChangeLookDirectionEvents
import ru.octol1ttle.flightassistant.api.event.autoflight.pitch.*
import ru.octol1ttle.flightassistant.api.util.*
import ru.octol1ttle.flightassistant.impl.computer.ComputerHost

class PitchComputer : Computer(), PitchController {
    private val limiters: ArrayList<PitchLimiter> = ArrayList()
    private val controllers: ArrayList<PitchController> = ArrayList()
    var minimumPitch: ControlInput? = null
        private set
    var maximumPitch: ControlInput? = null
        private set
    var pitchMode: Text? = null
        private set

    override fun subscribeToEvents() {
        PitchControllerRegistrationCallback.EVENT.register { it.accept(this) }
        ChangeLookDirectionEvents.PITCH.register(this::onPitchChange)
    }

    override fun invokeEvents() {
        PitchLimiterRegistrationCallback.EVENT.invoker().register(limiters::add)
        PitchControllerRegistrationCallback.EVENT.invoker().register(controllers::add)
    }

    override fun tick(computers: ComputerAccess) {
        if (!computers.data.automationsAllowed()) {
            return
        }

        updateSafePitches(computers)

        val inputs: List<ControlInput> = controllers.mapNotNull { it.getPitchInput(computers) }.sortedBy { it.priority.value }
        if (inputs.isEmpty()) {
            pitchMode = null
            return
        }

        val pitch: Float = computers.data.pitch
        val finalInput: ControlInput? = inputs.filter {
            it.priority.value == inputs[0].priority.value
                    && !limiters.any { limiter -> it.priority != ControlInput.Priority.SUGGESTION && !it.priority.isHigherOrSame(limiter.blockPitchChange(computers, if (it.target > pitch) Direction.UP else Direction.DOWN)) }
                    && (it.priority.isHigherOrSame(minimumPitch?.priority) || it.target >= (minimumPitch?.target ?: -90.0f))
                    && (it.priority.isHigherOrSame(maximumPitch?.priority) || it.target <= (maximumPitch?.target ?: 90.0f))
        }.maxByOrNull { it.target }
        if (finalInput == null) {
            pitchMode = null
            return
        }

        smoothSetPitch(computers.data.player, pitch, finalInput.target)
        pitchMode = finalInput.text
    }

    private fun onPitchChange(entity: Entity, mcPitchDelta: Float): Float? {
        if (entity is ClientPlayerEntity) {
            val pitchDelta: Float = -mcPitchDelta

            val oldPitch: Float = -entity.pitch
            val newPitch: Float = oldPitch + pitchDelta

            val min: ControlInput? = minimumPitch
            val max: ControlInput? = maximumPitch
            if (min != null && pitchDelta < 0.0f && newPitch < min.target) {
                return -(min.target - oldPitch).coerceAtMost(0.0f)
            }
            if (max != null && pitchDelta > 0.0f && newPitch > max.target) {
                return -(max.target - oldPitch).coerceAtLeast(0.0f)
            }
            if (limiters.any { limiter ->
                    limiter.blockPitchChange(ComputerHost, if (pitchDelta > 0) Direction.UP else Direction.DOWN) != null
                }) {
                return 0.0f
            }
        }
        return null
    }

    private fun updateSafePitches(computers: ComputerAccess) {
        val minimums: List<ControlInput> = limiters.mapNotNull { it.getMinimumPitch(computers) }.sortedBy { it.priority.value }
        minimumPitch =
            if (minimums.isNotEmpty()) minimums.filter { it.priority.value == minimums[0].priority.value }
                .maxBy { it.target }
            else null

        val maximums: List<ControlInput> = limiters.mapNotNull { it.getMaximumPitch(computers) }.sortedBy { it.priority.value }
        maximumPitch =
            if (maximums.isNotEmpty()) maximums.filter { it.priority.value == maximums[0].priority.value }
                .minBy { it.target }
            else null
    }

    override fun getPitchInput(computers: ComputerAccess): ControlInput? {
        if (maximumPitch != null && computers.data.pitch > maximumPitch!!.target) {
            val (target: Float, priority: ControlInput.Priority, text: Text?) = maximumPitch!!
            return ControlInput(target, priority, text)
        } else if (minimumPitch != null && computers.data.pitch < minimumPitch!!.target) {
            val (target: Float, priority: ControlInput.Priority, text: Text?) = minimumPitch!!
            return ControlInput(target, priority, text)
        }

        return null
    }

    private fun smoothSetPitch(player: PlayerEntity, current: Float, target: Float) {
        player.pitch -= (target - current) * FATickCounter.timePassed
    }

    companion object {
        val ID: Identifier = FlightAssistant.id("pitch")
    }
}
