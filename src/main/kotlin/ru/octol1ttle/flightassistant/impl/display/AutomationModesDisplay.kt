package ru.octol1ttle.flightassistant.impl.display

import java.util.Objects
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.api.computer.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.display.*
import ru.octol1ttle.flightassistant.api.util.*
import ru.octol1ttle.flightassistant.config.FAConfig

class AutomationModesDisplay : Display() {
    private val thrustDisplay: ModeDisplay = ModeDisplay(1) { computers -> toPair(computers.thrust.activeInput) }
    private val pitchDisplay: ModeDisplay = ModeDisplay(2) { computers -> toPair(computers.pitch.activeInput) }
    private val autoFlightDisplay: ModeDisplay = ModeDisplay(5) { computers ->
        val text: MutableText = Text.empty()
        if (computers.autoflight.flightDirectors) {
            text.appendWithSeparation(Text.translatable("mode.flightassistant.autoflight.flight_directors"))
        }
        if (computers.autoflight.autoThrust) {
            text.appendWithSeparation(Text.translatable("mode.flightassistant.autoflight.auto_thrust"))
        }
        if (computers.autoflight.autopilot) {
            text.appendWithSeparation(Text.translatable("mode.flightassistant.autoflight.autopilot"))
        }

        return@ModeDisplay Pair(text, true)
    }

    private fun toPair(input: ControlInput?): Pair<Text, Boolean>? {
        if (input != null) {
            return Pair(input.text, input.active)
        }
        return null
    }

    override fun allowedByConfig(): Boolean {
        return FAConfig.display.showAutomationModes
    }

    override fun render(drawContext: DrawContext, computers: ComputerAccess) {
        thrustDisplay.render(drawContext, computers)
        pitchDisplay.render(drawContext, computers)
        autoFlightDisplay.render(drawContext, computers)
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

    class ModeDisplay(private val order: Int, private val textSupplier: (ComputerAccess) -> (Pair<Text, Boolean>?)) {
        private var lastText: Text? = null
        private var textChangeTicks: Int = 0

        fun render(drawContext: DrawContext, computers: ComputerAccess) {
            val leftX: Int = (HudFrame.left + HudFrame.width * ((order - 1) / TOTAL_MODES)).toInt()
            val rightX: Int = (HudFrame.left + HudFrame.width * (order / TOTAL_MODES)).toInt()
            val y: Int = HudFrame.top - 9

            val pair = textSupplier.invoke(computers)
            val text: Text? = pair?.first
            val active: Boolean? = pair?.second
            if (active != false && !Objects.equals(text, lastText)) {
                textChangeTicks = FATickCounter.totalTicks
                lastText = text
            }

            if (text != null) {
                drawContext.drawMiddleAlignedText(if (pair.second) text else text.copy().styled { it.withStrikethrough(true) }, (leftX + rightX) / 2, y, if (pair.second) primaryColor else 0xFFFFFFFF.toInt())
            }
            if (FATickCounter.totalTicks <= textChangeTicks + (if (text == null) 60 else 100)) {
                drawContext.drawBorder(leftX + 1, y - 2, rightX - leftX - 1, 11, 0xFFFFFFFF.toInt())
            }
        }
    }
}
