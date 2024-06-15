package ru.octol1ttle.flightassistant.computers.impl.autoflight;

import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
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
import ru.octol1ttle.flightassistant.computers.impl.FlightPhaseComputer.Phase;
import ru.octol1ttle.flightassistant.computers.impl.navigation.FlightPlanner;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;

public class AutopilotComputer implements ITickableComputer, IAutopilotProvider, IPitchController, IHeadingController, IRollController, IThrustController {
    private static final float THRUST_CLIMB = 0.9f;
    private static final float THRUST_CLIMB_REDUCED = 0.75f;

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

    private Float togaHeading;

    @Override
    public void tick() {
        if (phase.phase == Phase.TAKEOFF
                || phase.phase == Phase.GO_AROUND && data.heightAboveGround() < 15.0f) {
            if (togaHeading == null) {
                togaHeading = data.heading();
            }

            setTargetThrust(1.0f, Text.translatable("mode.flightassistant.thrust.toga"));
            setTargetPitch(55.0f, Text.translatable("mode.flightassistant.vert.climb.optimum"));

            String lat = phase.phase == Phase.TAKEOFF ? ".takeoff" : ".go_around";
            setTargetHeading(togaHeading, Text.translatable("mode.flightassistant.lat" + lat, togaHeading.intValue()));

            return;
        }

        togaHeading = null;
        setTargetHeading(null, Text.empty());
        tickLateral();

        Integer targetSpeed = autoflight.getTargetSpeed();
        setTargetThrust(null, Text.empty());

        setTargetPitch(null, Text.empty());
        Integer targetAltitude = autoflight.getTargetAltitude();
        if (targetAltitude == null) {
            return;
        }

        float diff = targetAltitude - data.altitude();

        boolean useReducedThrust = thrust.getThrustHandler().isFireworkLike(); // I wish I didn't have to account for this
        float climbThrust = useReducedThrust ? THRUST_CLIMB_REDUCED : THRUST_CLIMB;
        String thrustSuffix = useReducedThrust ? "_reduced" : "";

        if (autoflight.selectedAltitude != null) {
            float speedAdjustment = targetSpeed != null ? data.speed() - targetSpeed : 0.0f;
            if (targetAltitude >= data.altitude()) {
                setTargetThrust(climbThrust, Text.translatable("mode.flightassistant.thrust.climb" + thrustSuffix));
                float pitch;
                if (useReducedThrust) {
                    pitch = 47.5f - 47.5f * (Math.max(20.0f - diff, 0.0f) / 15.0f) + 7.5f;
                } else {
                    pitch = 55.0f - 55.0f * (Math.max(15.0f - diff, 0.0f) / 15.0f) + speedAdjustment;
                }
                setTargetPitch(pitch, Text.translatable("mode.flightassistant.vert.climb.selected"));
            } else {
                setTargetThrust(climbThrust, Text.translatable("mode.flightassistant.thrust.descend" + thrustSuffix));
            }
        }

        if (targetAltitude > data.altitude()) {

        }

        // overwrite any thrust setting if we have a target speed
        tickSpeed(targetSpeed);
    }

    private void tickSpeed(Integer targetSpeed) {
        if (targetSpeed == null) {
            return;
        }

        float diff = Math.abs(targetSpeed - data.speed());

        float thr = targetSpeed > data.speed() ? THRUST_CLIMB : -0.5f;
        if (thrust.getThrustHandler().isFireworkLike()) {
            thr = targetSpeed / FireworkController.FIREWORK_SPEED;
        }
        setTargetThrust(thr * Math.min(diff * 0.1f, 1.0f), Text.translatable("mode.flightassistant.thrust.speed.selected", targetSpeed));
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
        boolean landing = phase.phase == Phase.LAND;
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
        return "autopilot";
    }

    @Override
    public void reset() {
        verticalMode = null;
        lateralMode = null;
        thrustMode = null;

        targetPitch = null;
        targetHeading = null;
        targetThrust = null;
        togaHeading = null;

        autoflight.disconnectAutoFirework(true);
        autoflight.disconnectAutopilot(true);
    }
}
