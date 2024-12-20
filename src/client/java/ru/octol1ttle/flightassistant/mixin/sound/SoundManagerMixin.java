package ru.octol1ttle.flightassistant.mixin.sound;

import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.SoundSystem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import ru.octol1ttle.flightassistant.api.util.SoundExtensions;

@Mixin(SoundManager.class)
abstract class SoundManagerMixin implements SoundExtensions {
    @Shadow
    @Final
    private SoundSystem soundSystem;

    @Override
    public void flightassistant$setLooping(SoundInstance soundInstance, boolean looping) {
        ((SoundExtensions) soundSystem).flightassistant$setLooping(soundInstance, looping);
    }

    @Override
    public void flightassistant$pause(SoundInstance soundInstance) {
        ((SoundExtensions) soundSystem).flightassistant$pause(soundInstance);

    }

    @Override
    public void flightassistant$resume(SoundInstance soundInstance) {
        ((SoundExtensions) soundSystem).flightassistant$resume(soundInstance);
    }
}
