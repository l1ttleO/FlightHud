package ru.octol1ttle.flightassistant.impl.display

import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.api.display.Display
import ru.octol1ttle.flightassistant.api.display.HudFrame
import ru.octol1ttle.flightassistant.api.util.*

class FlightDirectorsDisplay : Display() {
    override fun allowedByConfig(): Boolean {
        return true // TODO
    }

    override fun render(drawContext: DrawContext, computers: ComputerAccess) {
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
