package ru.octol1ttle.flightassistant.alerts.impl.other;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import ru.octol1ttle.flightassistant.DrawHelper;
import ru.octol1ttle.flightassistant.alerts.api.BaseAlert;
import ru.octol1ttle.flightassistant.alerts.api.IECAMAlert;
import ru.octol1ttle.flightassistant.alerts.impl.AlertSoundData;
import ru.octol1ttle.flightassistant.computers.impl.AirDataComputer;
import ru.octol1ttle.flightassistant.config.FAConfig;
import ru.octol1ttle.flightassistant.config.IndicatorConfig;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;

public class ElytraHealthLowAlert extends BaseAlert implements IECAMAlert {
    private final AirDataComputer data = ComputerRegistry.resolve(AirDataComputer.class);

    @Override
    public boolean isTriggered() {
        return data.elytraData != null && data.elytraData.getHealth(IndicatorConfig.ElytraHealthDisplayUnits.PERCENTAGE) <= 5.0f;
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
