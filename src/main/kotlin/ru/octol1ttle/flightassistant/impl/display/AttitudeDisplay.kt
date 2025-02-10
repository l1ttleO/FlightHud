package ru.octol1ttle.flightassistant.impl.display

import kotlin.math.sign
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.RotationAxis
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.computer.ComputerView
import ru.octol1ttle.flightassistant.api.display.Display
import ru.octol1ttle.flightassistant.api.display.HudFrame
import ru.octol1ttle.flightassistant.api.util.ScreenSpace
import ru.octol1ttle.flightassistant.api.util.extensions.*
import ru.octol1ttle.flightassistant.config.FAConfig
import ru.octol1ttle.flightassistant.config.options.DisplayOptions


class AttitudeDisplay(computers: ComputerView) : Display(computers) {
    override fun allowedByConfig(): Boolean {
        return FAConfig.display.showAttitude != DisplayOptions.AttitudeDisplayMode.DISABLED
    }

    override fun render(drawContext: DrawContext) {
        with(drawContext) {
            matrices.push()
            matrices.translate(0, 0, -200)
            matrices.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(computers.data.roll), centerX, centerY, 0.0f)

            if (FAConfig.display.showAttitude <= DisplayOptions.AttitudeDisplayMode.HORIZON_ONLY) {
                renderHorizon()
            }
            if (FAConfig.display.showAttitude == DisplayOptions.AttitudeDisplayMode.HORIZON_AND_LADDER) {
                renderPitchBars()
                renderPitchLimits()
            }

            matrices.pop()
        }
    }

    private fun DrawContext.renderHorizon() {
        if (!FAConfig.display.drawHorizonOutsideFrame) {
            HudFrame.scissor(this)
        }

        ScreenSpace.getY(0.0f)?.let {
            val leftXEnd: Int = (centerX - halfWidth * 0.025).toInt()
            drawHorizontalLine(0, leftXEnd, it, primaryColor)

            val rightXStart: Int = (centerX + halfWidth * 0.025).toInt()
            drawHorizontalLine(rightXStart, scaledWindowWidth, it, primaryColor)
        }

        if (!FAConfig.display.drawHorizonOutsideFrame) {
            disableScissor()
        }
    }

    private fun DrawContext.renderPitchBars() {
        val step: Int = FAConfig.display.attitudeDegreeStep
        if (!FAConfig.display.drawPitchOutsideFrame) {
            HudFrame.scissor(this)
        }
        val nextUp: Int = MathHelper.roundUpToMultiple(computers.data.pitch.toInt(), step)
        for (i: Int in nextUp..90 step step) {
            drawPitchBar(i, (ScreenSpace.getY(i.toFloat()) ?: break))
        }

        val nextDown: Int = MathHelper.roundDownToMultiple(computers.data.pitch.toDouble(), step)
        for (i: Int in nextDown downTo -90 step step) {
            drawPitchBar(i, (ScreenSpace.getY(i.toFloat()) ?: break))
        }
        if (!FAConfig.display.drawPitchOutsideFrame) {
            disableScissor()
        }
    }

    private fun DrawContext.renderPitchLimits() {
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
            val y: Int = ScreenSpace.getY(max) ?: break
            matrices.push()

            matrices.translate(centerXI, y, 0) // Rotate around the middle of the arrow
            matrices.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(180.0f)) // Flip upside down
            drawMiddleAlignedText(arrowText, -1, 0, if (maxInput?.active == true) warningColor else cautionColor)

            matrices.pop()
            max += step
        }
        while (min >= -180) {
            val y: Int = ScreenSpace.getY(min) ?: break
            matrices.push()

            drawMiddleAlignedText(arrowText, centerXI, y, if (minInput?.active == true) warningColor else cautionColor)

            matrices.pop()
            min -= step
        }

        if (!FAConfig.display.drawPitchOutsideFrame) {
            disableScissor()
        }
    }

    private fun DrawContext.drawPitchBar(pitch: Int, y: Int) {
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
