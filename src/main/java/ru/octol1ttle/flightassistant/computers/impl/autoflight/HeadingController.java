package ru.octol1ttle.flightassistant.computers.impl.autoflight;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.util.math.MathHelper;
import ru.octol1ttle.flightassistant.computers.api.ControlInput;
import ru.octol1ttle.flightassistant.computers.api.IAutopilotProvider;
import ru.octol1ttle.flightassistant.computers.api.IHeadingController;
import ru.octol1ttle.flightassistant.computers.api.ITickableComputer;
import ru.octol1ttle.flightassistant.computers.api.InputPriority;
import ru.octol1ttle.flightassistant.computers.impl.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.impl.TimeComputer;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;
import ru.octol1ttle.flightassistant.registries.events.AllowComputerRegisterCallback;

public class HeadingController implements ITickableComputer, IAutopilotProvider {
    private static final List<IHeadingController> controllers = new ArrayList<>();
    private final AirDataComputer data = ComputerRegistry.resolve(AirDataComputer.class);
    private final TimeComputer time = ComputerRegistry.resolve(TimeComputer.class);

    static {
        AllowComputerRegisterCallback.EVENT.register((computer -> {
            if (computer instanceof IHeadingController controller) {
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
        for (IHeadingController controller : controllers) {
            if (ComputerRegistry.isFaulted(controller.getClass())) {
                continue;
            }
            ControlInput input = controller.getHeadingInput();
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

            smoothSetHeading(input.target(), MathHelper.clamp(time.deltaTime * input.deltaTimeMultiplier(), 0.001f, 1.0f));
            lastPriority = input.priority();
        }
    }

    private void smoothSetHeading(float heading, float delta) {
        float difference = heading - data.heading();
        if (difference < -180.0f) {
            difference += 360.0f;
        }
        if (difference > 180.0f) {
            difference -= 360.0f;
        }

        float newHeading;
        if (Math.abs(difference) < 0.05f) {
            newHeading = heading;
        } else {
            newHeading = data.heading() + difference * delta;
        }

        data.player().setYaw(newHeading - 180.0f);
    }

    @Override
    public String getFaultTextBaseKey() {
        return "alerts.flightassistant.fault.computers.heading_ctl";
    }

    @Override
    public void reset() {
    }
}
