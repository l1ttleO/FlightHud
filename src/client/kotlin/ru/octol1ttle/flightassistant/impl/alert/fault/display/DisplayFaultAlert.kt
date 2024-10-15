package ru.octol1ttle.flightassistant.impl.alert.fault.display

import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.api.alert.*
import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.api.util.*
import ru.octol1ttle.flightassistant.impl.display.HudDisplayHost

class DisplayFaultAlert(val identifier: Identifier) : Alert() {
    override val priority: AlertPriority
        get() = AlertPriority.MASTER_CAUTION

    override fun shouldActivate(computers: ComputerAccess): Boolean {
        return HudDisplayHost.isFaulted(identifier)
    }

    override fun render(
        drawContext: DrawContext,
        computers: ComputerAccess,
        firstLineX: Int,
        x: Int,
        y: Int,
        soundPlaying: Boolean
    ): Int {
        drawContext.drawHighlightedText(
            Text.translatable("alerts.flightassistant.fault.hud.$identifier"),
            firstLineX,
            y,
            soundPlaying,
            cautionColor
        )
        return 1
    }
}
