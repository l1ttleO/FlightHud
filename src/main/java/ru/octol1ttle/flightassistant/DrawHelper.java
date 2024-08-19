package ru.octol1ttle.flightassistant;

import java.awt.Color;
import me.x150.renderer.util.RendererUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import ru.octol1ttle.flightassistant.compatibility.immediatelyfast.HUDBatching;

public abstract class DrawHelper {
    private static final int SINGLE_LINE_DRAWN = 1;

    public static MutableText asText(String key, Object... args) {
        return Text.literal(key.formatted(args));
    }

    public static Vec3d getScreenSpace(Vec3d delta) {
        Vec3d vec = RendererUtils.worldSpaceToScreenSpace(MinecraftClient.getInstance().getEntityRenderDispatcher().camera.getPos().add(delta));
        if (!RendererUtils.screenSpaceCoordinateIsVisible(vec)) {
            return null;
        }

        return vec;
    }

    public static Integer getScreenSpaceX(float pitch, float yaw) {
        Vec3d vec = RendererUtils.worldSpaceToScreenSpace(MinecraftClient.getInstance().getEntityRenderDispatcher().camera.getPos().add(Vec3d.fromPolar(-pitch, yaw)));
        if (!RendererUtils.screenSpaceCoordinateIsVisible(vec)) {
            return null;
        }

        return FAMathHelper.round(vec.x);
    }

    public static Integer getScreenSpaceY(float pitch, float yaw) {
        Vec3d vec = RendererUtils.worldSpaceToScreenSpace(MinecraftClient.getInstance().getEntityRenderDispatcher().camera.getPos().add(Vec3d.fromPolar(-pitch, yaw)));
        if (!RendererUtils.screenSpaceCoordinateIsVisible(vec)) {
            return null;
        }

        return FAMathHelper.round(vec.y);
    }

    public static void fill(DrawContext context, int x1, int y1, int x2, int y2, Color color) {
        context.fill(x1, y1, x2, y2, color.getRGB());
    }

    public static void drawRightAlignedText(TextRenderer textRenderer, DrawContext context, Text text, int x, int y, Color color) {
        drawText(textRenderer, context, text, x - textRenderer.getWidth(text), y, color);
    }

    public static int drawText(TextRenderer textRenderer, DrawContext context, Text text, int x, int y, Color color) {
        context.drawText(textRenderer, text, x, y, color.getRGB(), false);
        return SINGLE_LINE_DRAWN;
    }

    public static void drawMiddleAlignedText(TextRenderer textRenderer, DrawContext context, Text text, int x, int y, Color color) {
        drawText(textRenderer, context, text, x - textRenderer.getWidth(text) / 2, y, color);
    }

    public static int drawHighlightedText(TextRenderer textRenderer, DrawContext context, Text text, int x, int y, Color color, boolean highlight) {
        HUDBatching.tryEnd();
        if (highlight) {
            DrawHelper.fill(context, x - 2, y - 1, x + textRenderer.getWidth(text) + 1, y + 8, color);
            DrawHelper.drawText(textRenderer, context, text, x, y, getContrasting(color));
        } else {
            DrawHelper.drawText(textRenderer, context, text, x, y, color);
        }
        HUDBatching.tryBegin();
        return SINGLE_LINE_DRAWN;
    }

    private static Color getContrasting(Color original) {
        double luma = ((0.299 * original.getRed()) + (0.587 * original.getGreen()) + (0.114 * original.getBlue())) / 255.0d;
        return luma > 0.5d ? Color.BLACK : Color.WHITE;
    }

    // that name doe
    public static void drawHighlightedMiddleAlignedText(TextRenderer textRenderer, DrawContext context, Text text, int x, int y, Color color, boolean highlight) {
        drawHighlightedText(textRenderer, context, text, x - textRenderer.getWidth(text) / 2, y, color, highlight);
    }

    public static void drawHorizontalLine(DrawContext context, int x1, int x2, int y, Color color) {
        context.drawHorizontalLine(x1, x2, y, color.getRGB());
    }

    public static void drawVerticalLine(DrawContext context, int x, int y1, int y2, Color color) {
        context.drawVerticalLine(x, y1, y2, color.getRGB());
    }

    public static void drawBorder(DrawContext context, int x, int y, int w, Color color) {
        context.drawBorder(x, y, w, 11, color.getRGB());
    }

    public static void drawHorizontalLineDashed(DrawContext context, int x1, int x2, int y,
                                                   int dashCount, Color color) {
        int width = x2 - x1;
        int segmentCount = dashCount * 2 - 1;
        int dashSize = width / segmentCount;
        for (int i = 0; i < segmentCount; i++) {
            if (i % 2 != 0) {
                continue;
            }
            int dx1 = i * dashSize + x1;

            int dx2;
            if (i == segmentCount - 1) {
                dx2 = x2;
            } else {
                dx2 = ((i + 1) * dashSize) + x1;
            }
            context.drawHorizontalLine(dx1, dx2, y, color.getRGB());
        }
    }

    public static void drawAircraftIcon(DrawContext context, int left, int right, int top, int bottom, int x, int y, Color color) {
        drawVerticalLine(context, left, top, bottom, color);
        drawVerticalLine(context, right, top, bottom, color);

        drawHorizontalLine(context, left, right, top, color);
        drawHorizontalLine(context, left, right, bottom, color);

        drawVerticalLine(context, x, top - 5, top, color);
        drawHorizontalLine(context, left - 4, left, y, color);
        drawHorizontalLine(context, right, right + 4, y, color);
    }
}
