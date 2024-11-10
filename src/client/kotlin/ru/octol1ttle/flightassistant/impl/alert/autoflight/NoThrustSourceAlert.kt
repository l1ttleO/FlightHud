package ru.octol1ttle.flightassistant.impl.alert.autoflight

import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import ru.octol1ttle.flightassistant.api.alert.*
import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.api.util.*

class NoThrustSourceAlert : Alert(), ECAMAlert {
    override val data: AlertData
        get() = AlertData.MASTER_CAUTION

    override fun shouldActivate(computers: ComputerAccess): Boolean {
        return computers.thrust.noThrustSource
    }

    override fun render(drawContext: DrawContext, computers: ComputerAccess, firstLineX: Int, x: Int, y: Int): Int {
        drawContext.drawText(Text.translatable("alerts.flightassistant.thrust.no_source"), firstLineX, y, cautionColor)
        return 1
    }
}
