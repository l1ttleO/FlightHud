package ru.octol1ttle.flightassistant.computers.impl.autoflight;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.util.Pair;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import ru.octol1ttle.flightassistant.computers.api.IPitchController;
import ru.octol1ttle.flightassistant.computers.api.ITickableComputer;
import ru.octol1ttle.flightassistant.computers.impl.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.impl.TimeComputer;
import ru.octol1ttle.flightassistant.computers.impl.safety.PitchLimitComputer;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;
import ru.octol1ttle.flightassistant.registries.events.ComputerRegisteredCallback;

public class PitchController implements ITickableComputer {
    public static final float CLIMB_PITCH = 55.0f;
    public static final float ALTITUDE_PRESERVE_PITCH = 15.0f;
    public static final float GLIDE_PITCH = -2.2f;
    public static final float DESCEND_PITCH = -35.0f;
    private final List<IPitchController> controllers = new ArrayList<>();
    private final AirDataComputer data = ComputerRegistry.resolve(AirDataComputer.class);
    private final TimeComputer time = ComputerRegistry.resolve(TimeComputer.class);
    private final PitchLimitComputer limit = ComputerRegistry.resolve(PitchLimitComputer.class);

    public PitchController() {
        ComputerRegisteredCallback.EVENT.register((computer -> {
            if (computer instanceof IPitchController controller) {
                controllers.add(controller);
                controllers.sort(Comparator.comparingInt(ctl -> ctl.getPriority().priority));
            }
        }));
    }

    @Override
    public void tick() {
        if (!data.canAutomationsActivate()) {
            return;
        }

        IPitchController.Priority lastPriority = null;
        for (IPitchController controller : controllers) {
            if (lastPriority != null && controller.getPriority() != lastPriority) {
                break;
            }

            Pair<@NotNull Float, @NotNull Float> targetPitch = controller.getTargetPitch();
            if (targetPitch != null) {
                smoothSetPitch(targetPitch.getLeft(), MathHelper.clamp(time.deltaTime * targetPitch.getRight(), 0.001f, 1.0f));
                lastPriority = controller.getPriority();
            }
        }

        //smoothSetPitch(TODO autopilotTargetPitch, time.deltaTime);
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
    public String getId() {
        return "pitch_ctl";
    }

    @Override
    public void reset() {
    }
}
