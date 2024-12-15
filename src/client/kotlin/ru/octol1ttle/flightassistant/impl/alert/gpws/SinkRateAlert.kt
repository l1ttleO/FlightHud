package ru.octol1ttle.flightassistant.impl.alert.gpws

import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import ru.octol1ttle.flightassistant.api.alert.Alert
import ru.octol1ttle.flightassistant.api.alert.AlertData
import ru.octol1ttle.flightassistant.api.alert.CenteredAlert
import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.api.util.FATickCounter.totalTicks
import ru.octol1ttle.flightassistant.api.util.cautionColor
import ru.octol1ttle.flightassistant.api.util.centerXI
import ru.octol1ttle.flightassistant.api.util.drawHighlightedCenteredText
import ru.octol1ttle.flightassistant.api.util.gpws
import ru.octol1ttle.flightassistant.impl.computer.safety.GroundProximityComputer

class SinkRateAlert: Alert(), CenteredAlert {
    override val data: AlertData
        get() = AlertData.SINK_RATE

    override fun shouldActivate(computers: ComputerAccess): Boolean {
        return computers.gpws.groundImpactStatus == GroundProximityComputer.Status.CAUTION
    }

    override fun render(drawContext: DrawContext, computers: ComputerAccess, y: Int): Boolean {
        drawContext.drawHighlightedCenteredText(Text.translatable("alerts.flightassistant.gpws.sink_rate"), drawContext.centerXI, y, cautionColor, totalTicks % 40 >= 20)
        return true
    }
}
