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

class AirDataComputer(private val mc: MinecraftClient) : Computer() {
    lateinit var player: ClientPlayerEntity

    val world: World
        get() = player.world
    var position: Vec3d = Vec3d.ZERO
        private set
    var voidLevel: Int = -64
        private set
    var groundLevel: Double? = 0.0
        private set
    var velocity: Vec3d = Vec3d.ZERO
        private set
    var forwardVelocity: Vec3d = Vec3d.ZERO
        private set
    var pitch: Float = 0.0f
        private set
    var yaw: Float = 0.0f
        private set
    var heading: Float = 0.0f
        private set
    var roll: Float = 0.0f
        private set

    override fun tick(computers: ComputerAccess) {
        player = checkNotNull(mc.player)
        position = player.getLerpedPos(tickDelta)
        voidLevel = world.bottomY - 64
        groundLevel = computeGroundLevel()
        velocity = player.lerpVelocity(tickDelta)
        val rotationVelocity: Vec3d = velocity.multiply(player.rotationVector)
        forwardVelocity = Vec3d(
            rotationVelocity.x.coerceAtLeast(rotationVelocity.x * 0.1),
            rotationVelocity.y.coerceAtLeast(rotationVelocity.y * 0.01),
            rotationVelocity.z.coerceAtLeast(rotationVelocity.z * 0.1)
        )
        pitch = -player.pitch
        yaw = wrapDegrees(player.yaw)
        heading = yaw + 180.0f
        roll = degrees(atan2(-RenderMatrices.worldSpaceMatrix.m10(), RenderMatrices.worldSpaceMatrix.m11()))
    }

    private fun computeGroundLevel(): Double? {
        val minY: Double = voidLevel.toDouble().coerceAtLeast(position.y - 1000)
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
        val ID: Identifier = FlightAssistant.id("air_data")
    }
}
