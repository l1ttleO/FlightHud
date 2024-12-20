package ru.octol1ttle.flightassistant.api.util;

import net.minecraft.client.sound.SoundInstance;

public interface SoundExtensions {
    void flightassistant$setLooping(SoundInstance soundInstance, boolean looping);
    void flightassistant$pause(SoundInstance soundInstance);
    void flightassistant$resume(SoundInstance soundInstance);
}
