package ru.octol1ttle.flightassistant.impl.alert.thrust

import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import ru.octol1ttle.flightassistant.api.alert.*
import ru.octol1ttle.flightassistant.api.computer.ComputerView
import ru.octol1ttle.flightassistant.api.util.extensions.advisoryColor
import ru.octol1ttle.flightassistant.api.util.extensions.cautionColor
import ru.octol1ttle.flightassistant.api.util.extensions.drawText

class ReverseThrustNotSupportedAlert(computers: ComputerView) : Alert(computers), ECAMAlert {
    override val priorityOffset: Int = 40
    override val data: AlertData = AlertData.MASTER_CAUTION

    override fun shouldActivate(): Boolean {
        return computers.thrust.reverseUnsupported
    }

    override fun render(drawContext: DrawContext, firstLineX: Int, otherLinesX: Int, firstLineY: Int): Int {
        var i = 0
        i += drawContext.drawText(Text.translatable("alerts.flightassistant.thrust.reverse_not_supported"), firstLineX, firstLineY, cautionColor)
        i += drawContext.drawText(Text.translatable("alerts.flightassistant.thrust.reverse_not_supported.set_forward"), otherLinesX, firstLineY + 11, advisoryColor)
        return i
    }
}
