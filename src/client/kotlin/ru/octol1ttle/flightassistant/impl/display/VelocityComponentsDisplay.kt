package ru.octol1ttle.flightassistant.impl.display

import kotlin.math.roundToInt
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.api.display.*
import ru.octol1ttle.flightassistant.api.util.*
import ru.octol1ttle.flightassistant.config.FAConfig

class VelocityComponentsDisplay : Display() {
    override fun enabled(): Boolean {
        return FAConfig.display.showGroundSpeed || FAConfig.display.showVerticalSpeed
    }

    override fun render(drawContext: DrawContext, computers: ComputerAccess) {
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
            var y: Int = HudFrame.bottom - 19

            if (FAConfig.display.showVerticalSpeed) {
                drawText(Text.translatable("short.flightassistant.vertical_speed", ""), x, y, primaryColor)
                y -= fontHeight
            }
            if (FAConfig.display.showGroundSpeed) {
                drawText(Text.translatable("short.flightassistant.ground_speed", ""), x, y, primaryColor)
            }
        }
    }

    companion object {
        val ID: Identifier = FlightAssistant.id("velocity_components")
    }
}
