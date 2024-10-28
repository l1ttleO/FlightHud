package ru.octol1ttle.flightassistant.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MinecraftClient.class)
public interface ClientShouldTickInvoker {
    @Invoker("shouldTick")
    boolean invokeShouldTick();
}
