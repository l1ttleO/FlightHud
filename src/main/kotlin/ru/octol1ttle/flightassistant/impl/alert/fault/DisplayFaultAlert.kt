package ru.octol1ttle.flightassistant.impl.alert.fault

import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.api.alert.Alert
import ru.octol1ttle.flightassistant.api.alert.AlertData
import ru.octol1ttle.flightassistant.api.alert.ECAMAlert
import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.api.util.advisoryColor
import ru.octol1ttle.flightassistant.api.util.cautionColor
import ru.octol1ttle.flightassistant.api.util.drawText
import ru.octol1ttle.flightassistant.impl.display.HudDisplayHost

class DisplayFaultAlert(val identifier: Identifier) : Alert(), ECAMAlert {
    override val data: AlertData
        get() = AlertData.MASTER_CAUTION

    override fun shouldActivate(computers: ComputerAccess): Boolean {
        return HudDisplayHost.isFaulted(identifier)
    }

    override fun render(drawContext: DrawContext, computers: ComputerAccess, firstLineX: Int, x: Int, y: Int): Int {
        var i = 0
        i += drawContext.drawText(Text.translatable("alerts.flightassistant.fault.hud.$identifier"), firstLineX, y, cautionColor)
        i +=
            if (HudDisplayHost.countFaults(identifier) == 1) {
                drawContext.drawText(Text.translatable("alerts.flightassistant.fault.computer.reset"), x, y + 11, advisoryColor)
            } else {
                drawContext.drawText(Text.translatable("alerts.flightassistant.fault.computer.turn_off"), x, y + 11, advisoryColor)
            }
        return i
    }
}
