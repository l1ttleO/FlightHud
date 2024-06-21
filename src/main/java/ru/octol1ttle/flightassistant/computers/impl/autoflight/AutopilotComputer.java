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
import ru.octol1ttle.flightassistant.computers.impl.TimeComputer;
import ru.octol1ttle.flightassistant.computers.impl.navigation.FlightPlanner;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;

public class AutopilotComputer implements ITickableComputer, IAutopilotProvider, IPitchController, IHeadingController, IRollController, IThrustController {
    private static final float THRUST_TOGA = 1.0f;
    private static final float THRUST_CLIMB = 0.9f;
    private static final float THRUST_CLIMB_REDUCED = 0.75f;
    private static final float THRUST_APPROACH = 0.75f;
    private static final float THRUST_APPROACH_REDUCED = 0.25f;
    private static final float THRUST_IDLE = 0.0f;
    private static final float THRUST_LAND = -0.2f;

    private final AirDataComputer data = ComputerRegistry.resolve(AirDataComputer.class);
    private final AutoFlightController autoflight = ComputerRegistry.resolve(AutoFlightController.class);
    private final FlightPhaseComputer phase = ComputerRegistry.resolve(FlightPhaseComputer.class);
    private final FlightPlanner plan = ComputerRegistry.resolve(FlightPlanner.class);
    private final ThrustController thrust = ComputerRegistry.resolve(ThrustController.class);
    private final TimeComputer time = ComputerRegistry.resolve(TimeComputer.class);

    public Text verticalMode;
    public Text lateralMode;
    public Text thrustMode;

    private Float targetPitch;
    private Float targetHeading;
    private Float targetThrust;

    private Float togaHeading;

    public boolean autolandInProgress;

    @Override
    public void tick() {
        setTargetThrust(null, Text.empty());
        setTargetPitch(null, Text.empty());
        setTargetHeading(null, Text.empty());

        if (phase.get() != Phase.APPROACH && phase.get() != Phase.LAND) {
            autolandInProgress = false;
        }

        if (plan.autolandAllowed) {
            autolandInProgress = true;
        }

        if (phase.get() == Phase.ON_GROUND) {
            togaHeading = null;
            return;
        }

        if (phase.get() == Phase.TAKEOFF
                || phase.get() == Phase.GO_AROUND) {
            if (togaHeading == null) {
                togaHeading = data.heading();
            }

            boolean useReducedThrust = thrust.getThrustHandler().isFireworkLike();
            setTargetThrust(THRUST_TOGA, Text.translatable("mode.flightassistant.thrust.toga"));
            if (data.heightAboveGround() < 15.0f) {
                setTargetPitch(useReducedThrust ? 55.0f : 35.0f, Text.translatable("mode.flightassistant.vert.climb.optimum"));
            } else {
                setTargetPitch(useReducedThrust ? 10.0f : 5.0f, Text.translatable("mode.flightassistant.vert.go_around"));
            }

            String lat = phase.get() == Phase.GO_AROUND ? ".go_around" : ".takeoff";
            setTargetHeading(togaHeading, Text.translatable("mode.flightassistant.lat" + lat, togaHeading.intValue()));

            return;
        }

        togaHeading = null;
        tickLateral();

        Integer targetSpeed = autoflight.getTargetSpeed();
        if (!thrust.getThrustHandler().canBeUsed()) {
            setTargetThrust(THRUST_IDLE, Text.translatable("mode.flightassistant.thrust.unavailable"));
            setTargetPitch(PitchController.GLIDE_PITCH, Text.translatable("mode.flightassistant.vert.glide"));
        } else {
            tickVertical(targetSpeed);
            tickSpeed(targetSpeed);
        }
    }

    private void tickVertical(Integer targetSpeed) {
        Integer targetAltitude = autoflight.getTargetAltitude();
        if (targetAltitude != null) {
            float diff = targetAltitude - data.altitude();
            float speedAdjustment = targetSpeed != null ? data.speed() - targetSpeed : 0.0f;
            if (autoflight.selectedAltitude != null) {
                tickSelectedAltitude(diff, speedAdjustment);
            } else if (plan.getTargetPosition() != null) {
                tickManagedAltitude(targetAltitude, speedAdjustment, plan.getTargetPosition());
            }
        }
    }

    private void tickSelectedAltitude(float diff, float speedAdjustment) {
        boolean useReducedThrust = thrust.getThrustHandler().isFireworkLike(); // I wish I didn't have to account for this
        float abs = Math.abs(diff);

        float pitch;
        if (diff > 0) {
            if (useReducedThrust) {
                setTargetThrust(THRUST_CLIMB_REDUCED, Text.translatable("mode.flightassistant.thrust.climb_reduced"));
                pitch = 55.0f;
                if (abs <= 200) {
                    pitch -= 30.0f * MathHelper.clamp((200.0f - abs) / 100.0f, 0.0f, 1.0f);
                }
                if (abs <= 100) {
                    pitch -= 15.0f * MathHelper.clamp((100.0f - abs) / 95.0f, 0.0f, 1.0f);
                }
            } else {
                setTargetThrust(THRUST_CLIMB, Text.translatable("mode.flightassistant.thrust.climb"));
                pitch = 35.0f + speedAdjustment;
                if (abs <= 200) {
                    pitch -= 25.0f * MathHelper.clamp((200.0f - abs) / 100.0f, 0.0f, 1.0f);
                }
                if (abs <= 100) {
                    pitch -= (5.0f + speedAdjustment) * MathHelper.clamp((100.0f - abs) / 100.0f, 0.0f, 1.0f);
                }
            }
            setTargetPitch(pitch, Text.translatable("mode.flightassistant.vert.climb.selected", autoflight.selectedAltitude));
        } else {
            setTargetThrust(THRUST_IDLE, Text.translatable("mode.flightassistant.thrust.idle"));
            if (useReducedThrust) {
                pitch = -35.0f;
                if (abs <= 100) {
                    pitch += 15.0f * MathHelper.clamp((100.0f - abs) / 50.0f, 0.0f, 1.0f);
                }
                if (abs <= 50) {
                    pitch += 10.0f * MathHelper.clamp((50.0f - abs) / 40.0f, 0.0f, 1.0f);
                }
            } else {
                pitch = -35.0f + speedAdjustment;
                if (abs <= 100) {
                    pitch += 20.0f * MathHelper.clamp((100.0f - abs) / 50.0f, 0.0f, 1.0f);
                }
                if (abs <= 50) {
                    pitch += (20.0f + speedAdjustment) * MathHelper.clamp((50.0f - abs) / 50.0f, 0.0f, 1.0f);
                }
            }
            setTargetPitch(pitch, Text.translatable("mode.flightassistant.vert.descend.selected", autoflight.selectedAltitude));
        }

        if (abs <= 10.0f) {
            setTargetThrust(THRUST_CLIMB, Text.translatable("mode.flightassistant.thrust.climb"));
            setTargetPitch(pitch, Text.translatable("mode.flightassistant.vert.hold.selected", autoflight.selectedAltitude));
        }
    }

    private void tickManagedAltitude(Integer targetAltitude, float speedAdjustment, Vector2d pos) {
        boolean useReducedThrust = thrust.getThrustHandler().isFireworkLike();
        float diff = targetAltitude - data.altitude();
        double distance = Vector2d.distance(data.position().x, data.position().z, pos.x, pos.y);
        float degrees = FAMathHelper.toDegrees(MathHelper.atan2(diff, distance));

        float pitch;

        if (autolandInProgress) {
            pitch = degrees;
            setTargetThrust(THRUST_LAND, Text.translatable("mode.flightassistant.thrust.land"));
            setTargetPitch(pitch, Text.translatable("mode.flightassistant.vert.land", targetAltitude));
        } else if (diff > 0) {
            if (useReducedThrust) {
                setTargetThrust(THRUST_CLIMB_REDUCED, Text.translatable("mode.flightassistant.thrust.climb_reduced"));
                pitch = MathHelper.clamp(degrees, 10.0f, PitchController.CLIMB_PITCH);
            } else {
                setTargetThrust(THRUST_CLIMB, Text.translatable("mode.flightassistant.thrust.climb"));
                pitch = MathHelper.clamp(degrees + speedAdjustment, 5.0f, 35.0f);
            }
            setTargetPitch(pitch, Text.translatable("mode.flightassistant.vert.climb.managed", targetAltitude));
        } else {
            setTargetThrust(THRUST_IDLE, Text.translatable("mode.flightassistant.thrust.idle"));
            if (useReducedThrust) {
                pitch = MathHelper.clamp(degrees, PitchController.DESCEND_PITCH, PitchController.GLIDE_PITCH);
            } else {
                pitch = MathHelper.clamp(degrees + 5.0f + speedAdjustment, PitchController.DESCEND_PITCH, 5.0f);
            }
            setTargetPitch(pitch, Text.translatable("mode.flightassistant.vert.descend.managed", targetAltitude));
        }

        if (phase.get() == Phase.APPROACH) {
            if (useReducedThrust) {
                setTargetThrust(THRUST_APPROACH_REDUCED, Text.translatable("mode.flightassistant.thrust.approach_reduced"));
            } else {
                setTargetThrust(THRUST_APPROACH, Text.translatable("mode.flightassistant.thrust.approach"));
            }
        }

        if (!plan.isOnApproach() && Math.abs(diff) <= 10.0f) {
            if (useReducedThrust) {
                setTargetThrust(THRUST_CLIMB_REDUCED, Text.translatable("mode.flightassistant.thrust.climb_reduced"));
            } else {
                setTargetThrust(THRUST_CLIMB, Text.translatable("mode.flightassistant.thrust.climb"));
            }
            setTargetPitch(pitch, targetAltitude.equals(plan.getCruiseAltitude())
                    ? Text.translatable("mode.flightassistant.vert.hold.cruise", targetAltitude)
                    : Text.translatable("mode.flightassistant.vert.hold.managed", targetAltitude));
        }
    }

    private void tickSpeed(Integer targetSpeed) {
        if (targetSpeed == null) {
            return;
        }

        float diff = targetSpeed - data.speed();

        float thr = MathHelper.clamp(thrust.getTargetThrust() + diff * 0.01f * time.deltaTime, -0.1f, THRUST_CLIMB);
        if (thrust.getThrustHandler().isFireworkLike()) {
            thr = targetSpeed / FireworkController.FIREWORK_SPEED;
        }
        setTargetThrust(thr, Text.translatable("mode.flightassistant.thrust.speed.selected", targetSpeed));
    }

    private void tickLateral() {
        Vector2d planPos = plan.getTargetPosition();
        if (autoflight.selectedHeading != null) {
            setTargetHeading(Float.valueOf(autoflight.selectedHeading), Text.translatable("mode.flightassistant.lat.selected", autoflight.selectedHeading));
        } else if (planPos != null) {
            setTargetHeading(plan.getManagedHeading(), Text.translatable(
                    phase.get() == Phase.APPROACH ? "mode.flightassistant.lat.approach" : "mode.flightassistant.lat.managed",
                    (int) planPos.x, (int) planPos.y
            ));
        }
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

        autolandInProgress = false;

        autoflight.disconnectAutoThrust(true);
        autoflight.disconnectAutopilot(true);
    }
}
