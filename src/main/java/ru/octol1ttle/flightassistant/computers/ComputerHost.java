package ru.octol1ttle.flightassistant.computers;

import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.NotNull;
import ru.octol1ttle.flightassistant.FlightAssistant;
import ru.octol1ttle.flightassistant.computers.api.IComputer;
import ru.octol1ttle.flightassistant.computers.api.ITickableComputer;
import ru.octol1ttle.flightassistant.computers.impl.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.impl.FlightPhaseComputer;
import ru.octol1ttle.flightassistant.computers.impl.TimeComputer;
import ru.octol1ttle.flightassistant.computers.impl.autoflight.AutoFlightController;
import ru.octol1ttle.flightassistant.computers.impl.autoflight.AutopilotComputer;
import ru.octol1ttle.flightassistant.computers.impl.autoflight.FireworkController;
import ru.octol1ttle.flightassistant.computers.impl.autoflight.HeadingController;
import ru.octol1ttle.flightassistant.computers.impl.autoflight.PitchController;
import ru.octol1ttle.flightassistant.computers.impl.autoflight.RollController;
import ru.octol1ttle.flightassistant.computers.impl.autoflight.ThrustController;
import ru.octol1ttle.flightassistant.computers.impl.navigation.FlightPlanner;
import ru.octol1ttle.flightassistant.computers.impl.safety.AlertController;
import ru.octol1ttle.flightassistant.computers.impl.safety.ChunkStatusComputer;
import ru.octol1ttle.flightassistant.computers.impl.safety.ElytraStateController;
import ru.octol1ttle.flightassistant.computers.impl.safety.FlightProtectionsComputer;
import ru.octol1ttle.flightassistant.computers.impl.safety.GroundProximityComputer;
import ru.octol1ttle.flightassistant.computers.impl.safety.PitchLimitComputer;
import ru.octol1ttle.flightassistant.computers.impl.safety.StallComputer;
import ru.octol1ttle.flightassistant.computers.impl.safety.VoidLevelComputer;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;
import ru.octol1ttle.flightassistant.registries.events.CustomComputerRegistrationCallback;

public class ComputerHost {
    public static ComputerHost instance() {
        return FlightAssistant.getComputerHost();
    }

    public ComputerHost(@NotNull MinecraftClient mc) {
        ComputerRegistry.register(new AirDataComputer(mc));
        ComputerRegistry.register(new TimeComputer(mc));

        ComputerRegistry.register(new FireworkController(mc));
        ComputerRegistry.register(new PitchLimitComputer());
        ComputerRegistry.register(new FlightProtectionsComputer());
        ComputerRegistry.register(new ChunkStatusComputer());
        ComputerRegistry.register(new StallComputer());
        ComputerRegistry.register(new VoidLevelComputer());
        ComputerRegistry.register(new FlightPlanner());
        ComputerRegistry.register(new GroundProximityComputer());
        ComputerRegistry.register(new ElytraStateController());

        ComputerRegistry.register(new AutoFlightController());
        ComputerRegistry.register(new FlightPhaseComputer());
        ComputerRegistry.register(new ThrustController());
        ComputerRegistry.register(new AutopilotComputer());
        ComputerRegistry.register(new PitchController());
        ComputerRegistry.register(new HeadingController());
        ComputerRegistry.register(new RollController());

        ComputerRegistry.register(new AlertController(mc.getSoundManager()));

        CustomComputerRegistrationCallback.EVENT.invoker().registerCustomComputers();
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
            } catch (AssertionError e) { // TODO: stop using AssertionErrors
                ComputerRegistry.markFaulted(computer, e, "Invalid data encountered by computer");
            } catch (Throwable t) {
                ComputerRegistry.markFaulted(computer, t, "Exception ticking computer");
            }
        }
    }
}
