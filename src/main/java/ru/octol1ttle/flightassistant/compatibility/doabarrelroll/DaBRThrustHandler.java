package ru.octol1ttle.flightassistant.compatibility.doabarrelroll;

import nl.enjarai.doabarrelroll.api.event.ThrustEvents;
import nl.enjarai.doabarrelroll.config.ModConfig;
import ru.octol1ttle.flightassistant.computers.api.IThrustHandler;
import ru.octol1ttle.flightassistant.computers.impl.TimeComputer;
import ru.octol1ttle.flightassistant.computers.impl.autoflight.AutoFlightController;
import ru.octol1ttle.flightassistant.computers.impl.autoflight.ThrustController;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;

public class DaBRThrustHandler implements IThrustHandler {
    private final AutoFlightController autoflight = ComputerRegistry.resolve(AutoFlightController.class);
    private final ThrustController thrust = ComputerRegistry.resolve(ThrustController.class);
    private final TimeComputer time = ComputerRegistry.resolve(TimeComputer.class);

    public DaBRThrustHandler() {
        ThrustEvents.MODIFY_THRUST_INPUT.register(v -> {
            if (Math.abs(v) > 0.001f) {
                autoflight.disconnectAutoFirework(true);
            }
            return thrust.targetThrust += (float) (v * time.deltaTime * 0.5f);
        }, 10);
    }

    @Override
    public boolean canBeUsed() {
        return ModConfig.INSTANCE.getEnableThrust();
    }

    @Override
    public void reset() {
    }
}
