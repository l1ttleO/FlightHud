package ru.octol1ttle.flightassistant.impl.computer

import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.max
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.damage.DamageSource
import net.minecraft.registry.tag.DamageTypeTags
import net.minecraft.util.Identifier
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.Direction
import net.minecraft.util.math.MathHelper.wrapDegrees
import net.minecraft.util.math.Vec3d
import net.minecraft.world.RaycastContext
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerView
import ru.octol1ttle.flightassistant.api.util.FATickCounter.tickProgress
import ru.octol1ttle.flightassistant.api.util.RenderMatrices
import ru.octol1ttle.flightassistant.api.util.degrees
import ru.octol1ttle.flightassistant.api.util.extensions.fallFlying
import ru.octol1ttle.flightassistant.api.util.requireIn
import ru.octol1ttle.flightassistant.config.FAConfig

class AirDataComputer(computers: ComputerView, private val mc: MinecraftClient) : Computer(computers) {
    val player: ClientPlayerEntity
        get() = checkNotNull(mc.player)
    val flying: Boolean
        get() = player.fallFlying
    val world: ClientWorld
        get() = checkNotNull(mc.world)

    var position: Vec3d = Vec3d.ZERO
        private set
    val altitude: Double
        get() = position.y
    val voidLevel: Int
        get() = world.bottomY - 64
    var groundLevel: Double? = null
        private set(value) { field = value?.requireIn(world.bottomY.toDouble()..Double.MAX_VALUE) }

    private val fallDistance: Float
        get() =
            if (groundLevel == null || groundLevel!! == Double.MAX_VALUE) Float.MAX_VALUE
            else max(player.fallDistance, (altitude - groundLevel!!).toFloat())
    val fallDistanceSafe: Boolean
        get() = player.isTouchingWater || fallDistance <= player.safeFallDistance || isInvulnerableTo(player.damageSources.fall())

    var velocity: Vec3d = Vec3d.ZERO
        private set
    var forwardVelocity: Vec3d = Vec3d.ZERO
        private set

    val pitch: Float
        get() = -player.pitch.requireIn(-90.0f..90.0f)
    val yaw: Float
        get() = wrapDegrees(player.yaw).requireIn(-180.0f..180.0f)
    val heading: Float
        get() = (yaw + 180.0f).requireIn(0.0f..360.0f)
    var roll: Float = 0.0f
        private set(value) { field = value.requireIn(-180.0f..180.0f) }

    val flightPitch: Float
        get() = degrees(asin(velocity.normalize().y).toFloat())

    val isCurrentChunkLoaded: Boolean
        get() = world.chunkManager.isChunkLoaded(player.chunkPos.x, player.chunkPos.z)

    override fun tick() {
        position = player.getLerpedPos(tickProgress)
        groundLevel = computeGroundLevel()
        velocity = player.lerpVelocity(tickProgress)
        forwardVelocity = computeForwardVelocity()
        roll = degrees(atan2(-RenderMatrices.worldSpaceMatrix.m10(), RenderMatrices.worldSpaceMatrix.m11()))
    }

    fun automationsAllowed(checkFlying: Boolean = true): Boolean {
        return (!checkFlying || flying) && (FAConfig.global.automationsAllowedInOverlays || (mc.currentScreen == null && mc.overlay == null))
    }

    fun isInvulnerableTo(source: DamageSource): Boolean {
        if (!FAConfig.safety.considerInvulnerability) {
            return false
        }
//? if >=1.21.2 {
        /*return (player as ru.octol1ttle.flightassistant.mixin.EntityInvoker).invokeIsAlwaysInvulnerableTo(source)
*///?} else
        return player.isInvulnerableTo(source)
                || player.abilities.invulnerable && !source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)
                || player.abilities.allowFlying && source.isIn(DamageTypeTags.IS_FALL)
    }

    private fun computeGroundLevel(): Double? {
        if (!isCurrentChunkLoaded) {
            return groundLevel
        }

        val minY: Double = world.bottomY.toDouble().coerceAtLeast(altitude - 1000)
        val result: BlockHitResult = world.raycast(
            RaycastContext(
                position,
                position.withAxis(Direction.Axis.Y, minY),
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.ANY,
                player
            )
        )
        if (result.type == HitResult.Type.MISS) {
            return if (result.pos.y > world.bottomY) Double.MAX_VALUE else null
        }
        return result.pos.y
    }

    private fun computeForwardVelocity(): Vec3d {
        val normalizedRotation: Vec3d = player.rotationVector.normalize()
        val normalizedVelocity: Vec3d = velocity.normalize()
        return velocity.multiply(normalizedRotation.dotProduct(normalizedVelocity).coerceAtLeast(0.0))
    }

    override fun reset() {
        position = Vec3d.ZERO
        groundLevel = null
        velocity = Vec3d.ZERO
        forwardVelocity = Vec3d.ZERO
        roll = 0.0f
    }

    companion object {
        val ID: Identifier = FlightAssistant.id("air_data")
    }
}
