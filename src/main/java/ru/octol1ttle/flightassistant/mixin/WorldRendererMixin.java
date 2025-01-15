package ru.octol1ttle.flightassistant.mixin;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.octol1ttle.flightassistant.api.event.WorldRenderCallback;

@Mixin(WorldRenderer.class)
abstract class WorldRendererMixin {
    @Inject(method = "render", at = @At("HEAD"))
//? if >=1.21 {
/*//? if >=1.21.2 {
    /^private void onStartRender(net.minecraft.client.util.ObjectAllocator allocator, net.minecraft.client.render.RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f positionMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {
^///?} else {
    private void onStartRender(net.minecraft.client.render.RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f positionMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {
//?}
        WorldRenderCallback.EVENT.invoker().onStartRenderWorld(tickCounter.getTickDelta(true), camera, projectionMatrix, positionMatrix.get3x3(new org.joml.Matrix3f()));
*///?} else {
    private void onStartRender(net.minecraft.client.util.math.MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f projectionMatrix, CallbackInfo ci) {
        WorldRenderCallback.EVENT.invoker().onStartRenderWorld(tickDelta, camera, projectionMatrix, com.mojang.blaze3d.systems.RenderSystem.getInverseViewRotationMatrix().invert());
//?}
    }
}
