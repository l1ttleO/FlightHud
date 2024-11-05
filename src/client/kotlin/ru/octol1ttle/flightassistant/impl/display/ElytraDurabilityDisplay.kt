package ru.octol1ttle.flightassistant.impl.display

import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.api.display.*
import ru.octol1ttle.flightassistant.api.util.*
import ru.octol1ttle.flightassistant.config.FAConfig

class ElytraDurabilityDisplay : Display() {
    override fun enabled(): Boolean {
        return FAConfig.display.showElytraDurability
    }

    override fun render(drawContext: DrawContext, computers: ComputerAccess) {
        with(drawContext) {
            val x: Int = centerXI
            val y: Int = HudFrame.bottom + 1

            val text: Text =
                computers.elytra.formatDurability(FAConfig.display.elytraDurabilityUnits, computers.data.player)
                    ?: return

            val remainingFlightTime: Int = computers.elytra.getRemainingFlightTime(computers.data.player)!!
            val color: Int = when {
                remainingFlightTime < 30 -> warningColor
                remainingFlightTime < 90 -> cautionColor
                else -> primaryColor
            }

            drawRightAlignedText(Text.translatable("short.flightassistant.elytra"), x - 15, y + 2, color)
            drawBorder(x - 14, y, 29, 11, color)
            drawMiddleAlignedText(text, x, y + 2, color)
        }
    }

    override fun renderFaulted(drawContext: DrawContext) {
        with(drawContext) {
            drawMiddleAlignedText(
                Text.translatable("short.flightassistant.elytra_durability"),
                centerXI,
                HudFrame.bottom + 1,
                warningColor
            )
        }
    }

    companion object {
        val ID: Identifier = FlightAssistant.id("elytra_durability")
    }
}
