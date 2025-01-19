package ru.octol1ttle.flightassistant.impl.alert.thrust

import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import ru.octol1ttle.flightassistant.api.alert.*
import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.api.util.*

class ReverseThrustNotSupportedAlert : Alert(), ECAMAlert {
    override val data: AlertData
        get() = AlertData.MASTER_CAUTION

    override fun shouldActivate(computers: ComputerAccess): Boolean {
        return computers.thrust.reverseUnsupported
    }

    override fun render(drawContext: DrawContext, computers: ComputerAccess, firstLineX: Int, x: Int, y: Int): Int {
        var i = 0
        i += drawContext.drawText(Text.translatable("alerts.flightassistant.thrust.reverse_not_supported"), firstLineX, y, cautionColor)
        i += drawContext.drawText(Text.translatable("alerts.flightassistant.thrust.reverse_not_supported.set_forward"), x, y + 11, advisoryColor)
        return i
    }
}
