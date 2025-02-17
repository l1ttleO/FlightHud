package ru.octol1ttle.flightassistant.impl.display

import net.minecraft.client.gui.DrawContext
import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.ComputerView
import ru.octol1ttle.flightassistant.api.display.Display
import ru.octol1ttle.flightassistant.api.display.HudFrame
import ru.octol1ttle.flightassistant.api.util.extensions.drawText
import ru.octol1ttle.flightassistant.api.util.extensions.fontHeight
import ru.octol1ttle.flightassistant.api.util.extensions.primaryColor
import ru.octol1ttle.flightassistant.api.util.extensions.warningColor
import ru.octol1ttle.flightassistant.config.FAConfig

class CoordinatesDisplay(computers: ComputerView) : Display(computers) {
    override fun allowedByConfig(): Boolean {
        return FAConfig.display.showCoordinates
    }

    override fun render(drawContext: DrawContext) {
        with(drawContext) {
            val x: Int = HudFrame.left + 10
            val y: Int = HudFrame.bottom - 19

            drawText("X: ${computers.data.position.x.toInt()}${getDirectionSignX(computers.data.heading)}", x, y, primaryColor)
            drawText("Z: ${computers.data.position.z.toInt()}${getDirectionSignZ(computers.data.heading)}", x, y + fontHeight, primaryColor)
        }
    }

    private fun getDirectionSignX(heading: Float): String {
        if (heading in 30.0..150.0) {
            return " (+)"
        }

        if (heading in 210.0..330.0) {
            return " (-)"
        }

        return ""
    }

    private fun getDirectionSignZ(heading: Float): String {
        if (heading >= 300 || heading <= 60) {
            return " (-)"
        }

        if (heading in 120.0..240.0) {
            return " (+)"
        }

        return ""
    }

    override fun renderFaulted(drawContext: DrawContext) {
        with(drawContext) {
            val x: Int = HudFrame.left + 10
            val y: Int = HudFrame.bottom - 19

            drawText("X", x, y, warningColor)
            drawText("Z", x, y + fontHeight, warningColor)
        }
    }

    companion object {
        val ID: Identifier = FlightAssistant.id("coordinates")
    }
}
