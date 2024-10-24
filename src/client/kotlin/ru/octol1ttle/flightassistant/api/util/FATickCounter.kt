package ru.octol1ttle.flightassistant.api.util

import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.render.RenderTickCounter
import net.minecraft.util.Util

object FATickCounter {
    private var lastPlayerAge: Int = 0
    private var lastMillis: Long = 0

    var totalTicks: Int = 0
    var ticksPassed: Int = 0
    var timePassed: Float = 0.0f
    var tickDelta: Float = 0.0f

    fun tick(player: ClientPlayerEntity, tickCounter: RenderTickCounter) {
        ticksPassed = if (player.age > lastPlayerAge) player.age - lastPlayerAge else 0
        lastPlayerAge = player.age
        totalTicks += ticksPassed

        val millis: Long = Util.getMeasuringTimeMs()
        timePassed = (millis - lastMillis) / 1000.0f
        lastMillis = millis

        tickDelta = tickCounter.getTickDelta(true)
    }
}
