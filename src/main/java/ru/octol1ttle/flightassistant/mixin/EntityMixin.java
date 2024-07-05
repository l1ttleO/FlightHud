package ru.octol1ttle.flightassistant.mixin;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import ru.octol1ttle.flightassistant.util.events.ChangeLookDirectionEvents;

@SuppressWarnings("UnreachableCode")
@Mixin(Entity.class)
public abstract class EntityMixin {
    @ModifyVariable(method = "changeLookDirection", at = @At("STORE"), ordinal = 0)
    public float preventUpsetPitch(float pitchDelta) {
        return ChangeLookDirectionEvents.PITCH.invoker().onPitchChange((Entity) (Object) this, pitchDelta).orElse(pitchDelta);
    }
}
