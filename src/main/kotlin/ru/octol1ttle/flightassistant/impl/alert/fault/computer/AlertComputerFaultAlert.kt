package ru.octol1ttle.flightassistant.impl.alert.fault.computer

import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import ru.octol1ttle.flightassistant.api.alert.Alert
import ru.octol1ttle.flightassistant.api.alert.AlertData
import ru.octol1ttle.flightassistant.api.alert.ECAMAlert
import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.api.util.alert
import ru.octol1ttle.flightassistant.api.util.cautionColor
import ru.octol1ttle.flightassistant.api.util.drawText

class AlertComputerFaultAlert : Alert(), ECAMAlert {
    override val priorityOffset: Int = 15
    override val data: AlertData
        get() = AlertData.MASTER_CAUTION

    override fun shouldActivate(computers: ComputerAccess): Boolean {
        return computers.alert.alertsFaulted
    }

    override fun render(drawContext: DrawContext, computers: ComputerAccess, firstLineX: Int, otherLinesX: Int, firstLineY: Int): Int {
        return drawContext.drawText(Text.translatable("alerts.flightassistant.alert.fault"), firstLineX, firstLineY, cautionColor)
    }
}
