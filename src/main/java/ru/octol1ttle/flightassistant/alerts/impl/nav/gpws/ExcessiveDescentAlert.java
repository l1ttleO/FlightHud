package ru.octol1ttle.flightassistant.alerts.impl.nav.gpws;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import ru.octol1ttle.flightassistant.DrawHelper;
import ru.octol1ttle.flightassistant.alerts.impl.AlertSoundData;
import ru.octol1ttle.flightassistant.alerts.api.BaseAlert;
import ru.octol1ttle.flightassistant.alerts.api.ICenteredAlert;
import ru.octol1ttle.flightassistant.computers.impl.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.impl.safety.GroundProximityComputer;
import ru.octol1ttle.flightassistant.config.FAConfig;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;


public class ExcessiveDescentAlert extends BaseAlert implements ICenteredAlert {
    private static final float SINK_RATE_THRESHOLD = 7.5f;
    private static final float PULL_UP_THRESHOLD = 5.0f;
    private final AirDataComputer data = ComputerRegistry.resolve(AirDataComputer.class);
    private final GroundProximityComputer gpws = ComputerRegistry.resolve(GroundProximityComputer.class);

    @Override
    public boolean isTriggered() {
        return data.pitch() < 0.0f && gpws.descentImpactTime >= 0.0f;
    }

    @Override
    public boolean render(TextRenderer textRenderer, DrawContext context, int x, int y, boolean highlight) {
        if (FAConfig.computer().sinkrateWarning.screenDisabled()) {
            return false;
        }

        if (gpws.descentImpactTime <= PULL_UP_THRESHOLD) {
            DrawHelper.drawHighlightedMiddleAlignedText(textRenderer, context, Text.translatable("alerts.flightassistant.pull_up"), x, y,
                    FAConfig.indicator().warningColor, highlight);

            return true;
        }

        if (gpws.descentImpactTime <= SINK_RATE_THRESHOLD) {
            DrawHelper.drawHighlightedMiddleAlignedText(textRenderer, context, Text.translatable("alerts.flightassistant.sink_rate"), x, y,
                    FAConfig.indicator().cautionColor, highlight);

            return true;
        }

        return false;
    }

    @Override
    public @NotNull AlertSoundData getSoundData() {
        if (FAConfig.computer().sinkrateWarning.audioDisabled()) {
            return AlertSoundData.EMPTY;
        }

        if (gpws.descentImpactTime <= PULL_UP_THRESHOLD) {
            return AlertSoundData.PULL_UP;
        }
        if (gpws.descentImpactTime <= SINK_RATE_THRESHOLD) {
            return AlertSoundData.SINK_RATE;
        }

        return AlertSoundData.EMPTY;
    }
}
