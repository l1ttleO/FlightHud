package ru.octol1ttle.flightassistant.compatibility.doabarrelroll;

import nl.enjarai.doabarrelroll.api.event.ThrustEvents;
import nl.enjarai.doabarrelroll.config.ModConfig;
import ru.octol1ttle.flightassistant.computers.api.IThrustHandler;
import ru.octol1ttle.flightassistant.computers.impl.TimeComputer;
import ru.octol1ttle.flightassistant.computers.impl.autoflight.AutoFlightController;
import ru.octol1ttle.flightassistant.computers.impl.autoflight.ThrustController;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;

public class DaBRThrustHandler implements IThrustHandler, ThrustEvents.ModifyThrustInputEvent {
    private final AutoFlightController autoflight = ComputerRegistry.resolve(AutoFlightController.class);
    private final ThrustController thrust = ComputerRegistry.resolve(ThrustController.class);

    public DaBRThrustHandler() {
        ThrustEvents.MODIFY_THRUST_INPUT.register(this, 10);
    }

    @Override
    public double modify(double v) {
        if (ComputerRegistry.isFaulted(ThrustController.class) || ComputerRegistry.isFaulted(TimeComputer.class)) {
            return v;
        }
        if (Math.abs(v) > 0.001) {
            autoflight.disconnectAutoThrust(true);
        }
        thrust.addThrustTick((float) v);
        return thrust.getThrust();
    }

    @Override
    public boolean enabled() {
        return ModConfig.INSTANCE.getEnableThrust();
    }

    @Override
    public boolean canBeUsed() {
        return true;
    }

    @Override
    public boolean isFireworkLike() {
        return false;
    }

    @Override
    public void reset() {
    }
}
