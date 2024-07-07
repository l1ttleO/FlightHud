package ru.octol1ttle.flightassistant.computers.impl.autoflight;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.util.math.MathHelper;
import ru.octol1ttle.flightassistant.computers.api.ControlInput;
import ru.octol1ttle.flightassistant.computers.api.IAutopilotProvider;
import ru.octol1ttle.flightassistant.computers.api.IPitchController;
import ru.octol1ttle.flightassistant.computers.api.ITickableComputer;
import ru.octol1ttle.flightassistant.computers.api.InputPriority;
import ru.octol1ttle.flightassistant.computers.impl.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.impl.TimeComputer;
import ru.octol1ttle.flightassistant.computers.impl.safety.PitchLimitComputer;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;
import ru.octol1ttle.flightassistant.registries.events.AllowComputerRegisterCallback;

public class PitchController implements ITickableComputer, IAutopilotProvider {
    public static final float CLIMB_PITCH = 55.0f;
    public static final float ALTITUDE_PRESERVE_PITCH = 15.0f;
    public static final float GLIDE_PITCH = -2.2f;
    public static final float DESCEND_PITCH = -35.0f;
    private static final List<IPitchController> controllers = new ArrayList<>();
    private final AirDataComputer data = ComputerRegistry.resolve(AirDataComputer.class);
    private final TimeComputer time = ComputerRegistry.resolve(TimeComputer.class);
    private final PitchLimitComputer limit = ComputerRegistry.resolve(PitchLimitComputer.class);

    static {
        AllowComputerRegisterCallback.EVENT.register((computer -> {
            if (computer instanceof IPitchController controller) {
                controllers.add(controller);
            }
            return true;
        }));
    }

    @Override
    public void tick() {
        if (!data.canAutomationsActivate()) {
            return;
        }

        List<ControlInput> inputs = new ArrayList<>();
        for (IPitchController controller : controllers) {
            if (ComputerRegistry.isFaulted(controller.getClass())) {
                continue;
            }
            ControlInput input = controller.getPitchInput();
            if (input != null) {
                inputs.add(input);
            }
        }
        inputs.sort(Comparator.comparingInt(input -> input.priority().numerical));

        InputPriority lastPriority = null;
        for (ControlInput input : inputs) {
            if (lastPriority != null && input.priority() != lastPriority) {
                break;
            }

            smoothSetPitch(input.target(), MathHelper.clamp(time.deltaTime * input.deltaTimeMultiplier(), 0.001f, 1.0f));
            lastPriority = input.priority();
        }
    }

    /**
     * Smoothly changes the player's pitch to the specified pitch using the delta
     *
     * @param pitch Target pitch
     * @param delta Delta time, in seconds
     */
    public void smoothSetPitch(float pitch, float delta) {
        float difference = pitch - data.pitch();

        float newPitch;
        if (Math.abs(difference) < 0.05f) {
            newPitch = pitch;
        } else {
            if (difference > 0) { // going UP
                pitch = MathHelper.clamp(pitch, -90.0f, Math.min(CLIMB_PITCH, limit.maximumSafePitch));
            }
            if (difference < 0) { // going DOWN
                pitch = MathHelper.clamp(pitch, Math.max(DESCEND_PITCH, limit.minimumSafePitch), 90.0f);
            }

            newPitch = data.pitch() + (pitch - data.pitch()) * delta;
        }

        data.player().setPitch(-newPitch);
    }

    @Override
    public String getFaultTextBaseKey() {
        return "alerts.flightassistant.fault.computers.pitch_ctl";
    }

    @Override
    public void reset() {
    }
}
