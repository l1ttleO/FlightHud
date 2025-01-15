package ru.octol1ttle.flightassistant.api.util

import net.minecraft.item.ItemStack

fun ItemStack?.canUse(): Boolean {
//? if >=1.21.2 {
    /*return this?.willBreakNextUse() == false
*///?} else
    return net.minecraft.item.ElytraItem.isUsable(this)
}
