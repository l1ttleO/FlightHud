package ru.octol1ttle.flightassistant.impl.display

import java.util.Objects
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.api.display.*
import ru.octol1ttle.flightassistant.api.util.*
import ru.octol1ttle.flightassistant.config.FAConfig

class AutomationModesDisplay : Display() {
    private val thrustDisplay: ModeDisplay = ModeDisplay(1) { computers -> computers.thrust.thrustMode }
    private val pitchDisplay: ModeDisplay = ModeDisplay(2) { computers -> computers.pitch.pitchMode }

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

            drawMiddleAlignedText(Text.translatable("short.flightassistant.automation_mode"), x, y, warningColor)
        }
    }

    companion object {
        val ID: Identifier = FlightAssistant.id("automation_mode")
        const val TOTAL_MODES: Float = 5.0f
    }

    class ModeDisplay(private val order: Int, private val textSupplier: (ComputerAccess) -> Text?) {
        private var lastText: Text? = null
        private var textChangeTicks: Int = 0

        fun render(drawContext: DrawContext, computers: ComputerAccess) {
            val leftX: Int = (HudFrame.left + HudFrame.width * ((order - 1) / TOTAL_MODES)).toInt()
            val rightX: Int = (HudFrame.left + HudFrame.width * (order / TOTAL_MODES)).toInt()
            val y: Int = HudFrame.top - 9

            val text: Text? = textSupplier.invoke(computers)
            if (!Objects.equals(text, lastText)) {
                textChangeTicks = FATickCounter.totalTicks
                lastText = text
            }

            if (text != null) {
                drawContext.drawMiddleAlignedText(text, (leftX + rightX) / 2, y, primaryColor)
            }
            if (FATickCounter.totalTicks <= textChangeTicks + (if (text == null) 60 else 100)) {
                drawContext.drawBorder(leftX + 1, y - 2, rightX - leftX - 1, 11, 0xFFFFFFFF.toInt())
            }
        }
    }
}
