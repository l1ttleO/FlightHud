package ru.octol1ttle.flightassistant.hud.impl;

import java.awt.Color;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import ru.octol1ttle.flightassistant.Dimensions;
import ru.octol1ttle.flightassistant.DrawHelper;
import ru.octol1ttle.flightassistant.computers.impl.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.impl.safety.GPWSComputer;
import ru.octol1ttle.flightassistant.config.FAConfig;
import ru.octol1ttle.flightassistant.hud.api.IHudDisplay;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;

public class FlightPathDisplay implements IHudDisplay {
    private final Dimensions dim;
    private final AirDataComputer data = ComputerRegistry.resolve(AirDataComputer.class);
    private final GPWSComputer gpws = ComputerRegistry.resolve(GPWSComputer.class);

    public FlightPathDisplay(Dimensions dim) {
        this.dim = dim;
    }

    @Override
    public void render(DrawContext context, TextRenderer textRenderer) {
        if (!FAConfig.indicator().showFlightPath) {
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

        int l = x - 3;
        int r = x + 3;
        int t = y - 3;
        int b = y + 3;

        Color color = gpws.getGPWSLampColor();
        // TODO: move to DrawHelper
        DrawHelper.drawVerticalLine(context, l, t, b, color);
        DrawHelper.drawVerticalLine(context, r, t, b, color);

        DrawHelper.drawHorizontalLine(context, l, r, t, color);
        DrawHelper.drawHorizontalLine(context, l, r, b, color);

        DrawHelper.drawVerticalLine(context, x, t - 5, t, color);
        DrawHelper.drawHorizontalLine(context, l - 4, l, y, color);
        DrawHelper.drawHorizontalLine(context, r, r + 4, y, color);
    }

    @Override
    public void renderFaulted(DrawContext context, TextRenderer textRenderer) {
        DrawHelper.drawMiddleAlignedText(textRenderer, context, Text.translatable("flightassistant.flight_path_short"), dim.xMid, dim.yMid + 10, FAConfig.indicator().warningColor);
    }

    @Override
    public String getId() {
        return "flight_path";
    }
}
