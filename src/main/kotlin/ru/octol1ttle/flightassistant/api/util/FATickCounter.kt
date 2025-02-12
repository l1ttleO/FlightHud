package ru.octol1ttle.flightassistant.api.util

import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.util.Util

object FATickCounter {
    private var lastPlayerAge: Int = 0
    private var lastMillis: Long = 0

    var totalTicks: Int = 0
        private set
    var ticksSinceWorldLoad: Int = 0
        private set
    var ticksPassed: Int = 0
        private set
    var timePassed: Float = 0.0f
        private set
    var tickProgress: Float = 0.0f
        private set

    fun tick(player: ClientPlayerEntity, tickProgress: Float, paused: Boolean) {
        if (!paused) {
            if (player.age < lastPlayerAge) {
                ticksSinceWorldLoad = player.age
            }
            ticksPassed = if (player.age >= lastPlayerAge) player.age - lastPlayerAge else player.age
            lastPlayerAge = player.age
            totalTicks += ticksPassed
            ticksSinceWorldLoad += ticksPassed
            this.tickProgress = tickProgress
        }

        val millis: Long = Util.getMeasuringTimeMs()
        timePassed = (millis - lastMillis) / 1000.0f
        lastMillis = millis
    }
}
