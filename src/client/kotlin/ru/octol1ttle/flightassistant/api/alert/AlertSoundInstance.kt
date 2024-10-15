package ru.octol1ttle.flightassistant.api.alert

import net.minecraft.client.sound.*
import net.minecraft.sound.*

class AlertSoundInstance(soundEvent: SoundEvent) :
    AbstractSoundInstance(soundEvent, SoundCategory.MASTER, SoundInstance.createRandom()) {
    init {
        this.relative = true
        this.attenuationType = SoundInstance.AttenuationType.NONE
    }

    override fun canPlay(): Boolean {
        return true
    }
}
