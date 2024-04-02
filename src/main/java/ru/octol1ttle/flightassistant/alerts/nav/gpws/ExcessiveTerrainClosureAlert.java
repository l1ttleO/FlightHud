package ru.octol1ttle.flightassistant.alerts.nav.gpws;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import ru.octol1ttle.flightassistant.DrawHelper;
import ru.octol1ttle.flightassistant.alerts.AlertSoundData;
import ru.octol1ttle.flightassistant.alerts.BaseAlert;
import ru.octol1ttle.flightassistant.alerts.ICenteredAlert;
import ru.octol1ttle.flightassistant.computers.TimeComputer;
import ru.octol1ttle.flightassistant.computers.safety.GPWSComputer;
import ru.octol1ttle.flightassistant.config.FAConfig;


public class ExcessiveTerrainClosureAlert extends BaseAlert implements ICenteredAlert {
    private static final float TERRAIN_THRESHOLD = 7.5f;

    private static final float PULL_UP_THRESHOLD = 5.0f;
    private static final float DELAY_ALERT_FOR = 0.5f;
    private final GPWSComputer gpws;
    private final TimeComputer time;
    private boolean delayFull = false;
    private float delay = 0.0f;

    public ExcessiveTerrainClosureAlert(GPWSComputer gpws, TimeComputer time) {
        this.gpws = gpws;
        this.time = time;
    }

    @Override
    public boolean isTriggered() {
        if (gpws.descentImpactTime >= 0.0f) {
            return false;
        }

        boolean triggered = gpws.terrainImpactTime >= 0.0f;
        if (triggered) {
            delay = MathHelper.clamp(delay + time.deltaTime, 0.0f, DELAY_ALERT_FOR);
        } else {
            delay = MathHelper.clamp(delay - time.deltaTime, 0.0f, DELAY_ALERT_FOR);
        }

        if (delay >= DELAY_ALERT_FOR) {
            delayFull = true;
        }
        if (delay <= 0.0f) {
            delayFull = false;
        }

        return delayFull;
    }

    @Override
    public boolean render(TextRenderer textRenderer, DrawContext context, int x, int y, boolean highlight) {
        if (FAConfig.computer().terrainWarning.screenDisabled()) {
            return false;
        }

        if (gpws.terrainImpactTime <= PULL_UP_THRESHOLD) {
            DrawHelper.drawHighlightedMiddleAlignedText(textRenderer, context, Text.translatable("alerts.flightassistant.pull_up"), x, y,
                    FAConfig.indicator().warningColor, highlight);

            return true;
        }

        if (gpws.terrainImpactTime <= TERRAIN_THRESHOLD) {
            DrawHelper.drawHighlightedMiddleAlignedText(textRenderer, context, Text.translatable("alerts.flightassistant.terrain_ahead"), x, y,
                    FAConfig.indicator().cautionColor, highlight);

            return true;
        }

        return false;
    }

    @Override
    public @NotNull AlertSoundData getSoundData() {
        if (FAConfig.computer().terrainWarning.audioDisabled()) {
            return AlertSoundData.EMPTY;
        }

        if (gpws.terrainImpactTime <= PULL_UP_THRESHOLD) {
            return AlertSoundData.PULL_UP;
        }
        if (gpws.terrainImpactTime <= TERRAIN_THRESHOLD) {
            return AlertSoundData.TERRAIN;
        }

        return AlertSoundData.EMPTY;
    }
}