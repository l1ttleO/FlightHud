package ru.octol1ttle.flightassistant.mixin;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Entity.class)
public interface EntityInvoker {
//? if >=1.21.2 {
    /*@org.spongepowered.asm.mixin.gen.Invoker
    boolean invokeIsAlwaysInvulnerableTo(net.minecraft.entity.damage.DamageSource damageSource);
*///?}
}
