package ru.octol1ttle.flightassistant.impl.computer.safety

import net.minecraft.text.Text
import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.api.computer.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.computer.autoflight.pitch.PitchController
import ru.octol1ttle.flightassistant.api.computer.autoflight.pitch.PitchLimiter
import ru.octol1ttle.flightassistant.api.event.autoflight.pitch.PitchControllerRegistrationCallback
import ru.octol1ttle.flightassistant.api.event.autoflight.pitch.PitchLimiterRegistrationCallback
import ru.octol1ttle.flightassistant.api.util.data
import ru.octol1ttle.flightassistant.impl.computer.AirDataComputer

class GroundProximityComputer : Computer(), PitchLimiter, PitchController {
    var groundImpactTime: Float = Float.MAX_VALUE
        private set
    var groundImpactStatus: Status = Status.SAFE
        private set

    override fun subscribeToEvents() {
        PitchLimiterRegistrationCallback.EVENT.register { it.accept(this) }
        PitchControllerRegistrationCallback.EVENT.register { it.accept(this) }
    }

    override fun tick(computers: ComputerAccess) {
        groundImpactTime = computeGroundImpactTime(computers.data)
        groundImpactStatus = if (groundImpactStatus == Status.SAFE && groundImpactTime > 10.0f || groundImpactTime > 15.0f) {
            Status.SAFE
        } else if (groundImpactStatus >= Status.CAUTION && groundImpactTime > 5.0f) {
            Status.CAUTION
        } else if (groundImpactStatus >= Status.WARNING && groundImpactTime > 0.75f) {
            Status.WARNING
        } else {
            Status.RECOVER
        }
    }

    private fun computeGroundImpactTime(data: AirDataComputer): Float {
        if (!data.flying || data.fallDistanceSafe || data.velocity.y * 20 > -10) {
            return Float.MAX_VALUE
        }

        val groundLevel: Double? = data.groundLevel
        val impactLevel: Double =
            if (groundLevel == null || groundLevel == Double.MAX_VALUE) data.voidLevel.toDouble()
            else groundLevel
        return ((data.altitude - impactLevel) / (data.velocity.y * -20)).toFloat()
    }

    override fun getMinimumPitch(computers: ComputerAccess): ControlInput? {
        if (groundImpactStatus <= Status.WARNING) {
            return ControlInput(
                computers.data.pitch,
                /*if (FAConfig.safety.voidLimitPitch) */ControlInput.Priority.HIGH/* else ControlInput.Priority.SUGGESTION TODO*/,
                Text.translatable("mode.flightassistant.pitch.terrain_protection")
            )
        }

        return null
    }

    override fun getPitchInput(computers: ComputerAccess): ControlInput? {
        if (groundImpactStatus == Status.RECOVER) {
            return ControlInput(90.0f, ControlInput.Priority.HIGH, Text.translatable("mode.flightassistant.pitch.terrain_escape"), 1.0f / groundImpactTime)
        }

        return null
    }

    enum class Status {
        RECOVER,
        WARNING,
        CAUTION,
        SAFE
    }

    companion object {
        val ID: Identifier = FlightAssistant.id("ground_proximity")
    }
}
