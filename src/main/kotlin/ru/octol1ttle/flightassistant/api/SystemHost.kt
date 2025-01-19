package ru.octol1ttle.flightassistant.api

import net.minecraft.util.Identifier

interface SystemHost {
    fun identifiers(): Collection<Identifier>
    fun isEnabled(identifier: Identifier): Boolean
    fun isFaulted(identifier: Identifier): Boolean
    fun toggleEnabled(identifier: Identifier): Boolean
}
