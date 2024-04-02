package ru.octol1ttle.flightassistant.hud.impl;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import ru.octol1ttle.flightassistant.Dimensions;
import ru.octol1ttle.flightassistant.DrawHelper;
import ru.octol1ttle.flightassistant.hud.api.IHudDisplay;
import ru.octol1ttle.flightassistant.computers.AirDataComputer;
import ru.octol1ttle.flightassistant.config.FAConfig;

public class LocationDisplay implements IHudDisplay {

    private final Dimensions dim;
    private final AirDataComputer data;

    public LocationDisplay(Dimensions dim, AirDataComputer data) {
        this.dim = dim;
        this.data = ComputerRegistry.resolve(AirDataComputer.class);
    }

    @Override
    public void render(DrawContext context, TextRenderer textRenderer) {
        if (!FAConfig.indicator().showCoordinates) {
            return;
        }

        int x = dim.lFrame + 15;
        int y = dim.bFrame;

        int xLoc = MathHelper.floor(data.position().x);
        int zLoc = MathHelper.floor(data.position().z);

        DrawHelper.drawText(textRenderer, context, DrawHelper.asText("%d / %d", xLoc, zLoc), x, y, FAConfig.indicator().frameColor);
    }

    @Override
    public void renderFaulted(DrawContext context, TextRenderer textRenderer) {
        DrawHelper.drawText(textRenderer, context, Text.translatable("flightassistant.location_short"),
                dim.lFrame + 15, dim.bFrame, FAConfig.indicator().warningColor);
    }

    @Override
    public String getId() {
        return "location";
    }
}
