package ru.octol1ttle.flightassistant.computers;

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
        ComputerRegistry.register(new AirDataComputer(mc));
        ComputerRegistry.register(new TimeComputer(mc));
        ComputerRegistry.register(new StallComputer());
        ComputerRegistry.register(new ChunkStatusComputer());
        ComputerRegistry.register(new GPWSComputer());
        ComputerRegistry.register(new VoidLevelComputer());
        ComputerRegistry.register(new ElytraStateController());
        ComputerRegistry.register(new FlightPlanner());
        ComputerRegistry.register(new AutoFlightComputer());
        ComputerRegistry.register(new FireworkController(mc));
        ComputerRegistry.register(new AlertController(mc.getSoundManager()));
        ComputerRegistry.register(new PitchController());
        ComputerRegistry.register(new YawController());
    }

    public void tick() {
        for (IComputer computer : ComputerRegistry.getComputers()) {
            if (!(computer instanceof ITickableComputer tickable)) {
                continue;
            }

            try {
                tickable.tick();
            } catch (AssertionError e) {
                FlightAssistant.LOGGER.error("Invalid data encountered by computer", e);
            } catch (Throwable t) {
                FlightAssistant.LOGGER.error("Exception ticking computer", t);
            }
        }
    }
}
