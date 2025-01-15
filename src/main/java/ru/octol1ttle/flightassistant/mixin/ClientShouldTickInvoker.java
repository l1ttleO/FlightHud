package ru.octol1ttle.flightassistant.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MinecraftClient.class)
public interface ClientShouldTickInvoker {
//? if >=1.21 {
    /*@org.spongepowered.asm.mixin.gen.Invoker("shouldTick")
    boolean invokeShouldTick();
*///?}
}
