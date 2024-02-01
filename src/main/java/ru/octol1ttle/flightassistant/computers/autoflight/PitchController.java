package ru.octol1ttle.flightassistant.computers.autoflight;

import net.minecraft.util.math.MathHelper;
import ru.octol1ttle.flightassistant.computers.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.ITickableComputer;
import ru.octol1ttle.flightassistant.computers.TimeComputer;
import ru.octol1ttle.flightassistant.computers.safety.GPWSComputer;
import ru.octol1ttle.flightassistant.computers.safety.StallComputer;
import ru.octol1ttle.flightassistant.computers.safety.VoidLevelComputer;

public class PitchController implements ITickableComputer {
    public static final float CLIMB_PITCH = 55.0f;
    public static final float DESCEND_PITCH = -35.0f;
    private final AirDataComputer data;
    private final StallComputer stall;
    private final TimeComputer time;
    private final VoidLevelComputer voidLevel;
    private final GPWSComputer gpws;
    public Float targetPitch = null;

    public PitchController(AirDataComputer data, StallComputer stall, TimeComputer time, VoidLevelComputer voidLevel, GPWSComputer gpws) {
        this.data = data;
        this.stall = stall;
        this.time = time;
        this.voidLevel = voidLevel;
        this.gpws = gpws;
    }

    @Override
    public void tick() {
        if (!data.canAutomationsActivate()) {
            return;
        }

        smoothSetPitch(targetPitch, time.deltaTime);

        if (data.pitch > stall.maximumSafePitch) {
            smoothSetPitch(stall.maximumSafePitch, time.deltaTime);
            return;
        }
        if (data.pitch < voidLevel.minimumSafePitch) {
            smoothSetPitch(voidLevel.minimumSafePitch, time.deltaTime);
            return;
        }
        if (gpws.shouldRecover()) {
            smoothSetPitch(90.0f, MathHelper.clamp(time.deltaTime / positiveMin(gpws.descentImpactTime, gpws.terrainImpactTime), 0.001f, 1.0f));
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

        float difference = pitch - data.pitch;

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

            newPitch = data.pitch + (pitch - data.pitch) * delta;
        }

        data.player.setPitch(-newPitch);
    }

    /**
     * Returns the lesser of the two provided numbers. If one of the numbers is less than zero, the other is returned instead.
     **/
    private float positiveMin(float a, float b) {
        if (a < 0.0f) {
            return b;
        }
        if (b < 0.0f) {
            return a;
        }

        return Math.min(a, b);
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
