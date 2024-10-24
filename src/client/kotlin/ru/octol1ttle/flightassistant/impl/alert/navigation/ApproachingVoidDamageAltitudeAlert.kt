package ru.octol1ttle.flightassistant.impl.alert.navigation

import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import ru.octol1ttle.flightassistant.api.alert.*
import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.api.util.*
import ru.octol1ttle.flightassistant.impl.computer.safety.VoidProximityComputer

class ApproachingVoidDamageAltitudeAlert : Alert() {
    override val data: AlertData
        get() = AlertData.MASTER_CAUTION

    override fun shouldActivate(computers: ComputerAccess): Boolean {
        return computers.voidProximity.status == VoidProximityComputer.Status.APPROACHING_DAMAGE_ALTITUDE
    }

    override fun render(drawContext: DrawContext, computers: ComputerAccess, firstLineX: Int, x: Int, y: Int): Int {
        drawContext.drawText(Text.translatable("alerts.flightassistant.navigation.approaching_void_damage_altitude"), firstLineX, y, cautionColor)
        return 1
    }
}
