package ru.octol1ttle.flightassistant.util.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.DrawContext;

public interface AlternateHudRendererCallback {
    // modifications mode: change HudRendererCallback -> AlternateHudRendererCallback
    Event<AlternateHudRendererCallback> EVENT = EventFactory.createArrayBacked(AlternateHudRendererCallback.class, (listeners) -> (matrixStack, delta) -> {
        for (AlternateHudRendererCallback event : listeners) {
            event.onHudRender(matrixStack, delta);
        }
    });

    /**
     * Called after rendering the whole hud, which is displayed in game, in a world.
     *
     * @param drawContext the {@link DrawContext} instance
     * @param tickDelta   Progress for linearly interpolating between the previous and current game state
     */
    void onHudRender(DrawContext drawContext, float tickDelta);
}
