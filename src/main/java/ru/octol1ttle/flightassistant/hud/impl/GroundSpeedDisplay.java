package ru.octol1ttle.flightassistant.hud.impl;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import ru.octol1ttle.flightassistant.Dimensions;
import ru.octol1ttle.flightassistant.DrawHelper;
import ru.octol1ttle.flightassistant.computers.impl.AirDataComputer;
import ru.octol1ttle.flightassistant.config.FAConfig;
import ru.octol1ttle.flightassistant.hud.api.IHudDisplay;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;

public class GroundSpeedDisplay implements IHudDisplay {
    private final Dimensions dim;
    private final AirDataComputer data = ComputerRegistry.resolve(AirDataComputer.class);

    public GroundSpeedDisplay(Dimensions dim) {
        this.dim = dim;
    }

    @Override
    public void render(DrawContext context, TextRenderer textRenderer) {
        if (!FAConfig.indicator().showGroundSpeedReadout) {
            return;
        }

        int frameWidth = dim.rFrame - dim.lFrame;
        int x = MathHelper.floor(dim.lFrame + frameWidth * 0.25f);
        DrawHelper.drawText(textRenderer, context,
                Text.translatable("flightassistant.ground_speed_short", ": %.2f".formatted(data.velocity.horizontalLength())),
                x, dim.bFrame, FAConfig.indicator().frameColor);
    }

    @Override
    public void renderFaulted(DrawContext context, TextRenderer textRenderer) {
        int frameWidth = dim.rFrame - dim.lFrame;
        int x = MathHelper.floor(dim.lFrame + frameWidth * 0.25f);

        DrawHelper.drawText(textRenderer, context, Text.translatable("flightassistant.ground_speed_short", ""), x, dim.bFrame, FAConfig.indicator().warningColor);
    }

    @Override
    public String getId() {
        return "ground_speed";
    }
}
