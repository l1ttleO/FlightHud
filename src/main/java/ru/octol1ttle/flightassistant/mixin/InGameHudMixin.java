package ru.octol1ttle.flightassistant.mixin;

import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.octol1ttle.flightassistant.api.event.FixedHudRenderCallback;

@Mixin(InGameHud.class)
abstract class InGameHudMixin {
//? if fabric {
//? if >=1.21 {
    /*@org.spongepowered.asm.mixin.Shadow @org.spongepowered.asm.mixin.Final
    private net.minecraft.client.gui.LayeredDrawer layeredDrawer;

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/LayeredDrawer;addLayer(Lnet/minecraft/client/gui/LayeredDrawer$Layer;)Lnet/minecraft/client/gui/LayeredDrawer;", ordinal = 3))
    public void render(net.minecraft.client.MinecraftClient client, CallbackInfo ci) {
        this.layeredDrawer.addLayer((context, tickCounter) -> FixedHudRenderCallback.EVENT.invoker().onRenderHud(context, tickCounter.getTickDelta(true)));
    }
*///?} else {
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;getCurrentGameMode()Lnet/minecraft/world/GameMode;", ordinal = 1))
    private void render(net.minecraft.client.gui.DrawContext drawContext, float tickDelta, CallbackInfo callbackInfo) {
        FixedHudRenderCallback.EVENT.invoker().onRenderHud(drawContext, tickDelta);
    }
//?}
//?}
}
