package ru.octol1ttle.flightassistant.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import ru.octol1ttle.flightassistant.computers.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.safety.GPWSComputer;
import ru.octol1ttle.flightassistant.computers.safety.StallComputer;
import ru.octol1ttle.flightassistant.computers.safety.VoidLevelComputer;
import ru.octol1ttle.flightassistant.config.FAConfig;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;

@SuppressWarnings("UnreachableCode")
@Mixin(Entity.class)
public abstract class EntityMixin {
    @ModifyVariable(method = "changeLookDirection", at = @At("STORE"), ordinal = 0)
    public float preventUpsetPitch(float pitchDelta) {
        Entity that = (Entity) (Object) this;

        AirDataComputer data = ComputerRegistry.resolve(AirDataComputer.class);
        if (that instanceof ClientPlayerEntity && data.canAutomationsActivate()) {
            float oldPitch = data.pitch();
            float newPitch = oldPitch + (-pitchDelta);

            boolean isStalling = !ComputerRegistry.isFaulted(StallComputer.class) && ComputerRegistry.resolve(StallComputer.class).isPitchUnsafe(newPitch);
            boolean stallLock = FAConfig.computer().stallProtection.override() && isStalling;

            boolean gpwsLock = !isStalling && !ComputerRegistry.isFaulted(GPWSComputer.class) && ComputerRegistry.resolve(GPWSComputer.class).shouldBlockPitchChanges();
            boolean voidLevelLock = !ComputerRegistry.isFaulted(VoidLevelComputer.class) && ComputerRegistry.resolve(VoidLevelComputer.class).shouldBlockPitchChange(newPitch);

            if (stallLock && newPitch > oldPitch ||
                    (gpwsLock || voidLevelLock) && newPitch < oldPitch) {
                return 0.0f;
            }
        }

        return pitchDelta;
    }
}
