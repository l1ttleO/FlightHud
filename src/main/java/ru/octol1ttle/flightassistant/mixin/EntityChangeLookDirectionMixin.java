package ru.octol1ttle.flightassistant.mixin;

import java.util.Objects;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import ru.octol1ttle.flightassistant.api.event.ChangeLookDirectionEvents;

@Mixin(Entity.class)
abstract class EntityChangeLookDirectionMixin {
    @ModifyVariable(method = "changeLookDirection", at = @At("STORE"), ordinal = 0)
    private float overridePitchChange(float pitchDelta) {
        return Objects.requireNonNullElse(ChangeLookDirectionEvents.PITCH.invoker().onPitchChange((Entity) (Object) this, pitchDelta), pitchDelta);
    }
}
