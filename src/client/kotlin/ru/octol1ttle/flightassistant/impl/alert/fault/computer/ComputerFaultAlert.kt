package ru.octol1ttle.flightassistant.impl.alert.fault.computer

import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.api.alert.*
import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.api.util.*
import ru.octol1ttle.flightassistant.impl.computer.ComputerHost

class ComputerFaultAlert(val identifier: Identifier, private val alertText: Text) : Alert() {
    override val data: AlertData
        get() = AlertData.MASTER_CAUTION

    override fun shouldActivate(computers: ComputerAccess): Boolean {
        return ComputerHost.isFaulted(identifier)
    }

    override fun render(drawContext: DrawContext, computers: ComputerAccess, firstLineX: Int, x: Int, y: Int): Int {
        drawContext.drawText(alertText, firstLineX, y, cautionColor)
        return 1
    }
}
