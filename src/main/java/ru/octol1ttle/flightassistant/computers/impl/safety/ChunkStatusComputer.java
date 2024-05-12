package ru.octol1ttle.flightassistant.computers.impl.safety;

import org.jetbrains.annotations.Nullable;
import ru.octol1ttle.flightassistant.computers.api.ControlInput;
import ru.octol1ttle.flightassistant.computers.api.IPitchController;
import ru.octol1ttle.flightassistant.computers.api.ITickableComputer;
import ru.octol1ttle.flightassistant.computers.api.InputPriority;
import ru.octol1ttle.flightassistant.computers.impl.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.impl.TimeComputer;
import ru.octol1ttle.flightassistant.computers.impl.autoflight.PitchController;
import ru.octol1ttle.flightassistant.config.FAConfig;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;

public class ChunkStatusComputer implements ITickableComputer, IPitchController {
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

    public boolean shouldWarn() {
        return lastDiff >= WARN_THRESHOLD;
    }

    @Override
    public @Nullable ControlInput getPitchInput() {
        if (FAConfig.computer().preserveAltitudeInUnloadedChunk && lastDiff >= PROTECT_THRESHOLD) {
            return new ControlInput(PitchController.ALTITUDE_PRESERVE_PITCH, 1.0f, InputPriority.HIGH);
        }
        return null;
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
