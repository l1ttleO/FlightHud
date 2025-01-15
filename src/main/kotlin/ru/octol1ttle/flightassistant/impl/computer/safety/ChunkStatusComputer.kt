package ru.octol1ttle.flightassistant.impl.computer.safety

import kotlin.math.abs
import net.minecraft.client.world.ClientChunkManager
import net.minecraft.util.Identifier
import net.minecraft.util.math.ChunkPos
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.*
import ru.octol1ttle.flightassistant.api.util.data

class ChunkStatusComputer : Computer() {
    var status: Status = Status.LOADED
        private set

    override fun tick(computers: ComputerAccess) {
        val chunkPos: ChunkPos = computers.data.player.chunkPos
        val world: ClientChunkManager = computers.data.world.chunkManager

        var unloadedClose = 0
        var unloadedFar = false
        for (x: Int in -3..3) {
            for (z: Int in -3..3) {
                if (!world.isChunkLoaded(chunkPos.x + x, chunkPos.z + z)) {
                    if (abs(x) <= 1 && abs(z) <= 1) {
                        unloadedClose++
                    } else {
                        unloadedFar = true
                    }
                }
            }
        }

        status =
            if (!unloadedFar && unloadedClose == 0) {
                Status.LOADED
            } else if (status != Status.ALL_UNLOADED && (unloadedFar && unloadedClose in 0..<9)) {
                Status.SOME_UNLOADED
            } else {
                Status.ALL_UNLOADED
            }
    }

    enum class Status {
        ALL_UNLOADED,
        SOME_UNLOADED,
        LOADED
    }

    companion object {
        val ID: Identifier = FlightAssistant.id("chunk_status")
    }
}
