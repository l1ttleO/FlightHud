package ru.octol1ttle.flightassistant.impl.alert

import kotlin.math.max
import net.minecraft.client.sound.*
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.sound.SoundCategory
import ru.octol1ttle.flightassistant.api.alert.AlertData

// TODO: stop cutting out sounds like "PULL UP" and instead wait for them to finish
class AlertSoundInstance(val player: PlayerEntity, val data: AlertData) :
    AbstractSoundInstance(data.soundEvent, SoundCategory.MASTER, SoundInstance.createRandom()), TickableSoundInstance {
    private var age: Int = 0

    init {
        this.relative = true
        this.attenuationType = SoundInstance.AttenuationType.NONE
        this.repeat = data.repeat != AlertData.RepeatType.NO_REPEAT
    }

    override fun canPlay(): Boolean {
        return true
    }

    override fun isDone(): Boolean {
        return player.isDead || !player.isFallFlying
    }

    override fun tick() {
        age++
        if (data.repeat == AlertData.RepeatType.REPEAT_FADE_VOLUME && age > 100) {
            this.volume = max(0.3f, this.volume - 0.007f)
        }
    }
}
