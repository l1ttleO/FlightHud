package ru.octol1ttle.flightassistant.mixin;

import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import ru.octol1ttle.flightassistant.api.util.event.FixedHudRenderCallback;

@Mixin(InGameHud.class)
abstract class InGameHudMixin {
//? if fabric {
//? if >=1.21 {
    /*@com.llamalad7.mixinextras.injector.ModifyReceiver(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/LayeredDrawer;addLayer(Lnet/minecraft/client/gui/LayeredDrawer$Layer;)Lnet/minecraft/client/gui/LayeredDrawer;", ordinal = 2))
    public net.minecraft.client.gui.LayeredDrawer render(net.minecraft.client.gui.LayeredDrawer instance, net.minecraft.client.gui.LayeredDrawer.Layer layer) {
        return instance.addLayer((context, tickCounter) -> FixedHudRenderCallback.EVENT.invoker().onRenderHud(context, tickCounter.getTickDelta(true)));
    }
*///?} else {
    @org.spongepowered.asm.mixin.injection.Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;getCurrentGameMode()Lnet/minecraft/world/GameMode;", ordinal = 0))
    private void render(net.minecraft.client.gui.DrawContext drawContext, float tickDelta, org.spongepowered.asm.mixin.injection.callback.CallbackInfo callbackInfo) {
        FixedHudRenderCallback.EVENT.invoker().onRenderHud(drawContext, tickDelta);
    }
//?}
//?}
}
