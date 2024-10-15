package ru.octol1ttle.flightassistant.impl.alert.elytra

import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import ru.octol1ttle.flightassistant.api.alert.*
import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.api.util.*
import ru.octol1ttle.flightassistant.api.util.FATickCounter.totalTicks

class ElytraDurabilityCriticalAlert : Alert() {
    override val priority: AlertPriority
        get() = AlertPriority.MASTER_WARNING

    override fun shouldActivate(computers: ComputerAccess): Boolean {
        val remainingFlightTime: Int = computers.elytra.getRemainingFlightTime(computers.data.player) ?: return false
        return remainingFlightTime < 60
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
            Text.translatable("alerts.flightassistant.elytra.critical_durability"),
            firstLineX,
            y,
            totalTicks % 20 >= 10,
            warningColor
        )
        return 1
    }
}
