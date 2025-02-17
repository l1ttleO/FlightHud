package ru.octol1ttle.flightassistant.impl.display

import kotlin.math.roundToInt
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
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

class VelocityComponentsDisplay(computers: ComputerView) : Display(computers) {
    override fun allowedByConfig(): Boolean {
        return FAConfig.display.showGroundSpeed || FAConfig.display.showVerticalSpeed
    }

    override fun render(drawContext: DrawContext) {
        with(drawContext) {
            val x: Int = HudFrame.right - 45
            var y: Int = HudFrame.bottom - 10

            if (FAConfig.display.showVerticalSpeed) {
                val verticalSpeed: Double = computers.data.velocity.y * 20
                drawText(
                    Text.translatable(
                        "short.flightassistant.vertical_speed",
                        ": ${verticalSpeed.roundToInt()}"
                    ), x, y, if (verticalSpeed <= -10) warningColor else primaryColor
                )
                y -= fontHeight
            }
            if (FAConfig.display.showGroundSpeed) {
                drawText(
                    Text.translatable(
                        "short.flightassistant.ground_speed",
                        ": ${(computers.data.velocity.horizontalLength() * 20).roundToInt()}"
                    ), x, y, primaryColor
                )
            }
        }
    }

    override fun renderFaulted(drawContext: DrawContext) {
        with(drawContext) {
            val x: Int = HudFrame.right - 25
            var y: Int = HudFrame.bottom - 10

            if (FAConfig.display.showVerticalSpeed) {
                drawText(Text.translatable("short.flightassistant.vertical_speed", ""), x, y, warningColor)
                y -= fontHeight
            }
            if (FAConfig.display.showGroundSpeed) {
                drawText(Text.translatable("short.flightassistant.ground_speed", ""), x, y, warningColor)
            }
        }
    }

    companion object {
        val ID: Identifier = FlightAssistant.id("velocity_components")
    }
}
