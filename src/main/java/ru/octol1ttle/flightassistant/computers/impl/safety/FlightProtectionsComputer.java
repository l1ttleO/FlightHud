package ru.octol1ttle.flightassistant.computers.impl.safety;

import net.minecraft.util.Pair;
import org.jetbrains.annotations.Nullable;
import ru.octol1ttle.flightassistant.computers.api.ControlInput;
import ru.octol1ttle.flightassistant.computers.api.INormalLawProvider;
import ru.octol1ttle.flightassistant.computers.api.IPitchController;
import ru.octol1ttle.flightassistant.computers.api.ITickableComputer;
import ru.octol1ttle.flightassistant.computers.api.InputPriority;
import ru.octol1ttle.flightassistant.computers.impl.AirDataComputer;
import ru.octol1ttle.flightassistant.config.ComputerConfig;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;

public class FlightProtectionsComputer implements ITickableComputer, IPitchController, INormalLawProvider {
    private final AirDataComputer data = ComputerRegistry.resolve(AirDataComputer.class);
    private final PitchLimitComputer limit = ComputerRegistry.resolve(PitchLimitComputer.class);
    public FlightControlLaw law = FlightControlLaw.NORMAL;

    @Override
    public void tick() {
        if (ComputerRegistry.anyFaulted(computer -> computer instanceof INormalLawProvider)) {
            throw new IllegalStateException("Normal law provider faulted; entering alternate law");
        }

        law = FlightControlLaw.NORMAL;
    }

    @Override
    public @Nullable ControlInput getPitchInput() {
        Pair<Float, Float> safePitches = limit.getSafePitches(ComputerConfig.ProtectionMode::recover);

        if (data.pitch() > safePitches.getRight()) {
            return new ControlInput(safePitches.getRight(), 1.0f, InputPriority.HIGHEST);
        } else if (data.pitch() < safePitches.getLeft()) {
            return new ControlInput(safePitches.getLeft(), 1.0f, InputPriority.HIGHEST);
        }

        return null;
    }

    @Override
    public String getFaultTextBaseKey() {
        return "alerts.flightassistant.fault.computers.flight_prot";
    }

    @Override
    public void reset() {
        law = FlightControlLaw.ALTERNATE;
    }

    public enum FlightControlLaw {
        NORMAL,
        ALTERNATE
    }
}
