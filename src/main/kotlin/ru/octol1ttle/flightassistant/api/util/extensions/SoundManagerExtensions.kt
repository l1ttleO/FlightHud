package ru.octol1ttle.flightassistant.api.util.extensions

import net.minecraft.client.sound.SoundInstance
import net.minecraft.client.sound.SoundManager
import ru.octol1ttle.flightassistant.api.util.SoundExtensions

fun SoundManager.applyVolume(soundInstance: SoundInstance) {
    (this as SoundExtensions).`flightassistant$applyVolume`(soundInstance)
}

fun SoundManager.setLooping(soundInstance: SoundInstance, looping: Boolean) {
    (this as SoundExtensions).`flightassistant$setLooping`(soundInstance, looping)
}

fun SoundManager.pause(soundInstance: SoundInstance) {
    (this as SoundExtensions).`flightassistant$pause`(soundInstance)
}

fun SoundManager.resume(soundInstance: SoundInstance) {
    (this as SoundExtensions).`flightassistant$resume`(soundInstance)
}
