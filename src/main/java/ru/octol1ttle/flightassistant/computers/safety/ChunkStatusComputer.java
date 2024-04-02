package ru.octol1ttle.flightassistant.computers.safety;

import ru.octol1ttle.flightassistant.computers.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.ITickableComputer;
import ru.octol1ttle.flightassistant.computers.TimeComputer;
import ru.octol1ttle.flightassistant.config.FAConfig;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;

public class ChunkStatusComputer implements ITickableComputer {
    private static final int WARN_THRESHOLD = 2500; // MS
    private static final int PROTECT_THRESHOLD = 5000; // MS
    private final AirDataComputer data = ComputerRegistry.resolve(AirDataComputer.class);
    private final TimeComputer time = ComputerRegistry.resolve(TimeComputer.class);

    private float lastLoaded;
    private float lastDiff;

    @Override
    public void tick() {
        if (!data.isFlying()) {
            reset();
            return;
        }

        if (data.isCurrentChunkLoaded && time.millis != null) {
            lastLoaded = time.millis;
        }

        if (time.millis != null && lastLoaded > 0.0f) {
            lastDiff = time.millis - lastLoaded;
        }
    }

    public boolean shouldPreserveAltitude() {
        return FAConfig.computer().unloadedChunkProtection.recover() && lastDiff >= PROTECT_THRESHOLD;
    }

    public boolean shouldWarn() {
        return lastDiff >= WARN_THRESHOLD;
    }

    @Override
    public String getId() {
        return "chunk_state";
    }

    @Override
    public void reset() {
        lastDiff = 0f;
        lastLoaded = 0f;
    }
}
