package ru.octol1ttle.flightassistant.impl.alert.elytra

import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import ru.octol1ttle.flightassistant.api.alert.*
import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.api.util.*

class ElytraDurabilityLowAlert : Alert() {
    override val priority: AlertPriority
        get() = AlertPriority.MASTER_CAUTION

    override fun shouldActivate(computers: ComputerAccess): Boolean {
        val remainingFlightTime: Int = computers.elytra.getRemainingFlightTime(computers.data.player) ?: return false
        return remainingFlightTime in 60..<120
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
            Text.translatable("alerts.flightassistant.elytra.low_durability"),
            firstLineX,
            y,
            true,
            cautionColor
        )
        return 1
    }
}
