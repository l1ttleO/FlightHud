package ru.octol1ttle.flightassistant.hud.impl;

import java.awt.Color;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import ru.octol1ttle.flightassistant.Dimensions;
import ru.octol1ttle.flightassistant.DrawHelper;
import ru.octol1ttle.flightassistant.hud.api.IHudDisplay;
import ru.octol1ttle.flightassistant.computers.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.autoflight.AutoFlightComputer;
import ru.octol1ttle.flightassistant.config.FAConfig;

public class HeadingDisplay implements IHudDisplay {

    private final Dimensions dim;
    private final AirDataComputer data;
    private final AutoFlightComputer autoflight;

    public HeadingDisplay(Dimensions dim, AirDataComputer data, AutoFlightComputer autoflight) {
        this.dim = dim;
        this.data = ComputerRegistry.resolve(AirDataComputer.class);
        this.autoflight = autoflight;
    }

    @Override
    public void render(DrawContext context, TextRenderer textRenderer) {
        int left = dim.lFrame;
        int right = dim.rFrame;
        int top = dim.tFrame - 10;

        int yText = top - 7;
        int northOffset = MathHelper.floor(data.heading() * dim.degreesPerPixel);
        int xNorth = dim.xMid - northOffset;

        if (FAConfig.indicator().showHeadingReadout) {
            Color color = getHeadingColor(data.heading());
            DrawHelper.drawText(textRenderer, context, DrawHelper.asText("%03d", Math.round(data.heading())), dim.xMid - 8, yText, color);
            DrawHelper.drawBorder(context, dim.xMid - 15, yText - 2, 30, color);
        }

        if (FAConfig.indicator().showHeadingScale) {
            DrawHelper.drawMiddleAlignedText(textRenderer, context, DrawHelper.asText("^"), dim.xMid, top + 10, FAConfig.indicator().frameColor);

            for (int i = -540; i < 540; i++) {
                int x = (i * dim.degreesPerPixel) + xNorth;
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
        DrawHelper.drawText(textRenderer, context, Text.translatable("flightassistant.heading_short"), dim.xMid - 8, dim.tFrame - 17, FAConfig.indicator().warningColor);
    }

    @Override
    public String getId() {
        return "heading";
    }

    private Text headingToDirection(int degrees) {
        return switch (degrees) {
            case 0, 360 -> Text.translatable("flightassistant.north_short");
            case 90 -> Text.translatable("flightassistant.east_short");
            case 180 -> Text.translatable("flightassistant.south_short");
            case 270 -> Text.translatable("flightassistant.west_short");
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
