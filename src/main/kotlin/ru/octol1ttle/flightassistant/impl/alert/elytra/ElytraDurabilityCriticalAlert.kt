package ru.octol1ttle.flightassistant.impl.alert.elytra

import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import ru.octol1ttle.flightassistant.api.alert.*
import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.api.util.*
import ru.octol1ttle.flightassistant.config.FAConfig

class ElytraDurabilityCriticalAlert : Alert(), ECAMAlert {
    override val data: AlertData
        get() = AlertData.MASTER_WARNING

    override fun shouldActivate(computers: ComputerAccess): Boolean {
        if (!FAConfig.safety.elytraDurabilityAlertMode.warning()) {
            return false
        }
        val remainingFlightTime: Int = computers.elytra.getRemainingFlightTime(computers.data.player) ?: return false
        return remainingFlightTime < 30
    }

    override fun render(drawContext: DrawContext, computers: ComputerAccess, firstLineX: Int, x: Int, y: Int): Int {
        drawContext.drawText(Text.translatable("alerts.flightassistant.elytra.critical_durability"), firstLineX, y, warningColor)
        return 1
    }
}
