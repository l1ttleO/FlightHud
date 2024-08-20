package ru.octol1ttle.flightassistant.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.LayeredDrawer;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.octol1ttle.flightassistant.util.events.AlternateHudRendererCallback;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Shadow
    @Final
    private LayeredDrawer layeredDrawer;

    // modifications made: changed injection point
    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/LayeredDrawer;addLayer(Lnet/minecraft/client/gui/LayeredDrawer$Layer;)Lnet/minecraft/client/gui/LayeredDrawer;", ordinal = 3))
    public void render(MinecraftClient client, CallbackInfo ci) {
        // modifications made: changed from FAPIs interface to my interface
        this.layeredDrawer.addLayer((context, tickCounter) -> AlternateHudRendererCallback.EVENT.invoker().onHudRender(context, tickCounter.getTickDelta(true)));
    }
}
