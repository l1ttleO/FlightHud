package ru.octol1ttle.flightassistant.computers.impl.autoflight;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import ru.octol1ttle.flightassistant.FlightAssistant;
import ru.octol1ttle.flightassistant.compatibility.doabarrelroll.DaBRRollActuator;
import ru.octol1ttle.flightassistant.computers.api.ControlInput;
import ru.octol1ttle.flightassistant.computers.api.ControllerPriority;
import ru.octol1ttle.flightassistant.computers.api.INormalLawProvider;
import ru.octol1ttle.flightassistant.computers.api.IRollActuator;
import ru.octol1ttle.flightassistant.computers.api.IRollController;
import ru.octol1ttle.flightassistant.computers.api.ITickableComputer;
import ru.octol1ttle.flightassistant.computers.impl.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.impl.TimeComputer;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;
import ru.octol1ttle.flightassistant.registries.events.ComputerRegisteredCallback;
import ru.octol1ttle.flightassistant.registries.events.CustomComputerRegistrationCallback;

public class RollController implements ITickableComputer, INormalLawProvider {
    private final List<IRollController> controllers = new ArrayList<>();
    private final AirDataComputer data = ComputerRegistry.resolve(AirDataComputer.class);
    private final TimeComputer time = ComputerRegistry.resolve(TimeComputer.class);
    private @Nullable IRollActuator rollActuator = null;

    public RollController() {
        CustomComputerRegistrationCallback.EVENT.register(() -> {
            if (FabricLoader.getInstance().isModLoaded("do_a_barrel_roll")) {
                ComputerRegistry.register(new DaBRRollActuator());
            }
        });
        ComputerRegisteredCallback.EVENT.register(computer -> {
            if (computer instanceof IRollController controller) {
                controllers.add(controller);
                controllers.sort(Comparator.comparingInt(ctl -> ctl.getPriority().priority));
            }
            if (computer instanceof IRollActuator actuator) {
                if (rollActuator != null) {
                    FlightAssistant.LOGGER.warn("Multiple roll actuators found! Discarding actuator %s".formatted(actuator.getClass().getName()));
                } else {
                    rollActuator = actuator;
                    FlightAssistant.LOGGER.info("Active roll actuator is %s".formatted(rollActuator.getClass().getName()));
                }
            }
        });
    }

    @Override
    public void tick() {
        if (!data.canAutomationsActivate() || rollActuator == null) {
            return;
        }

        ControllerPriority lastPriority = null;
        for (IRollController controller : controllers) {
            if (lastPriority != null && controller.getPriority() != lastPriority) {
                break;
            }

            ControlInput rollInput = controller.getControlledRoll();
            if (rollInput != null) {
                smoothSetRoll(rollInput.target(), MathHelper.clamp(time.deltaTime * rollInput.deltaTimeMultiplier(), 0.001f, 1.0f));
                lastPriority = controller.getPriority();
            }
        }
    }

    /**
     * Smoothly changes the player's roll to the specified roll using the delta
     *
     * @param roll  Target roll
     * @param delta Delta time, in seconds
     */
    public void smoothSetRoll(float roll, float delta) {
        float difference = roll - data.roll;
        if (difference < -180.0f) {
            difference += 360.0f;
        }
        if (difference > 180.0f) {
            difference -= 360.0f;
        }

        float newRoll;
        if (Math.abs(difference) < 0.05f) {
            newRoll = roll;
        } else {
            newRoll = data.roll + difference * delta;
        }

        assert rollActuator != null;
        rollActuator.setRoll(newRoll);
    }

    @Override
    public String getId() {
        return "roll_ctl";
    }

    @Override
    public void reset() {
    }
}