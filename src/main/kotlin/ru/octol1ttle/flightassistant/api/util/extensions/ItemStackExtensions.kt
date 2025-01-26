package ru.octol1ttle.flightassistant.api.util.extensions

import net.minecraft.item.ItemStack

fun ItemStack?.canUse(): Boolean {
//? if >=1.21.2 {
    /*return this?.willBreakNextUse() == false
*///?} else
    return net.minecraft.item.ElytraItem.isUsable(this)
}
