package ru.octol1ttle.flightassistant.hud.impl;

import java.awt.Color;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import ru.octol1ttle.flightassistant.Dimensions;
import ru.octol1ttle.flightassistant.DrawHelper;
import ru.octol1ttle.flightassistant.computers.impl.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.impl.autoflight.AutoFlightController;
import ru.octol1ttle.flightassistant.config.FAConfig;
import ru.octol1ttle.flightassistant.hud.api.IHudDisplay;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;

public class HeadingDisplay implements IHudDisplay {
    private final Dimensions dim;
    private final AirDataComputer data = ComputerRegistry.resolve(AirDataComputer.class);
    private final AutoFlightController autoflight = ComputerRegistry.resolve(AutoFlightController.class);

    public HeadingDisplay(Dimensions dim) {
        this.dim = dim;
    }

    @Override
    public void render(DrawContext context, TextRenderer textRenderer) {
        int left = dim.lFrame;
        int right = dim.rFrame;
        int top = dim.tFrame - 10;

        int yText = top - 7;
        float xNorth = dim.xMid - data.heading() * dim.degreesPerPixel;

        if (FAConfig.indicator().showHeadingReadout) {
            Color color = getHeadingColor(data.heading());
            DrawHelper.drawText(textRenderer, context, DrawHelper.asText("%03d", Math.round(data.heading())), dim.xMid - 8, yText, color);
            DrawHelper.drawBorder(context, dim.xMid - 15, yText - 2, 30, color);
        }

        if (FAConfig.indicator().showHeadingScale) {
            DrawHelper.drawMiddleAlignedText(textRenderer, context, DrawHelper.asText("^"), dim.xMid, top + 10, FAConfig.indicator().frameColor);

            for (int i = -540; i < 540; i++) {
                int x = Math.round(i * dim.degreesPerPixel + xNorth);
                if (x < left) {
                    continue;
                }
                if (x > right) {
                    break;
                }

                int degrees = wrapHeading(i);
                Color color = getHeadingColor(degrees);
                double targetHeading = autoflight.getTargetHeading() != null ? autoflight.getTargetHeading() : Integer.MIN_VALUE + 1;

                boolean forceMark = degrees == Math.round(targetHeading);
                boolean enoughSpace = Math.abs(targetHeading - degrees) >= 5;

                if (forceMark || i % 15 == 0 && enoughSpace) {
                    if (i % 90 == 0) {
                        DrawHelper.drawText(textRenderer, context, headingToDirection(degrees), x - 2, yText + 10, color);
                        DrawHelper.drawText(textRenderer, context, DrawHelper.asText(headingToAxis(degrees)), x - 8, yText + 20, color);
                    } else {
                        DrawHelper.drawVerticalLine(context, x, top + 3, top + 10, color);
                    }

                    if (!FAConfig.indicator().showHeadingReadout || x <= dim.xMid - 26 || x >= dim.xMid + 26) {
                        DrawHelper.drawText(textRenderer, context, DrawHelper.asText("%03d", degrees), x - 8, yText, color);
                    }
                    continue;
                }

                if (i % 5 == 0 && enoughSpace) {
                    DrawHelper.drawVerticalLine(context, x, top + 6, top + 10, color);
                }
            }
        }
    }

    private Color getHeadingColor(float heading) {
        Float targetHeading = autoflight.getTargetHeading();
        if (targetHeading != null && Math.abs(targetHeading - heading) <= 5.0f) {
            return FAConfig.indicator().advisoryColor;
        } else {
            return FAConfig.indicator().frameColor;
        }
    }

    @Override
    public void renderFaulted(DrawContext context, TextRenderer textRenderer) {
        DrawHelper.drawText(textRenderer, context, Text.translatable("short.flightassistant.heading"), dim.xMid - 8, dim.tFrame - 17, FAConfig.indicator().warningColor);
    }

    private Text headingToDirection(int degrees) {
        return switch (degrees) {
            case 0, 360 -> Text.translatable("short.flightassistant.north");
            case 90 -> Text.translatable("short.flightassistant.east");
            case 180 -> Text.translatable("short.flightassistant.south");
            case 270 -> Text.translatable("short.flightassistant.west");
            default -> throw new IllegalArgumentException("Degree range out of bounds: " + degrees);
        };
    }

    private String headingToAxis(int degrees) {
        return switch (degrees) {
            case 0, 360 -> "-Z";
            case 90 -> "+X";
            case 180 -> "+Z";
            case 270 -> "-X";
            default -> throw new IllegalArgumentException("Degree range out of bounds: " + degrees);
        };
    }

    private int wrapHeading(int degrees) {
        int i = degrees % 360;
        if (i < 0) {
            i += 360;
        }

        return i;
    }
}
