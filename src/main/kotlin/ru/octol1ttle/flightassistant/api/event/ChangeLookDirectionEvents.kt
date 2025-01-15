package ru.octol1ttle.flightassistant.api.event

import dev.architectury.event.Event
import dev.architectury.event.EventFactory
import net.minecraft.entity.Entity

class ChangeLookDirectionEvents private constructor() {
    companion object {
        @JvmField
        val PITCH: Event<Pitch> = EventFactory.createLoop()
    }

    fun interface Pitch {
        fun onPitchChange(entity: Entity, pitchDelta: Float): Float?
    }
}
