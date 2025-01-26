package ru.octol1ttle.flightassistant.api.util.event

import dev.architectury.event.Event
import dev.architectury.event.EventFactory
import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.api.autoflight.ControlInput

class ChangeLookDirectionEvents private constructor() {
    companion object {
        @JvmField
        val PITCH: Event<ChangeLookDirection> = EventFactory.createLoop()
        @JvmField
        val HEADING: Event<ChangeLookDirection> = EventFactory.createLoop()
    }

    fun interface ChangeLookDirection {
        fun onChangeLookDirection(computers: ComputerAccess, delta: Float, output: MutableList<ControlInput>)
    }
}
