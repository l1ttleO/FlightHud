package ru.octol1ttle.flightassistant;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.shadowhunter22.api.client.renderer.v1.AlternateHudRendererCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.TypedActionResult;
import ru.octol1ttle.flightassistant.commands.FlightPlanCommand;
import ru.octol1ttle.flightassistant.commands.ResetCommand;
import ru.octol1ttle.flightassistant.commands.SelectCommand;
import ru.octol1ttle.flightassistant.computers.api.IPitchLimiter;
import ru.octol1ttle.flightassistant.computers.impl.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.ComputerHost;
import ru.octol1ttle.flightassistant.computers.impl.TimeComputer;
import ru.octol1ttle.flightassistant.computers.impl.autoflight.FireworkController;
import ru.octol1ttle.flightassistant.computers.impl.safety.GPWSComputer;
import ru.octol1ttle.flightassistant.config.FAConfig;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;
import ru.octol1ttle.flightassistant.registries.events.ComputerRegisteredCallback;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class FACallbacks {
    public static void setup() {
        setupClientStart();
        setupCommandRegistration();
        setupWorldRender();
        setupHudRender();
        setupUseItem();
        setupComputerRegistered();
    }

    private static void setupClientStart() {
        ClientLifecycleEvents.CLIENT_STARTED.register(FlightAssistant::onClientStarted);
    }

    private static void setupCommandRegistration() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            LiteralArgumentBuilder<FabricClientCommandSource> builder = literal(FlightAssistant.MODID);
            ResetCommand.register(builder);
            SelectCommand.register(builder);
            FlightPlanCommand.register(builder);

            LiteralCommandNode<FabricClientCommandSource> node = dispatcher.register(builder);
            dispatcher.register(literal("flas").redirect(node));
            dispatcher.register(literal("fhud").redirect(node));
            dispatcher.register(literal("fh").redirect(node));
        });
    }

    private static void setupWorldRender() {
        WorldRenderEvents.END.register(context ->
                ComputerHost.instance().tick()
        );
    }

    private static void setupHudRender() {
        AlternateHudRendererCallback.EVENT.register((drawContext, tickDelta) ->
                FlightAssistant.getDisplayHost().render(MinecraftClient.getInstance(), drawContext, tickDelta)
        );
    }

    private static void setupUseItem() {
        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack stack = player.getStackInHand(hand);
            AirDataComputer data = ComputerRegistry.resolve(AirDataComputer.class);
            GPWSComputer gpws = ComputerRegistry.resolve(GPWSComputer.class);
            FireworkController firework = ComputerRegistry.resolve(FireworkController.class);
            TimeComputer time = ComputerRegistry.resolve(TimeComputer.class);

            if (!world.isClient() || ComputerRegistry.isFaulted(FireworkController.class)) {
                return TypedActionResult.pass(stack);
            }
            if (!data.isFlying() || !(stack.getItem() instanceof FireworkRocketItem)) {
                return TypedActionResult.pass(stack);
            }

            boolean gpwsLocksFireworks = FAConfig.computer().lockFireworksFacingTerrain;
            boolean gpwsDanger = !ComputerRegistry.isFaulted(GPWSComputer.class) && gpwsLocksFireworks && (gpws.isInDanger() || !gpws.fireworkUseSafe);

            boolean unsafeFireworks = FAConfig.computer().lockUnsafeFireworks && !firework.isFireworkSafe(player.getStackInHand(hand));

            if (!firework.activationInProgress && (unsafeFireworks || firework.lockManualFireworks || gpwsDanger)) {
                return TypedActionResult.fail(stack);
            }

            if (firework.fireworkResponded) {
                if (!ComputerRegistry.isFaulted(TimeComputer.class) && time.millis != null) {
                    firework.lastUseTime = time.millis;
                    firework.lastDiff = 0.0f;
                }
                firework.fireworkResponded = false;
            }

            return TypedActionResult.pass(stack);
        });
    }

    private static void setupComputerRegistered() {
        ComputerRegisteredCallback.EVENT.register(computer -> {
            if (computer instanceof IPitchLimiter limiter) {
                IPitchLimiter.instances.add(limiter);
            }
        });
    }
}
