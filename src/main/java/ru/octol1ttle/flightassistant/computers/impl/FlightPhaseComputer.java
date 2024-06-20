package ru.octol1ttle.flightassistant.computers.impl;

import net.minecraft.text.Text;
import ru.octol1ttle.flightassistant.computers.api.ITickableComputer;
import ru.octol1ttle.flightassistant.computers.impl.autoflight.AutoFlightController;
import ru.octol1ttle.flightassistant.computers.impl.autoflight.ThrustController;
import ru.octol1ttle.flightassistant.computers.impl.navigation.FlightPlanner;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;

public class FlightPhaseComputer implements ITickableComputer {
    private final AirDataComputer data = ComputerRegistry.resolve(AirDataComputer.class);
    private final AutoFlightController autoflight = ComputerRegistry.resolve(AutoFlightController.class);
    private final FlightPlanner plan = ComputerRegistry.resolve(FlightPlanner.class);
    private final ThrustController thrust = ComputerRegistry.resolve(ThrustController.class);
    private Phase phase = Phase.UNKNOWN;


    @Override
    public void tick() {
        phase = computePhase();
    }

    private Phase computePhase() {
        if (data.player().isOnGround() || !data.isFlying()) {
            return Phase.ON_GROUND;
        }

        if (phase == Phase.ON_GROUND) {
            return Phase.TAKEOFF;
        }

        if (phase == Phase.TAKEOFF && data.heightAboveGround() <= 15.0f) {
            return Phase.TAKEOFF;
        }

        Integer cruiseAltitude = plan.getCruiseAltitude();
        Integer targetAltitude = autoflight.getTargetAltitude();
        if (cruiseAltitude == null || targetAltitude == null) {
            return Phase.UNKNOWN;
        }

        if (!plan.isOnApproach() && phase != Phase.GO_AROUND) {
            if (targetAltitude.equals(cruiseAltitude) && Math.abs(cruiseAltitude - data.altitude()) <= 5.0f) {
                return Phase.CRUISE;
            }
            if (data.altitude() - targetAltitude <= 5.0f) {
                return Phase.CLIMB;
            }
            return Phase.DESCENT;
        }

        if (phase == Phase.GO_AROUND) {
            Double distance = plan.getDistanceToWaypoint();
            if (distance != null && distance > 150.0f && thrust.getTargetThrust() < 0.99f) {
                return Phase.APPROACH;
            }
        } else {
            if (thrust.getTargetThrust() >= 0.99f) {
                return Phase.GO_AROUND;
            }

            return plan.autolandAllowed ? Phase.LAND : Phase.APPROACH;
        }

        return phase;
    }

    public Phase get() {
        return this.phase;
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
