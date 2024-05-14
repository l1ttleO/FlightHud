package ru.octol1ttle.flightassistant.hud.impl;

import java.awt.Color;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import ru.octol1ttle.flightassistant.Dimensions;
import ru.octol1ttle.flightassistant.DrawHelper;
import ru.octol1ttle.flightassistant.computers.impl.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.impl.navigation.FlightPlanner;
import ru.octol1ttle.flightassistant.config.FAConfig;
import ru.octol1ttle.flightassistant.hud.api.IHudDisplay;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;

public class RadarAltitudeDisplay implements IHudDisplay {
    private final Dimensions dim;
    private final AirDataComputer data = ComputerRegistry.resolve(AirDataComputer.class);
    private final FlightPlanner plan = ComputerRegistry.resolve(FlightPlanner.class);

    public RadarAltitudeDisplay(Dimensions dim) {
        this.dim = dim;
    }

    @Override
    public void render(DrawContext context, TextRenderer textRenderer) {
        if (!FAConfig.indicator().showGroundAltitude) {
            return;
        }
        if (!data.isCurrentChunkLoaded) {
            renderFaulted(context, textRenderer);
            return;
        }

        int bottom = dim.bFrame;
        int xAltText = dim.rFrame + 7;
        int safeLevel = data.groundLevel == data.voidLevel() ? data.voidLevel() + 16 : MathHelper.ceil(data.groundLevel);
        Color color = getAltitudeColor(safeLevel, data.altitude());

        DrawHelper.drawText(textRenderer, context, Text.translatable(data.groundLevel == data.voidLevel() ? "short.flightassistant.void_level" : "short.flightassistant.ground_level"), xAltText - 10, bottom, color);
        DrawHelper.drawText(textRenderer, context, DrawHelper.asText("%d", MathHelper.floor(data.heightAboveGround())), xAltText, bottom, color);
        DrawHelper.drawBorder(context, xAltText - 2, bottom - 2, 28, color);
    }

    private Color getAltitudeColor(int safeLevel, float altitude) {
        if (altitude <= safeLevel) {
            return FAConfig.indicator().warningColor;
        }

        Integer minimums = plan.getMinimums(data.groundLevel);
        if (minimums != null && altitude <= minimums) {
            return FAConfig.indicator().cautionColor;
        }

        return FAConfig.indicator().frameColor;
    }

    @Override
    public void renderFaulted(DrawContext context, TextRenderer textRenderer) {
        DrawHelper.drawText(textRenderer, context, Text.translatable("short.flightassistant.radar"), dim.rFrame - 3, dim.bFrame, FAConfig.indicator().warningColor);
    }

    @Override
    public String getId() {
        return "radar";
    }
}
