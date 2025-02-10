package ru.octol1ttle.flightassistant.impl.alert.thrust

import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import ru.octol1ttle.flightassistant.api.alert.Alert
import ru.octol1ttle.flightassistant.api.alert.AlertData
import ru.octol1ttle.flightassistant.api.alert.ECAMAlert
import ru.octol1ttle.flightassistant.api.computer.ComputerView
import ru.octol1ttle.flightassistant.api.util.extensions.advisoryColor
import ru.octol1ttle.flightassistant.api.util.extensions.cautionColor
import ru.octol1ttle.flightassistant.api.util.extensions.drawText

class ThrustLockedAlert(computers: ComputerView) : Alert(computers), ECAMAlert {
    override val data: AlertData = AlertData.THRUST_LOCKED

    override fun shouldActivate(): Boolean {
        return computers.thrust.thrustLocked
    }

    override fun render(drawContext: DrawContext, firstLineX: Int, otherLinesX: Int, firstLineY: Int): Int {
        var i = 0
        i += drawContext.drawText(Text.translatable("alerts.flightassistant.thrust.locked"), firstLineX, firstLineY, cautionColor)
        i += drawContext.drawText(Text.translatable("alerts.flightassistant.thrust.locked.use_keys"), otherLinesX, firstLineY + 11, advisoryColor)
        return i
    }
}
