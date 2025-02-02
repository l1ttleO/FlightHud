package ru.octol1ttle.flightassistant.impl.computer.safety

import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.api.util.extensions.data
import ru.octol1ttle.flightassistant.api.util.extensions.pitch

class FlightProtectionsComputer : Computer() {
    var protectionsLost: Boolean = false
        private set

    override fun tick(computers: ComputerAccess) {
        protectionsLost = this.disabledOrFaulted() || computers.data.disabledOrFaulted() || computers.pitch.disabledOrFaulted()
    }

    override fun reset() {
        protectionsLost = true
    }

    companion object {
        val ID: Identifier = FlightAssistant.id("flight_protections")
    }
}
