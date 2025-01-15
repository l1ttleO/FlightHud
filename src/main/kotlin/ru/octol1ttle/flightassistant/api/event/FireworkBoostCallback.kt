package ru.octol1ttle.flightassistant.api.event

import dev.architectury.event.Event
import dev.architectury.event.EventFactory
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.projectile.FireworkRocketEntity


fun interface FireworkBoostCallback {
    fun onFireworkBoost(rocket: FireworkRocketEntity?, shooter: ClientPlayerEntity)

    companion object {
        @JvmField
        val EVENT: Event<FireworkBoostCallback> = EventFactory.createLoop()
    }
}
