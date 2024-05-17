package ru.octol1ttle.flightassistant.computers.impl.safety;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Direction;
import ru.octol1ttle.flightassistant.computers.api.INormalLawProvider;
import ru.octol1ttle.flightassistant.computers.api.IPitchLimiter;
import ru.octol1ttle.flightassistant.computers.api.ITickableComputer;
import ru.octol1ttle.flightassistant.config.ComputerConfig;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;
import ru.octol1ttle.flightassistant.registries.events.ComputerRegisteredCallback;

public class PitchLimitComputer implements ITickableComputer, INormalLawProvider {
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
            if (ComputerRegistry.isFaulted(limiter.getClass())) {
                continue;
            }
            minimumSafePitch = Math.max(minimumSafePitch, limiter.getMinimumPitch());
            maximumSafePitch = Math.min(maximumSafePitch, limiter.getMaximumPitch());
        }
    }

    /**
     * Gets the pitches which are considered safe by pitch limiters satisfying a condition.
     * @param predicate The condition that pitch limiters' protection mode must satisfy in order to be considered
     * @return a pair of two floats: the first one is the minimum safe pitch, second is the maximum safe pitch
     */
    public Pair<Float, Float> getSafePitches(Predicate<ComputerConfig.ProtectionMode> predicate) {
        float minimum = -90.0f;
        float maximum = 90.0f;
        for (IPitchLimiter limiter : limiters) {
            if (ComputerRegistry.isFaulted(limiter.getClass()) || !predicate.test(limiter.getProtectionMode())) {
                continue;
            }

            minimum = Math.max(minimum, limiter.getMinimumPitch());
            maximum = Math.min(maximum, limiter.getMaximumPitch());
        }

        return new Pair<>(minimum, maximum);
    }

    public boolean blockPitchChange(Direction direction, Predicate<ComputerConfig.ProtectionMode> predicate) {
        for (IPitchLimiter limiter : limiters) {
            if (ComputerRegistry.isFaulted(limiter.getClass()) || !predicate.test(limiter.getProtectionMode())) {
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
