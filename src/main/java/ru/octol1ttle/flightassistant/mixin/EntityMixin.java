package ru.octol1ttle.flightassistant.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import ru.octol1ttle.flightassistant.computers.api.IComputer;
import ru.octol1ttle.flightassistant.computers.api.IPitchLimiter;
import ru.octol1ttle.flightassistant.computers.impl.AirDataComputer;
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

            for (IPitchLimiter limiter : IPitchLimiter.instances) {
                if (!limiter.getProtectionMode().override()
                        || limiter instanceof IComputer computer && ComputerRegistry.isFaulted(computer.getClass())) {
                    continue;
                }

                if (limiter.blockPitchChange(newPitch > oldPitch ? Direction.UP : Direction.DOWN)
                        || newPitch > oldPitch && limiter.getMaximumPitch() < newPitch
                        || newPitch < oldPitch && limiter.getMinimumPitch() > newPitch) {
                    return 0.0f;
                }
            }
        }

        return pitchDelta;
    }
}
