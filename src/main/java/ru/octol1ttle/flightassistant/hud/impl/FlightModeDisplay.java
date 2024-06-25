package ru.octol1ttle.flightassistant.hud.impl;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import ru.octol1ttle.flightassistant.Dimensions;
import ru.octol1ttle.flightassistant.DrawHelper;
import ru.octol1ttle.flightassistant.computers.impl.TimeComputer;
import ru.octol1ttle.flightassistant.computers.impl.autoflight.AutoFlightController;
import ru.octol1ttle.flightassistant.computers.impl.autoflight.AutopilotComputer;
import ru.octol1ttle.flightassistant.computers.impl.autoflight.ThrustController;
import ru.octol1ttle.flightassistant.computers.impl.navigation.FlightPlanner;
import ru.octol1ttle.flightassistant.config.FAConfig;
import ru.octol1ttle.flightassistant.hud.api.IHudDisplay;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;

public class FlightModeDisplay implements IHudDisplay {
    private final Dimensions dim;
    private final TimeComputer time = ComputerRegistry.resolve(TimeComputer.class);
    private final AutoFlightController autoflight = ComputerRegistry.resolve(AutoFlightController.class);
    private final AutopilotComputer autopilot = ComputerRegistry.resolve(AutopilotComputer.class);
    private final ThrustController thrust = ComputerRegistry.resolve(ThrustController.class);
    private final FlightPlanner plan = ComputerRegistry.resolve(FlightPlanner.class);

    private final FlightMode thrustMode;
    private final FlightMode verticalMode;
    private final FlightMode lateralMode;
    private final FlightMode automationMode;

    public FlightModeDisplay(Dimensions dim) {
        this.dim = dim;

        this.thrustMode = new FlightMode();
        this.verticalMode = new FlightMode();
        this.lateralMode = new FlightMode();
        this.automationMode = new FlightMode();
    }

    @Override
    public void render(DrawContext context, TextRenderer textRenderer) {
        if (time.millis == null) {
            renderFaulted(context, textRenderer);
            return;
        }

        if (FAConfig.indicator().showFireworkMode) {
            renderThrustMode(context, textRenderer);
        }
        if (FAConfig.indicator().showVerticalMode) {
            renderVerticalMode(context, textRenderer);
        }
        if (FAConfig.indicator().showLateralMode) {
            renderLateralMode(context, textRenderer);
        }
        if (FAConfig.indicator().showAutomationStatus) {
            renderAutomationStatus(context, textRenderer);
        }
    }

    private void renderThrustMode(DrawContext context, TextRenderer textRenderer) {
        if (!autoflight.autoThrustEnabled || Text.empty().equals(autopilot.thrustMode)) {
            thrustMode.update(Text.empty());
            return;
        }

        thrustMode.update(autopilot.thrustMode);

        int x = MathHelper.floor(dim.lFrame + dim.wFrame * (1 / 5.0f));
        int y = dim.bFrame - 10;
        thrustMode.render(context, textRenderer, x, y);
    }

    private void renderVerticalMode(DrawContext context, TextRenderer textRenderer) {
        if (!autoflight.flightDirectorsEnabled && !autoflight.autoPilotEnabled || Text.empty().equals(autopilot.verticalMode)) {
            verticalMode.update(Text.empty());
            return;
        }

        if (autopilot.autolandInProgress && !plan.autolandAllowed) {
            verticalMode.update(Text.translatable("mode.flightassistant.auto.no_autoland"), true);
        } else {
            verticalMode.update(autopilot.verticalMode);
        }
        int x = MathHelper.floor(dim.lFrame + dim.wFrame * (2 / 5.0f));
        int y = dim.bFrame - 10;
        verticalMode.render(context, textRenderer, x, y);
    }

    private void renderLateralMode(DrawContext context, TextRenderer textRenderer) {
        if (!autoflight.flightDirectorsEnabled && !autoflight.autoPilotEnabled || Text.empty().equals(autopilot.lateralMode)) {
            lateralMode.update(Text.empty());
            return;
        }

        if (autopilot.autolandInProgress && !plan.autolandAllowed) {
            lateralMode.update(Text.translatable("mode.flightassistant.thrust.set_toga"), true);
        } else {
            lateralMode.update(autopilot.lateralMode);
        }

        int x = MathHelper.floor(dim.lFrame + dim.wFrame * (3 / 5.0f));
        int y = dim.bFrame - 10;
        lateralMode.render(context, textRenderer, x, y);
    }

    private void renderAutomationStatus(DrawContext context, TextRenderer textRenderer) {
        MutableText automationStatus = Text.empty();
        if (autoflight.flightDirectorsEnabled) {
            appendWithSeparation(automationStatus, Text.translatable("mode.flightassistant.auto.flight_directors"));
        }
        if (autoflight.autoThrustEnabled) {
            appendWithSeparation(automationStatus, Text.translatable("mode.flightassistant.auto.auto_thrust"));
        }
        if (autoflight.autoPilotEnabled) {
            appendWithSeparation(automationStatus, Text.translatable("mode.flightassistant.auto.autopilot"));
        }
        if (automationStatus.getSiblings().isEmpty()) {
            return;
        }

        automationMode.update(automationStatus);

        int x = MathHelper.floor(dim.lFrame + dim.wFrame * (4 / 5.0f));
        int y = dim.bFrame - 10;
        automationMode.render(context, textRenderer, x, y);
    }

    private void appendWithSeparation(MutableText text, Text append) {
        if (!text.getSiblings().isEmpty()) {
            text.append(" ");
        }
        text.append(append);
    }

    @Override
    public void renderFaulted(DrawContext context, TextRenderer textRenderer) {
        DrawHelper.drawText(textRenderer, context, Text.translatable("short.flightassistant.flight_mode"), dim.lFrame + dim.wFrame / 5, dim.bFrame - 10, FAConfig.indicator().warningColor);
    }

    @Override
    public String getId() {
        return "flight_mode";
    }
}
