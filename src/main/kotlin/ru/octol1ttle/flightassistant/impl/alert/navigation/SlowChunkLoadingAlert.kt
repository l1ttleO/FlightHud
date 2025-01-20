package ru.octol1ttle.flightassistant.impl.alert.navigation

import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import ru.octol1ttle.flightassistant.api.alert.*
import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.api.util.*
import ru.octol1ttle.flightassistant.impl.computer.safety.*

class SlowChunkLoadingAlert : Alert(), ECAMAlert {
    override val priorityOffset: Int = 55
    override val data: AlertData
        get() = AlertData.MASTER_CAUTION
    private var alertDuration = 0

    override fun shouldActivate(computers: ComputerAccess): Boolean {
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

    override fun render(drawContext: DrawContext, computers: ComputerAccess, firstLineX: Int, otherLinesX: Int, firstLineY: Int): Int {
        return drawContext.drawText(Text.translatable("alerts.flightassistant.navigation.slow_chunk_loading"), firstLineX, firstLineY, cautionColor)
    }
}
