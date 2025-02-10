package ru.octol1ttle.flightassistant.api
// TODO: documentation for the whole package

import net.minecraft.util.Identifier

interface SystemView<T> {
    fun identifiers(): Collection<Identifier>
    fun get(identifier: Identifier): T
    fun isEnabled(identifier: Identifier): Boolean
    fun isFaulted(identifier: Identifier): Boolean
}
