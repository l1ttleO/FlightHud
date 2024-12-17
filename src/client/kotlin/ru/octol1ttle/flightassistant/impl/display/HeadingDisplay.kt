package ru.octol1ttle.flightassistant.impl.display

import net.minecraft.client.gui.DrawContext
import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.api.display.Display
import ru.octol1ttle.flightassistant.config.FAConfig

class HeadingDisplay : Display() {
    override fun enabled(): Boolean {
        return FAConfig.display.showHeadingReading || FAConfig.display.showHeadingScale
    }

    override fun render(drawContext: DrawContext, computers: ComputerAccess) {
        TODO("Not yet implemented")
    }

    override fun renderFaulted(drawContext: DrawContext) {
        TODO("Not yet implemented")
    }

    companion object {
        val ID: Identifier = FlightAssistant.id("heading")
    }
}
