package ru.octol1ttle.flightassistant.computers.impl.autoflight.pitch;

import net.minecraft.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.octol1ttle.flightassistant.computers.api.IComputer;
import ru.octol1ttle.flightassistant.computers.api.IPitchController;
import ru.octol1ttle.flightassistant.computers.impl.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.impl.safety.PitchLimitComputer;
import ru.octol1ttle.flightassistant.config.ComputerConfig;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;

public class ProtectionsPitchController implements IComputer, IPitchController {
    private final AirDataComputer data = ComputerRegistry.resolve(AirDataComputer.class);
    private final PitchLimitComputer limit = ComputerRegistry.resolve(PitchLimitComputer.class);

    @Override
    public @Nullable Pair<@NotNull Float, @NotNull Float> getTargetPitch() {
        Pair<Float, Float> safePitches = limit.getSafePitches(ComputerConfig.ProtectionMode::recover);

        if (data.pitch() > safePitches.getRight()) {
            return new Pair<>(safePitches.getRight(), 1.0f);
        } else if (data.pitch() < safePitches.getLeft()) {
            return new Pair<>(safePitches.getLeft(), 1.0f);
        }

        return null;
    }

    @Override
    public Priority getPriority() {
        return Priority.HIGHEST;
    }

    @Override
    public String getId() {
        return "pitch_normal_law";
    }

    @Override
    public void reset() {
    }
}
