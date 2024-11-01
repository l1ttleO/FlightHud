package ru.octol1ttle.flightassistant.api.event

import net.fabricmc.fabric.api.event.*
import net.minecraft.entity.Entity
import ru.octol1ttle.flightassistant.api.event.ChangeLookDirectionEvents.Pitch

class ChangeLookDirectionEvents private constructor() {
    companion object {
        @JvmField
        val PITCH: Event<Pitch> = EventFactory.createArrayBacked(Pitch::class.java)
        { listeners: Array<Pitch> ->
            Pitch { entity, pitchDelta ->
                for (event: Pitch in listeners) {
                    val result: Float? = event.onPitchChange(entity, pitchDelta)
                    if (result != null) {
                        return@Pitch result
                    }
                }
                return@Pitch null
            }
        }
    }

    fun interface Pitch {
        fun onPitchChange(entity: Entity, pitchDelta: Float): Float?
    }
}
