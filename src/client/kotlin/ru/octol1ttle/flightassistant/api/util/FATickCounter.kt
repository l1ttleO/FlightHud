package ru.octol1ttle.flightassistant.api.util

import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.render.RenderTickCounter
import net.minecraft.util.Util

object FATickCounter {
    private var lastPlayerAge: Int = 0
    private var lastMillis: Long = 0

    var totalTicks: Int = 0
        private set
    var ticksPassed: Int = 0
        private set
    var timePassed: Float = 0.0f
        private set
    var tickDelta: Float = 0.0f
        private set

    fun tick(player: ClientPlayerEntity, tickCounter: RenderTickCounter, paused: Boolean) {
        if (!paused) {
            ticksPassed = if (player.age > lastPlayerAge) player.age - lastPlayerAge else 0
            lastPlayerAge = player.age
            totalTicks += ticksPassed
            tickDelta = tickCounter.getTickDelta(true)
        }

        val millis: Long = Util.getMeasuringTimeMs()
        timePassed = (millis - lastMillis) / 1000.0f
        lastMillis = millis
    }
}
