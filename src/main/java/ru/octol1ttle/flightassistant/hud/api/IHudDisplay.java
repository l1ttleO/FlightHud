package ru.octol1ttle.flightassistant.hud.api;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public interface IHudDisplay {
    void render(DrawContext context, TextRenderer textRenderer);

    void renderFaulted(DrawContext context, TextRenderer textRenderer);

    String getId();
}
