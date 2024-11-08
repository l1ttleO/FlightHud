package ru.octol1ttle.flightassistant.api.util

import net.minecraft.client.sound.*

fun SoundManager.pause(soundInstance: SoundInstance) {
    (this as SoundPauseResumeController).`flightassistant$pause`(soundInstance)
}

fun SoundManager.resume(soundInstance: SoundInstance) {
    (this as SoundPauseResumeController).`flightassistant$resume`(soundInstance)
}
