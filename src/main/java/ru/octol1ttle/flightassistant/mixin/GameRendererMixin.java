package ru.octol1ttle.flightassistant.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.octol1ttle.flightassistant.util.events.RollMatrixCallback;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(
            method = "renderWorld",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lorg/joml/Matrix4f;rotation(Lorg/joml/Quaternionfc;)Lorg/joml/Matrix4f;",
                    ordinal = 0,
                    remap = false
            )
    )
    public void updateRoll(RenderTickCounter tickCounter, CallbackInfo ci, @Local(ordinal = 1) Matrix4f matrix4f2) {
        RollMatrixCallback.EVENT.invoker().onMatrixUpdate(matrix4f2);
    }
}
