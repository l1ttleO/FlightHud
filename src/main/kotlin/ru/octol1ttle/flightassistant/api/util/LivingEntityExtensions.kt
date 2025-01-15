package ru.octol1ttle.flightassistant.api.util

import net.minecraft.entity.LivingEntity

val LivingEntity.fallFlying: Boolean
    get() {
//? if >=1.21.2 {
        /*return this.isGliding
*///?} else
        return this.isFallFlying
    }
