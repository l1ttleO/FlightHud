package ru.octol1ttle.flightassistant.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.octol1ttle.flightassistant.api.event.FireworkBoostCallback;

@Mixin(FireworkRocketEntity.class)
abstract class FireworkRocketEntityMixin {
    @Shadow private @Nullable LivingEntity shooter;

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V", shift = At.Shift.AFTER))
    private void onFireworkActivation(CallbackInfo ci) {
        if (this.shooter instanceof ClientPlayerEntity cpe) {
            FireworkBoostCallback.EVENT.invoker().onFireworkBoost((FireworkRocketEntity) (Object) this, cpe);
        }
    }
}
