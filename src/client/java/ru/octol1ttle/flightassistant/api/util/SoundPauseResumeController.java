package ru.octol1ttle.flightassistant.api.util;

import net.minecraft.client.sound.SoundInstance;

public interface SoundPauseResumeController {
    void flightassistant$pause(SoundInstance soundInstance);

    void flightassistant$resume(SoundInstance soundInstance);
}
