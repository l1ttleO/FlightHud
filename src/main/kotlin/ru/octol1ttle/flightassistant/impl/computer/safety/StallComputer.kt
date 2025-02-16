package ru.octol1ttle.flightassistant.impl.computer.safety

import net.minecraft.text.Text
import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.autoflight.FlightController
import ru.octol1ttle.flightassistant.api.autoflight.pitch.PitchLimiter
import ru.octol1ttle.flightassistant.api.autoflight.pitch.PitchLimiterRegistrationCallback
import ru.octol1ttle.flightassistant.api.autoflight.thrust.ThrustControllerRegistrationCallback
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerView
import ru.octol1ttle.flightassistant.config.FAConfig

class StallComputer(computers: ComputerView) : Computer(computers), PitchLimiter, FlightController {
    var status: Status = Status.SAFE
        private set
    private var maximumSafePitch: Float = 90.0f

    override fun subscribeToEvents() {
        PitchLimiterRegistrationCallback.EVENT.register { it.accept(this) }
        ThrustControllerRegistrationCallback.EVENT.register { it.accept(this) }
    }

    override fun tick() {
        val angleOfAttack: Float = computers.data.pitch - computers.data.flightPitch
        status =
            if (computers.data.flying && !computers.data.fallDistanceSafe && angleOfAttack > 90.0f)
                if (status == Status.FULL_STALL || computers.data.velocity.y * 20 <= -10) Status.FULL_STALL else Status.APPROACHING_STALL
            else Status.SAFE

        maximumSafePitch = (computers.data.flightPitch + 90.0).coerceAtMost(computers.data.forwardVelocity.length() * 20.0 * 3.0 + 45.0).toFloat()
    }

    override fun getThrustInput(): ControlInput? {
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

    override fun getMaximumPitch(): ControlInput? {
        if (maximumSafePitch > 90.0f || computers.data.fallDistanceSafe) {
            return null
        }

        return ControlInput(
            maximumSafePitch,
            ControlInput.Priority.HIGHEST,
            Text.translatable("mode.flightassistant.pitch.stall_protection"),
            1.5f,
            FAConfig.safety.stallLimitPitch
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
