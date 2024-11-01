package ru.octol1ttle.flightassistant.impl.computer

import kotlin.math.atan2
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.hit.*
import net.minecraft.util.math.*
import net.minecraft.util.math.MathHelper.wrapDegrees
import net.minecraft.world.*
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.*
import ru.octol1ttle.flightassistant.api.util.*
import ru.octol1ttle.flightassistant.api.util.FATickCounter.tickDelta
import ru.octol1ttle.flightassistant.config.FAConfig

class AirDataComputer(private val mc: MinecraftClient) : Computer() {
    val player: ClientPlayerEntity
        get() = checkNotNull(mc.player)
    val flying: Boolean
        get() = player.isFallFlying
    val world: World
        get() = player.world

    var position: Vec3d = Vec3d.ZERO
        private set
    val altitude: Double
        get() = position.y
    val voidLevel: Int
        get() = world.bottomY - 64
    var groundLevel: Double? = 0.0
        private set
    var velocity: Vec3d = Vec3d.ZERO
        private set
    var forwardVelocity: Vec3d = Vec3d.ZERO
        private set
    val pitch: Float
        get() = -player.pitch
    val yaw: Float
        get() = wrapDegrees(player.yaw)
    val heading: Float
        get() = yaw + 180.0f
    var roll: Float = 0.0f
        private set

    override fun tick(computers: ComputerAccess) {
        position = player.getLerpedPos(tickDelta)
        groundLevel = computeGroundLevel()
        velocity = player.lerpVelocity(tickDelta)

        val normalizedRotation: Vec3d = player.rotationVector.normalize()
        val normalizedVelocity: Vec3d = velocity.normalize()
        forwardVelocity = velocity.multiply(normalizedRotation.dotProduct(normalizedVelocity).coerceAtLeast(0.0))

        roll = degrees(atan2(-RenderMatrices.worldSpaceMatrix.m10(), RenderMatrices.worldSpaceMatrix.m11()))
    }

    fun automationsAllowed(): Boolean {
        return flying && (FAConfig.global.automationsAllowedInOverlays || (mc.currentScreen == null && mc.overlay == null))
    }

    private fun computeGroundLevel(): Double? {
        val minY: Double = voidLevel.toDouble().coerceAtLeast(altitude - 1000)
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
            return if (result.pos.y > voidLevel) Double.MAX_VALUE else null
        }
        return result.pos.y
    }

    companion object {
        val ID: Identifier = FlightAssistant.computerId("air_data")
    }
}
