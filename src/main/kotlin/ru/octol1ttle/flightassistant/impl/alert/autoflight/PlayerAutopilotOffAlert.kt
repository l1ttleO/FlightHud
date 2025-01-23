package ru.octol1ttle.flightassistant.impl.alert.autoflight

import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import ru.octol1ttle.flightassistant.api.alert.Alert
import ru.octol1ttle.flightassistant.api.alert.AlertData
import ru.octol1ttle.flightassistant.api.alert.ECAMAlert
import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.api.util.autoflight
import ru.octol1ttle.flightassistant.api.util.drawText
import ru.octol1ttle.flightassistant.api.util.warningColor

class PlayerAutopilotOffAlert: Alert(), ECAMAlert {
    override val data: AlertData = AlertData.PLAYER_AUTOPILOT_OFF
    private var age: Int = 0
    private var wasAutopilot: Boolean = false

    override fun shouldActivate(computers: ComputerAccess): Boolean {
        if (age > 60) {
            wasAutopilot = false
            return false
        }

        if (computers.autoflight.autopilot) {
            wasAutopilot = true
        }
        val autopilotOff: Boolean = wasAutopilot && !computers.autoflight.autopilot
        if (autopilotOff) {
            age++
        }

        return autopilotOff
    }

    override fun render(drawContext: DrawContext, computers: ComputerAccess, firstLineX: Int, otherLinesX: Int, firstLineY: Int): Int {
        return drawContext.drawText(Text.translatable("alerts.flightassistant.autoflight.autopilot_off"), firstLineX, firstLineY, warningColor)
    }
}
