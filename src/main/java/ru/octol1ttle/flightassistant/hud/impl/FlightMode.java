package ru.octol1ttle.flightassistant.hud.impl;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import ru.octol1ttle.flightassistant.DrawHelper;
import ru.octol1ttle.flightassistant.computers.TimeComputer;
import ru.octol1ttle.flightassistant.config.FAConfig;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;


public class FlightMode {
    private static final int UPDATE_FLASH_TIME = 3000;
    private final TimeComputer time = ComputerRegistry.resolve(TimeComputer.class);
    @Nullable
    private Float lastUpdateTime;
    @Nullable
    private Text lastText;

    public FlightMode() {
    }

    public void update(Text newText) {
        update(newText, false);
    }

    public void update(Text newText, boolean forceFlash) {
        if (!forceFlash && newText.equals(lastText)) {
            return;
        }

        lastUpdateTime = time.millis;
        lastText = newText;
    }

    public void render(DrawContext context, TextRenderer textRenderer, int x, int y) {
        if (lastUpdateTime == null) {
            throw new IllegalStateException("Called render before updating");
        }
        if (time.millis != null && time.millis - lastUpdateTime <= UPDATE_FLASH_TIME) {
            DrawHelper.drawHighlightedMiddleAlignedText(textRenderer, context, lastText, x, y, FAConfig.indicator().cautionColor, time.highlight);
            return;
        }

        DrawHelper.drawMiddleAlignedText(textRenderer, context, lastText, x, y, FAConfig.indicator().statusColor);
    }
}
