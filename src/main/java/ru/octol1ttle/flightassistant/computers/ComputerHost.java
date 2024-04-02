package ru.octol1ttle.flightassistant.computers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.NotNull;
import ru.octol1ttle.flightassistant.FlightAssistant;
import ru.octol1ttle.flightassistant.hud.HudDisplayHost;
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

public class ComputerHost {
    public final AirDataComputer data;
    public final StallComputer stall;
    public final ChunkStatusComputer chunkStatus;
    public final GPWSComputer gpws;
    public final VoidLevelComputer voidLevel;
    public final FireworkController firework;
    public final AutoFlightComputer autoflight;
    public final AlertController alert;
    public final TimeComputer time;
    public final PitchController pitch;
    public final YawController yaw;
    public final FlightPlanner plan;
    public final ElytraStateController elytra;
    public final List<IComputer> faulted;
    private final List<ITickableComputer> tickables;

    public ComputerHost(@NotNull MinecraftClient mc, HudDisplayHost renderer) {
        this.data = new AirDataComputer(mc);
        this.time = new TimeComputer(mc);
        this.firework = new FireworkController(mc, data, time);
        this.chunkStatus = new ChunkStatusComputer(data, time);
        this.stall = new StallComputer(firework, data);
        this.voidLevel = new VoidLevelComputer(data, firework, stall);
        this.plan = new FlightPlanner(data);
        this.gpws = new GPWSComputer(data, plan);
        this.elytra = new ElytraStateController(data);

        this.yaw = new YawController(time, data);
        this.pitch = new PitchController(data, stall, time, voidLevel, gpws, chunkStatus);

        this.autoflight = new AutoFlightComputer(data, gpws, plan, firework, pitch, yaw);

        this.alert = new AlertController(this, mc.getSoundManager(), renderer);

        // computers are sorted in the order they should be ticked to avoid errors
        this.tickables = new ArrayList<>(List.of(
                data, time, stall, chunkStatus, gpws, voidLevel, elytra, plan, autoflight, firework, alert, pitch, yaw
        ));
        Collections.reverse(this.tickables); // we tick computers in reverse, so reverse the collections so that the order is correct

        this.faulted = new ArrayList<>(tickables.size());
    }

    public void tick() {
        for (int i = tickables.size() - 1; i >= 0; i--) {
            ITickableComputer computer = tickables.get(i);
            try {
                computer.tick();
            } catch (AssertionError e) {
                FlightAssistant.LOGGER.error("Data validation failed", e);
                onComputerFault(computer);
            } catch (Throwable t) {
                FlightAssistant.LOGGER.error("Exception ticking computer", t);
                onComputerFault(computer);
            }
        }
    }

    private void onComputerFault(ITickableComputer computer) {
        computer.reset();
        faulted.add(computer);
        tickables.remove(computer);
    }

    public void resetComputers(boolean resetWorking) {
        if (resetWorking) {
            for (ITickableComputer tickable : tickables) {
                tickable.reset();
            }
        }

        for (int i = faulted.size() - 1; i >= 0; i--) {
            IComputer computer = faulted.get(i);
            faulted.remove(computer);

            computer.reset();

            if (computer instanceof ITickableComputer tickable) {
                tickables.add(tickable);
            }
        }
    }
}
