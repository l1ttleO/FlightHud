package ru.octol1ttle.flightassistant.impl.alert

import kotlin.math.max
import kotlin.math.min
import net.minecraft.client.sound.AbstractSoundInstance
import net.minecraft.client.sound.SoundInstance
import net.minecraft.client.sound.SoundManager
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.sound.SoundCategory
import ru.octol1ttle.flightassistant.api.alert.AlertData
import ru.octol1ttle.flightassistant.api.util.setLooping
import ru.octol1ttle.flightassistant.config.FAConfig

class AlertSoundInstance(val player: PlayerEntity, val data: AlertData) :
    AbstractSoundInstance(data.soundEvent, SoundCategory.MASTER, SoundInstance.createRandom()) {
    var age: Int = 0
    private var actualVolume: Float = 1.0f
    private var fadingOut: Boolean = false

    init {
        this.relative = true
        this.attenuationType = SoundInstance.AttenuationType.NONE
        this.repeat = data.repeat != AlertData.RepeatType.NO_REPEAT
        if (data.repeat == AlertData.RepeatType.REPEAT_FADE_IN_OUT) {
            this.volume = 0.05f
            this.actualVolume = 0.05f
        }
    }

    override fun canPlay(): Boolean {
        return true
    }

    fun tick() {
        age++
        if (this.fadingOut) {
            return
        }

        if (age > 100) {
            if (data.repeat <= AlertData.RepeatType.REPEAT_FADE_OUT) {
                this.actualVolume = max(0.3f, this.actualVolume - 0.007f)
            }
        } else if (data.repeat == AlertData.RepeatType.REPEAT_FADE_IN_OUT) {
            this.actualVolume = min(1.0f, this.actualVolume + 0.05f)
        }

        this.volume = this.actualVolume * FAConfig.safety.alertVolume
    }

    fun setRepeat(repeat: Boolean, soundManager: SoundManager) {
        this.repeat = repeat
        soundManager.setLooping(this, this.repeat)
    }

    fun fadeOut(ticksPassed: Int): Boolean {
        this.fadingOut = true
        this.volume = max(0.0f, this.volume - 0.1f * ticksPassed)
        return this.volume <= 0.0f
    }
}
