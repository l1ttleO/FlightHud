package ru.octol1ttle.flightassistant.api

import net.minecraft.util.Identifier

interface ModuleController<T> : ModuleView<T> {
    fun register(identifier: Identifier, module: T)
    fun setEnabled(identifier: Identifier, enabled: Boolean): Boolean
    fun toggleEnabled(identifier: Identifier): Boolean {
        return setEnabled(identifier, !this.isEnabled(identifier))
    }
}
