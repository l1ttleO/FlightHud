package ru.octol1ttle.flightassistant.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.octol1ttle.flightassistant.util.events.FireworkBoostCallback;

@Mixin(FireworkRocketEntity.class)
public class FireworkRocketEntityMixin {
    @Shadow private @Nullable LivingEntity shooter;

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V", shift = At.Shift.AFTER))
    public void onFireworkActivation(CallbackInfo ci) {
        FireworkBoostCallback.EVENT.invoker().onFireworkBoost((FireworkRocketEntity) (Object) this, this.shooter);
    }
}
