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
import ru.octol1ttle.flightassistant.impl.computer.safety.VoidProximityComputer

class RadarAltitudeDisplay : Display() {
    override fun allowedByConfig(): Boolean {
        return FAConfig.display.showRadarAltitude
    }

    override fun render(drawContext: DrawContext, computers: ComputerAccess) {
        val groundLevel: Double? = computers.data.groundLevel
        if (!computers.data.isCurrentChunkLoaded || groundLevel != null && groundLevel > computers.data.altitude) {
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
                altString = (computers.data.altitude - groundLevel).roundToInt().toString()
                color = primaryColor
            } else {
                altType = Text.translatable("short.flightassistant.void")
                altString = (computers.data.altitude - computers.data.voidLevel).roundToInt().toString()
                color = when (computers.voidProximity.status) {
                    VoidProximityComputer.Status.REACHED_DAMAGE_ALTITUDE -> warningColor
                    VoidProximityComputer.Status.APPROACHING_DAMAGE_ALTITUDE -> cautionColor
                    else -> primaryColor
                }
            }
            val xOffset: Int = getTextWidth(altType) + 1

            drawText(altType, x - xOffset, y + 2, color)
            drawBorder(x, y, getTextWidth(altString) + 5, 11, color)
            drawText(altString, x + 3, y + 2, color)
        }
    }

    override fun renderFaulted(drawContext: DrawContext) {
        with(drawContext) {
            drawText(Text.translatable("short.flightassistant.radar_altitude"), HudFrame.right - 1, HudFrame.bottom + 4, warningColor)
        }
    }

    companion object {
        val ID: Identifier = FlightAssistant.id("radar_altitude")
    }
}
