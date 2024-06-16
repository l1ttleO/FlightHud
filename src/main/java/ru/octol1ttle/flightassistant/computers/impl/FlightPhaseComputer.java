package ru.octol1ttle.flightassistant.computers.impl;

import net.minecraft.text.Text;
import ru.octol1ttle.flightassistant.computers.api.ITickableComputer;
import ru.octol1ttle.flightassistant.computers.impl.autoflight.AutoFlightController;
import ru.octol1ttle.flightassistant.computers.impl.navigation.FlightPlanner;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;

public class FlightPhaseComputer implements ITickableComputer {
    private final AirDataComputer data = ComputerRegistry.resolve(AirDataComputer.class);
    private final AutoFlightController autoflight = ComputerRegistry.resolve(AutoFlightController.class);
    private final FlightPlanner plan = ComputerRegistry.resolve(FlightPlanner.class);
    private Phase phase = Phase.UNKNOWN;

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void tick() {
        if (data.player().isOnGround()) {
            phase = Phase.ON_GROUND;
        }

        if (!data.isFlying()) {
            return;
        }

        if (phase == Phase.ON_GROUND) {
            phase = Phase.TAKEOFF;
        }

        if (phase == Phase.TAKEOFF && data.heightAboveGround() <= 15.0f) {
            return;
        }

        Integer cruiseAltitude = plan.getCruiseAltitude();
        Integer targetAltitude = autoflight.getTargetAltitude();
        if (cruiseAltitude == null || targetAltitude == null) {
            phase = Phase.UNKNOWN;
            return;
        }

        if (!isNearDestination()) {
            if (data.altitude() - targetAltitude <= 5.0f) {
                phase = Phase.CLIMB;
            } else {
                phase = Phase.DESCENT;
            }

            if (targetAltitude.equals(cruiseAltitude) && Math.abs(cruiseAltitude - data.altitude()) <= 5.0f) {
                phase = Phase.CRUISE;
            }
        }

        if (phase == Phase.GO_AROUND) {
            if (plan.getDistanceToWaypoint() > 150.0f) {
                phase = Phase.APPROACH;
            }
        } else {
            if (plan.isOnApproach()) {
                phase = Phase.APPROACH;
            }

            if (phase == Phase.APPROACH && plan.autolandAllowed) {
                phase = Phase.LAND;
            }
        }
    }

    public Phase get() {
        return this.phase;
    }

    private boolean isAboutToLand() {
        return phase == Phase.APPROACH || phase == Phase.LAND;
    }

    public boolean isNearDestination() {
        return isAboutToLand() || phase == Phase.GO_AROUND;
    }

    @Override
    public String getId() {
        return "flight_phase";
    }

    @Override
    public void reset() {
        phase = Phase.UNKNOWN;
    }

    public enum Phase {
        ON_GROUND("on_ground"),
        TAKEOFF("takeoff"),
        CLIMB("climb"),
        CRUISE("cruise"),
        DESCENT("descent"),
        APPROACH("approach"),
        LAND("land"),
        GO_AROUND("go_around"),
        UNKNOWN("");

        public final Text text;

        Phase(Text text) {
            this.text = text;
        }

        Phase(String key) {
            this(Text.translatable("status.flightassistant.phase." + key));
        }
    }
}
