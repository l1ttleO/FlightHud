package ru.octol1ttle.flightassistant.impl.alert.gpws

import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import ru.octol1ttle.flightassistant.api.alert.Alert
import ru.octol1ttle.flightassistant.api.alert.AlertData
import ru.octol1ttle.flightassistant.api.alert.CenteredAlert
import ru.octol1ttle.flightassistant.api.computer.ComputerView
import ru.octol1ttle.flightassistant.api.util.FATickCounter.totalTicks
import ru.octol1ttle.flightassistant.api.util.extensions.cautionColor
import ru.octol1ttle.flightassistant.api.util.extensions.centerXI
import ru.octol1ttle.flightassistant.api.util.extensions.drawHighlightedCenteredText
import ru.octol1ttle.flightassistant.impl.computer.safety.GroundProximityComputer

class TerrainAheadAlert(computers: ComputerView) : Alert(computers), CenteredAlert {
    override val data: AlertData = AlertData.TERRAIN_AHEAD

    override fun shouldActivate(): Boolean {
        return computers.gpws.obstacleImpactStatus == GroundProximityComputer.Status.CAUTION
    }

    override fun render(drawContext: DrawContext, y: Int): Boolean {
        drawContext.drawHighlightedCenteredText(Text.translatable("alerts.flightassistant.gpws.terrain_ahead"), drawContext.centerXI, y, cautionColor, totalTicks % 40 >= 20)
        return true
    }
}
