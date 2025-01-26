package ru.octol1ttle.flightassistant.impl.alert.navigation

import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import ru.octol1ttle.flightassistant.api.alert.*
import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.api.util.extensions.drawText
import ru.octol1ttle.flightassistant.api.util.extensions.voidProximity
import ru.octol1ttle.flightassistant.api.util.extensions.warningColor
import ru.octol1ttle.flightassistant.config.FAConfig
import ru.octol1ttle.flightassistant.impl.computer.safety.VoidProximityComputer

class ReachedVoidDamageAltitudeAlert : Alert(), ECAMAlert {
    override val data: AlertData = AlertData.MASTER_WARNING

    override fun shouldActivate(computers: ComputerAccess): Boolean {
        return FAConfig.safety.voidAlertMode.warning() && computers.voidProximity.status == VoidProximityComputer.Status.REACHED_DAMAGE_ALTITUDE
    }

    override fun render(drawContext: DrawContext, computers: ComputerAccess, firstLineX: Int, otherLinesX: Int, firstLineY: Int): Int {
        return drawContext.drawText(Text.translatable("alerts.flightassistant.navigation.reached_void_damage_altitude"), firstLineX, firstLineY, warningColor)
    }
}
