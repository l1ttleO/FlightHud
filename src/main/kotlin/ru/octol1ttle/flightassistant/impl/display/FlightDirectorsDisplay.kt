package ru.octol1ttle.flightassistant.impl.display

import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.api.display.Display
import ru.octol1ttle.flightassistant.api.display.HudFrame
import ru.octol1ttle.flightassistant.api.util.*
import ru.octol1ttle.flightassistant.config.FAConfig
import ru.octol1ttle.flightassistant.impl.computer.autoflight.AutoFlightComputer

class FlightDirectorsDisplay : Display() {
    override fun allowedByConfig(): Boolean {
        return FAConfig.display.showFlightDirectors
    }

    override fun render(drawContext: DrawContext, computers: ComputerAccess) {
        if (!computers.autoflight.flightDirectors) {
            return
        }
        if (computers.pitch.activeInput?.identifier != AutoFlightComputer.ID) {
            renderFaulted(drawContext)
            return
        }

        with(drawContext) {
            val pitchY: Int = getScreenSpaceY(computers.autoflight.selectedPitch ?: return) ?: return

            val halfWidth: Int = (HudFrame.width / 10.0f).toInt()
            drawHorizontalLine(this.centerXI - halfWidth, this.centerXI + halfWidth, pitchY, advisoryColor)
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
