package ru.octol1ttle.flightassistant.impl.display

import kotlin.math.roundToInt
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.ComputerView
import ru.octol1ttle.flightassistant.api.display.Display
import ru.octol1ttle.flightassistant.api.display.HudFrame
import ru.octol1ttle.flightassistant.api.util.extensions.*
import ru.octol1ttle.flightassistant.config.FAConfig

class SpeedDisplay(computers: ComputerView) : Display(computers) {
    override fun allowedByConfig(): Boolean {
        return FAConfig.display.showSpeedReading || FAConfig.display.showSpeedScale
    }

    override fun render(drawContext: DrawContext) {
        with(drawContext) {
            val trueX: Int = HudFrame.left
            val trueY: Int = centerYI
            if (FAConfig.display.showSpeedReading) {
                renderSpeedReading(trueX, trueY)
            }
            if (FAConfig.display.showSpeedScale) {
                renderSpeedScale(trueX, trueY)
            }
        }
    }

    private fun DrawContext.renderSpeedReading(trueX: Int, trueY: Int) {
        matrices.push()
        val (x: Int, y: Int) = scaleMatrix(READING_MATRIX_SCALE, trueX, trueY)

        val speed: Double = computers.data.forwardVelocity.length() * 20
        val color: Int =
            if (speed <= 0.0) warningColor
            else primaryColor

        val text: String = speed.roundToInt().toString()
        val width: Int = getTextWidth(text) + 4
        val halfHeight = 6
        val textY: Int = y - 4

        drawBorder(x - width, y - halfHeight, width + 1, halfHeight * 2 - 1, color)
        drawRightAlignedText(text, x - 1, textY, color)

        matrices.pop()
    }

    private fun DrawContext.renderSpeedScale(x: Int, y: Int) {
        val speed: Double = computers.data.forwardVelocity.length() * 20
        val color: Int =
            if (speed <= 0.0) warningColor
            else primaryColor

        val minY: Int = HudFrame.top
        val maxY: Int = (y + fontHeight * (speed + 1)).toInt().coerceIn(minY - 1..HudFrame.bottom)

        drawVerticalLine(x, minY, maxY, color)

        enableScissor(0, minY, scaledWindowWidth, maxY + 1)

        enableScissor(0, minY, scaledWindowWidth, (if (FAConfig.display.showSpeedReading) y - 6 * READING_MATRIX_SCALE else maxY).toInt() + 1)
        drawHorizontalLine(x - 20, x, y, color)
        drawHorizontalLine(x - 35, x, minY, color)
        for (i: Int in speed.toInt()..speed.toInt() + 100) {
            if (!drawSpeedLine(x, y, i, speed, color)) {
                break
            }
        }
        disableScissor()

        enableScissor(
            0,
            (if (FAConfig.display.showSpeedReading) y + 5 * READING_MATRIX_SCALE else minY).toInt(),
            scaledWindowWidth,
            maxY + 1
        )
        drawHorizontalLine(x - 35, x, maxY, color)
        for (i: Int in speed.roundToInt() downTo 0) {
            if (!drawSpeedLine(x, y, i, speed, color)) {
                break
            }
        }
        disableScissor()

        disableScissor()
    }

    private fun DrawContext.drawSpeedLine(x: Int, y: Int, speed: Int, currentSpeed: Double, color: Int): Boolean {
        val textY: Int = (y + fontHeight * (currentSpeed - speed)).toInt()
        if (textY < HudFrame.top - 100 || textY > HudFrame.bottom + 100) {
            return false
        }
        drawHorizontalLine(x - 5, x, textY, color)
        if (speed % 5 == 0) {
            drawRightAlignedText(speed.toString(), x - 6, textY - 3, color)
        }

        return true
    }

    override fun renderFaulted(drawContext: DrawContext) {
        with(drawContext) {
            drawRightAlignedText(
                Text.translatable("short.flightassistant.speed"),
                HudFrame.left, centerYI - 5, warningColor
            )
        }
    }

    companion object {
        private const val READING_MATRIX_SCALE: Float = 1.5f
        val ID: Identifier = FlightAssistant.id("speed")
    }
}
