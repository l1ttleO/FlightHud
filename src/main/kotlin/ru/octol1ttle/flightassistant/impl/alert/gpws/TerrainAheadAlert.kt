package ru.octol1ttle.flightassistant.impl.alert.gpws

import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import ru.octol1ttle.flightassistant.api.alert.*
import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.api.util.FATickCounter.totalTicks
import ru.octol1ttle.flightassistant.api.util.extensions.cautionColor
import ru.octol1ttle.flightassistant.api.util.extensions.centerXI
import ru.octol1ttle.flightassistant.api.util.extensions.drawHighlightedCenteredText
import ru.octol1ttle.flightassistant.api.util.extensions.gpws
import ru.octol1ttle.flightassistant.impl.computer.safety.GroundProximityComputer

class TerrainAheadAlert: Alert(), CenteredAlert {
    override val data: AlertData = AlertData.TERRAIN_AHEAD

    override fun shouldActivate(computers: ComputerAccess): Boolean {
        return computers.gpws.obstacleImpactStatus == GroundProximityComputer.Status.CAUTION
    }

    override fun render(drawContext: DrawContext, computers: ComputerAccess, y: Int): Boolean {
        drawContext.drawHighlightedCenteredText(Text.translatable("alerts.flightassistant.gpws.terrain_ahead"), drawContext.centerXI, y, cautionColor, totalTicks % 40 >= 20)
        return true
    }
}
