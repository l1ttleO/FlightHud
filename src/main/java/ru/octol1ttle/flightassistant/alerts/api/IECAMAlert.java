package ru.octol1ttle.flightassistant.alerts.api;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public interface IECAMAlert {
    int render(TextRenderer textRenderer, DrawContext context, int x, int y, boolean highlight);
}
