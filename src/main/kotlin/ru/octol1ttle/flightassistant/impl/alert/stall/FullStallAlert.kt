package ru.octol1ttle.flightassistant.impl.alert.stall

import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import ru.octol1ttle.flightassistant.api.alert.*
import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.api.util.FATickCounter.totalTicks
import ru.octol1ttle.flightassistant.api.util.extensions.centerXI
import ru.octol1ttle.flightassistant.api.util.extensions.drawHighlightedCenteredText
import ru.octol1ttle.flightassistant.api.util.extensions.stall
import ru.octol1ttle.flightassistant.api.util.extensions.warningColor
import ru.octol1ttle.flightassistant.config.FAConfig
import ru.octol1ttle.flightassistant.impl.computer.safety.StallComputer

class FullStallAlert : Alert(), CenteredAlert {
    override val data: AlertData = AlertData.FULL_STALL

    override fun shouldActivate(computers: ComputerAccess): Boolean {
        return FAConfig.safety.stallAlertMode.warning() && computers.stall.status == StallComputer.Status.FULL_STALL
    }

    override fun render(drawContext: DrawContext, computers: ComputerAccess, y: Int): Boolean {
        drawContext.drawHighlightedCenteredText(Text.translatable("alerts.flightassistant.stall"), drawContext.centerXI, y, warningColor, totalTicks % 20 >= 10)
        return true
    }
}
