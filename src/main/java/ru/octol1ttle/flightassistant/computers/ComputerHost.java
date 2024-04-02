package ru.octol1ttle.flightassistant.computers;

import java.util.Map;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.NotNull;
import ru.octol1ttle.flightassistant.FlightAssistant;
import ru.octol1ttle.flightassistant.computers.autoflight.AutoFlightComputer;
import ru.octol1ttle.flightassistant.computers.autoflight.FireworkController;
import ru.octol1ttle.flightassistant.computers.autoflight.PitchController;
import ru.octol1ttle.flightassistant.computers.autoflight.YawController;
import ru.octol1ttle.flightassistant.computers.navigation.FlightPlanner;
import ru.octol1ttle.flightassistant.computers.safety.AlertController;
import ru.octol1ttle.flightassistant.computers.safety.ChunkStatusComputer;
import ru.octol1ttle.flightassistant.computers.safety.ElytraStateController;
import ru.octol1ttle.flightassistant.computers.safety.GPWSComputer;
import ru.octol1ttle.flightassistant.computers.safety.StallComputer;
import ru.octol1ttle.flightassistant.computers.safety.VoidLevelComputer;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;

public class ComputerHost {
    public static ComputerHost instance() {
        return FlightAssistant.getComputerHost();
    }

    public ComputerHost(@NotNull MinecraftClient mc) {
        ComputerRegistry.register(FlightAssistant.id("data"), () -> new AirDataComputer(mc));
        ComputerRegistry.register(FlightAssistant.id("time"), () -> new TimeComputer(mc));
        ComputerRegistry.register(FlightAssistant.id("firework"), () -> new FireworkController(mc));
        ComputerRegistry.register(FlightAssistant.id("chunk_status"), ChunkStatusComputer::new);
        ComputerRegistry.register(FlightAssistant.id("stall"), StallComputer::new);
        ComputerRegistry.register(FlightAssistant.id("void_level"), VoidLevelComputer::new);
        ComputerRegistry.register(FlightAssistant.id("flight_plan"), FlightPlanner::new);
        ComputerRegistry.register(FlightAssistant.id("ground_proximity"), GPWSComputer::new);
        ComputerRegistry.register(FlightAssistant.id("elytra_state"), ElytraStateController::new);
        ComputerRegistry.register(FlightAssistant.id("yaw"), YawController::new);
        ComputerRegistry.register(FlightAssistant.id("pitch"), PitchController::new);
        ComputerRegistry.register(FlightAssistant.id("autoflight"), AutoFlightComputer::new);
        ComputerRegistry.register(FlightAssistant.id("alert"), () -> new AlertController(this, mc.getSoundManager()));
    }

    public void tick() {
        for (Map.Entry<Class<IComputer>, IComputer> computer : ComputerRegistry.getComputers()) {
            if (!(computer.getValue() instanceof ITickableComputer tickable)) {
                continue;
            }

            try {
                tickable.tick();
            } catch (AssertionError e) {
                FlightAssistant.LOGGER.error("Invalid data encountered by computer with ID: %s".formatted(computer.getKey()), e);
            } catch (Throwable t) {
                FlightAssistant.LOGGER.error("Exception ticking computer with ID: %s".formatted(computer.getKey()), t);
            }
        }
    }
}
