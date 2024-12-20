package ru.octol1ttle.flightassistant.api.alert

import net.minecraft.client.sound.SoundManager
import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.api.util.FATickCounter.ticksPassed
import ru.octol1ttle.flightassistant.impl.alert.AlertSoundInstance

abstract class Alert {
    abstract val data: AlertData
    private var shouldResetSound: Boolean = false
    internal var soundInstance: AlertSoundInstance? = null

    internal fun tick() {
        val instance: AlertSoundInstance? = soundInstance
        if (instance == null) {
            shouldResetSound = false
        } else if (shouldResetSound && instance.fadeOut(ticksPassed)) {
            soundInstance = null
            shouldResetSound = false
        }
    }

    internal fun stopSound(soundManager: SoundManager) {
        val instance: AlertSoundInstance? = soundInstance
        if (instance?.isRepeatable == true) {
            instance.setRepeat(false, soundManager)
        }
        shouldResetSound = true
    }

    abstract fun shouldActivate(computers: ComputerAccess): Boolean
}
