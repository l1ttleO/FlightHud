package ru.octol1ttle.flightassistant.hud.impl;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector2d;
import ru.octol1ttle.flightassistant.Dimensions;
import ru.octol1ttle.flightassistant.DrawHelper;
import ru.octol1ttle.flightassistant.hud.api.IHudDisplay;
import ru.octol1ttle.flightassistant.computers.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.TimeComputer;
import ru.octol1ttle.flightassistant.computers.autoflight.AutoFlightComputer;
import ru.octol1ttle.flightassistant.computers.autoflight.FireworkController;
import ru.octol1ttle.flightassistant.computers.navigation.FlightPlanner;
import ru.octol1ttle.flightassistant.config.FAConfig;

public class FlightModeDisplay implements IHudDisplay {
    private final Dimensions dim;
    private final AirDataComputer data;
    private final TimeComputer time;
    private final FireworkController firework;
    private final AutoFlightComputer autoflight;
    private final FlightPlanner plan;

    private final FlightMode fireworkMode;
    private final FlightMode verticalMode;
    private final FlightMode lateralMode;
    private final FlightMode automationMode;

    public FlightModeDisplay(Dimensions dim, AirDataComputer data, TimeComputer time, FireworkController firework, AutoFlightComputer autoflight, FlightPlanner plan) {
        this.dim = dim;
        this.data = ComputerRegistry.resolve(AirDataComputer.class);
        this.time = ComputerRegistry.resolve(TimeComputer.class);
        this.firework = firework;
        this.autoflight = autoflight;
        this.plan = plan;

        this.fireworkMode = new FlightMode(this.time);
        this.verticalMode = new FlightMode(this.time);
        this.lateralMode = new FlightMode(this.time);
        this.automationMode = new FlightMode(this.time);
    }

    // TODO: consider delegating mode update tasks to a computer
    @Override
    public void render(DrawContext context, TextRenderer textRenderer) {
        if (time.millis == null) {
            renderFaulted(context, textRenderer);
            return;
        }

        if (FAConfig.indicator().showFireworkMode) {
            renderFireworkMode(context, textRenderer);
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

    private void renderFireworkMode(DrawContext context, TextRenderer textRenderer) {
        Integer targetSpeed = autoflight.getTargetSpeed();
        Text minimums = plan.formatMinimums();

        if (firework.noFireworks) {
            fireworkMode.update(Text.translatable("mode.flightassistant.firework.none_in_hotbar"), autoflight.autoFireworkEnabled);
        } else if (firework.lastProtTrigger != null && time.millis - firework.lastProtTrigger < 2000) {
            fireworkMode.update(Text.translatable("mode.flightassistant.firework.protection"), true);
        } else if (minimums != null && plan.isOnApproach()) {
            fireworkMode.update(minimums, plan.isBelowMinimums());
        } else if (autoflight.autoFireworkEnabled) {
            if (targetSpeed != null) {
                String type = autoflight.selectedSpeed != null ? ".selected" : ".managed";
                fireworkMode.update(Text.translatable("mode.flightassistant.firework.speed" + type, targetSpeed));
            } else if (autoflight.getTargetAltitude() != null) {
                if (autoflight.getTargetAltitude() + 1.0f > data.altitude()) {
                    fireworkMode.update(Text.translatable("mode.flightassistant.firework.climb"));
                } else {
                    fireworkMode.update(Text.translatable("mode.flightassistant.firework.idle"));
                }
            } else {
                if (firework.lockManualFireworks) {
                    fireworkMode.update(Text.translatable("mode.flightassistant.firework.locked"), true);
                } else {
                    fireworkMode.update(Text.translatable("mode.flightassistant.firework.armed"));
                }
            }
        } else {
            if (firework.lockManualFireworks) {
                fireworkMode.update(Text.translatable("mode.flightassistant.firework.locked"), true);
            } else {
                fireworkMode.update(Text.translatable("mode.flightassistant.firework.manual"));
            }
        }

        int x = MathHelper.floor(dim.lFrame + dim.wFrame * (1 / 5.0f));
        int y = dim.bFrame - 10;
        fireworkMode.render(context, textRenderer, x, y);
    }

    private void renderVerticalMode(DrawContext context, TextRenderer textRenderer) {
        Integer targetAltitude = autoflight.getTargetAltitude();
        if (targetAltitude == null || !autoflight.flightDirectorsEnabled && !autoflight.autoPilotEnabled) {
            verticalMode.update(Text.empty());
            return;
        }

        float diff = Math.abs(targetAltitude - data.altitude());
        String type = autoflight.selectedAltitude != null ? ".selected" : ".managed";

        if (plan.landAltitude != null) {
            verticalMode.update(Text.translatable("mode.flightassistant.vert.land", plan.landAltitude));
        } else if (!autoflight.autoPilotEnabled || diff <= 5) {
            verticalMode.update(Text.translatable("mode.flightassistant.vert.alt_hold" + type, targetAltitude));
        } else if (diff <= 10) {
            verticalMode.update(Text.translatable("mode.flightassistant.vert.alt_approaching" + type, targetAltitude));
        } else if (targetAltitude > data.altitude()) {
            verticalMode.update(Text.translatable("mode.flightassistant.vert.climb" + type, targetAltitude));
        } else {
            verticalMode.update(Text.translatable("mode.flightassistant.vert.descend" + type, targetAltitude));
        }

        int x = MathHelper.floor(dim.lFrame + dim.wFrame * (2 / 5.0f));
        int y = dim.bFrame - 10;
        verticalMode.render(context, textRenderer, x, y);
    }

    private void renderLateralMode(DrawContext context, TextRenderer textRenderer) {
        if (autoflight.getTargetHeading() == null || !autoflight.flightDirectorsEnabled && !autoflight.autoPilotEnabled) {
            lateralMode.update(Text.empty());
            return;
        }

        if (autoflight.selectedHeading != null) {
            lateralMode.update(Text.translatable("mode.flightassistant.lat.heading", autoflight.selectedHeading));
        } else if (plan.getTargetPosition() != null) {
            Vector2d target = plan.getTargetPosition();
            String key = plan.isOnApproach() ? "mode.flightassistant.lat.approach" : "mode.flightassistant.lat.position";
            lateralMode.update(Text.translatable(key, (int) target.x, (int) target.y));
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
        if (autoflight.autoFireworkEnabled) {
            appendWithSeparation(automationStatus, Text.translatable("mode.flightassistant.auto.firework"));
        }
        if (autoflight.autoPilotEnabled) {
            appendWithSeparation(automationStatus, Text.translatable("mode.flightassistant.auto.pilot"));
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
        DrawHelper.drawText(textRenderer, context, Text.translatable("flightassistant.flight_mode_short"), dim.lFrame + dim.wFrame / 5, dim.bFrame - 10, FAConfig.indicator().warningColor);
    }

    @Override
    public String getId() {
        return "flight_mode";
    }
}