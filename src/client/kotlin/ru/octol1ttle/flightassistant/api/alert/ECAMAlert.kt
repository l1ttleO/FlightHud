package ru.octol1ttle.flightassistant.api.alert

import net.minecraft.client.gui.DrawContext
import ru.octol1ttle.flightassistant.api.computer.ComputerAccess

interface ECAMAlert {
    /**
     * Renders the text of this alert
     *
     * @return the amount of lines rendered
     */
    fun render(drawContext: DrawContext, computers: ComputerAccess, firstLineX: Int, x: Int, y: Int): Int
}
