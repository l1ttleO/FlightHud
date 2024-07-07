package ru.octol1ttle.flightassistant.computers.impl.autoflight;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import ru.octol1ttle.flightassistant.FlightAssistant;
import ru.octol1ttle.flightassistant.compatibility.doabarrelroll.DaBRRollHandler;
import ru.octol1ttle.flightassistant.computers.api.ControlInput;
import ru.octol1ttle.flightassistant.computers.api.IAutopilotProvider;
import ru.octol1ttle.flightassistant.computers.api.IRollController;
import ru.octol1ttle.flightassistant.computers.api.IRollHandler;
import ru.octol1ttle.flightassistant.computers.api.ITickableComputer;
import ru.octol1ttle.flightassistant.computers.api.InputPriority;
import ru.octol1ttle.flightassistant.computers.impl.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.impl.TimeComputer;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;
import ru.octol1ttle.flightassistant.registries.events.AllowComputerRegisterCallback;
import ru.octol1ttle.flightassistant.registries.events.RegisterCustomComputersCallback;

public class RollController implements ITickableComputer, IAutopilotProvider {
    private static final List<IRollController> controllers = new ArrayList<>();
    private static @Nullable IRollHandler rollHandler = null;
    private final AirDataComputer data = ComputerRegistry.resolve(AirDataComputer.class);
    private final TimeComputer time = ComputerRegistry.resolve(TimeComputer.class);

    static {
        RegisterCustomComputersCallback.EVENT.register(() -> {
            if (FabricLoader.getInstance().isModLoaded("do_a_barrel_roll")) {
                ComputerRegistry.register(new DaBRRollHandler());
            }
        });
        AllowComputerRegisterCallback.EVENT.register(computer -> {
            if (computer instanceof IRollController controller) {
                controllers.add(controller);
            }
            if (computer instanceof IRollHandler handler) {
                if (rollHandler != null) {
                    FlightAssistant.LOGGER.error("Multiple roll handlers found! Discarding handler %s".formatted(handler.getClass().getName()));
                    return false;
                } else {
                    rollHandler = handler;
                    FlightAssistant.LOGGER.info("Active roll handler is %s".formatted(rollHandler.getClass().getName()));
                }
            }
            return true;
        });
    }

    @Override
    public void tick() {
        if (!data.canAutomationsActivate() || rollHandler == null) {
            return;
        }

        List<ControlInput> inputs = new ArrayList<>();
        for (IRollController controller : controllers) {
            if (ComputerRegistry.isFaulted(controller.getClass())) {
                continue;
            }
            ControlInput input = controller.getRollInput();
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

            smoothSetRoll(input.target(), MathHelper.clamp(time.deltaTime * input.deltaTimeMultiplier(), 0.001f, 1.0f));
            lastPriority = input.priority();
        }
    }

    /**
     * Smoothly changes the player's roll to the specified roll using the delta
     *
     * @param roll  Target roll
     * @param delta Delta time, in seconds
     */
    public void smoothSetRoll(float roll, float delta) {
        assert rollHandler != null;

        float difference = roll - rollHandler.getRoll();
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
            newRoll = rollHandler.getRoll() + difference * delta;
        }

        rollHandler.setRoll(newRoll);
    }

    @Override
    public String getFaultTextBaseKey() {
        return "alerts.flightassistant.fault.computers.roll_ctl";
    }

    @Override
    public void reset() {
    }
}
