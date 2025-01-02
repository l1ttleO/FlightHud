package ru.octol1ttle.flightassistant.api.event

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.projectile.FireworkRocketEntity


fun interface FireworkBoostCallback {
    fun onFireworkBoost(rocket: FireworkRocketEntity?, shooter: ClientPlayerEntity)

    companion object {
        @JvmField
        val EVENT: Event<FireworkBoostCallback> =
            EventFactory.createArrayBacked(FireworkBoostCallback::class.java)
            { listeners: Array<FireworkBoostCallback> ->
                FireworkBoostCallback { rocket: FireworkRocketEntity?, shooter: ClientPlayerEntity ->
                    for (event in listeners) {
                        event.onFireworkBoost(rocket, shooter)
                    }
                }
            }
    }
}
