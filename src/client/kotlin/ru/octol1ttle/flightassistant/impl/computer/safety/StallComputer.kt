package ru.octol1ttle.flightassistant.impl.computer.safety

import net.minecraft.text.Text
import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.*
import ru.octol1ttle.flightassistant.api.computer.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.computer.autoflight.pitch.PitchLimiter
import ru.octol1ttle.flightassistant.api.computer.autoflight.thrust.ThrustController
import ru.octol1ttle.flightassistant.api.event.autoflight.pitch.PitchLimiterRegistrationCallback
import ru.octol1ttle.flightassistant.api.event.autoflight.thrust.ThrustControllerRegistrationCallback
import ru.octol1ttle.flightassistant.api.util.data
import ru.octol1ttle.flightassistant.config.FAConfig

class StallComputer : Computer(), PitchLimiter, ThrustController {
    var status: Status = Status.SAFE
        private set

    override fun subscribeToEvents() {
        PitchLimiterRegistrationCallback.EVENT.register { it.accept(this) }
        ThrustControllerRegistrationCallback.EVENT.register { it.accept(this) }
    }

    override fun tick(computers: ComputerAccess) {
        status =
            if (computers.data.flying && computers.data.forwardVelocity.length() == 0.0)
                if (status == Status.FULL_STALL || computers.data.velocity.y * 20 <= -10) Status.FULL_STALL else Status.APPROACHING_STALL
            else Status.SAFE
    }

    override fun getThrustInput(computers: ComputerAccess): ControlInput? {
        if (status != Status.SAFE) {
            return ControlInput(
                1.0f,
                if (FAConfig.safety.stallAutoThrust && status == Status.FULL_STALL) ControlInput.Priority.HIGHEST else ControlInput.Priority.SUGGESTION,
                Text.translatable("mode.flightassistant.thrust.maximum")
            )
        }

        return null
    }

    override fun getMaximumPitch(computers: ComputerAccess): ControlInput? {
        return ControlInput(
            (computers.data.forwardVelocity.length() * 20.0f * 3.0f).toFloat().coerceIn(0.0f..90.0f),
            if (FAConfig.safety.stallLimitPitch) ControlInput.Priority.HIGHEST else ControlInput.Priority.SUGGESTION,
            Text.translatable("mode.flightassistant.pitch.stall_protection")
        )
    }

    enum class Status {
        FULL_STALL,
        APPROACHING_STALL,
        SAFE
    }

    companion object {
        val ID: Identifier = FlightAssistant.id("stall")
    }
}
