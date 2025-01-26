package ru.octol1ttle.flightassistant.api.util.event

import dev.architectury.event.Event
import dev.architectury.event.EventFactory
import net.minecraft.client.gui.DrawContext

fun interface FixedHudRenderCallback {
    /**
     * Called when the main HUD is being rendered.
     */
    fun onRenderHud(context: DrawContext, tickDelta: Float)

    companion object {
        @JvmField
        val EVENT: Event<FixedHudRenderCallback> = EventFactory.createLoop()
    }
}
