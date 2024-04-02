package ru.octol1ttle.flightassistant.hud.impl;

import java.awt.Color;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import ru.octol1ttle.flightassistant.Dimensions;
import ru.octol1ttle.flightassistant.DrawHelper;
import ru.octol1ttle.flightassistant.hud.api.IHudDisplay;
import ru.octol1ttle.flightassistant.computers.autoflight.FireworkController;
import ru.octol1ttle.flightassistant.computers.navigation.FlightPlanner;
import ru.octol1ttle.flightassistant.config.FAConfig;

public class StatusDisplay implements IHudDisplay {
    private final Dimensions dim;
    private final FireworkController firework;
    private final FlightPlanner plan;

    public StatusDisplay(Dimensions dim, FireworkController firework, FlightPlanner plan) {
        this.dim = dim;
        this.firework = firework;
        this.plan = plan;
    }

    @Override
    public void render(DrawContext context, TextRenderer textRenderer) {
        int x = dim.rFrame - 5;
        int y = dim.tFrame + 5;

        if (FAConfig.indicator().showFireworkCount) {
            Color fireworkColor = FAConfig.indicator().statusColor;
            if (firework.safeFireworkCount > 0) {
                if (firework.safeFireworkCount <= 24) {
                    fireworkColor = FAConfig.indicator().cautionColor;
                }
            } else {
                fireworkColor = FAConfig.indicator().warningColor;
            }
            DrawHelper.drawRightAlignedText(textRenderer, context,
                    Text.translatable("status.flightassistant.firework_count", firework.safeFireworkCount),
                    x, y += 10, fireworkColor);
        }

        if (FAConfig.indicator().showDistanceToWaypoint) {
            Double distance = plan.getDistanceToWaypoint();
            if (distance != null) {
                DrawHelper.drawRightAlignedText(textRenderer, context,
                        Text.translatable("status.flightassistant.waypoint_distance", distance.intValue()),
                        x, y + 10, FAConfig.indicator().statusColor);
            }
        }
    }

    @Override
    public void renderFaulted(DrawContext context, TextRenderer textRenderer) {
        DrawHelper.drawRightAlignedText(textRenderer, context,
                Text.translatable("flightassistant.status_short"),
                dim.rFrame - 5, dim.tFrame + 15, FAConfig.indicator().warningColor);
    }

    @Override
    public String getId() {
        return "status";
    }
}
