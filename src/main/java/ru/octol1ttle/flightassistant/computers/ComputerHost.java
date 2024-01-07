package ru.octol1ttle.flightassistant.computers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;
import ru.octol1ttle.flightassistant.FlightAssistant;
import ru.octol1ttle.flightassistant.HudComponent;
import ru.octol1ttle.flightassistant.HudRenderer;
import ru.octol1ttle.flightassistant.computers.autoflight.AutoFlightComputer;
import ru.octol1ttle.flightassistant.computers.autoflight.FireworkController;
import ru.octol1ttle.flightassistant.computers.autoflight.PitchController;
import ru.octol1ttle.flightassistant.computers.autoflight.YawController;
import ru.octol1ttle.flightassistant.computers.navigation.FlightPlanner;
import ru.octol1ttle.flightassistant.computers.safety.AlertController;
import ru.octol1ttle.flightassistant.computers.safety.GPWSComputer;
import ru.octol1ttle.flightassistant.computers.safety.StallComputer;
import ru.octol1ttle.flightassistant.computers.safety.VoidLevelComputer;
import ru.octol1ttle.flightassistant.computers.safety.WallCollisionComputer;

public class ComputerHost {
    public final AirDataComputer data;
    public final StallComputer stall;
    public final GPWSComputer gpws;
    public final VoidLevelComputer voidLevel;
    public final FireworkController firework;
    public final AutoFlightComputer autoflight;
    public final AlertController alert;
    public final TimeComputer time;
    public final PitchController pitch;
    public final YawController yaw;
    public final FlightPlanner plan;
    public final WallCollisionComputer collision;
    public final List<IComputer> faulted;
    private final List<ITickableComputer> tickables;
    private final List<IRenderTickableComputer> renderTickables;

    public ComputerHost(@NotNull MinecraftClient mc, HudRenderer renderer) {
        assert mc.player != null;
        PlayerEntity player = mc.player;

        this.data = new AirDataComputer(mc, player);
        this.time = new TimeComputer();
        this.firework = new FireworkController(time, data, mc.interactionManager);
        this.stall = new StallComputer(firework, data);
        this.voidLevel = new VoidLevelComputer(data, firework, stall);
        this.gpws = new GPWSComputer(data);
        this.collision = new WallCollisionComputer(data);

        this.yaw = new YawController(time, data);
        this.pitch = new PitchController(data, stall, time, voidLevel, gpws);

        this.plan = new FlightPlanner(data);
        this.autoflight = new AutoFlightComputer(data, gpws, plan, firework, collision, pitch, yaw);

        this.alert = new AlertController(this, mc.getSoundManager(), renderer);

        // computers are sorted in the order they should be ticked to avoid errors
        this.tickables = new ArrayList<>(List.of(data, stall, gpws, voidLevel, collision, plan, autoflight, firework, alert));
        this.renderTickables = new ArrayList<>(List.of(time, pitch, yaw));
        Collections.reverse(this.tickables); // we tick computers in reverse, so reverse the collections so that the order is correct
        Collections.reverse(this.renderTickables);

        this.faulted = new ArrayList<>(tickables.size() + renderTickables.size());
    }

    public void tick() {
        if (HudComponent.CONFIG == null) {
            return;
        }

        for (int i = tickables.size() - 1; i >= 0; i--) {
            ITickableComputer computer = tickables.get(i);
            try {
                computer.tick();
            } catch (Exception e) {
                FlightAssistant.LOGGER.error("Exception ticking computer", e);
                computer.reset();
                faulted.add(computer);
                tickables.remove(computer);
            }
        }
    }

    public void render() {
        if (HudComponent.CONFIG == null) {
            return;
        }

        for (int i = renderTickables.size() - 1; i >= 0; i--) {
            IRenderTickableComputer computer = renderTickables.get(i);
            try {
                computer.tick();
            } catch (Exception e) {
                FlightAssistant.LOGGER.error("Exception ticking computer (on render)", e);
                computer.reset();
                faulted.add(computer);
                renderTickables.remove(computer);
            }
        }
    }

    public void resetComputers(boolean resetWorking) {
        if (resetWorking) {
            for (ITickableComputer tickable : tickables) {
                tickable.reset();
            }
            for (IRenderTickableComputer renderTickable : renderTickables) {
                renderTickable.reset();
            }
        }

        for (int i = faulted.size() - 1; i >= 0; i--) {
            IComputer computer = faulted.get(i);
            faulted.remove(computer);

            computer.reset();

            if (computer instanceof ITickableComputer tickable) {
                tickables.add(tickable);
                continue;
            }
            if (computer instanceof IRenderTickableComputer renderTickable) {
                renderTickables.add(renderTickable);
                continue;
            }

            throw new RuntimeException("Unknown computer type for " + computer);
        }
    }
}
