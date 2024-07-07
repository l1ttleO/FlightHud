package ru.octol1ttle.flightassistant.computers.impl.autoflight;

import org.jetbrains.annotations.Nullable;
import ru.octol1ttle.flightassistant.computers.api.IAutopilotProvider;
import ru.octol1ttle.flightassistant.computers.api.ITickableComputer;
import ru.octol1ttle.flightassistant.computers.impl.navigation.FlightPlanner;
import ru.octol1ttle.flightassistant.computers.impl.safety.FlightProtectionsComputer;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;


public class AutoFlightController implements ITickableComputer, IAutopilotProvider {
    private final FlightProtectionsComputer prot = ComputerRegistry.resolve(FlightProtectionsComputer.class);
    private final FlightPlanner plan = ComputerRegistry.resolve(FlightPlanner.class);

    public boolean flightDirectorsEnabled = false;
    public boolean autoThrustEnabled = false;
    public boolean autoPilotEnabled = false;

    public boolean athrDisconnectionForced = false;
    public boolean apDisconnectionForced = false;

    public Integer selectedSpeed;
    public Integer selectedAltitude;
    public Integer selectedHeading;

    @Override
    public void tick() {
        if (prot.law != FlightProtectionsComputer.FlightControlLaw.NORMAL
                || ComputerRegistry.anyFaulted(computer -> computer instanceof IAutopilotProvider)) {
            disconnectAutoThrust(true);
            disconnectAutopilot(true);
        }
    }

    public @Nullable Integer getTargetSpeed() {
        return selectedSpeed != null ? selectedSpeed : plan.getManagedSpeed();
    }

    public @Nullable Integer getTargetAltitude() {
        return selectedAltitude != null ? selectedAltitude : plan.getManagedAltitude();
    }

    public @Nullable Float getTargetHeading() {
        return selectedHeading != null ? Float.valueOf(selectedHeading) : plan.getManagedHeading();
    }

    public void disconnectAutopilot(boolean force) {
        if (autoPilotEnabled) {
            autoPilotEnabled = false;
            apDisconnectionForced = force;
        }
    }

    public void disconnectAutoThrust(boolean force) {
        if (autoThrustEnabled) {
            autoThrustEnabled = false;
            athrDisconnectionForced = force;
        }
    }

    @Override
    public String getFaultTextBaseKey() {
        return "alerts.flightassistant.fault.computers.auto_flt";
    }

    @Override
    public void reset() {
        flightDirectorsEnabled = false;
        disconnectAutoThrust(true);
        disconnectAutopilot(true);
    }
}
