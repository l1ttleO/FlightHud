package ru.octol1ttle.flightassistant.impl.computer.safety

import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.api.util.data
import ru.octol1ttle.flightassistant.api.util.pitch

class FlightProtectionsComputer : Computer() {
    var protectionsLost: Boolean = true
        private set

    override fun tick(computers: ComputerAccess) {
        protectionsLost = this.faulted || !computers.data.enabled || computers.data.faulted || !computers.pitch.enabled || computers.pitch.faulted
    }

    override fun reset() {
        protectionsLost = true
    }

    companion object {
        val ID: Identifier = FlightAssistant.id("flight_protections")
    }
}
