package ru.octol1ttle.flightassistant.hud.impl;

import java.awt.Color;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import ru.octol1ttle.flightassistant.Dimensions;
import ru.octol1ttle.flightassistant.DrawHelper;
import ru.octol1ttle.flightassistant.computers.impl.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.impl.autoflight.AutoFlightController;
import ru.octol1ttle.flightassistant.config.FAConfig;
import ru.octol1ttle.flightassistant.hud.api.IHudDisplay;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;

public class SpeedDisplay implements IHudDisplay {
    private final Dimensions dim;
    private final AirDataComputer data = ComputerRegistry.resolve(AirDataComputer.class);
    private final AutoFlightController autoflight = ComputerRegistry.resolve(AutoFlightController.class);

    public SpeedDisplay(Dimensions dim) {
        this.dim = dim;
    }

    @Override
    public void render(DrawContext context, TextRenderer textRenderer) {
        int top = dim.tFrame;
        int bottom = dim.bFrame;

        int left = dim.lFrame - 2;
        int right = dim.lFrame;
        int unitPerPixel = 30;

        int floorOffset = MathHelper.floor(data.speed() * unitPerPixel);
        int yFloor = dim.yMid - floorOffset;

        int xSpeedText = left - 5;

        if (FAConfig.indicator().showSpeedReadout) {
            Color color = getSpeedColor(data.speed());
            DrawHelper.drawRightAlignedText(textRenderer, context, DrawHelper.asText("%.2f", data.speed()), xSpeedText, dim.yMid - 3, color);
            DrawHelper.drawBorder(context, xSpeedText - 29, dim.yMid - 5, 30, color);
        }

        if (FAConfig.indicator().showSpeedScale) {
            for (float i = 0; i <= 100; i += 0.25f) {
                int y = MathHelper.floor(dim.hScreen - i * unitPerPixel - yFloor);
                if (y < top || y > (bottom - 5))
                    continue;

                Color color = getSpeedColor(i);
                if (i % 1 == 0) {
                    DrawHelper.drawHorizontalLine(context, left - 2, right, y, color);
                    if (!FAConfig.indicator().showSpeedReadout || y > dim.yMid + 7 || y < dim.yMid - 7) {
                        DrawHelper.drawRightAlignedText(textRenderer, context, DrawHelper.asText("%.0f", i), xSpeedText, y - 3, color);
                    }
                }
                DrawHelper.drawHorizontalLine(context, left, right, y, color);
            }
        }
    }

    private Color getSpeedColor(float speed) {
        Integer targetSpeed = autoflight.getTargetSpeed();
        if (targetSpeed != null && Math.abs(targetSpeed - speed) <= 1.0f) {
            return FAConfig.indicator().advisoryColor;
        } else {
            return FAConfig.indicator().frameColor;
        }
    }

    @Override
    public void renderFaulted(DrawContext context, TextRenderer textRenderer) {
        DrawHelper.drawRightAlignedText(textRenderer, context, Text.translatable("short.flightassistant.speed"), dim.lFrame - 7, dim.yMid - 3, FAConfig.indicator().warningColor);
    }

    @Override
    public String getId() {
        return "speed";
    }
}
