package ru.octol1ttle.flightassistant.mixin;

import net.minecraft.entity.projectile.FireworkRocketEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.octol1ttle.flightassistant.computers.autoflight.FireworkController;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;

@Mixin(FireworkRocketEntity.class)
public class FireworkRocketEntityMixin {
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V", shift = At.Shift.AFTER))
    public void onFireworkActivation(CallbackInfo ci) {
        FireworkController firework = ComputerRegistry.resolve(FireworkController.class);
        /*if (host.faulted.contains(host.firework)) {
            return;
        }*/
        firework.fireworkResponded = true;
    }
}
