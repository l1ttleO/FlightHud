package ru.octol1ttle.flightassistant.alerts.nav.gpws;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import ru.octol1ttle.flightassistant.DrawHelper;
import ru.octol1ttle.flightassistant.alerts.AlertSoundData;
import ru.octol1ttle.flightassistant.alerts.BaseAlert;
import ru.octol1ttle.flightassistant.alerts.ICenteredAlert;
import ru.octol1ttle.flightassistant.computers.navigation.FlightPlanner;
import ru.octol1ttle.flightassistant.computers.safety.GPWSComputer;
import ru.octol1ttle.flightassistant.config.FAConfig;

public class UnsafeTerrainClearanceAlert extends BaseAlert implements ICenteredAlert {
    private final GPWSComputer gpws;
    private final FlightPlanner plan;

    public UnsafeTerrainClearanceAlert(GPWSComputer gpws, FlightPlanner plan) {
        this.gpws = gpws;
        this.plan = plan;
    }

    @Override
    public boolean isTriggered() {
        return gpws.landingClearanceStatus == GPWSComputer.LandingClearanceStatus.TOO_LOW;
    }

    @Override
    public @NotNull AlertSoundData getSoundData() {
        return AlertSoundData.ifEnabled(FAConfig.computer().landingClearanceWarning, AlertSoundData.TOO_LOW_TERRAIN);
    }

    @Override
    public boolean render(TextRenderer textRenderer, DrawContext context, int x, int y, boolean highlight) {
        if (FAConfig.computer().landingClearanceWarning.screenDisabled()) {
            return false;
        }

        DrawHelper.drawHighlightedMiddleAlignedText(textRenderer, context, Text.translatable("alerts.flightassistant.too_low_terrain"), x, y,
                plan.isBelowMinimums()
                        ? FAConfig.indicator().warningColor
                        : FAConfig.indicator().cautionColor
                , highlight);
        return true;
    }
}
