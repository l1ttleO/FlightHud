package ru.octol1ttle.flightassistant.impl.display

import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3d
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.ComputerView
import ru.octol1ttle.flightassistant.api.display.Display
import ru.octol1ttle.flightassistant.api.util.ScreenSpace
import ru.octol1ttle.flightassistant.api.util.extensions.*
import ru.octol1ttle.flightassistant.config.FAConfig

class FlightPathDisplay(computers: ComputerView) : Display(computers) {
    override fun allowedByConfig(): Boolean {
        return FAConfig.display.showFlightPathVector
    }

    override fun render(drawContext: DrawContext) {
        with(drawContext) {
            val screenSpaceVec: Vec3d = ScreenSpace.getVec3d(computers.data.velocity, false) ?: return
            val trueX: Int = screenSpaceVec.x.toInt()
            val trueY: Int = screenSpaceVec.y.toInt()

            matrices.push()
            matrices.translate(0, 0, -100)
            val (x: Int, y: Int) = scaleMatrix(FAConfig.display.flightPathVectorSize, trueX, trueY)

            val bodySideSize = 3
            drawVerticalLine(x - bodySideSize, y - bodySideSize, y + bodySideSize, primaryColor)
            drawVerticalLine(x + bodySideSize, y - bodySideSize, y + bodySideSize, primaryColor)
            drawHorizontalLine(x - bodySideSize, x + bodySideSize, y - bodySideSize, primaryColor)
            drawHorizontalLine(x - bodySideSize, x + bodySideSize, y + bodySideSize, primaryColor)

            val stabilizerSize = 5
            drawVerticalLine(x, y - bodySideSize - stabilizerSize, y - bodySideSize, primaryColor)

            val wingSize = 5
            drawHorizontalLine(x - bodySideSize - wingSize, x - bodySideSize, y, primaryColor)
            drawHorizontalLine(x + bodySideSize, x + bodySideSize + wingSize, y, primaryColor)

            matrices.pop()
        }
    }

    override fun renderFaulted(drawContext: DrawContext) {
        with(drawContext) {
            drawMiddleAlignedText(Text.translatable("short.flightassistant.flight_path"), centerXI, centerYI + 16, warningColor)
        }
    }

    companion object {
        val ID: Identifier = FlightAssistant.id("flight_path")
    }
}
