package ru.octol1ttle.flightassistant.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityInvoker {
    @Invoker
    boolean invokeIsAlwaysInvulnerableTo(DamageSource damageSource);
}
