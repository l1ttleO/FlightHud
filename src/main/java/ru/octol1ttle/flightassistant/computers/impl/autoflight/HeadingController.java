package ru.octol1ttle.flightassistant.computers.impl.autoflight;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.Pair;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import ru.octol1ttle.flightassistant.computers.api.ControllerPriority;
import ru.octol1ttle.flightassistant.computers.api.IHeadingController;
import ru.octol1ttle.flightassistant.computers.impl.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.api.ITickableComputer;
import ru.octol1ttle.flightassistant.computers.impl.TimeComputer;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;
import ru.octol1ttle.flightassistant.registries.events.ComputerRegisteredCallback;

public class HeadingController implements ITickableComputer {
    private final AirDataComputer data = ComputerRegistry.resolve(AirDataComputer.class);
    private final TimeComputer time = ComputerRegistry.resolve(TimeComputer.class);
    private final List<IHeadingController> controllers = new ArrayList<>();

    public HeadingController() {
        ComputerRegisteredCallback.EVENT.register((computer -> {
            if (computer instanceof IHeadingController controller) {
                controllers.add(controller);
            }
        }));
    }

    @Override
    public void tick() {
        if (!data.canAutomationsActivate()) {
            return;
        }

        ControllerPriority lastPriority = null;
        for (IHeadingController controller : controllers) {
            if (lastPriority != null && controller.getPriority() != lastPriority) {
                break;
            }

            Pair<@NotNull Float, @NotNull Float> targetPitch = controller.getControlledHeading();
            if (targetPitch != null) {
                smoothSetHeading(targetPitch.getLeft(), MathHelper.clamp(time.deltaTime * targetPitch.getRight(), 0.001f, 1.0f));
                lastPriority = controller.getPriority();
            }
        }
    }

    private void smoothSetHeading(Float heading, float delta) {
        if (heading == null) {
            return;
        }

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
    public String getId() {
        return "heading_ctl";
    }

    @Override
    public void reset() {
    }
}
