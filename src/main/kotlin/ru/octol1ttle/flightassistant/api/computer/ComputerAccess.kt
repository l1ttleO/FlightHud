package ru.octol1ttle.flightassistant.api.computer

import net.minecraft.util.Identifier

interface ComputerAccess {
    /**
     * Retrieves a Computer instance by its identifier.
     *
     * @throws IllegalArgumentException if no computer was registered with the specified indentifier
     * @return the requested Computer instance
     */
    fun get(identifier: Identifier): Computer
}
