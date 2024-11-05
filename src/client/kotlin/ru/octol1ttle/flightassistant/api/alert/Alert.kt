package ru.octol1ttle.flightassistant.api.alert

import net.minecraft.client.sound.SoundManager
import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.impl.alert.AlertSoundInstance

abstract class Alert {
    abstract val data: AlertData
    internal var soundInstance: AlertSoundInstance? = null

    internal fun stop(soundManager: SoundManager) {
        if (soundInstance != null) {
            soundManager.stop(soundInstance)
        }
        soundInstance = null
    }

    abstract fun shouldActivate(computers: ComputerAccess): Boolean
}
