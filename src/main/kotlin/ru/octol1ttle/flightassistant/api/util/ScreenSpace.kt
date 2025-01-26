package ru.octol1ttle.flightassistant.api.util

import net.minecraft.util.math.Vec3d
import org.jetbrains.annotations.Contract
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector4f
import org.lwjgl.opengl.GL11
import ru.octol1ttle.flightassistant.FlightAssistant.mc

object ScreenSpace {
    private var viewport: IntArray = IntArray(4)

    internal fun updateViewport() {
        GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport)
    }

    /**
     * @author 0x150
     * @see <a href="https://github.com/0x3C50/Renderer">Original source code</a>
     */

    /**
     *
     * Transforms an input position into a (x, y, d) coordinate, transformed to screen space. d specifies the far plane of the position, and can be used to check if the position is on screen. Use [.isVisible].
     * Example:
     * <pre>
     * `// Hud render event
     * Vec3d targetPos = new Vec3d(100, 64, 100); // world space
     * Vec3d screenSpace = ScreenSpaceRendering.fromWorldSpace(targetPos);
     * if (ScreenSpaceRendering.isVisible(screenSpace)) {
     * // do something with screenSpace.x and .y
     * }
    ` *
    </pre> *
     *
     * @param deltaPos The world space coordinates to translate, relative to the camera's current position
     * @return The (x, y, d) coordinates
     * @throws NullPointerException If `pos` is null
     */
    @Contract(value = "_ -> new", pure = true)
    private fun fromWorldSpace(deltaPos: Vec3d, useNoRollMatrix: Boolean = true): Vec3d {
        val displayHeight: Int = mc.window.height
        val target = Vector3f()

        val transformedCoordinates: Vector4f =
            Vector4f(deltaPos.x.toFloat(), deltaPos.y.toFloat(), deltaPos.z.toFloat(), 1f).mul(
                if (useNoRollMatrix) RenderMatrices.worldSpaceNoRollMatrix else RenderMatrices.worldSpaceMatrix
            )

        val matrixProj = Matrix4f(RenderMatrices.projectionMatrix)
        val matrixModel = Matrix4f(RenderMatrices.modelViewMatrix)

        matrixProj.mul(matrixModel)
            .project(
                transformedCoordinates.x(), transformedCoordinates.y(), transformedCoordinates.z(), viewport,
                target
            )

        return Vec3d(
            target.x / mc.window.scaleFactor,
            (displayHeight - target.y) / mc.window.scaleFactor,
            target.z.toDouble()
        )
    }

    /**
     * Checks if a screen space coordinate (x, y, d) is on screen
     *
     * @param pos The (x, y, d) coordinates to check
     * @return True if the coordinates are visible
     */
    private fun isVisible(pos: Vec3d?): Boolean {
        if (pos == null) {
            return false
        }
        return pos.z > -1 && pos.z < 1
    }

    fun getX(heading: Float): Int? {
        val vec: Vec3d = fromWorldSpace(Vec3d.fromPolar(0.0f, heading - 180.0f))
        if (!isVisible(vec)) {
            return null
        }

        return vec.x.toInt()
    }

    fun getY(pitch: Float): Int? {
        val vec: Vec3d = fromWorldSpace(Vec3d.fromPolar(-pitch, mc.entityRenderDispatcher.camera.yaw))
        if (!isVisible(vec)) {
            return null
        }

        return vec.y.toInt()
    }

    fun getVec3d(deltaPos: Vec3d): Vec3d? {
        val vec: Vec3d = fromWorldSpace(deltaPos, false)
        if (!isVisible(vec)) {
            return null
        }

        return vec
    }
}
