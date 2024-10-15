package ru.octol1ttle.flightassistant.impl.alert.fault.computer

import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.api.alert.*
import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.api.util.*
import ru.octol1ttle.flightassistant.impl.computer.ComputerHost

class ComputerFaultAlert(val identifier: Identifier) : Alert() {
    override val priority: AlertPriority
        get() = AlertPriority.MASTER_WARNING

    override fun shouldActivate(computers: ComputerAccess): Boolean {
        return ComputerHost.isFaulted(identifier)
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
            Text.translatable("alerts.flightassistant.fault.computer.$identifier"),
            firstLineX,
            y,
            soundPlaying,
            warningColor
        )
        return 1
    }
}
