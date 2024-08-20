package ru.octol1ttle.flightassistant.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

/**
 * @author 0x150
 * @see <a href="https://github.com/0x3C50/Renderer">Original source code</a>
 */
public class ScreenSpaceRendering {
    private static final MinecraftClient client = MinecraftClient.getInstance();

    @ApiStatus.Internal
    public static final Matrix4f lastProjMat = new Matrix4f();
    @ApiStatus.Internal
    public static final Matrix4f lastModMat = new Matrix4f();
    @ApiStatus.Internal
    public static final Matrix4f lastWorldSpaceMatrix = new Matrix4f();

    /**
     *
     * Transforms an input position into a (x, y, d) coordinate, transformed to screen space. d specifies the far plane of the position, and can be used to check if the position is on screen. Use {@link #isVisible(Vec3d)}.
     * Example:
     * <pre>
     * {@code
     * // Hud render event
     * Vec3d targetPos = new Vec3d(100, 64, 100); // world space
     * Vec3d screenSpace = ScreenSpaceRendering.fromWorldSpace(targetPos);
     * if (ScreenSpaceRendering.isVisible(screenSpace)) {
     *     // do something with screenSpace.x and .y
     * }
     * }
     * </pre>
     *
     * @param pos The world space coordinates to translate
     * @return The (x, y, d) coordinates
     * @throws NullPointerException If {@code pos} is null
     */
    @Contract(value = "_ -> new", pure = true)
    public static Vec3d fromWorldSpace(@NotNull Vec3d pos) {
        Camera camera = client.getEntityRenderDispatcher().camera;
        int displayHeight = client.getWindow().getHeight();
        int[] viewport = new int[4];
        GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);
        Vector3f target = new Vector3f();

        double deltaX = pos.x - camera.getPos().x;
        double deltaY = pos.y - camera.getPos().y;
        double deltaZ = pos.z - camera.getPos().z;

        Vector4f transformedCoordinates = new Vector4f((float) deltaX, (float) deltaY, (float) deltaZ, 1.f).mul(
                lastWorldSpaceMatrix);

        Matrix4f matrixProj = new Matrix4f(lastProjMat);
        Matrix4f matrixModel = new Matrix4f(lastModMat);

        matrixProj.mul(matrixModel)
                .project(transformedCoordinates.x(), transformedCoordinates.y(), transformedCoordinates.z(), viewport,
                        target);

        return new Vec3d(target.x / client.getWindow().getScaleFactor(),
                (displayHeight - target.y) / client.getWindow().getScaleFactor(), target.z);
    }

    /**
     * Checks if a screen space coordinate (x, y, d) is on screen
     *
     * @param pos The (x, y, d) coordinates to check
     * @return True if the coordinates are visible
     */
    public static boolean isVisible(Vec3d pos) {
        return pos != null && pos.z > -1 && pos.z < 1;
    }
}
