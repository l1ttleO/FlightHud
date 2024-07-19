package ru.octol1ttle.flightassistant.hud.impl;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import ru.octol1ttle.flightassistant.Dimensions;
import ru.octol1ttle.flightassistant.DrawHelper;
import ru.octol1ttle.flightassistant.computers.impl.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.impl.autoflight.AutoFlightController;
import ru.octol1ttle.flightassistant.computers.impl.autoflight.AutopilotComputer;
import ru.octol1ttle.flightassistant.config.FAConfig;
import ru.octol1ttle.flightassistant.hud.api.IHudDisplay;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;

public class FlightDirectorsDisplay implements IHudDisplay {
    private final Dimensions dim;
    private final AirDataComputer data = ComputerRegistry.resolve(AirDataComputer.class);
    private final AutoFlightController autoflight = ComputerRegistry.resolve(AutoFlightController.class);
    private final AutopilotComputer autopilot = ComputerRegistry.resolve(AutopilotComputer.class);

    public FlightDirectorsDisplay(Dimensions dim) {
        this.dim = dim;
    }

    @Override
    public void render(DrawContext context, TextRenderer textRenderer) {
        if (!autoflight.flightDirectorsEnabled) {
            return;
        }
        if (Math.abs(data.roll) > 90.0f) {
            renderFaulted(context, textRenderer);
            return;
        }

        if (autopilot.getTargetPitch() != null) {
            float deltaPitch = autopilot.getTargetPitch() - data.pitch();
            int fdY = MathHelper.clamp(Math.round(dim.yMid - deltaPitch * dim.degreesPerPixel), dim.tFrame + 10, dim.bFrame - 20);
            DrawHelper.drawHorizontalLine(context, dim.xMid - dim.wFrame / 10, dim.xMid + dim.wFrame / 10, fdY, FAConfig.indicator().advisoryColor);
        }

        if (autopilot.getTargetHeading() != null) {
            float deltaHeading = autopilot.getTargetHeading() - data.heading();
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

}
