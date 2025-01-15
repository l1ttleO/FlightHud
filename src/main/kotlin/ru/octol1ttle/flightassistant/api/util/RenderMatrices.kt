package ru.octol1ttle.flightassistant.api.util

import org.joml.Matrix4f

object RenderMatrices {
    var ready: Boolean = false
        internal set

    val projectionMatrix = Matrix4f()
    val worldSpaceMatrix = Matrix4f()
    val worldSpaceNoRollMatrix = Matrix4f()
    val modelViewMatrix = Matrix4f()
}
