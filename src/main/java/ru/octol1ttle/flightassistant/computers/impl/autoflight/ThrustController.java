package ru.octol1ttle.flightassistant.computers.impl.autoflight;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.fabricmc.loader.api.FabricLoader;
import ru.octol1ttle.flightassistant.FlightAssistant;
import ru.octol1ttle.flightassistant.compatibility.doabarrelroll.DaBRThrustHandler;
import ru.octol1ttle.flightassistant.computers.api.INormalLawProvider;
import ru.octol1ttle.flightassistant.computers.api.IThrustController;
import ru.octol1ttle.flightassistant.computers.api.IThrustHandler;
import ru.octol1ttle.flightassistant.computers.api.ITickableComputer;
import ru.octol1ttle.flightassistant.computers.api.InputPriority;
import ru.octol1ttle.flightassistant.computers.api.ThrustControlInput;
import ru.octol1ttle.flightassistant.computers.impl.TimeComputer;
import ru.octol1ttle.flightassistant.config.FAConfig;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;
import ru.octol1ttle.flightassistant.registries.events.AllowComputerRegisterCallback;
import ru.octol1ttle.flightassistant.registries.events.CustomComputerRegistrationCallback;

public class ThrustController implements ITickableComputer, INormalLawProvider {
    private static final float MIN_TO_MAX_SPOOL_UP_TIME = 5.0f;
    private final TimeComputer time = ComputerRegistry.resolve(TimeComputer.class);
    private final List<IThrustController> controllers = new ArrayList<>();
    public float currentThrust = 0.0f;
    public float targetThrust = 0.0f;
    private boolean thrustHandlerRegistered = false;

    public ThrustController() {
        CustomComputerRegistrationCallback.EVENT.register(() -> {
            if (FabricLoader.getInstance().isModLoaded("do_a_barrel_roll")) {
                ComputerRegistry.register(new DaBRThrustHandler());
            }
        });
        AllowComputerRegisterCallback.EVENT.register(computer -> {
            if (computer instanceof IThrustController controller) {
                controllers.add(controller);
            }
            if (computer instanceof IThrustHandler handler) {
                if (thrustHandlerRegistered) {
                    FlightAssistant.LOGGER.error("Multiple thrust handlers found! Discarding handler %s".formatted(handler.getClass().getName()));
                    return false;
                }
                if (!(handler instanceof FireworkController)) {
                    thrustHandlerRegistered = true;
                    FlightAssistant.LOGGER.info("Active thrust handler is %s".formatted(handler.getClass().getName()));
                }
            }
            return true;
        });
    }

    @Override
    public void tick() {
        updateTargetThrust();

        float diff = targetThrust - currentThrust;
        if (!FAConfig.computer().simulateEngineSpoolUp || Math.abs(diff) < 0.001f) {
            currentThrust = targetThrust;
        } else {
            currentThrust = currentThrust + diff * time.deltaTime / MIN_TO_MAX_SPOOL_UP_TIME * Math.max(currentThrust, 0.1f);
        }
    }

    private void updateTargetThrust() {
        List<ThrustControlInput> inputs = new ArrayList<>();
        for (IThrustController controller : controllers) {
            if (ComputerRegistry.isFaulted(controller.getClass())) {
                continue;
            }
            ThrustControlInput input = controller.getThrustInput();
            if (input != null) {
                inputs.add(input);
            }
        }
        inputs.sort(Comparator.comparingInt(input -> input.priority().numerical));

        InputPriority lastPriority = null;
        for (ThrustControlInput input : inputs) {
            if (lastPriority != null && input.priority() != lastPriority) {
                break;
            }

            targetThrust = Math.max(targetThrust, input.target());
            lastPriority = input.priority();
        }
    }

    @Override
    public String getId() {
        return "thrust_ctl";
    }

    @Override
    public void reset() {
        currentThrust = 0.0f;
        targetThrust = 0.0f;
    }
}
