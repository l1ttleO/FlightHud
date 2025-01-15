package ru.octol1ttle.flightassistant.impl.alert.fault.computer

import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.api.alert.Alert
import ru.octol1ttle.flightassistant.api.alert.AlertData
import ru.octol1ttle.flightassistant.api.alert.ECAMAlert
import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.api.util.drawText
import ru.octol1ttle.flightassistant.impl.computer.ComputerHost

class ComputerFaultAlert(private val identifier: Identifier, private val alertText: Text, override val data: AlertData = AlertData.MASTER_CAUTION) : Alert(), ECAMAlert {
    override fun shouldActivate(computers: ComputerAccess): Boolean {
        return ComputerHost.isFaulted(identifier)
    }

    override fun render(drawContext: DrawContext, computers: ComputerAccess, firstLineX: Int, x: Int, y: Int): Int {
        drawContext.drawText(alertText, firstLineX, y, data.colorSupplier.invoke())
        return 1
    }
}
