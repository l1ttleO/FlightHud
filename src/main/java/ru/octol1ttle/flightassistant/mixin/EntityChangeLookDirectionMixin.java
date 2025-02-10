package ru.octol1ttle.flightassistant.mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import ru.octol1ttle.flightassistant.MixinHandlerKt;
import ru.octol1ttle.flightassistant.api.autoflight.ControlInput;
import ru.octol1ttle.flightassistant.api.util.event.ChangeLookDirectionEvents;
import ru.octol1ttle.flightassistant.impl.computer.ComputerHost;

@Mixin(Entity.class)
abstract class EntityChangeLookDirectionMixin {
    @ModifyVariable(method = "changeLookDirection", at = @At("STORE"), ordinal = 0)
    private float overridePitchChange(float pitchDelta) {
        List<ControlInput> list = new ArrayList<>();
        ChangeLookDirectionEvents.PITCH.invoker().onChangeLookDirection(pitchDelta, list);
        return Objects.requireNonNullElse(MixinHandlerKt.onEntityChangePitch(list), pitchDelta);
    }

    @ModifyVariable(method = "changeLookDirection", at = @At("STORE"), ordinal = 1)
    private float overrideHeadingChange(float headingDelta) {
        List<ControlInput> list = new ArrayList<>();
        ChangeLookDirectionEvents.HEADING.invoker().onChangeLookDirection(headingDelta, list);
        return Objects.requireNonNullElse(MixinHandlerKt.onEntityChangeHeading(list), headingDelta);
    }
}
