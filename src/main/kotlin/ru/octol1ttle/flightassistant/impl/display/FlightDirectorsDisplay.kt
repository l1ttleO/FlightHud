package ru.octol1ttle.flightassistant.impl.display

import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.ComputerView
import ru.octol1ttle.flightassistant.api.display.Display
import ru.octol1ttle.flightassistant.api.display.HudFrame
import ru.octol1ttle.flightassistant.api.util.ScreenSpace
import ru.octol1ttle.flightassistant.api.util.extensions.*
import ru.octol1ttle.flightassistant.config.FAConfig
import ru.octol1ttle.flightassistant.impl.computer.autoflight.AutomationsComputer

class FlightDirectorsDisplay(computers: ComputerView) : Display(computers) {
    override fun allowedByConfig(): Boolean {
        return FAConfig.display.showFlightDirectors
    }

    override fun render(drawContext: DrawContext) {
        if (!computers.automations.flightDirectors) {
            return
        }
        if (computers.pitch.activeInput?.identifier != AutomationsComputer.ID || computers.heading.activeInput?.identifier != AutomationsComputer.ID) {
            renderFaulted(drawContext)
            return
        }

        with(drawContext) {
            val halfWidth: Int = (HudFrame.width / 10.0f).toInt()

            matrices.push()
            matrices.translate(0, 0, -50)

            val pitchY: Int = ScreenSpace.getY(computers.pitch.activeInput?.target ?: return, false) ?: return
            drawHorizontalLine(this.centerXI - halfWidth, this.centerXI + halfWidth, pitchY, advisoryColor)

            val headingX: Int = ScreenSpace.getX(computers.heading.activeInput?.target ?: return, false) ?: return
            drawVerticalLine(headingX, this.centerYI - halfWidth, this.centerYI + halfWidth, advisoryColor)

            matrices.pop()
        }
    }

    override fun renderFaulted(drawContext: DrawContext) {
        with(drawContext) {
            drawMiddleAlignedText(Text.translatable("short.flightassistant.flight_directors"), centerXI, HudFrame.top + 30, warningColor)
        }
    }

    companion object {
        val ID: Identifier = FlightAssistant.id("flight_directors")
    }
}
