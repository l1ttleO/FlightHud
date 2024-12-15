package ru.octol1ttle.flightassistant.impl.alert.gpws

import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import ru.octol1ttle.flightassistant.api.alert.*
import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.api.util.*
import ru.octol1ttle.flightassistant.api.util.FATickCounter.totalTicks
import ru.octol1ttle.flightassistant.impl.computer.safety.GroundProximityComputer

class PullUpAlert : Alert(), CenteredAlert {
    override val data: AlertData
        get() = AlertData.PULL_UP

    override fun shouldActivate(computers: ComputerAccess): Boolean {
        return computers.gpws.groundImpactStatus <= GroundProximityComputer.Status.WARNING || computers.gpws.obstacleImpactStatus <= GroundProximityComputer.Status.WARNING
    }

    override fun render(drawContext: DrawContext, computers: ComputerAccess, y: Int): Boolean {
        val flash: Boolean =
            if (computers.gpws.groundImpactStatus == GroundProximityComputer.Status.RECOVER
                || computers.gpws.obstacleImpactStatus == GroundProximityComputer.Status.RECOVER) totalTicks % 10 >= 5
            else totalTicks % 20 >= 10
        drawContext.drawHighlightedCenteredText(Text.translatable("alerts.flightassistant.gpws.pull_up"), drawContext.centerXI, y, warningColor, flash)
        return true
    }
}
