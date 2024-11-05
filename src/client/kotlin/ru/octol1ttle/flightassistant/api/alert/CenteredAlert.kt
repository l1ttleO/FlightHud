package ru.octol1ttle.flightassistant.api.alert

import net.minecraft.client.gui.DrawContext
import ru.octol1ttle.flightassistant.api.computer.ComputerAccess

interface CenteredAlert {
    fun render(drawContext: DrawContext, computers: ComputerAccess, y: Int): Boolean
}
