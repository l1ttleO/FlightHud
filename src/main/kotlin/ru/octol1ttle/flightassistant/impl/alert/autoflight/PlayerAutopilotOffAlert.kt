package ru.octol1ttle.flightassistant.impl.alert.autoflight

import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import ru.octol1ttle.flightassistant.api.alert.Alert
import ru.octol1ttle.flightassistant.api.alert.AlertData
import ru.octol1ttle.flightassistant.api.alert.ECAMAlert
import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.api.util.FATickCounter
import ru.octol1ttle.flightassistant.api.util.extensions.autoflight
import ru.octol1ttle.flightassistant.api.util.extensions.drawText
import ru.octol1ttle.flightassistant.api.util.extensions.warningColor

class PlayerAutopilotOffAlert: Alert(), ECAMAlert {
    override val data: AlertData = AlertData.PLAYER_AUTOPILOT_OFF
    private var age: Int = 0
    private var wasAutopilot: Boolean = false

    override fun shouldActivate(computers: ComputerAccess): Boolean {
        if (computers.autoflight.autopilotAlert || age > 80) {
            wasAutopilot = false
            age = 0
            return false
        }

        if (computers.autoflight.autopilot) {
            wasAutopilot = true
        }
        val autopilotOff: Boolean = wasAutopilot && !computers.autoflight.autopilot
        if (autopilotOff) {
            age += FATickCounter.ticksPassed
        }

        return autopilotOff
    }

    override fun render(drawContext: DrawContext, computers: ComputerAccess, firstLineX: Int, otherLinesX: Int, firstLineY: Int): Int {
        return drawContext.drawText(Text.translatable("alerts.flightassistant.autoflight.autopilot_off"), firstLineX, firstLineY, warningColor)
    }
}
