package ru.octol1ttle.flightassistant.alerts.other;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import ru.octol1ttle.flightassistant.DrawHelper;
import ru.octol1ttle.flightassistant.alerts.AlertSoundData;
import ru.octol1ttle.flightassistant.alerts.BaseAlert;
import ru.octol1ttle.flightassistant.alerts.IECAMAlert;
import ru.octol1ttle.flightassistant.computers.AirDataComputer;
import ru.octol1ttle.flightassistant.config.FAConfig;

public class ElytraHealthLowAlert extends BaseAlert implements IECAMAlert {
    private final AirDataComputer data;

    public ElytraHealthLowAlert(AirDataComputer data) {
        this.data = data;
    }

    @Override
    public boolean isTriggered() {
        return data.elytraHealth != null && data.elytraHealth <= 5.0f;
    }

    @Override
    public @NotNull AlertSoundData getSoundData() {
        return data.isFlying() ? AlertSoundData.MASTER_WARNING : AlertSoundData.EMPTY;
    }

    @Override
    public int render(TextRenderer textRenderer, DrawContext context, int x, int y, boolean highlight) {
        return DrawHelper.drawHighlightedText(textRenderer, context, Text.translatable("alerts.flightassistant.elytra_health_low"), x, y,
                FAConfig.indicator().warningColor, highlight && data.isFlying());
    }
}