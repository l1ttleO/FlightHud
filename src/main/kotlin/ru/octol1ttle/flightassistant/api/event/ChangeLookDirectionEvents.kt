package ru.octol1ttle.flightassistant.api.event

import dev.architectury.event.Event
import dev.architectury.event.EventFactory
import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.api.computer.autoflight.ControlInput

class ChangeLookDirectionEvents private constructor() {
    companion object {
        @JvmField
        val PITCH: Event<Pitch> = EventFactory.createLoop()
    }

    fun interface Pitch {
        fun onPitchChange(computers: ComputerAccess, pitchDelta: Float, output: MutableList<ControlInput>)
    }
}
