package ru.octol1ttle.flightassistant.hud.impl;

import java.awt.Color;
import java.util.Objects;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import ru.octol1ttle.flightassistant.Dimensions;
import ru.octol1ttle.flightassistant.DrawHelper;
import ru.octol1ttle.flightassistant.computers.impl.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.impl.autoflight.AutoFlightComputer;
import ru.octol1ttle.flightassistant.computers.impl.navigation.FlightPlanner;
import ru.octol1ttle.flightassistant.config.FAConfig;
import ru.octol1ttle.flightassistant.hud.api.IHudDisplay;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;

public class AltitudeDisplay implements IHudDisplay {
    private final Dimensions dim;
    private final AirDataComputer data = ComputerRegistry.resolve(AirDataComputer.class);
    private final AutoFlightComputer autoflight = ComputerRegistry.resolve(AutoFlightComputer.class);
    private final FlightPlanner plan = ComputerRegistry.resolve(FlightPlanner.class);

    public AltitudeDisplay(Dimensions dim) {
        this.dim = dim;
    }

    @Override
    public void render(DrawContext context, TextRenderer textRenderer) {
        if (data.altitude() < -130 || data.altitude() > 1300) {
            renderFaulted(context, textRenderer);
            return;
        }

        int top = dim.tFrame;
        int bottom = dim.bFrame;

        int right = dim.rFrame + 2;
        int left = dim.rFrame;

        int blocksPerPixel = 1;

        int floorOffset = Math.round(data.altitude() * blocksPerPixel);
        int yFloor = dim.yMid - floorOffset;
        int xAltText = right + 5;

        int groundLevel = MathHelper.ceil(data.groundLevel);
        int safeLevel = data.groundLevel == data.voidLevel() ? data.voidLevel() + 16 : groundLevel;

        if (FAConfig.indicator().showAltitudeReadout) {
            Color color = getAltitudeColor(safeLevel, data.altitude());
            DrawHelper.drawText(textRenderer, context, DrawHelper.asText("%.0f", data.altitude()), xAltText, dim.yMid - 3, color);
            DrawHelper.drawBorder(context, xAltText - 2, dim.yMid - 5, 28, color);
        }

        if (FAConfig.indicator().showAltitudeScale) {
            for (int i = -130; i < 1300; i++) {
                int y = (dim.hScreen - i * blocksPerPixel) - yFloor;
                if (y > (bottom - 5) || i < groundLevel) {
                    continue;
                }
                if (y < top) {
                    break;
                }

                Color color = getAltitudeColor(safeLevel, i);
                Integer targetAltitude = autoflight.getTargetAltitude();
                Integer minimums = plan.getMinimums(data.groundLevel);

                boolean forceMark = shouldForceMark(i, groundLevel, targetAltitude, minimums);
                boolean enoughSpace = isEnoughSpace(i, groundLevel, targetAltitude, minimums);

                if (forceMark || i % 50 == 0 && enoughSpace) {
                    DrawHelper.drawHorizontalLine(context, left, right + 2, y, color);
                    if (!FAConfig.indicator().showAltitudeReadout || y > dim.yMid + 7 || y < dim.yMid - 7) {
                        DrawHelper.drawText(textRenderer, context, DrawHelper.asText("%d", i), xAltText, y - 3, color);
                    }
                    continue;
                }

                if (i % 10 == 0 && enoughSpace) {
                    DrawHelper.drawHorizontalLine(context, left, right, y, color);
                }
            }
        }
    }

    private boolean shouldForceMark(int i, @Nullable Integer... ints) {
        for (Integer j : ints) {
            if (Objects.equals(i, j)) {
                return true;
            }
        }

        return false;
    }

    private boolean isEnoughSpace(int i, @Nullable Integer... ints ) {
        for (Integer j : ints) {
            if (j != null && Math.abs(j - i) < 5) {
                return false;
            }
        }

        return true;
    }

    private Color getAltitudeColor(int safeLevel, float altitude) {
        if (!data.player().isOnGround() && altitude <= safeLevel) {
            return FAConfig.indicator().warningColor;
        }

        Integer minimums = plan.getMinimums(data.groundLevel);
        if (minimums != null && altitude <= minimums) {
            return FAConfig.indicator().cautionColor;
        }

        Integer targetAltitude = autoflight.getTargetAltitude();
        if (targetAltitude != null && Math.abs(targetAltitude - altitude) <= 5.0f) {
            return FAConfig.indicator().advisoryColor;
        } else {
            return FAConfig.indicator().frameColor;
        }
    }

    @Override
    public void renderFaulted(DrawContext context, TextRenderer textRenderer) {
        DrawHelper.drawText(textRenderer, context, Text.translatable("short.flightassistant.altitude"), dim.rFrame + 7, dim.yMid - 3, FAConfig.indicator().warningColor);
    }

    @Override
    public String getId() {
        return "altitude";
    }
}
