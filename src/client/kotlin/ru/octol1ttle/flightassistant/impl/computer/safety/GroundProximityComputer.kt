package ru.octol1ttle.flightassistant.impl.computer.safety

import kotlin.math.min
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.world.Heightmap
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.*
import ru.octol1ttle.flightassistant.api.computer.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.computer.autoflight.pitch.*
import ru.octol1ttle.flightassistant.api.event.autoflight.pitch.*
import ru.octol1ttle.flightassistant.api.util.data
import ru.octol1ttle.flightassistant.impl.computer.AirDataComputer

class GroundProximityComputer : Computer(), PitchLimiter, PitchController {
    private var maximumLookAheadTime: Float = 15.0f

    var groundImpactTime: Float = Float.MAX_VALUE
        private set
    var groundImpactStatus: Status = Status.SAFE
        private set
    var obstacleImpactTime: Float = Float.MAX_VALUE
        private set
    var obstacleImpactStatus: Status = Status.SAFE
        private set

    override fun subscribeToEvents() {
        PitchLimiterRegistrationCallback.EVENT.register { it.accept(this) }
        PitchControllerRegistrationCallback.EVENT.register { it.accept(this) }
    }

    override fun tick(computers: ComputerAccess) {
        val data: AirDataComputer = computers.data
        if (!data.flying) {
            groundImpactStatus = Status.SAFE
            obstacleImpactStatus = Status.SAFE
            return
        }

        val anyBlocksAbove: Boolean = data.world.getTopY(Heightmap.Type.MOTION_BLOCKING, data.player.blockX, data.player.blockZ) > data.player.y
        val clearThreshold: Float = if (anyBlocksAbove) 7.5f else 15.0f
        val cautionThreshold: Float = if (anyBlocksAbove) 5.0f else 10.0f
        val warningThreshold: Float = if (anyBlocksAbove) 2.5f else 5.0f

        maximumLookAheadTime = clearThreshold

        groundImpactTime = computeGroundImpactTime(data)
        groundImpactStatus =
            if (groundImpactStatus == Status.SAFE && (data.velocity.y * 20 > -10 || groundImpactTime > cautionThreshold)
                || data.fallDistanceSafe || data.velocity.y * 20 > -7.5 || groundImpactTime > clearThreshold) {
                Status.SAFE
            } else if (groundImpactStatus >= Status.CAUTION && groundImpactTime > warningThreshold) {
                Status.CAUTION
            } else if (groundImpactStatus >= Status.WARNING && groundImpactTime > 0.75f) {
                Status.WARNING
            } else {
                Status.RECOVER
            }

        obstacleImpactTime = computeObstacleImpactTime(computers.data)
        obstacleImpactStatus =
            if (obstacleImpactStatus == Status.SAFE && ((data.velocity.horizontalLength() * 10 - 3) < data.player.health * 0.5f || obstacleImpactTime > cautionThreshold)
                || (data.velocity.horizontalLength() * 10 - 3) < data.player.health * 0.25f || obstacleImpactTime > clearThreshold) {
                Status.SAFE
            } else if (obstacleImpactStatus >= Status.CAUTION && obstacleImpactTime > warningThreshold) {
                Status.CAUTION
            } else if (obstacleImpactStatus >= Status.WARNING && obstacleImpactTime > 0.5f) {
                Status.WARNING
            } else {
                Status.RECOVER
            }
    }

    private fun computeGroundImpactTime(data: AirDataComputer): Float {
        if (data.velocity.y >= 0) {
            return Float.MAX_VALUE
        }

        val groundLevel: Double? = data.groundLevel
        val impactLevel: Double =
            if (groundLevel == null || groundLevel == Double.MAX_VALUE) data.voidLevel.toDouble()
            else groundLevel
        return ((data.altitude - impactLevel) / (data.velocity.y * -20)).toFloat()
    }

    // TODO: max/min terrain altitude on status display (that's gonna be so fucking cool /srs)
    private fun computeObstacleImpactTime(data: AirDataComputer): Float {
        TODO()
    }

    override fun getMinimumPitch(computers: ComputerAccess): ControlInput? {
        if (groundImpactStatus <= Status.WARNING || obstacleImpactStatus <= Status.WARNING) {
            return ControlInput(
                computers.data.pitch,
                /*if (FAConfig.safety.voidLimitPitch) */ControlInput.Priority.HIGH/* else ControlInput.Priority.SUGGESTION TODO*/,
                Text.translatable("mode.flightassistant.pitch.terrain_protection")
            )
        }

        return null
    }

    override fun getPitchInput(computers: ComputerAccess): ControlInput? {
        if (groundImpactStatus == Status.RECOVER || obstacleImpactStatus <= Status.RECOVER) {
            return ControlInput(90.0f, ControlInput.Priority.HIGH, Text.translatable("mode.flightassistant.pitch.terrain_escape"), 1.0f / min(groundImpactTime, obstacleImpactTime))
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
