package ru.octol1ttle.flightassistant.computers;

import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.NotNull;
import ru.octol1ttle.flightassistant.FlightAssistant;
import ru.octol1ttle.flightassistant.computers.api.IComputer;
import ru.octol1ttle.flightassistant.computers.api.ITickableComputer;
import ru.octol1ttle.flightassistant.computers.impl.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.impl.TimeComputer;
import ru.octol1ttle.flightassistant.computers.impl.autoflight.AutoFlightComputer;
import ru.octol1ttle.flightassistant.computers.impl.autoflight.FireworkController;
import ru.octol1ttle.flightassistant.computers.impl.autoflight.PitchController;
import ru.octol1ttle.flightassistant.computers.impl.autoflight.YawController;
import ru.octol1ttle.flightassistant.computers.impl.navigation.FlightPlanner;
import ru.octol1ttle.flightassistant.computers.impl.safety.AlertController;
import ru.octol1ttle.flightassistant.computers.impl.safety.ChunkStatusComputer;
import ru.octol1ttle.flightassistant.computers.impl.safety.ElytraStateController;
import ru.octol1ttle.flightassistant.computers.impl.safety.GPWSComputer;
import ru.octol1ttle.flightassistant.computers.impl.safety.StallComputer;
import ru.octol1ttle.flightassistant.computers.impl.safety.VoidLevelComputer;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;

public class ComputerHost {
    public static ComputerHost instance() {
        return FlightAssistant.getComputerHost();
    }

    public ComputerHost(@NotNull MinecraftClient mc) {
        ComputerRegistry.register(new AirDataComputer(mc));
        ComputerRegistry.register(new TimeComputer(mc));
        ComputerRegistry.register(new FireworkController(mc));
        ComputerRegistry.register(new ChunkStatusComputer());
        ComputerRegistry.register(new StallComputer());
        ComputerRegistry.register(new VoidLevelComputer());
        ComputerRegistry.register(new FlightPlanner());
        ComputerRegistry.register(new GPWSComputer());
        ComputerRegistry.register(new ElytraStateController());
        ComputerRegistry.register(new YawController());
        ComputerRegistry.register(new PitchController());
        ComputerRegistry.register(new AutoFlightComputer());
        ComputerRegistry.register(new AlertController(mc.getSoundManager()));
    }

    public void tick() {
        for (IComputer computer : ComputerRegistry.getComputers()) {
            if (ComputerRegistry.isFaulted(computer.getClass())) {
                continue;
            }
            if (!(computer instanceof ITickableComputer tickable)) {
                continue;
            }

            try {
                tickable.tick();
            } catch (AssertionError e) {
                ComputerRegistry.markFaulted(computer, e, "Invalid data encountered by computer");
            } catch (Throwable t) {
                ComputerRegistry.markFaulted(computer, t, "Exception ticking computer");
            }
        }
    }
}
