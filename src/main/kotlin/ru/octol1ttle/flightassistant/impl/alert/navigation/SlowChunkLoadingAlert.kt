package ru.octol1ttle.flightassistant.impl.alert.navigation

import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import ru.octol1ttle.flightassistant.api.alert.Alert
import ru.octol1ttle.flightassistant.api.alert.AlertData
import ru.octol1ttle.flightassistant.api.alert.ECAMAlert
import ru.octol1ttle.flightassistant.api.computer.ComputerView
import ru.octol1ttle.flightassistant.api.util.FATickCounter
import ru.octol1ttle.flightassistant.api.util.extensions.cautionColor
import ru.octol1ttle.flightassistant.api.util.extensions.drawText
import ru.octol1ttle.flightassistant.impl.computer.safety.ChunkStatusComputer

class SlowChunkLoadingAlert(computers: ComputerView) : Alert(computers), ECAMAlert {
    override val priorityOffset: Int = 55
    override val data: AlertData = AlertData.MASTER_CAUTION
    private var alertDuration = 0

    override fun shouldActivate(): Boolean {
        val status: ChunkStatusComputer.Status = computers.chunk.status
        if (status != ChunkStatusComputer.Status.ALL_UNLOADED && alertDuration > 0) {
            alertDuration -= FATickCounter.ticksPassed
            return true
        }

        val shouldActivate: Boolean = status == ChunkStatusComputer.Status.SOME_UNLOADED
        if (shouldActivate) {
            alertDuration = 100
        }
        return shouldActivate
    }

    override fun render(drawContext: DrawContext, firstLineX: Int, otherLinesX: Int, firstLineY: Int): Int {
        return drawContext.drawText(Text.translatable("alerts.flightassistant.navigation.slow_chunk_loading"), firstLineX, firstLineY, cautionColor)
    }
}
