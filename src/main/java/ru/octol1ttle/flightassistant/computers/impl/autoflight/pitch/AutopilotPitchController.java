package ru.octol1ttle.flightassistant.computers.impl.autoflight.pitch;

import net.minecraft.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.octol1ttle.flightassistant.computers.api.IPitchController;
import ru.octol1ttle.flightassistant.computers.api.ITickableComputer;
import ru.octol1ttle.flightassistant.computers.impl.autoflight.AutoFlightComputer;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;

public class AutopilotPitchController implements ITickableComputer, IPitchController {
    private final AutoFlightComputer autoflight = ComputerRegistry.resolve(AutoFlightComputer.class);

    @Override
    public void tick() {
        // TODO
        autoflight.disconnectAutopilot(true);
    }

    @Override
    public @Nullable Pair<@NotNull Float, @NotNull Float> getTargetPitch() {
        return null;
    }

    @Override
    public Priority getPriority() {
        return Priority.NORMAL;
    }

    @Override
    public String getId() {
        return "autopilot_pitch_ctl";
    }

    @Override
    public void reset() {
        autoflight.disconnectAutopilot(true);
    }
}
