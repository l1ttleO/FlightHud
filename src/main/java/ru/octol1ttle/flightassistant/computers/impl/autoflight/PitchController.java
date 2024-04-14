package ru.octol1ttle.flightassistant.computers.impl.autoflight;

import net.minecraft.util.math.MathHelper;
import ru.octol1ttle.flightassistant.computers.impl.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.api.ITickableComputer;
import ru.octol1ttle.flightassistant.computers.impl.TimeComputer;
import ru.octol1ttle.flightassistant.computers.impl.safety.ChunkStatusComputer;
import ru.octol1ttle.flightassistant.computers.impl.safety.GPWSComputer;
import ru.octol1ttle.flightassistant.computers.impl.safety.StallComputer;
import ru.octol1ttle.flightassistant.computers.impl.safety.VoidLevelComputer;
import ru.octol1ttle.flightassistant.config.FAConfig;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;

public class PitchController implements ITickableComputer {
    public static final float CLIMB_PITCH = 55.0f;
    public static final float ALTITUDE_PRESERVE_PITCH = 15.0f;
    public static final float GLIDE_PITCH = -2.2f;
    public static final float DESCEND_PITCH = -35.0f;
    private final AirDataComputer data = ComputerRegistry.resolve(AirDataComputer.class);
    private final StallComputer stall = ComputerRegistry.resolve(StallComputer.class);
    private final TimeComputer time = ComputerRegistry.resolve(TimeComputer.class);
    private final VoidLevelComputer voidLevel = ComputerRegistry.resolve(VoidLevelComputer.class);
    private final GPWSComputer gpws = ComputerRegistry.resolve(GPWSComputer.class);
    private final ChunkStatusComputer chunkStatus = ComputerRegistry.resolve(ChunkStatusComputer.class);
    public Float targetPitch = null;

    @Override
    public void tick() {
        if (!data.canAutomationsActivate()) {
            return;
        }

        if (FAConfig.computer().stallProtection.recover() && data.pitch() > 0.0f && data.pitch() > stall.maximumSafePitch) {
            smoothSetPitch(stall.maximumSafePitch, time.deltaTime);
        } else if (FAConfig.computer().voidProtection.recover() && data.pitch() < voidLevel.minimumSafePitch) {
            smoothSetPitch(voidLevel.minimumSafePitch, time.deltaTime);
        }

        if (gpws.shouldCorrectSinkrate()) {
            smoothSetPitch(90.0f, MathHelper.clamp(time.deltaTime / gpws.descentImpactTime, 0.001f, 1.0f));
        } else if (gpws.shouldCorrectTerrain()) {
            smoothSetPitch(CLIMB_PITCH, MathHelper.clamp(time.deltaTime / gpws.terrainImpactTime, 0.001f, 1.0f));
        } else if (chunkStatus.shouldPreserveAltitude()) {
            smoothSetPitch(ALTITUDE_PRESERVE_PITCH, time.deltaTime);
        } else {
            smoothSetPitch(targetPitch, time.deltaTime);
        }
    }

    /**
     * Smoothly changes the player's pitch to the specified pitch using the delta
     *
     * @param pitch Target pitch
     * @param delta Delta time, in seconds
     */
    public void smoothSetPitch(Float pitch, float delta) {
        if (pitch == null) {
            return;
        }

        float difference = pitch - data.pitch();

        float newPitch;
        if (Math.abs(difference) < 0.05f) {
            newPitch = pitch;
        } else {
            if (difference > 0) { // going UP
                pitch = MathHelper.clamp(pitch, -90.0f, Math.min(CLIMB_PITCH, stall.maximumSafePitch));
            }
            if (difference < 0) { // going DOWN
                pitch = MathHelper.clamp(pitch, Math.max(DESCEND_PITCH, voidLevel.minimumSafePitch), 90.0f);
            }

            newPitch = data.pitch() + (pitch - data.pitch()) * delta;
        }

        data.player().setPitch(-newPitch);
    }

    @Override
    public String getId() {
        return "pitch_ctl";
    }

    @Override
    public void reset() {
        targetPitch = null;
    }
}
