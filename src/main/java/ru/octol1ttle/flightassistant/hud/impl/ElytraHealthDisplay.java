package ru.octol1ttle.flightassistant.hud.impl;

import java.awt.Color;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import ru.octol1ttle.flightassistant.Dimensions;
import ru.octol1ttle.flightassistant.DrawHelper;
import ru.octol1ttle.flightassistant.computers.AirDataComputer;
import ru.octol1ttle.flightassistant.config.FAConfig;
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
            if (data.elytraHealth <= 5.0f) { // TODO: configurable
                color = FAConfig.indicator().warningColor;
            } else {
                color = data.elytraHealth <= 10.0f ? FAConfig.indicator().cautionColor : FAConfig.indicator().frameColor;
            }
            DrawHelper.drawBorder(context, x - 3, y - 2, 30, color);
            DrawHelper.drawText(textRenderer, context, Text.translatable("flightassistant.elytra_short"), x - 10, y, color);

            DrawHelper.drawText(textRenderer, context, DrawHelper.asText("%d", MathHelper.ceil(data.elytraHealth)).append("%"), x, y, color);
        }
    }

    @Override
    public void renderFaulted(DrawContext context, TextRenderer textRenderer) {
        DrawHelper.drawText(textRenderer, context, Text.translatable("flightassistant.elytra_health_short"),
                dim.xMid, dim.bFrame,
                FAConfig.indicator().warningColor);
    }

    @Override
    public String getId() {
        return "elytra_health";
    }
}
