package ru.octol1ttle.flightassistant.api.alert

import net.minecraft.client.gui.DrawContext

interface ECAMAlert {
    /**
     * Renders the text of this alert
     *
     * @return the amount of lines rendered
     */
    fun render(drawContext: DrawContext, firstLineX: Int, otherLinesX: Int, firstLineY: Int): Int
}
