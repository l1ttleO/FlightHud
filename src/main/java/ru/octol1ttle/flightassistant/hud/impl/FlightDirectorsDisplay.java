package ru.octol1ttle.flightassistant.hud.impl;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import ru.octol1ttle.flightassistant.Dimensions;
import ru.octol1ttle.flightassistant.DrawHelper;
import ru.octol1ttle.flightassistant.computers.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.autoflight.AutoFlightComputer;
import ru.octol1ttle.flightassistant.config.FAConfig;
import ru.octol1ttle.flightassistant.hud.api.IHudDisplay;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;

public class FlightDirectorsDisplay implements IHudDisplay {
    private final Dimensions dim;
    private final AirDataComputer data;
    private final AutoFlightComputer autoflight;

    public FlightDirectorsDisplay(Dimensions dim) {
        this.dim = dim;
        this.data = ComputerRegistry.resolve(AirDataComputer.class);
        this.autoflight = ComputerRegistry.resolve(AutoFlightComputer.class);
    }

    @Override
    public void render(DrawContext context, TextRenderer textRenderer) {
        if (!autoflight.flightDirectorsEnabled) {
            return;
        }

        if (autoflight.getTargetPitch() != null) {
            float deltaPitch = autoflight.getTargetPitch() - data.pitch();
            int fdY = MathHelper.clamp(Math.round(dim.yMid - deltaPitch * dim.degreesPerPixel), dim.tFrame + 10, dim.bFrame - 20);
            DrawHelper.drawHorizontalLine(context, dim.xMid - dim.wFrame / 10, dim.xMid + dim.wFrame / 10, fdY, FAConfig.indicator().advisoryColor);
        }

        if (autoflight.getTargetHeading() != null) {
            float deltaHeading = autoflight.getTargetHeading() - data.heading();
            if (deltaHeading < -180.0f) {
                deltaHeading += 360.0f;
            }
            if (deltaHeading > 180.0f) {
                deltaHeading -= 360.0f;
            }

            int fdX = MathHelper.clamp(Math.round(dim.xMid + deltaHeading * dim.degreesPerPixel), dim.lFrame + 10, dim.rFrame - 10);
            DrawHelper.drawVerticalLine(context, fdX, dim.yMid - dim.hFrame / 7, dim.yMid + dim.hFrame / 7, FAConfig.indicator().advisoryColor);
        }
    }

    @Override
    public void renderFaulted(DrawContext context, TextRenderer textRenderer) {
        DrawHelper.drawMiddleAlignedText(textRenderer, context, Text.translatable("mode.flightassistant.auto.flight_directors"), dim.xMid, dim.yMid - 20, FAConfig.indicator().warningColor);
    }

    @Override
    public String getId() {
        return "flt_dir";
    }
}
