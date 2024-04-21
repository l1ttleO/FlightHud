package ru.octol1ttle.flightassistant.computers.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import ru.octol1ttle.flightassistant.FAMathHelper;
import ru.octol1ttle.flightassistant.computers.api.ITickableComputer;
import ru.octol1ttle.flightassistant.config.ComputerConfig;
import ru.octol1ttle.flightassistant.config.FAConfig;

import static net.minecraft.SharedConstants.TICKS_PER_SECOND;

public class AirDataComputer implements ITickableComputer {
    public static final float OPTIMUM_GLIDE_RATIO = 10.0f;
    private final MinecraftClient mc;
    public Vec3d velocity = Vec3d.ZERO;
    public float roll;
    public float flightPitch;
    public float flightYaw;
    public float groundLevel;
    public Float elytraHealth;
    public boolean isCurrentChunkLoaded;

    public AirDataComputer(MinecraftClient mc) {
        this.mc = mc;
    }

    @Override
    public void tick() {
        velocity = player().getVelocity().multiply(TICKS_PER_SECOND);
        roll = computeRoll(RenderSystem.getInverseViewRotationMatrix().invert());
        isCurrentChunkLoaded = isCurrentChunkLoaded();
        groundLevel = computeGroundLevel();
        flightPitch = computeFlightPitch(velocity, pitch());
        flightYaw = computeFlightYaw(velocity, yaw());
        elytraHealth = computeElytraHealth();
    }

    public boolean canAutomationsActivate() {
        return canAutomationsActivate(true);
    }

    public boolean canAutomationsActivate(boolean checkFlying) {
        ComputerConfig.GlobalAutomationsMode mode = FAConfig.computer().globalMode;
        boolean flying = !checkFlying || isFlying();
        return switch (mode) {
            case FULL -> flying && (!mc.isInSingleplayer() || !mc.isPaused());
            case NO_OVERLAYS -> flying && mc.currentScreen == null && mc.getOverlay() == null;
            case DISABLED -> false;
        };
    }

    private float computeRoll(Matrix3f normalMatrix) {
        float y = normalMatrix.getRowColumn(0, 1);
        float x = normalMatrix.getRowColumn(1, 1);

        return validate(FAMathHelper.toDegrees(Math.atan2(y, x)), 180.0f);
    }

    private float computeFlightPitch(Vec3d velocity, float pitch) {
        if (velocity.length() < 0.01) {
            return pitch;
        }
        Vec3d n = velocity.normalize();
        return validate(90 - FAMathHelper.toDegrees(Math.acos(n.y)), 90.0f);
    }

    private float computeFlightYaw(Vec3d velocity, float yaw) {
        if (velocity.horizontalLength() < 0.01) {
            return validate(yaw, 180.0f);
        }
        return validate(FAMathHelper.toDegrees(Math.atan2(-velocity.x, velocity.z)), 180.0f);
    }

    private Float computeElytraHealth() {
        ItemStack stack = player().getEquippedStack(EquipmentSlot.CHEST);
        if (stack != null && stack.getItem().equals(Items.ELYTRA)) {
            float remain = (float) (stack.getMaxDamage() - stack.getDamage()) / stack.getMaxDamage();
            return validate(remain * 100.0f, 0.0f, 100.0f);
        }
        return null;
    }

    private float computeGroundLevel() {
        if (!isCurrentChunkLoaded) {
            return groundLevel; // last known cache
        }
        Vec3d ground = findGround(player().getBlockPos().mutableCopy());
        return ground == null ? voidLevel() : (float) ground.getY();
    }

    public boolean isGround(BlockPos pos) {
        BlockState block = world().getBlockState(pos);
        return !block.isAir();
    }

    public Vec3d findGround(BlockPos.Mutable from) {
        if (!isChunkLoadedAt(from)) {
            return null;
        }

        BlockHitResult result = world().raycast(new RaycastContext(
                position().offset(Direction.UP, 0.5),
                position().withAxis(Direction.Axis.Y, voidLevel()),
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.ANY,
                player()
        ));
        return result.getPos();
    }

    public static float toHeading(float yawDegrees) {
        return validate(yawDegrees + 180.0f, 0.0f, 360.0f);
    }

    public @NotNull ClientPlayerEntity player() {
        if (mc.player == null) {
            throw new AssertionError();
        }
        return mc.player;
    }

    public boolean isFlying() {
        return player().isFallFlying();
    }

    public Vec3d position() {
        return player().getPos();
    }

    public float altitude() {
        return (float) position().y;
    }

    public float speed() {
        return (float) velocity.length();
    }

    public float pitch() {
        return validate(-player().getPitch(), 90.0f);
    }

    public float yaw() {
        return validate(MathHelper.wrapDegrees(player().getYaw()), 180.0f);
    }

    public float heading() {
        return toHeading(yaw());
    }

    public float flightHeading() {
        return toHeading(flightYaw);
    }

    public float heightAboveGround() {
        return altitude() - groundLevel;
    }

    public int voidLevel() {
        return world().getBottomY() - 64;
    }

    public float fallDistance() {
        return Math.max(player().fallDistance, heightAboveGround());
    }

    public World world() {
        return player().getWorld();
    }

    public boolean isChunkLoadedAt(BlockPos pos){
        return world().getChunkManager().isChunkLoaded(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()));
    }

    private boolean isCurrentChunkLoaded(){
        BlockPos pos = player().getBlockPos();
        return isChunkLoadedAt(pos);
    }

    public static float validate(float f, float bounds) {
        return validate(f, -bounds, bounds);
    }

    public static float validate(float f, float min, float max) {
        if (f < min || f > max) {
            throw new AssertionError(f);
        }

        return f;
    }

    @Override
    public String getId() {
        return "air_data";
    }

    @Override
    public void reset() {
        velocity = Vec3d.ZERO;
        flightPitch = 0.0f;
        flightYaw = 0.0f;
        roll = 0.0f;
        groundLevel = 0;
        elytraHealth = null;
        isCurrentChunkLoaded = true;
    }
}
