package ru.octol1ttle.flightassistant.mixin.sound;

import java.util.Map;
import net.minecraft.client.sound.Channel;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.client.sound.Source;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import ru.octol1ttle.flightassistant.api.util.SoundExtensions;

@Mixin(SoundSystem.class)
abstract class SoundSystemMixin implements SoundExtensions {
    @Shadow
    private boolean started;

    @Shadow
    @Final
    private Map<SoundInstance, Channel.SourceManager> sources;

    @Override
    public void flightassistant$setLooping(SoundInstance soundInstance, boolean looping) {
        if (started) {
            Channel.SourceManager sourceManager = this.sources.get(soundInstance);
            if (sourceManager != null) {
                sourceManager.run(source -> source.setLooping(looping));
            }
        }
    }

    @Override
    public void flightassistant$pause(SoundInstance soundInstance) {
        if (started) {
            Channel.SourceManager sourceManager = this.sources.get(soundInstance);
            if (sourceManager != null) {
                sourceManager.run(Source::pause);
            }
        }
    }

    @Override
    public void flightassistant$resume(SoundInstance soundInstance) {
        if (started) {
            Channel.SourceManager sourceManager = this.sources.get(soundInstance);
            if (sourceManager != null) {
                sourceManager.run(Source::resume);
            }
        }
    }
}
