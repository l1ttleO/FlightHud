package ru.octol1ttle.flightassistant.hud;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import org.jetbrains.annotations.NotNull;
import ru.octol1ttle.flightassistant.Dimensions;
import ru.octol1ttle.flightassistant.FlightAssistant;
import ru.octol1ttle.flightassistant.compatibility.ImmediatelyFastBatchingAccessor;
import ru.octol1ttle.flightassistant.computers.ComputerHost;
import ru.octol1ttle.flightassistant.config.FAConfig;
import ru.octol1ttle.flightassistant.config.HUDConfig;
import ru.octol1ttle.flightassistant.hud.api.HudDisplayRegistry;
import ru.octol1ttle.flightassistant.hud.api.IHudDisplay;
import ru.octol1ttle.flightassistant.hud.impl.AlertDisplay;
import ru.octol1ttle.flightassistant.hud.impl.AltitudeDisplay;
import ru.octol1ttle.flightassistant.hud.impl.AttitudeDisplay;
import ru.octol1ttle.flightassistant.hud.impl.ElytraHealthDisplay;
import ru.octol1ttle.flightassistant.hud.impl.FlightDirectorsDisplay;
import ru.octol1ttle.flightassistant.hud.impl.FlightModeDisplay;
import ru.octol1ttle.flightassistant.hud.impl.FlightPathDisplay;
import ru.octol1ttle.flightassistant.hud.impl.GroundSpeedDisplay;
import ru.octol1ttle.flightassistant.hud.impl.HeadingDisplay;
import ru.octol1ttle.flightassistant.hud.impl.LocationDisplay;
import ru.octol1ttle.flightassistant.hud.impl.RadarAltitudeDisplay;
import ru.octol1ttle.flightassistant.hud.impl.SpeedDisplay;
import ru.octol1ttle.flightassistant.hud.impl.StatusDisplay;
import ru.octol1ttle.flightassistant.hud.impl.VerticalSpeedDisplay;
import ru.octol1ttle.flightassistant.mixin.GameRendererInvoker;

public class HudDisplayHost {
    @NotNull
    public final ComputerHost host;
    private final Dimensions dim = new Dimensions();
    private final List<IHudDisplay> displays;
    public final List<IHudDisplay> faulted;

    public HudDisplayHost(MinecraftClient mc) {
        this.host = new ComputerHost(mc, this);
        HudDisplayRegistry.register(AlertDisplay.ID, new AlertDisplay(dim, host, host.alert, host.time));
        this.displays = new ArrayList<>(List.of(
                new AlertDisplay(dim, host, host.alert, host.time),
                new AltitudeDisplay(dim, host.data, host.autoflight, host.plan),
                new AttitudeDisplay(dim, host.data, host.stall, host.voidLevel),
                new ElytraHealthDisplay(dim, host.data),
                new FlightDirectorsDisplay(dim, host.data, host.autoflight),
                new FlightModeDisplay(dim, host.data, host.time, host.firework, host.autoflight, host.plan),
                new FlightPathDisplay(dim, host.data, host.gpws),
                new GroundSpeedDisplay(dim, host.data),
                new HeadingDisplay(dim, host.data, host.autoflight),
                new RadarAltitudeDisplay(dim, host.data, host.plan),
                new LocationDisplay(dim, host.data),
                new SpeedDisplay(dim, host.data),
                new StatusDisplay(dim, host.firework, host.plan),
                new VerticalSpeedDisplay(dim, host.data)
        ));
        this.faulted = new ArrayList<>(displays.size());
    }

    public void render(MinecraftClient mc, DrawContext context, float tickDelta) {
        GameRendererInvoker renderer = (GameRendererInvoker) mc.gameRenderer; // TODO: replace with AW once on Loom 1.6
        dim.update(context, renderer.invokeGetFov(mc.gameRenderer.getCamera(), tickDelta, true));

        float hudScale = FAConfig.hud().hudScale;
        boolean batchAll = FlightAssistant.canUseBatching() && FAConfig.hud().batchedRendering == HUDConfig.BatchedRendering.SINGLE_BATCH;

        context.getMatrices().push();
        context.getMatrices().scale(hudScale, hudScale, hudScale);

        if (batchAll) {
            ImmediatelyFastBatchingAccessor.beginHudBatching();
        }
        for (int i = displays.size() - 1; i >= 0; i--) {
            IHudDisplay display = displays.get(i);
            drawBatchedComponent(() -> {
                try {
                    display.render(context, mc.textRenderer);
                } catch (Throwable t) {
                    FlightAssistant.LOGGER.error("Exception rendering display", t);
                    faulted.add(display);
                    displays.remove(display);
                }
            });
        }
        if (batchAll) {
            ImmediatelyFastBatchingAccessor.endHudBatching();
        }

        for (IHudDisplay display : faulted) {
            drawBatchedComponent(() -> {
                try {
                    display.renderFaulted(context, mc.textRenderer);
                } catch (Throwable t) {
                    FlightAssistant.LOGGER.error("Exception rendering faulted display", t);
                }
            });
        }

        context.getMatrices().pop();
    }

    public void drawBatchedComponent(Runnable draw) {
        boolean batch = FlightAssistant.canUseBatching() && FAConfig.hud().batchedRendering == HUDConfig.BatchedRendering.PER_COMPONENT;
        if (batch) {
            ImmediatelyFastBatchingAccessor.beginHudBatching();
        }
        draw.run();
        if (batch) {
            ImmediatelyFastBatchingAccessor.endHudBatching();
        }
    }

    public void resetFaulted() {
        for (int i = faulted.size() - 1; i >= 0; i--) {
            IHudDisplay display = faulted.get(i);
            faulted.remove(display);
            displays.add(display);
        }
    }
}
