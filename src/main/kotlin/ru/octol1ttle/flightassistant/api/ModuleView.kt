package ru.octol1ttle.flightassistant.api

import net.minecraft.util.Identifier

interface ModuleView<T> {
    fun identifiers(): Collection<Identifier>
    fun get(identifier: Identifier): T
    fun isEnabled(identifier: Identifier): Boolean
    fun isFaulted(identifier: Identifier): Boolean
}
