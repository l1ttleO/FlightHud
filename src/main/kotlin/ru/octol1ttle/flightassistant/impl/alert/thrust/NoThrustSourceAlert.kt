package ru.octol1ttle.flightassistant.impl.alert.thrust

import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import ru.octol1ttle.flightassistant.api.alert.*
import ru.octol1ttle.flightassistant.api.computer.ComputerView
import ru.octol1ttle.flightassistant.api.util.extensions.cautionColor
import ru.octol1ttle.flightassistant.api.util.extensions.drawText

class NoThrustSourceAlert(computers: ComputerView) : Alert(computers), ECAMAlert {
    override val priorityOffset: Int = 35
    override val data: AlertData = AlertData.MASTER_CAUTION

    override fun shouldActivate(): Boolean {
        return computers.thrust.noThrustSource
    }

    override fun render(drawContext: DrawContext, firstLineX: Int, otherLinesX: Int, firstLineY: Int): Int {
        return drawContext.drawText(Text.translatable("alerts.flightassistant.thrust.no_source"), firstLineX, firstLineY, cautionColor)
    }
}
