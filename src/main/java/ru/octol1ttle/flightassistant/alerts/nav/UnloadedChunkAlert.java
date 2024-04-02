package ru.octol1ttle.flightassistant.alerts.nav;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import ru.octol1ttle.flightassistant.DrawHelper;
import ru.octol1ttle.flightassistant.alerts.AlertSoundData;
import ru.octol1ttle.flightassistant.alerts.BaseAlert;
import ru.octol1ttle.flightassistant.alerts.IECAMAlert;
import ru.octol1ttle.flightassistant.computers.safety.ChunkStatusComputer;
import ru.octol1ttle.flightassistant.config.FAConfig;

public class UnloadedChunkAlert extends BaseAlert implements IECAMAlert {
    private final ChunkStatusComputer chunkStatus;

    public UnloadedChunkAlert(ChunkStatusComputer chunkStatus) {
        this.chunkStatus = chunkStatus;
    }

    @Override
    public boolean isTriggered() {
        return chunkStatus.shouldWarn();
    }

    @Override
    public int render(TextRenderer textRenderer, DrawContext context, int x, int y, boolean highlight) {
        return DrawHelper.drawHighlightedText(textRenderer, context, Text.translatable("alerts.flightassistant.unloaded_chunk"), x, y,
                FAConfig.indicator().warningColor, highlight);
    }

    @Override
    public @NotNull AlertSoundData getSoundData() {
        return AlertSoundData.MASTER_WARNING;
    }
}
