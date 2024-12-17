package ru.octol1ttle.flightassistant.impl.display

import kotlin.math.sign
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.*
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.api.computer.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.display.*
import ru.octol1ttle.flightassistant.api.util.*
import ru.octol1ttle.flightassistant.config.FAConfig
import ru.octol1ttle.flightassistant.config.options.DisplayOptions


class AttitudeDisplay : Display() {
    override fun enabled(): Boolean {
        return FAConfig.display.showAttitude != DisplayOptions.AttitudeDisplayMode.DISABLED
    }

    override fun render(drawContext: DrawContext, computers: ComputerAccess) {
        with(drawContext) {
            matrices.push()
            matrices.translate(0, 0, -200)
            matrices.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(computers.data.roll), centerX, centerY, 0.0f)

            if (FAConfig.display.showAttitude <= DisplayOptions.AttitudeDisplayMode.HORIZON_ONLY) {
                renderHorizon(computers)
            }
            if (FAConfig.display.showAttitude == DisplayOptions.AttitudeDisplayMode.HORIZON_AND_LADDER) {
                renderPitchBars(computers)
                renderPitchLimits(computers)
            }

            matrices.pop()
        }
    }

    private fun DrawContext.renderHorizon(computers: ComputerAccess) {
        if (!FAConfig.display.drawHorizonOutsideFrame) {
            HudFrame.scissor(this)
        }

        getScreenSpaceY(0.0f)?.let {
            val leftXEnd: Int = (centerX - halfWidth * 0.025).toInt()
            drawHorizontalLine(0, leftXEnd, it, primaryColor)

            val rightXStart: Int = (centerX + halfWidth * 0.025).toInt()
            drawHorizontalLine(rightXStart, scaledWindowWidth, it, primaryColor)

            if (FAConfig.display.showHeadingScale) {
                drawHeading(computers, it)
            }
        }

        if (!FAConfig.display.drawHorizonOutsideFrame) {
            disableScissor()
        }
    }

    private fun DrawContext.renderPitchBars(computers: ComputerAccess) {
        val step: Int = FAConfig.display.attitudeDegreeStep
        if (!FAConfig.display.drawPitchOutsideFrame) {
            HudFrame.scissor(this)
        }
        val nextUp: Int = MathHelper.roundUpToMultiple(computers.data.pitch.toInt(), step)
        for (i: Int in nextUp..90 step step) {
            drawPitchBar(computers, i, (getScreenSpaceY(i.toFloat()) ?: break))
        }

        val nextDown: Int = MathHelper.roundDownToMultiple(computers.data.pitch.toDouble(), step)
        for (i: Int in nextDown downTo -90 step step) {
            drawPitchBar(computers, i, (getScreenSpaceY(i.toFloat()) ?: break))
        }
        if (!FAConfig.display.drawPitchOutsideFrame) {
            disableScissor()
        }
    }

    private fun DrawContext.renderPitchLimits(computers: ComputerAccess) {
        val step: Int = FAConfig.display.attitudeDegreeStep / 2
        if (!FAConfig.display.drawPitchOutsideFrame) {
            HudFrame.scissor(this)
        }
        val arrowText: Text = Text.literal("^")

        val maxInput: ControlInput? = computers.pitch.maximumPitch
        val minInput: ControlInput? = computers.pitch.minimumPitch
        var max: Float = maxInput?.target ?: 90.0f
        var min: Float = (minInput?.target ?: -90.0f).coerceAtMost(max)

        while (max <= 180) {
            val y: Int = getScreenSpaceY(max) ?: break
            matrices.push()

            matrices.translate(centerXI, y, 0) // Rotate around the middle of the arrow
            matrices.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(180.0f)) // Flip upside down
            drawMiddleAlignedText(arrowText, -1, 0, if (maxInput?.active == true) warningColor else cautionColor)

            matrices.pop()
            max += step
        }
        while (min >= -180) {
            val y: Int = getScreenSpaceY(min) ?: break
            matrices.push()

            drawMiddleAlignedText(arrowText, centerXI, y, if (minInput?.active == true) warningColor else cautionColor)

            matrices.pop()
            min -= step
        }

        if (!FAConfig.display.drawPitchOutsideFrame) {
            disableScissor()
        }
    }

    private fun DrawContext.drawHeading(computers: ComputerAccess, y: Int) {
        val step: Int = FAConfig.display.headingDegreeStep

        val nextDown: Int = MathHelper.roundDownToMultiple(computers.data.heading.toDouble(), step)
        for (i: Int in nextDown downTo -360 step step) {
            val heading: Int = i % 360
            val x: Int = getScreenSpaceX(heading.toFloat()) ?: break
            val text: String = (if (i > 0) i else 360 + i).toString()

            drawHeadingLine(x, y, heading, text)
        }

        val nextUp: Int = MathHelper.roundUpToMultiple(computers.data.heading.toInt(), step)
        for (i: Int in nextUp..720 step step) {
            val heading: Int = i % 360
            val x: Int = getScreenSpaceX(heading.toFloat()) ?: break
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

    private fun DrawContext.drawPitchBar(computers: ComputerAccess, pitch: Int, y: Int) {
        if (pitch == 0) return
        val min: ControlInput? = computers.pitch.minimumPitch
        val max: ControlInput? = computers.pitch.maximumPitch
        val color: Int =
            if (max != null && pitch >= max.target)
                if (max.active) warningColor else cautionColor
            else if (min != null && pitch <= min.target)
                if (min.active) warningColor else cautionColor
            else
                primaryColor

        val leftXEnd: Int = (centerX - halfWidth * 0.05).toInt()
        val leftXStart: Int = (leftXEnd - halfWidth * 0.075).toInt()
        drawRightAlignedText(pitch.toString(), leftXStart - 2, if (pitch > 0) y else y - 4, color)
        drawVerticalLine(leftXStart, y, y + 5 * pitch.sign, color)
        drawHorizontalLineDashed(leftXStart, leftXEnd, y, if (pitch < 0) 3 else 1, color)

        val rightXStart: Int = (centerX + halfWidth * 0.05).toInt()
        val rightXEnd: Int = (rightXStart + halfWidth * 0.075).toInt()
        drawHorizontalLineDashed(rightXStart, rightXEnd, y, if (pitch < 0) 3 else 1, color)
        drawVerticalLine(rightXEnd, y, y + 5 * pitch.sign, color)
        drawText(pitch.toString(), rightXEnd + 4, if (pitch > 0) y else y - 4, color)
    }

    override fun renderFaulted(drawContext: DrawContext) {
        with(drawContext) {
            drawMiddleAlignedText(Text.translatable("short.flightassistant.attitude"), centerXI, centerYI - 16, warningColor)
        }
    }

    companion object {
        val ID: Identifier = FlightAssistant.id("attitude")
    }
}
