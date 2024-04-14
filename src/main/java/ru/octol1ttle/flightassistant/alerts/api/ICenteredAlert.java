package ru.octol1ttle.flightassistant.alerts.api;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public interface ICenteredAlert {
    boolean render(TextRenderer textRenderer, DrawContext context, int width, int y, boolean highlight);
}
