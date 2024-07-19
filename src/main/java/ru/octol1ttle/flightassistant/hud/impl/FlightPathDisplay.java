package ru.octol1ttle.flightassistant.hud.impl;

import java.awt.Color;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import ru.octol1ttle.flightassistant.Dimensions;
import ru.octol1ttle.flightassistant.DrawHelper;
import ru.octol1ttle.flightassistant.computers.impl.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.impl.safety.GroundProximityComputer;
import ru.octol1ttle.flightassistant.config.FAConfig;
import ru.octol1ttle.flightassistant.hud.api.IHudDisplay;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;

public class FlightPathDisplay implements IHudDisplay {
    private final Dimensions dim;
    private final AirDataComputer data = ComputerRegistry.resolve(AirDataComputer.class);
    private final GroundProximityComputer gpws = ComputerRegistry.resolve(GroundProximityComputer.class);

    public FlightPathDisplay(Dimensions dim) {
        this.dim = dim;
    }

    @Override
    public void render(DrawContext context, TextRenderer textRenderer) {
        if (!FAConfig.indicator().showFlightPath || Math.abs(data.roll) > 1.0f) {
            return;
        }

        float deltaPitch = data.pitch() - data.flightPitch;
        float deltaHeading = data.flightHeading() - data.heading();

        if (deltaHeading < -180) {
            deltaHeading += 360;
        }

        int y = dim.yMid;
        int x = dim.xMid;

        y += MathHelper.floor(deltaPitch * dim.degreesPerPixel);
        x += MathHelper.floor(deltaHeading * dim.degreesPerPixel);

        if (y < dim.tFrame || y > dim.bFrame || x < dim.lFrame || x > dim.rFrame) {
            return;
        }

        int left = x - 3;
        int right = x + 3;
        int top = y - 3;
        int bottom = y + 3;

        Color color = gpws.getGPWSLampColor();
        DrawHelper.drawAircraftIcon(context, left, right, top, bottom, x, y, color);
    }

    @Override
    public void renderFaulted(DrawContext context, TextRenderer textRenderer) {
        DrawHelper.drawMiddleAlignedText(textRenderer, context, Text.translatable("short.flightassistant.flight_path"), dim.xMid, dim.yMid + 10, FAConfig.indicator().warningColor);
    }

}
