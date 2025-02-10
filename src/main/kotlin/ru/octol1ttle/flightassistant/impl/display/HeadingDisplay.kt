package ru.octol1ttle.flightassistant.impl.display

import kotlin.math.roundToInt
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.RotationAxis
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.ComputerView
import ru.octol1ttle.flightassistant.api.display.Display
import ru.octol1ttle.flightassistant.api.display.HudFrame
import ru.octol1ttle.flightassistant.api.util.ScreenSpace
import ru.octol1ttle.flightassistant.api.util.extensions.*
import ru.octol1ttle.flightassistant.config.FAConfig

class HeadingDisplay(computers: ComputerView) : Display(computers) {
    override fun allowedByConfig(): Boolean {
        return FAConfig.display.showHeadingReading || FAConfig.display.showHeadingScale
    }

    override fun render(drawContext: DrawContext) {
        with(drawContext) {
            if (FAConfig.display.showHeadingReading) {
                val x: Int = centerXI
                val y: Int = HudFrame.top + 1

                drawBorder(x - 11, y, 23, 11, primaryColor)
                drawMiddleAlignedText("%03d".format(computers.data.heading.roundToInt()), x, y + 2, primaryColor)
            }

            if (FAConfig.display.showHeadingScale) {
                if (!FAConfig.display.drawHorizonOutsideFrame) {
                    HudFrame.scissor(this)
                }
                matrices.push()
                matrices.translate(0, 0, -200)
                matrices.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(computers.data.roll), centerX, centerY, 0.0f)

                ScreenSpace.getY(0.0f)?.let {
                    drawHeading(it)
                }

                matrices.pop()
                if (!FAConfig.display.drawHorizonOutsideFrame) {
                    disableScissor()
                }
            }
        }
    }

    private fun DrawContext.drawHeading(y: Int) {
        val step: Int = FAConfig.display.headingDegreeStep

        val nextDown: Int = MathHelper.roundDownToMultiple(computers.data.heading.toDouble(), step)
        for (i: Int in nextDown downTo -360 step step) {
            val heading: Int = i % 360
            val x: Int = ScreenSpace.getX(heading.toFloat()) ?: break
            val text: String = (if (i > 0) i else 360 + i).toString()

            drawHeadingLine(x, y, heading, text)
        }

        val nextUp: Int = MathHelper.roundUpToMultiple(computers.data.heading.toInt(), step)
        for (i: Int in nextUp..720 step step) {
            val heading: Int = i % 360
            val x: Int = ScreenSpace.getX(heading.toFloat()) ?: break
            val text: String = (if (heading == 0) 360 else heading).toString()

            drawHeadingLine(x, y, heading, text)
        }
    }

    private fun DrawContext.drawHeadingLine(x: Int, y: Int, heading: Int, headingText: String) {
        drawVerticalLine(x, y, y - 3, primaryColor)
        drawMiddleAlignedText(headingText, x, y - 12, primaryColor)

        if (heading % 90 == 0) {
            drawMiddleAlignedText(
                when (heading) {
                    0 -> "-Z"
                    90 -> "+X"
                    180 -> "+Z"
                    270 -> "-X"
                    else -> throw IllegalArgumentException("Degree range out of bounds: $heading")
                }, x, y + 3, primaryColor
            )
        }
    }

    override fun renderFaulted(drawContext: DrawContext) {
        with(drawContext) {
            drawMiddleAlignedText(Text.translatable("short.flightassistant.heading"), centerXI, HudFrame.top + 1, warningColor)
        }
    }

    companion object {
        val ID: Identifier = FlightAssistant.id("heading")
    }
}
