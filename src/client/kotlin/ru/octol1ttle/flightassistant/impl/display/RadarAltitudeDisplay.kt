package ru.octol1ttle.flightassistant.impl.display

import kotlin.math.roundToInt
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.*
import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.api.display.*
import ru.octol1ttle.flightassistant.api.util.*
import ru.octol1ttle.flightassistant.config.FAConfig

class RadarAltitudeDisplay : Display() {
    override fun enabled(): Boolean {
        return FAConfig.display.showAltitudeReading || FAConfig.display.showAltitudeScale
    }

    override fun render(drawContext: DrawContext, computers: ComputerAccess) {
        val groundLevel: Double? = computers.data.groundLevel
        if (groundLevel != null && groundLevel > computers.data.position.y) {
            renderFaulted(drawContext)
            return
        }

        with(drawContext) {
            val x: Int = HudFrame.right - 1
            val y: Int = HudFrame.bottom + 2

            val altType: MutableText
            val altString: String
            val color: Int
            if (groundLevel != null) {
                altType = Text.translatable("short.flightassistant.ground")
                altString = (computers.data.position.y - groundLevel).roundToInt().toString()
                color = primaryColor
            } else {
                altType = Text.translatable("short.flightassistant.void")
                val altValue: Int = (computers.data.position.y - computers.data.voidLevel).roundToInt()
                altString = altValue.toString()
                color = if (altValue <= 0) warningColor else cautionColor
            }
            val xOffset: Int = getTextWidth(altType) + 1

            drawText(altType, x, y + 2, color)
            drawBorder(x + xOffset, y, getTextWidth(altString) + 5, 11, color)
            drawText(altString, x + xOffset + 3, y + 2, color)
        }
    }

    override fun renderFaulted(drawContext: DrawContext) {
        with(drawContext) {
            drawText(
                Text.translatable("short.flightassistant.radar_altitude"),
                HudFrame.right - 1,
                HudFrame.bottom + 4,
                warningColor
            )
        }
    }

    companion object {
        val ID: Identifier = FlightAssistant.id("radar_altitude")
    }
}
