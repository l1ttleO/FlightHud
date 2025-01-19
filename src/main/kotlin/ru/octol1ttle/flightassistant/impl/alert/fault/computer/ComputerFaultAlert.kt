package ru.octol1ttle.flightassistant.impl.alert.fault.computer

import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.api.alert.Alert
import ru.octol1ttle.flightassistant.api.alert.AlertData
import ru.octol1ttle.flightassistant.api.alert.ECAMAlert
import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.api.util.advisoryColor
import ru.octol1ttle.flightassistant.api.util.drawText
import ru.octol1ttle.flightassistant.impl.computer.ComputerHost

class ComputerFaultAlert(private val identifier: Identifier, private val alertText: Text, private val extraTexts: Collection<Text>? = null, override val data: AlertData = AlertData.MASTER_CAUTION) : Alert(), ECAMAlert {
    override fun shouldActivate(computers: ComputerAccess): Boolean {
        return ComputerHost.isFaulted(identifier)
    }

    override fun render(drawContext: DrawContext, computers: ComputerAccess, firstLineX: Int, x: Int, y: Int): Int {
        val color: Int = data.colorSupplier.invoke()
        var i = 0
        var newY: Int = y

        i += drawContext.drawText(alertText, firstLineX, y, color)
        newY += 11

        if (extraTexts != null) {
            for (text in extraTexts) {
                i += drawContext.drawText(text, x, newY, advisoryColor)
                newY += 10
            }
        }

        i += drawResetText(drawContext, x, newY)
        return i
    }

    private fun drawResetText(drawContext: DrawContext, x: Int, y: Int): Int {
        return if (ComputerHost.getFaultCount(identifier) == 1) {
            drawContext.drawText(Text.translatable("alerts.flightassistant.fault.computer.reset"), x, y, advisoryColor)
        } else {
            drawContext.drawText(Text.translatable("alerts.flightassistant.fault.computer.turn_off"), x, y, advisoryColor)
        }
    }
}
