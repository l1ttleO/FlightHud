package ru.octol1ttle.flightassistant.impl.display

import java.util.Objects
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.api.computer.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.display.*
import ru.octol1ttle.flightassistant.api.util.*
import ru.octol1ttle.flightassistant.config.FAConfig

class AutomationModesDisplay : Display() {
    private val thrustDisplay: ModeDisplay = ModeDisplay(1) { computers -> computers.thrust.activeThrustInput }
    private val pitchDisplay: ModeDisplay = ModeDisplay(2) { computers -> computers.pitch.activePitchInput }

    override fun enabled(): Boolean {
        return FAConfig.display.showAutomationModes
    }

    override fun render(drawContext: DrawContext, computers: ComputerAccess) {
        thrustDisplay.render(drawContext, computers)
        pitchDisplay.render(drawContext, computers)
    }

    override fun renderFaulted(drawContext: DrawContext) {
        with(drawContext) {
            val x: Int = centerXI
            val y: Int = HudFrame.top - 9

            drawMiddleAlignedText(Text.translatable("short.flightassistant.automation_modes"), x, y, warningColor)
        }
    }

    companion object {
        val ID: Identifier = FlightAssistant.id("automation_modes")
        const val TOTAL_MODES: Float = 5.0f
    }

    class ModeDisplay(private val order: Int, private val inputSupplier: (ComputerAccess) -> ControlInput?) {
        private var lastText: Text? = null
        private var textChangeTicks: Int = 0

        fun render(drawContext: DrawContext, computers: ComputerAccess) {
            val leftX: Int = (HudFrame.left + HudFrame.width * ((order - 1) / TOTAL_MODES)).toInt()
            val rightX: Int = (HudFrame.left + HudFrame.width * (order / TOTAL_MODES)).toInt()
            val y: Int = HudFrame.top - 9

            val input: ControlInput? = inputSupplier.invoke(computers)
            val text: Text? = input?.text
            if (input?.active != false && !Objects.equals(text, lastText)) {
                textChangeTicks = FATickCounter.totalTicks
                lastText = text
            }

            if (text != null) {
                drawContext.drawMiddleAlignedText(if (input.active) text else text.copy().styled { it.withStrikethrough(true) }, (leftX + rightX) / 2, y, if (input.active) primaryColor else 0xFFFFFFFF.toInt())
            }
            if (FATickCounter.totalTicks <= textChangeTicks + (if (text == null) 60 else 100)) {
                drawContext.drawBorder(leftX + 1, y - 2, rightX - leftX - 1, 11, 0xFFFFFFFF.toInt())
            }
        }
    }
}
