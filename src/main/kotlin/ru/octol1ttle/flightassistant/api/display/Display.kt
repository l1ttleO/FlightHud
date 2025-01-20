package ru.octol1ttle.flightassistant.api.display

import net.minecraft.client.gui.DrawContext
import ru.octol1ttle.flightassistant.api.computer.ComputerAccess

/**
 *
 * A class responsible for presenting data on the HUD. Should *not* be used for computing data, do this in a [ru.octol1ttle.flightassistant.api.computer.Computer] instead
 */
abstract class Display {
    var enabled: Boolean = true
    var faulted: Boolean = false
    var faultCount: Int = 0

    abstract fun allowedByConfig(): Boolean
    abstract fun render(drawContext: DrawContext, computers: ComputerAccess)
    abstract fun renderFaulted(drawContext: DrawContext)
}
