package ru.octol1ttle.flightassistant.hud;

import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import ru.octol1ttle.flightassistant.Dimensions;
import ru.octol1ttle.flightassistant.FlightAssistant;
import ru.octol1ttle.flightassistant.compatibility.ImmediatelyFastBatchingAccessor;
import ru.octol1ttle.flightassistant.computers.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.ComputerHost;
import ru.octol1ttle.flightassistant.computers.TimeComputer;
import ru.octol1ttle.flightassistant.computers.autoflight.AutoFlightComputer;
import ru.octol1ttle.flightassistant.computers.autoflight.FireworkController;
import ru.octol1ttle.flightassistant.computers.navigation.FlightPlanner;
import ru.octol1ttle.flightassistant.computers.safety.AlertController;
import ru.octol1ttle.flightassistant.computers.safety.GPWSComputer;
import ru.octol1ttle.flightassistant.computers.safety.StallComputer;
import ru.octol1ttle.flightassistant.computers.safety.VoidLevelComputer;
import ru.octol1ttle.flightassistant.config.FAConfig;
import ru.octol1ttle.flightassistant.config.HUDConfig;
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
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;
import ru.octol1ttle.flightassistant.registries.HudDisplayRegistry;

public class HudDisplayHost {
    private final Dimensions dim = new Dimensions();

    public HudDisplayHost(ComputerHost computerHost) {
        HudDisplayRegistry.register(FlightAssistant.id("alert"),
                new AlertDisplay(dim, computerHost, ComputerRegistry.resolve(AlertController.class), ComputerRegistry.resolve(TimeComputer.class)));
        HudDisplayRegistry.register(FlightAssistant.id("altitude"),
                new AltitudeDisplay(dim, ComputerRegistry.resolve(AirDataComputer.class), ComputerRegistry.resolve(AutoFlightComputer.class), ComputerRegistry.resolve(FlightPlanner.class)));
        HudDisplayRegistry.register(FlightAssistant.id("attitude"),
                new AttitudeDisplay(dim, ComputerRegistry.resolve(AirDataComputer.class), ComputerRegistry.resolve(StallComputer.class), ComputerRegistry.resolve(VoidLevelComputer.class)));
        HudDisplayRegistry.register(FlightAssistant.id("elytra_health"),
                new ElytraHealthDisplay(dim, ComputerRegistry.resolve(AirDataComputer.class)));
        HudDisplayRegistry.register(FlightAssistant.id("flight_directors"),
                new FlightDirectorsDisplay(dim, ComputerRegistry.resolve(AirDataComputer.class), ComputerRegistry.resolve(AutoFlightComputer.class)));
        HudDisplayRegistry.register(FlightAssistant.id("flight_mode"),
                new FlightModeDisplay(dim, ComputerRegistry.resolve(AirDataComputer.class), ComputerRegistry.resolve(TimeComputer.class), ComputerRegistry.resolve(FireworkController.class), ComputerRegistry.resolve(AutoFlightComputer.class), ComputerRegistry.resolve(FlightPlanner.class)));
        HudDisplayRegistry.register(FlightAssistant.id("flight_path"),
                new FlightPathDisplay(dim, ComputerRegistry.resolve(AirDataComputer.class), ComputerRegistry.resolve(GPWSComputer.class)));
        HudDisplayRegistry.register(FlightAssistant.id("ground_speed"),
                new GroundSpeedDisplay(dim, ComputerRegistry.resolve(AirDataComputer.class)));
        HudDisplayRegistry.register(FlightAssistant.id("heading"),
                new HeadingDisplay(dim, ComputerRegistry.resolve(AirDataComputer.class), ComputerRegistry.resolve(AutoFlightComputer.class)));
        HudDisplayRegistry.register(FlightAssistant.id("radar_altitude"),
                new RadarAltitudeDisplay(dim, ComputerRegistry.resolve(AirDataComputer.class), ComputerRegistry.resolve(FlightPlanner.class)));
        HudDisplayRegistry.register(FlightAssistant.id("location"),
                new LocationDisplay(dim, ComputerRegistry.resolve(AirDataComputer.class)));
        HudDisplayRegistry.register(FlightAssistant.id("speed"),
                new SpeedDisplay(dim, ComputerRegistry.resolve(AirDataComputer.class)));
        HudDisplayRegistry.register(FlightAssistant.id("status"),
                new StatusDisplay(dim, ComputerRegistry.resolve(FireworkController.class), ComputerRegistry.resolve(FlightPlanner.class)));
        HudDisplayRegistry.register(FlightAssistant.id("vertical_speed"),
                new VerticalSpeedDisplay(dim, ComputerRegistry.resolve(AirDataComputer.class)));
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

        for (Map.Entry<Identifier, IHudDisplay> entry : HudDisplayRegistry.getDisplays()) {
            IHudDisplay display = entry.getValue();
            drawBatchedComponent(() -> {
                try {
                    display.render(context, mc.textRenderer);
                } catch (Throwable t) {
                    FlightAssistant.LOGGER.error("Exception rendering display with ID: %s".formatted(entry.getKey()), t);
                }
            });
        }
        if (batchAll) {
            ImmediatelyFastBatchingAccessor.endHudBatching();
        }

        // TODO: faulted display renders

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
}
