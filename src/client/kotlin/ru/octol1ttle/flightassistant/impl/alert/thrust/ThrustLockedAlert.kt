package ru.octol1ttle.flightassistant.impl.alert.thrust

import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import ru.octol1ttle.flightassistant.api.alert.*
import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.api.util.*

class ThrustLockedAlert : Alert(), ECAMAlert {
    override val data: AlertData
        get() = AlertData.THRUST_LOCKED

    override fun shouldActivate(computers: ComputerAccess): Boolean {
        return computers.thrust.thrustLocked
    }

    override fun render(drawContext: DrawContext, computers: ComputerAccess, firstLineX: Int, x: Int, y: Int): Int {
        drawContext.drawText(Text.translatable("alerts.flightassistant.thrust.locked"), firstLineX, y, cautionColor)
        drawContext.drawText(Text.translatable("alerts.flightassistant.thrust.locked.use_keys"), x, y.plus(11), advisoryColor)
        return 2
    }
}
