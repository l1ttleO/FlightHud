package ru.octol1ttle.flightassistant.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import ru.octol1ttle.flightassistant.hud.HudDisplayHost;
import ru.octol1ttle.flightassistant.computers.ComputerHost;
import ru.octol1ttle.flightassistant.config.FAConfig;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @ModifyVariable(method = "changeLookDirection", at = @At("STORE"), ordinal = 0)
    public float preventUpsetPitch(float pitchDelta) {
        Entity that = (Entity) (Object) this;

        ComputerHost host = HudDisplayHost.getHost();
        if (that instanceof ClientPlayerEntity && host.data.canAutomationsActivate()) {
            float oldPitch = host.data.pitch();
            float newPitch = oldPitch + (-pitchDelta);

            boolean isStalling = !host.faulted.contains(host.stall) && host.stall.isPitchUnsafe(newPitch);
            boolean stallLock = FAConfig.computer().stallProtection.override() && isStalling;

            boolean gpwsLock = !isStalling && !host.faulted.contains(host.gpws) && host.gpws.shouldBlockPitchChanges();
            boolean voidLevelLock = !host.faulted.contains(host.voidLevel) && host.voidLevel.shouldBlockPitchChange(newPitch);

            if (stallLock && newPitch > oldPitch ||
                    (gpwsLock || voidLevelLock) && newPitch < oldPitch) {
                return 0.0f;
            }
        }

        return pitchDelta;
    }
}
