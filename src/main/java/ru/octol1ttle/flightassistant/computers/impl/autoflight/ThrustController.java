package ru.octol1ttle.flightassistant.computers.impl.autoflight;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.math.MathHelper;
import ru.octol1ttle.flightassistant.FlightAssistant;
import ru.octol1ttle.flightassistant.compatibility.doabarrelroll.DaBRThrustHandler;
import ru.octol1ttle.flightassistant.computers.api.ControlInput;
import ru.octol1ttle.flightassistant.computers.api.INormalLawProvider;
import ru.octol1ttle.flightassistant.computers.api.IThrustController;
import ru.octol1ttle.flightassistant.computers.api.IThrustHandler;
import ru.octol1ttle.flightassistant.computers.api.ITickableComputer;
import ru.octol1ttle.flightassistant.computers.api.InputPriority;
import ru.octol1ttle.flightassistant.computers.impl.TimeComputer;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;
import ru.octol1ttle.flightassistant.registries.events.AllowComputerRegisterCallback;
import ru.octol1ttle.flightassistant.registries.events.RegisterCustomComputersCallback;

public class ThrustController implements ITickableComputer, INormalLawProvider {
    private static final List<IThrustController> controllers = new ArrayList<>();

    private static IThrustHandler thrustHandler;
    private static IThrustHandler fallback;

    private final TimeComputer time = ComputerRegistry.resolve(TimeComputer.class);
    private float thrust = 0.0f;

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
        getThrustHandler().tickThrust(getThrust());
    }

    private void updateTargetThrust() {
        List<ControlInput> inputs = new ArrayList<>();
        for (IThrustController controller : controllers) {
            if (ComputerRegistry.isFaulted(controller.getClass())) {
                continue;
            }
            ControlInput input = controller.getThrustInput();
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

            smoothSetThrust(input.target(), MathHelper.clamp(time.deltaTime * input.deltaTimeMultiplier(), 0.001f, 1.0f));
            lastPriority = input.priority();
        }
    }

    /**
     * Smoothly changes the player's thrust to the specified thrust using the delta
     *
     * @param targetThrust Target thrust
     * @param delta        Delta time, in seconds
     */
    public void smoothSetThrust(float targetThrust, float delta) {
        float difference = targetThrust - thrust;

        if (Math.abs(difference) < 0.001f) {
            setThrust(targetThrust);
        } else {
            addThrust(difference * delta);
        }
    }

    public IThrustHandler getThrustHandler() {
        return thrustHandler.enabled() ? thrustHandler : fallback;
    }

    public float getThrust() {
        return thrust;
    }

    /**
     * Sets the thrust.
     *
     * @param newThrust The new thrust value, ranging from -1.0 to 1.0. Will be clamped if outside the range.
     */
    public void setThrust(float newThrust) {
        newThrust = MathHelper.clamp(newThrust, -1.0f, 1.0f);
        this.thrust = newThrust;
    }

    public void addThrust(float deltaThrust) {
        setThrust(thrust + deltaThrust);
    }

    public void addThrustTick(float multiplier) {
        addThrust(multiplier * time.deltaTime * 0.75f);
    }

    @Override
    public String getId() {
        return "thrust_ctl";
    }

    @Override
    public void reset() {
        thrust = 0.0f;
    }
}
