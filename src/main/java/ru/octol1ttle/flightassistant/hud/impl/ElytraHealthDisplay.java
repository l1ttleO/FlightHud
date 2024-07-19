package ru.octol1ttle.flightassistant.hud.impl;

import java.awt.Color;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import ru.octol1ttle.flightassistant.Dimensions;
import ru.octol1ttle.flightassistant.DrawHelper;
import ru.octol1ttle.flightassistant.computers.impl.AirDataComputer;
import ru.octol1ttle.flightassistant.config.FAConfig;
import ru.octol1ttle.flightassistant.config.IndicatorConfig;
import ru.octol1ttle.flightassistant.hud.api.IHudDisplay;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;

public class ElytraHealthDisplay implements IHudDisplay {

    private final Dimensions dim;
    private final AirDataComputer data = ComputerRegistry.resolve(AirDataComputer.class);

    public ElytraHealthDisplay(Dimensions dim) {
        this.dim = dim;
    }

    @Override
    public void render(DrawContext context, TextRenderer textRenderer) {
        int x = dim.xMid;
        int y = dim.bFrame;

        if (FAConfig.indicator().showElytraHealth && data.elytraHealth != null) {
            Color color;
            float percentage = data.elytraHealth.getInUnits(IndicatorConfig.ElytraHealthDisplayUnits.PERCENTAGE);
            if (percentage <= 5.0f) {
                color = FAConfig.indicator().warningColor;
            } else {
                color = percentage <= 10.0f ? FAConfig.indicator().cautionColor : FAConfig.indicator().frameColor;
            }
            DrawHelper.drawBorder(context, x - 3, y - 2, 30, color);
            DrawHelper.drawText(textRenderer, context, Text.translatable("short.flightassistant.elytra"), x - 10, y, color);

            DrawHelper.drawText(textRenderer, context, data.elytraHealth.format(FAConfig.indicator().elytraHealthUnits), x, y, color);
        }
    }

    @Override
    public void renderFaulted(DrawContext context, TextRenderer textRenderer) {
        DrawHelper.drawText(textRenderer, context, Text.translatable("short.flightassistant.elytra_health"),
                dim.xMid, dim.bFrame,
                FAConfig.indicator().warningColor);
    }

}
