package ru.octol1ttle.flightassistant.impl.computer.safety

import net.minecraft.text.Text
import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.*
import ru.octol1ttle.flightassistant.api.computer.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.computer.autoflight.pitch.*
import ru.octol1ttle.flightassistant.api.computer.autoflight.thrust.ThrustController
import ru.octol1ttle.flightassistant.api.event.autoflight.pitch.*
import ru.octol1ttle.flightassistant.api.event.autoflight.thrust.ThrustControllerRegistrationCallback
import ru.octol1ttle.flightassistant.api.util.*
import ru.octol1ttle.flightassistant.config.FAConfig

class VoidProximityComputer : Computer(), PitchLimiter, PitchController, ThrustController {
    var status: Status = Status.ABOVE_GROUND
        private set

    override fun subscribeToEvents() {
        PitchLimiterRegistrationCallback.EVENT.register { it.accept(this) }
        PitchControllerRegistrationCallback.EVENT.register { it.accept(this) }
        ThrustControllerRegistrationCallback.EVENT.register { it.accept(this) }
    }

    override fun tick(computers: ComputerAccess) {
        status = if (computers.data.groundLevel != null) {
            Status.ABOVE_GROUND
        } else {
            val heightAboveDamageAltitude: Double = computers.data.altitude - computers.data.voidLevel
            if (heightAboveDamageAltitude > 16.0) {
                Status.CLEAR_OF_DAMAGE_ALTITUDE
            } else if (status != Status.REACHED_DAMAGE_ALTITUDE && heightAboveDamageAltitude > 1.0) {
                Status.APPROACHING_DAMAGE_ALTITUDE
            } else {
                Status.REACHED_DAMAGE_ALTITUDE
            }
        }
    }

    override fun getMinimumPitch(computers: ComputerAccess): ControlInput? {
        if (FAConfig.safety.voidLimitPitch && status != Status.ABOVE_GROUND) {
            return ControlInput(
                (-90.0f + (computers.data.world.bottomY - (computers.data.altitude + computers.data.velocity.y * 20)) / 64.0f * 105.0f).toFloat()
                    .coerceIn(-35.0f..computers.thrust.getOptimumClimbPitch()),
                ControlInput.Priority.HIGH,
                Text.translatable("mode.flightassistant.pitch.void_protection")
            )
        }

        return null
    }

    override fun getPitchInput(computers: ComputerAccess): ControlInput? {
        if (FAConfig.safety.voidAutoPitch && status <= Status.APPROACHING_DAMAGE_ALTITUDE) {
            return ControlInput(computers.thrust.getOptimumClimbPitch(), ControlInput.Priority.HIGH, Text.translatable("mode.flightassistant.pitch.void_escape"),
                active = status == Status.REACHED_DAMAGE_ALTITUDE && computers.thrust.targetThrust == 1.0f && !computers.thrust.noThrustSource)
        }

        return null
    }

    override fun getThrustInput(computers: ComputerAccess): ControlInput? {
        if (FAConfig.safety.voidAutoThrust && status <= Status.APPROACHING_DAMAGE_ALTITUDE) {
            return ControlInput(
                1.0f,
                ControlInput.Priority.HIGH,
                Text.translatable("mode.flightassistant.thrust.toga"),
                active = status == Status.REACHED_DAMAGE_ALTITUDE
            )
        }

        return null
    }

    override fun reset() {
        status = Status.ABOVE_GROUND
    }

    enum class Status {
        REACHED_DAMAGE_ALTITUDE,
        APPROACHING_DAMAGE_ALTITUDE,
        CLEAR_OF_DAMAGE_ALTITUDE,
        ABOVE_GROUND
    }

    companion object {
        val ID: Identifier = FlightAssistant.id("void_proximity")
    }
}
