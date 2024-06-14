package ru.octol1ttle.flightassistant.computers.impl.autoflight;

import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2d;
import ru.octol1ttle.flightassistant.FAMathHelper;
import ru.octol1ttle.flightassistant.computers.api.ControlInput;
import ru.octol1ttle.flightassistant.computers.api.IAutopilotProvider;
import ru.octol1ttle.flightassistant.computers.api.IHeadingController;
import ru.octol1ttle.flightassistant.computers.api.IPitchController;
import ru.octol1ttle.flightassistant.computers.api.IRollController;
import ru.octol1ttle.flightassistant.computers.api.IThrustController;
import ru.octol1ttle.flightassistant.computers.api.ITickableComputer;
import ru.octol1ttle.flightassistant.computers.api.InputPriority;
import ru.octol1ttle.flightassistant.computers.api.ThrustControlInput;
import ru.octol1ttle.flightassistant.computers.impl.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.impl.FlightPhaseComputer;
import ru.octol1ttle.flightassistant.computers.impl.navigation.FlightPlanner;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;

public class AutopilotComputer implements ITickableComputer, IAutopilotProvider, IPitchController, IHeadingController, IRollController, IThrustController {
    private final AirDataComputer data = ComputerRegistry.resolve(AirDataComputer.class);
    private final AutoFlightController autoflight = ComputerRegistry.resolve(AutoFlightController.class);
    private final FlightPhaseComputer phase = ComputerRegistry.resolve(FlightPhaseComputer.class);
    private final FlightPlanner plan = ComputerRegistry.resolve(FlightPlanner.class);
    private final ThrustController thrust = ComputerRegistry.resolve(ThrustController.class);
    public Text verticalMode;
    public Text lateralMode;
    public Text thrustMode;
    private Float targetPitch;
    private Float targetHeading;
    private Float targetThrust;

    @Override
    public void tick() {
        if (phase.phase == FlightPhaseComputer.FlightPhase.TAKEOFF
                || phase.phase == FlightPhaseComputer.FlightPhase.GO_AROUND && data.heightAboveGround() < 15.0f) {
            setTargetThrust(1.0f, Text.translatable("mode.flightassistant.thrust.toga"));
            setTargetPitch(PitchController.CLIMB_PITCH, Text.translatable("mode.flightassistant.vert.climb.optimum"));
            setTargetHeading(data.heading(), Text.translatable("mode.flightassistant.lat.current"));

            return;
        }

        tickLateral();

        Integer targetAltitude = autoflight.getTargetAltitude();
        if (targetAltitude == null) {
            targetPitch = null;
            targetThrust = null;
            return;
        }

        boolean useThrustConservatively = thrust.thrustHandler instanceof FireworkController || !thrust.thrustHandler.canBeUsed();
        // TODO
        throw new NotImplementedException();
    }

    private void tickLateral() {
        Vector2d planPos = plan.getTargetPosition();
        if (autoflight.selectedHeading != null) {
            setTargetHeading(Float.valueOf(autoflight.selectedHeading), Text.translatable("mode.flightassistant.lat.selected", autoflight.selectedHeading));
        } else if (planPos != null) {
            setTargetHeading(plan.getManagedHeading(), Text.translatable("mode.flightassistant.lat.managed", (int) planPos.x, (int) planPos.y));
        }
    }

    private Float computeTargetPitch() {
        Integer targetAltitude = autoflight.getTargetAltitude();
        if (targetAltitude == null) {
            return null;
        }
        Vector2d planPos = plan.getTargetPosition();

        float diff = targetAltitude - data.altitude();
        boolean landing = phase.phase == FlightPhaseComputer.FlightPhase.LAND;
        if (!landing && diff > -10.0f && diff < 5.0f) {
            return (PitchController.GLIDE_PITCH + PitchController.ALTITUDE_PRESERVE_PITCH) * 0.5f;
        }

        if (data.altitude() < targetAltitude) {
            return computeClimbPitch(diff, planPos);
        }

        if (planPos == null || autoflight.selectedAltitude != null) {
            if (diff > -15.0f) {
                return PitchController.GLIDE_PITCH;
            }

            return (PitchController.GLIDE_PITCH + PitchController.DESCEND_PITCH) * 0.5f;
        }

        float degrees = FAMathHelper.toDegrees(
                MathHelper.atan2(
                        diff,
                        Vector2d.distance(data.position().x, data.position().z, planPos.x, planPos.y)
                )
        );
        if (!landing && diff > -15.0f) {
            return Math.max(PitchController.GLIDE_PITCH, degrees);
        }

        return degrees;
    }

    private float computeClimbPitch(float diff, Vector2d target) {
        if (target == null || autoflight.selectedAltitude != null) {
            if (diff <= 15.0f) {
                return PitchController.ALTITUDE_PRESERVE_PITCH;
            }

            return (PitchController.ALTITUDE_PRESERVE_PITCH + PitchController.CLIMB_PITCH) * 0.5f;
        }

        float degrees = Math.max(5.0f, FAMathHelper.toDegrees(
                MathHelper.atan2(
                        diff,
                        Vector2d.distance(data.position().x, data.position().z, target.x, target.y)
                )
        ));
        if (diff <= 15.0f) {
            return Math.min(PitchController.ALTITUDE_PRESERVE_PITCH, degrees);
        }

        return degrees;
    }

    public Float getTargetPitch() {
        return targetPitch;
    }

    public void setTargetPitch(Float targetPitch, Text mode) {
        this.targetPitch = targetPitch;
        this.verticalMode = mode;
    }

    public Float getTargetHeading() {
        return targetHeading;
    }

    public void setTargetHeading(Float targetHeading, Text mode) {
        this.targetHeading = targetHeading;
        this.lateralMode = mode;
    }

    public void setTargetThrust(Float targetThrust, Text mode) {
        this.targetThrust = targetThrust;
        this.thrustMode = mode;
    }

    @Override
    public @Nullable ControlInput getPitchInput() {
        if (!autoflight.autoPilotEnabled || targetPitch == null) {
            return null;
        }

        return new ControlInput(targetPitch, 1.0f, InputPriority.NORMAL);
    }

    @Override
    public @Nullable ControlInput getHeadingInput() {
        if (!autoflight.autoPilotEnabled || targetHeading == null) {
            return null;
        }

        return new ControlInput(targetHeading, 1.0f, InputPriority.NORMAL);
    }

    @Override
    public @Nullable ControlInput getRollInput() {
        if (!autoflight.autoPilotEnabled || targetPitch == null && targetHeading == null) {
            return null;
        }

        return new ControlInput(0.0f, 2.0f, InputPriority.NORMAL);
    }

    @Override
    public ThrustControlInput getThrustInput() {
        if (!autoflight.autoThrustEnabled || targetThrust == null) {
            return null;
        }

        return new ThrustControlInput(targetThrust, InputPriority.NORMAL);
    }

    @Override
    public String getId() {
        return "autopilot_ctl";
    }

    @Override
    public void reset() {
        targetPitch = null;
        verticalMode = null;
        targetHeading = null;
        lateralMode = null;
        targetThrust = null;

        autoflight.disconnectAutoFirework(true);
        autoflight.disconnectAutopilot(true);
    }
}
