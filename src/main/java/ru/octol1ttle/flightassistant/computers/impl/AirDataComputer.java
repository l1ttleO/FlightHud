package ru.octol1ttle.flightassistant.computers.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import ru.octol1ttle.flightassistant.DrawHelper;
import ru.octol1ttle.flightassistant.FAMathHelper;
import ru.octol1ttle.flightassistant.computers.api.ITickableComputer;
import ru.octol1ttle.flightassistant.config.ComputerConfig;
import ru.octol1ttle.flightassistant.config.FAConfig;
import ru.octol1ttle.flightassistant.config.IndicatorConfig;

import static net.minecraft.SharedConstants.TICKS_PER_SECOND;

public class AirDataComputer implements ITickableComputer {
    public static final float OPTIMUM_GLIDE_RATIO = 10.0f;
    private final MinecraftClient mc;
    public Vec3d velocity = Vec3d.ZERO;
    public Vec3d forwardVelocity = velocity;
    public float roll;
    public float flightPitch;
    public float flightYaw;
    public float groundLevel;
    public @Nullable AirDataComputer.ElytraData elytraData = null;
    public boolean isCurrentChunkLoaded;

    public AirDataComputer(MinecraftClient mc) {
        this.mc = mc;
    }

    @Override
    public void tick() {
        velocity = player().getVelocity().multiply(TICKS_PER_SECOND);
        forwardVelocity = computeForwardVelocity();
        roll = computeRoll(RenderSystem.getInverseViewRotationMatrix().invert());
        isCurrentChunkLoaded = isCurrentChunkLoaded();
        groundLevel = computeGroundLevel();
        flightPitch = computeFlightPitch(velocity, pitch());
        flightYaw = computeFlightYaw(velocity, yaw());
        elytraData = computeElytraData();
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

    public boolean isInvulnerableTo(DamageSource source) {
        if (!FAConfig.computer().considerInvulnerability) {
            return false;
        }
        return player().isInvulnerableTo(source)
                || player().getAbilities().invulnerable && !source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)
                || player().getAbilities().allowFlying && source.isIn(DamageTypeTags.IS_FALL);
    }

    public boolean isInvulnerable() {
        if (!FAConfig.computer().considerInvulnerability) {
            return false;
        }
        return player().isInvulnerable() || player().getAbilities().invulnerable;
    }

    private Vec3d computeForwardVelocity() {
        Vec3d lookVelocity = velocity.multiply(player().getRotationVector());
        return new Vec3d(Math.max(0.0, lookVelocity.x), Math.max(0.0, lookVelocity.y), Math.max(0.0, lookVelocity.z));
    }

    private float computeRoll(Matrix3f matrix) {
        return validate(FAMathHelper.toDegrees(Math.atan2(-matrix.m10(), matrix.m11())), -180.0f, 180.0f);
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

    private ElytraData computeElytraData() {
        for (ItemStack stack : player().getArmorItems()) {
            if (Items.ELYTRA.equals(stack.getItem())) {
                return new ElytraData(stack.copy(), true);
            }
        }
        for (ItemStack stack : player().getHandItems()) {
            if (Items.ELYTRA.equals(stack.getItem())) {
                return new ElytraData(stack.copy(), false);
            }
        }

        return null;
    }

    private float computeGroundLevel() {
        if (!isCurrentChunkLoaded) {
            return groundLevel; // last known cache
        }
        Vec3d ground = findGround(position());
        return ground == null ? voidLevel() : (float) ground.getY();
    }

    public boolean isGround(BlockPos pos) {
        BlockState block = world().getBlockState(pos);
        return !block.isAir();
    }

    public Vec3d findGround(Vec3d from) {
        if (!isChunkLoadedAt(from)) {
            return null;
        }

        BlockHitResult result = world().raycast(new RaycastContext(
                from.offset(Direction.UP, 0.5),
                from.withAxis(Direction.Axis.Y, voidLevel()),
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
            throw new IllegalStateException("Attempted to get client player when they do not exist");
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

    public boolean isChunkLoadedAt(Vec3d pos) {
        return world().getChunkManager().isChunkLoaded(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()));
    }

    private boolean isCurrentChunkLoaded() {
        return isChunkLoadedAt(position());
    }

    public static float validate(float f, float bounds) {
        return validate(f, -bounds, bounds);
    }

    public static float validate(float f, float min, float max) {
        if (f < min || f > max) {
            throw new IllegalArgumentException("Float value out of bounds: expected values from %s to %s (inclusive), got %s".formatted(min, max, f));
        }

        return f;
    }

    @Override
    public String getFaultTextBaseKey() {
        return "alerts.flightassistant.fault.computers.air_data";
    }

    @Override
    public void reset() {
        velocity = Vec3d.ZERO;
        flightPitch = 0.0f;
        flightYaw = 0.0f;
        roll = 0.0f;
        groundLevel = 0;
        elytraData = null;
        isCurrentChunkLoaded = true;
    }

    public static class ElytraData {
        private final ItemStack stack;
        private final boolean canFallFly;

        public ElytraData(ItemStack stack, boolean canFallFly) {
            if (!Items.ELYTRA.equals(stack.getItem())) {
                throw new IllegalStateException("Attempted to initialize ElytraData, but the ItemStack does not contain an Elytra");
            }
            this.stack = stack;
            this.canFallFly = canFallFly;
        }

        public float getHealth(IndicatorConfig.ElytraHealthDisplayUnits units) {
            float remaining = (stack.getMaxDamage() - 1) - stack.getDamage();
            return switch (units) {
                case REMAINING_DURABILITY -> validate(remaining, 0.0f, stack.getMaxDamage());
                case PERCENTAGE -> validate(remaining / stack.getMaxDamage() * 100.0f, 0.0f, 100.0f);
            };
        }

        public Text formatHealth(IndicatorConfig.ElytraHealthDisplayUnits units) {
            if (!stack.isDamageable()) {
                return Text.translatable("short.flightassistant.infinite");
            }

            MutableText text = DrawHelper.asText("%s", MathHelper.ceil(getHealth(units)));
            if (units == IndicatorConfig.ElytraHealthDisplayUnits.PERCENTAGE) {
                text.append("%");
            }

            return text;
        }

        public boolean isUsable() {
            return canFallFly && stack.getMaxDamage() - stack.getDamage() > 1;
        }
    }
}
