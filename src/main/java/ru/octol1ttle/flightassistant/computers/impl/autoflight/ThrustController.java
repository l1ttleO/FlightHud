package ru.octol1ttle.flightassistant.computers.impl.autoflight;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.math.MathHelper;
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
import ru.octol1ttle.flightassistant.registries.events.RegisterCustomComputersCallback;

public class ThrustController implements ITickableComputer, INormalLawProvider {
    private static final List<IThrustController> controllers = new ArrayList<>();
    private static final Random random = new Random();

    private static IThrustHandler thrustHandler;
    private static IThrustHandler fallback;

    private final TimeComputer time = ComputerRegistry.resolve(TimeComputer.class);

    private float currentThrust = 0.0f;
    private float targetThrust = 0.0f;
    private boolean currentReverse = false;
    private boolean targetReverse = false;

    private Float syncReverseAt = null;

    static {
        RegisterCustomComputersCallback.EVENT.register(() -> {
            if (FabricLoader.getInstance().isModLoaded("do_a_barrel_roll")) {
                ComputerRegistry.register(new DaBRThrustHandler());
            }
        });
        AllowComputerRegisterCallback.EVENT.register(computer -> {
            if (computer instanceof IThrustController controller) {
                controllers.add(controller);
            }
            if (computer instanceof IThrustHandler handler) {
                if (thrustHandler != null && !(thrustHandler instanceof FireworkController)) {
                    FlightAssistant.LOGGER.error("Multiple thrust handlers found! Discarding handler %s".formatted(handler.getClass().getName()));
                    return false;
                }
                thrustHandler = handler;
                if (!(thrustHandler instanceof FireworkController)) {
                    FlightAssistant.LOGGER.info("Active thrust handler is %s".formatted(handler.getClass().getName()));
                } else {
                    fallback = handler;
                }
            }
            return true;
        });
    }

    @Override
    public void tick() {
        updateTargetThrust();

        if (!FAConfig.computer().simulateEngine) {
            currentThrust = targetThrust;
            currentReverse = targetReverse;
        } else {
            if (targetThrust > currentThrust) {
                currentThrust = Math.min(targetThrust, currentThrust + time.deltaTime * Math.max(currentThrust * 1.5f, 0.1f) / 3.0f);
            } else {
                float diff = currentThrust - targetThrust;
                currentThrust = Math.max(targetThrust, currentThrust - diff * time.deltaTime / 1.5f);
            }

            if (currentReverse != targetReverse) {
                if (syncReverseAt == null) {
                    syncReverseAt = time.millis + random.nextFloat(750, 1500);
                }

                if (time.millis >= syncReverseAt) {
                    currentReverse = targetReverse;
                }
            } else {
                syncReverseAt = null;
            }
        }

        getThrustHandler().tickThrust(getCurrentThrust());
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
        Float newTarget = null;
        for (ThrustControlInput input : inputs) {
            if (lastPriority != null && input.priority() != lastPriority) {
                break;
            }

            newTarget = Math.max(Objects.requireNonNullElse(newTarget, -1.0f), input.target());
            lastPriority = input.priority();
        }

        if (newTarget != null) {
            setThrust(newTarget);
        }
    }

    public IThrustHandler getThrustHandler() {
        return thrustHandler.enabled() ? thrustHandler : fallback;
    }

    public float getCurrentThrust() {
        return currentThrust * (currentReverse ? -1 : 1);
    }

    public float getTargetThrust() {
        return targetThrust * (targetReverse ? -1 : 1);
    }

    /**
     * Sets the target thrust.
     *
     * @param newTarget The new target, ranging from -1.0 to 1.0.
     */
    public void setThrust(float newTarget) {
        newTarget = MathHelper.clamp(newTarget, -1.0f, 1.0f);
        this.targetThrust = Math.abs(newTarget);
        this.targetReverse = newTarget < 0.0f;
    }


    @Override
    public String getId() {
        return "thrust_ctl";
    }

    @Override
    public void reset() {
        currentThrust = 0.0f;
        targetThrust = 0.0f;
        currentReverse = false;
        targetReverse = false;
        syncReverseAt = null;
    }
}
