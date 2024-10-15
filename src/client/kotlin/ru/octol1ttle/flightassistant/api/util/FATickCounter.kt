package ru.octol1ttle.flightassistant.api.util

import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.render.RenderTickCounter

object FATickCounter {
    private var lastPlayerAge: Int = 0

    var totalTicks: Int = 0
    var ticksPassed: Int = 0
    var tickDelta: Float = 0.0f

    fun tick(player: ClientPlayerEntity, tickCounter: RenderTickCounter) {
        ticksPassed = player.age - lastPlayerAge
        lastPlayerAge = player.age

        totalTicks += ticksPassed

        tickDelta = tickCounter.getTickDelta(true)
    }
}
