package ru.octol1ttle.flightassistant.computers.impl.safety;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Direction;
import ru.octol1ttle.flightassistant.computers.api.IComputer;
import ru.octol1ttle.flightassistant.computers.api.IPitchLimiter;
import ru.octol1ttle.flightassistant.computers.api.ITickableComputer;
import ru.octol1ttle.flightassistant.config.ComputerConfig;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;
import ru.octol1ttle.flightassistant.registries.events.ComputerRegisteredCallback;

public class PitchLimitComputer implements ITickableComputer {
    private final List<IPitchLimiter> limiters = new ArrayList<>();
    public float minimumSafePitch = -90.0f;
    public float maximumSafePitch = 90.0f;

    public PitchLimitComputer() {
        ComputerRegisteredCallback.EVENT.register((computer -> {
            if (computer instanceof IPitchLimiter limiter) {
                limiters.add(limiter);
            }
        }));
    }

    @Override
    public void tick() {
        minimumSafePitch = -90.0f;
        maximumSafePitch = 90.0f;
        for (IPitchLimiter limiter : limiters) {
            if (limiter instanceof IComputer computer && ComputerRegistry.isFaulted(computer.getClass())) {
                continue;
            }
            minimumSafePitch = Math.max(minimumSafePitch, limiter.getMinimumPitch());
            maximumSafePitch = Math.min(maximumSafePitch, limiter.getMaximumPitch());
        }
    }

    /**
     * Gets the pitches which are considered safe by pitch limiters satisfying a condition.
     * @param condition The condition that pitch limiters' protection mode must satisfy in order to be considered
     * @return a pair of two floats: the first one is the minimum safe pitch, second is the maximum safe pitch
     */
    public Pair<Float, Float> getSafePitches(Function<ComputerConfig.ProtectionMode, Boolean> condition) {
        float minimum = -90.0f;
        float maximum = 90.0f;
        for (IPitchLimiter limiter : limiters) {
            if (!condition.apply(limiter.getProtectionMode()) || limiter instanceof IComputer computer && ComputerRegistry.isFaulted(computer.getClass())) {
                continue;
            }

            minimum = Math.max(minimum, limiter.getMinimumPitch());
            maximum = Math.min(maximum, limiter.getMaximumPitch());
        }

        return new Pair<>(minimum, maximum);
    }

    public boolean blockPitchChange(Direction direction, Function<ComputerConfig.ProtectionMode, Boolean> condition) {
        for (IPitchLimiter limiter : limiters) {
            if (!condition.apply(limiter.getProtectionMode()) || limiter instanceof IComputer computer && ComputerRegistry.isFaulted(computer.getClass())) {
                continue;
            }

            if (limiter.blockPitchChange(direction)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String getId() {
        return "pitch_limit";
    }

    @Override
    public void reset() {
        minimumSafePitch = -90.0f;
        maximumSafePitch = 90.0f;
    }
}
