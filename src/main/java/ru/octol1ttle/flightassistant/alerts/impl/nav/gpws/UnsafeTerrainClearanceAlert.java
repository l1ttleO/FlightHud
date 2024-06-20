package ru.octol1ttle.flightassistant.alerts.impl.nav.gpws;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import ru.octol1ttle.flightassistant.DrawHelper;
import ru.octol1ttle.flightassistant.alerts.api.BaseAlert;
import ru.octol1ttle.flightassistant.alerts.api.ICenteredAlert;
import ru.octol1ttle.flightassistant.alerts.impl.AlertSoundData;
import ru.octol1ttle.flightassistant.computers.impl.navigation.FlightPlanner;
import ru.octol1ttle.flightassistant.computers.impl.safety.GroundProximityComputer;
import ru.octol1ttle.flightassistant.computers.impl.safety.GroundProximityComputer.TerrainClearanceStatus;
import ru.octol1ttle.flightassistant.config.FAConfig;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;

public class UnsafeTerrainClearanceAlert extends BaseAlert implements ICenteredAlert {
    private final GroundProximityComputer gpws = ComputerRegistry.resolve(GroundProximityComputer.class);
    private final FlightPlanner plan = ComputerRegistry.resolve(FlightPlanner.class);

    @Override
    public boolean isTriggered() {
        return gpws.terrainClearanceStatus == TerrainClearanceStatus.TOO_LOW;
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

        DrawHelper.drawHighlightedMiddleAlignedText(textRenderer, context, Text.translatable("alerts.flightassistant.gpws.too_low_terrain"), x, y,
                plan.isBelowMinimums()
                        ? FAConfig.indicator().warningColor
                        : FAConfig.indicator().cautionColor
                , highlight);
        return true;
    }
}
