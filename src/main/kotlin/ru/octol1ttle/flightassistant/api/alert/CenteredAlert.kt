package ru.octol1ttle.flightassistant.api.alert

import net.minecraft.client.gui.DrawContext

interface CenteredAlert {
    /**
     * @return whether or not this alert has rendered and occupied the center of the screen
     */
    fun render(drawContext: DrawContext, y: Int): Boolean
}
