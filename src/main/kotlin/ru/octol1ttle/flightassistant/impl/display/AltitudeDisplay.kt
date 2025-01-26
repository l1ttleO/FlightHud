package ru.octol1ttle.flightassistant.impl.display

import kotlin.math.roundToInt
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.api.display.Display
import ru.octol1ttle.flightassistant.api.display.HudFrame
import ru.octol1ttle.flightassistant.api.util.extensions.*
import ru.octol1ttle.flightassistant.config.FAConfig

class AltitudeDisplay : Display() {
    override fun allowedByConfig(): Boolean {
        return FAConfig.display.showAltitudeReading || FAConfig.display.showAltitudeScale
    }

    override fun render(drawContext: DrawContext, computers: ComputerAccess) {
        with(drawContext) {
            val trueX: Int = HudFrame.right
            val trueY: Int = centerYI
            if (FAConfig.display.showAltitudeReading) {
                renderAltitudeReading(trueX, trueY, computers)
            }
            if (FAConfig.display.showAltitudeScale) {
                renderAltitudeScale(trueX - 1, trueY, computers)
            }
        }
    }

    private fun DrawContext.renderAltitudeReading(trueX: Int, trueY: Int, computers: ComputerAccess) {
        matrices.push()
        val (x: Int, y: Int) = scaleMatrix(READING_MATRIX_SCALE, trueX, trueY)

        val altitude: Double = computers.data.altitude
        val text: String = altitude.roundToInt().toString()

        val width: Int = getTextWidth(text) + 5
        val halfHeight = 6
        drawBorder(x, y - halfHeight, width, halfHeight * 2 - 1, primaryColor)

        val textY: Int = y - 4
        drawText(text, x + 3, textY, primaryColor)

        matrices.pop()
    }

    private fun DrawContext.renderAltitudeScale(x: Int, y: Int, computers: ComputerAccess) {
        val altitude: Double = computers.data.altitude

        val minY: Int = HudFrame.top
        val maxY: Int =
            (y + 2 * (altitude - computers.data.world.bottomY + 1)).toInt().coerceIn(minY - 1..HudFrame.bottom)

        drawVerticalLine(x, minY, maxY, primaryColor)

        enableScissor(0, minY, scaledWindowWidth, maxY + 1)

        val scissorMaxY: Int = (if (FAConfig.display.showAltitudeReading) y - 6 * READING_MATRIX_SCALE else maxY).toInt()
        enableScissor(0, minY, scaledWindowWidth, scissorMaxY + 1)
        drawHorizontalLine(x, x + 30, y, primaryColor)
        drawHorizontalLine(x, x + 35, minY, primaryColor)
        if (maxY < scissorMaxY) {
            drawHorizontalLine(x, x + 35, maxY, primaryColor)
        }
        val altitudeRoundedUp: Int = MathHelper.roundUpToMultiple(altitude.toInt(), 5)
        for (i: Int in altitudeRoundedUp..altitudeRoundedUp + 1000 step 5) {
            if (!drawAltitudeLine(x, y, i, altitude)) {
                break
            }
        }
        disableScissor()

        enableScissor(0, (if (FAConfig.display.showAltitudeReading) y + 5 * READING_MATRIX_SCALE else minY).toInt(), scaledWindowWidth, maxY + 1)
        drawHorizontalLine(x, x + 35, maxY, primaryColor)
        val altitudeRoundedDown: Int = MathHelper.roundDownToMultiple(altitude, 5)
        for (i: Int in altitudeRoundedDown downTo (altitudeRoundedDown - 1000).coerceAtLeast(computers.data.world.bottomY) step 5) {
            if (!drawAltitudeLine(x, y, i, altitude)) {
                break
            }
        }
        disableScissor()

        disableScissor()
    }

    private fun DrawContext.drawAltitudeLine(x: Int, y: Int, altitude: Int, currentAltitude: Double): Boolean {
        val textY: Int = (y + 2 * (currentAltitude - altitude)).toInt()
        if (textY < HudFrame.top - 100 || textY > HudFrame.bottom + 100) {
            return false
        }
        drawHorizontalLine(x + 5, x, textY, primaryColor)
        if (altitude % 20 == 0) {
            drawText(altitude.toString(), x + 8, textY - 3, primaryColor)
        }

        return true
    }

    override fun renderFaulted(drawContext: DrawContext) {
        with(drawContext) {
            drawText(Text.translatable("short.flightassistant.altitude"), HudFrame.right, centerYI - 5, warningColor)
        }
    }

    companion object {
        private const val READING_MATRIX_SCALE: Float = 1.5f
        val ID: Identifier = FlightAssistant.id("altitude")
    }
}
