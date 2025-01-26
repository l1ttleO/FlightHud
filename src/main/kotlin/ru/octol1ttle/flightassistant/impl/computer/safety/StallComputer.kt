package ru.octol1ttle.flightassistant.impl.computer.safety

import net.minecraft.text.Text
import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.api.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.autoflight.pitch.PitchLimiter
import ru.octol1ttle.flightassistant.api.autoflight.thrust.ThrustController
import ru.octol1ttle.flightassistant.api.autoflight.pitch.PitchLimiterRegistrationCallback
import ru.octol1ttle.flightassistant.api.autoflight.thrust.ThrustControllerRegistrationCallback
import ru.octol1ttle.flightassistant.api.util.extensions.data
import ru.octol1ttle.flightassistant.config.FAConfig

class StallComputer : Computer(), PitchLimiter, ThrustController {
    var status: Status = Status.SAFE
        private set
    private var maximumSafePitch: Float = 90.0f

    override fun subscribeToEvents() {
        PitchLimiterRegistrationCallback.EVENT.register { it.accept(this) }
        ThrustControllerRegistrationCallback.EVENT.register { it.accept(this) }
    }

    override fun tick(computers: ComputerAccess) {
        status =
            if (computers.data.flying && computers.data.velocity.y < 0 && !computers.data.fallDistanceSafe && computers.data.forwardVelocity.length() == 0.0)
                if (status == Status.FULL_STALL || computers.data.velocity.y * 20 <= -10) Status.FULL_STALL else Status.APPROACHING_STALL
            else Status.SAFE
        maximumSafePitch = (computers.data.forwardVelocity.length() * 20.0f * 3.0f).toFloat().coerceIn(0.0f..90.0f)
    }

    override fun getThrustInput(computers: ComputerAccess): ControlInput? {
        if (FAConfig.safety.stallAutoThrust && status != Status.SAFE) {
            return ControlInput(
                1.0f,
                ControlInput.Priority.HIGHEST,
                Text.translatable("mode.flightassistant.thrust.toga"),
                active = status == Status.FULL_STALL
            )
        }

        return null
    }

    override fun getMaximumPitch(computers: ComputerAccess): ControlInput? {
        if (computers.data.fallDistanceSafe) {
            return null
        }

        return ControlInput(
            maximumSafePitch,
            ControlInput.Priority.HIGHEST,
            Text.translatable("mode.flightassistant.pitch.stall_protection"),
            active = FAConfig.safety.stallLimitPitch
        )
    }

    override fun reset() {
        status = Status.SAFE
        maximumSafePitch = 90.0f
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
