package ru.octol1ttle.flightassistant;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.octol1ttle.flightassistant.computers.ComputerHost;
import ru.octol1ttle.flightassistant.config.FAConfig;
import ru.octol1ttle.flightassistant.config.HUDConfig;
import ru.octol1ttle.flightassistant.hud.HudDisplayHost;

public class FlightAssistant implements ClientModInitializer {
    public static final String MODID = "flightassistant";
    public static final Logger LOGGER = LoggerFactory.getLogger("FlightAssistant");
    private static HudDisplayHost displayHost;
    private static ComputerHost computerHost;

    @Override
    public void onInitializeClient() {
        FAConfig.setup();
        FAKeyBindings bindings = new FAKeyBindings();
        bindings.registerAll();
        FACallbackListener.setup(new FACallbackListener(bindings));
    }

    public static void onClientStarted(MinecraftClient mc) {
        if (computerHost != null) {
            throw new IllegalStateException();
        }
        computerHost = new ComputerHost(mc);

        if (displayHost != null) {
            throw new IllegalStateException();
        }
        displayHost = new HudDisplayHost();
    }

    public static HudDisplayHost getDisplayHost() {
        if (displayHost == null) {
            throw new IllegalStateException();
        }
        return displayHost;
    }

    public static ComputerHost getComputerHost() {
        if (computerHost == null) {
            throw new IllegalStateException();
        }
        return computerHost;
    }

    public static Identifier id(String path) {
        return new Identifier(FlightAssistant.MODID, path);
    }

    public static boolean isHUDBatched() {
        return canUseBatching() && FAConfig.hud().batchedRendering != HUDConfig.BatchedRendering.NO_BATCHING;
    }

    public static boolean canUseBatching() {
        return FabricLoader.getInstance().isModLoaded("immediatelyfast");
    }
}
