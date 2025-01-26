package ru.octol1ttle.flightassistant.impl.alert.fault.computer

import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.api.alert.Alert
import ru.octol1ttle.flightassistant.api.alert.AlertData
import ru.octol1ttle.flightassistant.api.alert.ECAMAlert
import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.api.util.extensions.advisoryColor
import ru.octol1ttle.flightassistant.api.util.extensions.drawText
import ru.octol1ttle.flightassistant.impl.computer.ComputerHost

class ComputerFaultAlert(private val identifier: Identifier, private val alertText: Text, private val extraTexts: Collection<Text>? = null, override val data: AlertData = AlertData.MASTER_CAUTION) : Alert(), ECAMAlert {
    override val priorityOffset: Int = 25

    override fun shouldActivate(computers: ComputerAccess): Boolean {
        return ComputerHost.isFaulted(identifier)
    }

    override fun render(drawContext: DrawContext, computers: ComputerAccess, firstLineX: Int, otherLinesX: Int, firstLineY: Int): Int {
        val color: Int = data.colorSupplier.invoke()
        var i = 0
        i += drawContext.drawText(alertText, firstLineX, firstLineY, color)
        var y: Int = firstLineY + 11

        if (extraTexts != null) {
            for (text in extraTexts) {
                i += drawContext.drawText(text, otherLinesX, y, advisoryColor)
                y += 10
            }
        }

        i += drawResetText(drawContext, otherLinesX, y)
        return i
    }

    private fun drawResetText(drawContext: DrawContext, x: Int, y: Int): Int {
        return if (ComputerHost.getFaultCount(identifier) == 1) {
            drawContext.drawText(Text.translatable("alerts.flightassistant.fault.computer.reset"), x, y, advisoryColor)
        } else {
            0
        }
    }
}
