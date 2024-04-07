package ru.octol1ttle.flightassistant.alerts.other;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import ru.octol1ttle.flightassistant.DrawHelper;
import ru.octol1ttle.flightassistant.alerts.AlertSoundData;
import ru.octol1ttle.flightassistant.alerts.BaseAlert;
import ru.octol1ttle.flightassistant.alerts.ICenteredAlert;
import ru.octol1ttle.flightassistant.computers.safety.StallComputer;
import ru.octol1ttle.flightassistant.config.FAConfig;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;


public class StallAlert extends BaseAlert implements ICenteredAlert {
    private final StallComputer stall;

    public StallAlert() {
        this.stall = ComputerRegistry.resolve(StallComputer.class);
    }

    @Override
    public boolean isTriggered() {
        return stall.status == StallComputer.StallStatus.FULL_STALL;
    }

    @Override
    public @NotNull AlertSoundData getSoundData() {
        return AlertSoundData.ifEnabled(FAConfig.computer().stallWarning, AlertSoundData.STALL);
    }

    @Override
    public boolean render(TextRenderer textRenderer, DrawContext context, int x, int y, boolean highlight) {
        if (FAConfig.computer().stallWarning.screenDisabled()) {
            return false;
        }

        DrawHelper.drawHighlightedMiddleAlignedText(textRenderer, context, Text.translatable("alerts.flightassistant.stall"), x, y, FAConfig.indicator().warningColor, highlight);
        return true;
    }
}
