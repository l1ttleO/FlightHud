package ru.octol1ttle.flightassistant.api.alert

import net.minecraft.client.gui.DrawContext

interface ECAMAlert {
    /**
     * Renders the text of this alert
     *
     * @param firstLineX The X coordinate value that should be used only for the first line.
     * @param otherLinesX The X coordinate value that should be used for the second line and onwards.
     * @return the amount of lines rendered
     */
    fun render(drawContext: DrawContext, firstLineX: Int, otherLinesX: Int, firstLineY: Int): Int
}
