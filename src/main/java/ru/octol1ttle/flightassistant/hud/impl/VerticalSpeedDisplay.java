package ru.octol1ttle.flightassistant.hud.impl;

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

public class VerticalSpeedDisplay implements IHudDisplay {
    private final Dimensions dim;
    private final AirDataComputer data = ComputerRegistry.resolve(AirDataComputer.class);

    public VerticalSpeedDisplay(Dimensions dim) {
        this.dim = dim;
    }

    @Override
    public void render(DrawContext context, TextRenderer textRenderer) {
        if (!FAConfig.indicator().showVerticalSpeedReadout) {
            return;
        }

        int frameWidth = dim.rFrame - dim.lFrame;
        int x = MathHelper.floor(dim.lFrame + frameWidth * 0.75f - 7);
        DrawHelper.drawText(textRenderer, context,
                Text.translatable("flightassistant.vertical_speed_short", ": %.2f".formatted(data.velocity.y)),
                x, dim.bFrame, data.velocity.y <= -10.0f ? FAConfig.indicator().warningColor : FAConfig.indicator().frameColor);
    }

    @Override
    public void renderFaulted(DrawContext context, TextRenderer textRenderer) {
        int frameWidth = dim.rFrame - dim.lFrame;
        int x = MathHelper.floor(dim.lFrame + frameWidth * 0.75f - 7);

        DrawHelper.drawText(textRenderer, context, Text.translatable("flightassistant.vertical_speed_short", ""), x, dim.bFrame, FAConfig.indicator().warningColor);
    }

    @Override
    public String getId() {
        return "vertical_speed";
    }
}

