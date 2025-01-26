package ru.octol1ttle.flightassistant.api.util.event

import dev.architectury.event.Event
import dev.architectury.event.EventFactory
import net.minecraft.client.render.Camera
import org.joml.Matrix3f
import org.joml.Matrix4f

fun interface WorldRenderCallback {
    /**
     * Called when the WorldRenderer starts rendering the world
     */
    fun onStartRenderWorld(tickDelta: Float, camera: Camera, projectionMatrix: Matrix4f, positionMatrix: Matrix3f)

    companion object {
        @JvmField
        val EVENT: Event<WorldRenderCallback> = EventFactory.createLoop()
    }
}
