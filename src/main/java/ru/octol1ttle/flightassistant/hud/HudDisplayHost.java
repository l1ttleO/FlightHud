package ru.octol1ttle.flightassistant.hud;

import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import ru.octol1ttle.flightassistant.Dimensions;
import ru.octol1ttle.flightassistant.FlightAssistant;
import ru.octol1ttle.flightassistant.compatibility.immediatelyfast.HUDBatching;
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
import ru.octol1ttle.flightassistant.registries.HudDisplayRegistry;
import ru.octol1ttle.flightassistant.registries.events.CustomHudDisplayRegistrationCallback;

import static ru.octol1ttle.flightassistant.FlightAssistant.id;

public class HudDisplayHost {
    private final Dimensions dim = new Dimensions();

    public HudDisplayHost() {
        HudDisplayRegistry.register(id("alert"), new AlertDisplay(dim));
        HudDisplayRegistry.register(id("altitude"), new AltitudeDisplay(dim));
        HudDisplayRegistry.register(id("attitude"), new AttitudeDisplay(dim));
        HudDisplayRegistry.register(id("elytra_health"), new ElytraHealthDisplay(dim));
        HudDisplayRegistry.register(id("flight_directors"), new FlightDirectorsDisplay(dim));
        HudDisplayRegistry.register(id("flight_mode"), new FlightModeDisplay(dim));
        HudDisplayRegistry.register(id("flight_path"), new FlightPathDisplay(dim));
        HudDisplayRegistry.register(id("ground_speed"), new GroundSpeedDisplay(dim));
        HudDisplayRegistry.register(id("heading"), new HeadingDisplay(dim));
        HudDisplayRegistry.register(id("radar_altitude"), new RadarAltitudeDisplay(dim));
        HudDisplayRegistry.register(id("location"), new LocationDisplay(dim));
        HudDisplayRegistry.register(id("speed"), new SpeedDisplay(dim));
        HudDisplayRegistry.register(id("status"), new StatusDisplay(dim));
        HudDisplayRegistry.register(id("vertical_speed"), new VerticalSpeedDisplay(dim));

        CustomHudDisplayRegistrationCallback.EVENT.invoker().registerCustomDisplays();
    }

    public void render(MinecraftClient mc, DrawContext context, float tickDelta) {
        GameRendererInvoker renderer = (GameRendererInvoker) mc.gameRenderer;
        dim.update(context, renderer.invokeGetFov(mc.gameRenderer.getCamera(), tickDelta, true));

        float hudScale = FAConfig.hud().hudScale;
        boolean batchAll = FlightAssistant.canUseBatching() && FAConfig.hud().batchedRendering == HUDConfig.BatchedRendering.SINGLE_BATCH;

        context.getMatrices().push();
        context.getMatrices().scale(hudScale, hudScale, hudScale);
        HUDBatching.tryBeginIf(batchAll);

        for (Map.Entry<Identifier, IHudDisplay> entry : HudDisplayRegistry.getDisplays()) {
            Identifier id = entry.getKey();
            IHudDisplay display = entry.getValue();
            boolean perComponent = FlightAssistant.canUseBatching() && FAConfig.hud().batchedRendering == HUDConfig.BatchedRendering.PER_COMPONENT;
            HUDBatching.tryBeginIf(perComponent);
            try {
                if (!HudDisplayRegistry.isFaulted(id)) {
                    display.render(context, mc.textRenderer);
                } else {
                    display.renderFaulted(context, mc.textRenderer);
                }
            } catch (Throwable t) {
                HudDisplayRegistry.markFaulted(id, t, "Exception rendering display with ID: %s".formatted(id));
            }
            HUDBatching.tryEndIf(perComponent);
        }

        HUDBatching.tryEndIf(batchAll);
        context.getMatrices().pop();
    }
}
