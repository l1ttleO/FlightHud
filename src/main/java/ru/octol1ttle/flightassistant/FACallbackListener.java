package ru.octol1ttle.flightassistant;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Optional;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import org.joml.Matrix4f;
import ru.octol1ttle.flightassistant.commands.FlightPlanCommand;
import ru.octol1ttle.flightassistant.commands.ResetCommand;
import ru.octol1ttle.flightassistant.commands.SelectCommand;
import ru.octol1ttle.flightassistant.computers.ComputerHost;
import ru.octol1ttle.flightassistant.computers.impl.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.impl.TimeComputer;
import ru.octol1ttle.flightassistant.computers.impl.autoflight.FireworkController;
import ru.octol1ttle.flightassistant.computers.impl.safety.GroundProximityComputer;
import ru.octol1ttle.flightassistant.computers.impl.safety.PitchLimitComputer;
import ru.octol1ttle.flightassistant.config.ComputerConfig;
import ru.octol1ttle.flightassistant.config.FAConfig;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;
import ru.octol1ttle.flightassistant.util.ScreenSpaceRendering;
import ru.octol1ttle.flightassistant.util.events.AlternateHudRendererCallback;
import ru.octol1ttle.flightassistant.util.events.ChangeLookDirectionEvents;
import ru.octol1ttle.flightassistant.util.events.FireworkBoostCallback;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class FACallbackListener implements ClientCommandRegistrationCallback, ClientLifecycleEvents.ClientStarted, ClientTickEvents.EndTick, WorldRenderEvents.Start, AlternateHudRendererCallback, ChangeLookDirectionEvents.Pitch, UseItemCallback, FireworkBoostCallback {
    private final FAKeyBindings keyBindings;

    public FACallbackListener(FAKeyBindings keyBindings) {
        this.keyBindings = keyBindings;
    }

    public static void setup(FACallbackListener listener) {
        ClientCommandRegistrationCallback.EVENT.register(listener);
        ClientLifecycleEvents.CLIENT_STARTED.register(listener);
        ClientTickEvents.END_CLIENT_TICK.register(listener);
        WorldRenderEvents.START.register(listener);
        AlternateHudRendererCallback.EVENT.register(listener);
        ChangeLookDirectionEvents.PITCH.register(listener);
        UseItemCallback.EVENT.register(listener);
        FireworkBoostCallback.EVENT.register(listener);
    }

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        // TODO: cleaner command registration
        LiteralArgumentBuilder<FabricClientCommandSource> builder = literal(FlightAssistant.MODID);
        ResetCommand.register(builder);
        SelectCommand.register(builder);
        FlightPlanCommand.register(builder);

        LiteralCommandNode<FabricClientCommandSource> node = dispatcher.register(builder);
        dispatcher.register(literal("flas").redirect(node));
        dispatcher.register(literal("fhud").redirect(node));
        dispatcher.register(literal("fh").redirect(node));
    }

    @Override
    public void onClientStarted(MinecraftClient client) {
        FlightAssistant.onClientStarted(client);
    }

    @Override
    public void onEndTick(MinecraftClient client) {
        keyBindings.tick();
    }

    @Override
    public void onStart(WorldRenderContext context) {
        ComputerHost.instance().tick();

        ScreenSpaceRendering.lastProjMat.set(RenderSystem.getProjectionMatrix());
        ScreenSpaceRendering.lastModMat.set(RenderSystem.getModelViewMatrix());

        Matrix4f worldSpaceNoRoll = new Matrix4f();
        worldSpaceNoRoll.rotate(RotationAxis.POSITIVE_X.rotationDegrees(context.camera().getPitch()));
        worldSpaceNoRoll.rotate(RotationAxis.POSITIVE_Y.rotationDegrees(context.camera().getYaw() + 180.0F));
        ScreenSpaceRendering.lastWorldSpaceMatrix.set(worldSpaceNoRoll);
    }

    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {
        FlightAssistant.getDisplayHost().render(MinecraftClient.getInstance(), drawContext, tickDelta);
    }

    @Override
    public Optional<Float> onPitchChange(Entity entity, float pitchDelta) {
        if (!(entity instanceof ClientPlayerEntity) || ComputerRegistry.isFaulted(AirDataComputer.class) || ComputerRegistry.isFaulted(PitchLimitComputer.class)) {
            return Optional.empty();
        }

        AirDataComputer data = ComputerRegistry.resolve(AirDataComputer.class);
        PitchLimitComputer limit = ComputerRegistry.resolve(PitchLimitComputer.class);
        if (data.canAutomationsActivate()) {
            float oldPitch = data.pitch();
            float newPitch = oldPitch + (-pitchDelta);

            if (limit.blockPitchChange(newPitch > oldPitch ? Direction.UP : Direction.DOWN, ComputerConfig.ProtectionMode::override)) {
                return Optional.of(0.0f);
            }

            Pair<Float, Float> safePitches = limit.getSafePitches(ComputerConfig.ProtectionMode::override);
            if (newPitch < oldPitch && safePitches.getLeft() > newPitch) {
                return Optional.of(0.0f);
            }
            if (newPitch > oldPitch && safePitches.getRight() < newPitch) {
                return Optional.of(0.0f);
            }
        }

        return Optional.empty();
    }

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        AirDataComputer data = ComputerRegistry.resolve(AirDataComputer.class);
        GroundProximityComputer gpws = ComputerRegistry.resolve(GroundProximityComputer.class);
        FireworkController firework = ComputerRegistry.resolve(FireworkController.class);
        TimeComputer time = ComputerRegistry.resolve(TimeComputer.class);

        if (!world.isClient() || ComputerRegistry.isFaulted(FireworkController.class)) {
            return ActionResult.PASS;
        }
        if (!data.isFlying() || !(stack.getItem() instanceof FireworkRocketItem)) {
            return ActionResult.PASS;
        }

        boolean gpwsLocksFireworks = FAConfig.computer().lockFireworksFacingTerrain;
        boolean gpwsDanger = !ComputerRegistry.isFaulted(GroundProximityComputer.class) && gpwsLocksFireworks && (gpws.isInDanger() || !gpws.fireworkUseSafe);

        boolean unsafeFireworks = FAConfig.computer().lockUnsafeFireworks && !firework.isFireworkSafe(player.getStackInHand(hand));

        if (!firework.activationInProgress && (unsafeFireworks || gpwsDanger)) {
            return ActionResult.FAIL;
        }

        if (firework.fireworkResponded) {
            if (!ComputerRegistry.isFaulted(TimeComputer.class) && time.millis != null) {
                firework.lastUseTime = time.millis;
                firework.lastDiff = 0.0f;
            }
            firework.fireworkResponded = false;
        }

        return ActionResult.PASS;
    }

    @Override
    public void onFireworkBoost(FireworkRocketEntity rocket, LivingEntity shooter) {
        if (!(shooter instanceof ClientPlayerEntity) || ComputerRegistry.isFaulted(FireworkController.class)) {
            return;
        }
        ComputerRegistry.resolve(FireworkController.class).fireworkResponded = true;
    }
}
