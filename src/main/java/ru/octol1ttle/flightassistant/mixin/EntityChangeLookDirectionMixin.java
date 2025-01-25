package ru.octol1ttle.flightassistant.mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import ru.octol1ttle.flightassistant.MixinHandlerKt;
import ru.octol1ttle.flightassistant.api.computer.autoflight.ControlInput;
import ru.octol1ttle.flightassistant.api.event.ChangeLookDirectionEvents;
import ru.octol1ttle.flightassistant.impl.computer.ComputerHost;

@Mixin(Entity.class)
abstract class EntityChangeLookDirectionMixin {
    @ModifyVariable(method = "changeLookDirection", at = @At("STORE"), ordinal = 0)
    private float overridePitchChange(float pitchDelta) {
        List<ControlInput> list = new ArrayList<>();
        ChangeLookDirectionEvents.PITCH.invoker().onPitchChange(ComputerHost.INSTANCE, pitchDelta, list);
        return Objects.requireNonNullElse(MixinHandlerKt.onEntityChangeLookDirection(list), pitchDelta);
    }
}
