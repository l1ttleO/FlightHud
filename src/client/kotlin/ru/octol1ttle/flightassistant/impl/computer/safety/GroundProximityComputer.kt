package ru.octol1ttle.flightassistant.impl.computer.safety

import kotlin.math.min
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.Heightmap
import net.minecraft.world.RaycastContext
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.api.computer.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.computer.autoflight.pitch.PitchController
import ru.octol1ttle.flightassistant.api.computer.autoflight.pitch.PitchLimiter
import ru.octol1ttle.flightassistant.api.event.autoflight.pitch.PitchControllerRegistrationCallback
import ru.octol1ttle.flightassistant.api.event.autoflight.pitch.PitchLimiterRegistrationCallback
import ru.octol1ttle.flightassistant.api.util.data
import ru.octol1ttle.flightassistant.api.util.requireIn
import ru.octol1ttle.flightassistant.config.FAConfig
import ru.octol1ttle.flightassistant.impl.computer.AirDataComputer

class GroundProximityComputer : Computer(), PitchLimiter, PitchController {
    private var groundImpactTime: Float = Float.MAX_VALUE
    var groundImpactStatus: Status = Status.SAFE
        private set
    private var obstacleImpactTime: Float = Float.MAX_VALUE
    var obstacleImpactStatus: Status = Status.SAFE
        private set

    override fun subscribeToEvents() {
        PitchLimiterRegistrationCallback.EVENT.register { it.accept(this) }
        PitchControllerRegistrationCallback.EVENT.register { it.accept(this) }
    }

    override fun tick(computers: ComputerAccess) {
        val data: AirDataComputer = computers.data
        if (!data.flying || data.player.isTouchingWater) {
            groundImpactStatus = Status.SAFE
            obstacleImpactStatus = Status.SAFE
            return
        }

        val anyBlocksAbove: Boolean = data.world.getTopY(Heightmap.Type.MOTION_BLOCKING, data.player.blockX, data.player.blockZ) > data.player.y
        val clearThreshold: Float = if (anyBlocksAbove) 7.5f else 15.0f
        val cautionThreshold: Float = if (anyBlocksAbove) 5.0f else 10.0f
        val warningThreshold: Float = if (anyBlocksAbove) 2.5f else 5.0f
        val recoverThreshold: Float = if (anyBlocksAbove) 0.5f else 0.75f

        groundImpactTime = computeGroundImpactTime(data).requireIn(0.0f..Float.MAX_VALUE)
        groundImpactStatus =
            if (data.isInvulnerableTo(data.player.damageSources.fall())) {
                Status.SAFE
            } else if (groundImpactStatus == Status.SAFE && (data.velocity.y * 20 > -10 || groundImpactTime > cautionThreshold)) {
                Status.SAFE
            } else if (data.fallDistanceSafe || data.velocity.y * 20 > -7.5 || groundImpactTime > clearThreshold) {
                Status.SAFE
            } else if (groundImpactStatus >= Status.CAUTION && groundImpactTime > warningThreshold) {
                Status.CAUTION
            } else if (groundImpactStatus >= Status.WARNING && groundImpactTime > recoverThreshold) {
                Status.WARNING
            } else {
                Status.RECOVER
            }

        obstacleImpactTime = computeObstacleImpactTime(data, clearThreshold).requireIn(0.0f..Float.MAX_VALUE)
        obstacleImpactStatus =
            if (data.isInvulnerableTo(data.player.damageSources.flyIntoWall())) {
                Status.SAFE
            } else if (obstacleImpactStatus == Status.SAFE && ((data.velocity.horizontalLength() * 10 - 3) < data.player.health * 0.5f || obstacleImpactTime > groundImpactTime * 1.1f || obstacleImpactTime > cautionThreshold)) {
                Status.SAFE
            } else if ((data.velocity.horizontalLength() * 10 - 3) < data.player.health * 0.25f || obstacleImpactTime > groundImpactTime * 1.5f || obstacleImpactTime > clearThreshold) {
                Status.SAFE
            } else if (obstacleImpactStatus >= Status.CAUTION && obstacleImpactTime > warningThreshold) {
                Status.CAUTION
            } else if (obstacleImpactStatus >= Status.WARNING && obstacleImpactTime > recoverThreshold) {
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

    // IDEA: max/min terrain altitude on status display (that's gonna be so fucking cool /srs)
    private fun computeObstacleImpactTime(data: AirDataComputer, lookAheadTime: Float): Float {
        val end: Vec3d = data.position.add(data.velocity.multiply(lookAheadTime * 20.0, 0.0, lookAheadTime * 20.0))
        val result: BlockHitResult = data.world.raycast(
            RaycastContext(
                data.position,
                end,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.ANY,
                data.player
            )
        )
        if (result.type != HitResult.Type.BLOCK) {
            return Float.MAX_VALUE
        }
        val relative: Vec3d = result.pos.subtract(data.position)
        return (relative.horizontalLength() / (data.velocity.horizontalLength() * 20.0f)).toFloat()
    }

    override fun getMinimumPitch(computers: ComputerAccess): ControlInput? {
        if (groundImpactStatus <= Status.WARNING && FAConfig.safety.sinkRateLimitPitch || obstacleImpactStatus <= Status.WARNING && FAConfig.safety.obstacleLimitPitch) {
            return ControlInput(
                computers.data.pitch,
                ControlInput.Priority.HIGH,
                Text.translatable("mode.flightassistant.pitch.terrain_protection")
            )
        }

        return null
    }

    override fun getPitchInput(computers: ComputerAccess): ControlInput? {
        if (groundImpactStatus <= Status.WARNING && FAConfig.safety.sinkRateAutoPitch || obstacleImpactStatus <= Status.WARNING && FAConfig.safety.obstacleAutoPitch) {
            return ControlInput(
                90.0f,
                ControlInput.Priority.HIGH,
                Text.translatable("mode.flightassistant.pitch.terrain_escape"),
                1.0f / min(groundImpactTime, obstacleImpactTime),
                active = groundImpactStatus == Status.RECOVER || obstacleImpactStatus == Status.RECOVER
            )
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
