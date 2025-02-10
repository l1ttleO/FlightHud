package ru.octol1ttle.flightassistant.impl.computer.safety

import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerView

class FlightProtectionsComputer(computers: ComputerView) : Computer(computers) {
    var protectionsLost: Boolean = false
        private set

    override fun tick() {
        protectionsLost = this.disabledOrFaulted() || computers.data.disabledOrFaulted() || computers.pitch.disabledOrFaulted()
    }

    override fun reset() {
        protectionsLost = true
    }

    companion object {
        val ID: Identifier = FlightAssistant.id("flight_protections")
    }
}
