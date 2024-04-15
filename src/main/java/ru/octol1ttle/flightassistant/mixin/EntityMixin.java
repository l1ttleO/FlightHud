package ru.octol1ttle.flightassistant.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import ru.octol1ttle.flightassistant.computers.impl.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.impl.safety.PitchLimitComputer;
import ru.octol1ttle.flightassistant.config.ComputerConfig;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;

@SuppressWarnings("UnreachableCode")
@Mixin(Entity.class)
public abstract class EntityMixin {
    @ModifyVariable(method = "changeLookDirection", at = @At("STORE"), ordinal = 0)
    public float preventUpsetPitch(float pitchDelta) {
        Entity that = (Entity) (Object) this;

        AirDataComputer data = ComputerRegistry.resolve(AirDataComputer.class);
        PitchLimitComputer limit = ComputerRegistry.resolve(PitchLimitComputer.class);
        if (that instanceof ClientPlayerEntity && data.canAutomationsActivate()) {
            float oldPitch = data.pitch();
            float newPitch = oldPitch + (-pitchDelta);

            if (limit.blockPitchChange(newPitch > oldPitch ? Direction.UP : Direction.DOWN, ComputerConfig.ProtectionMode::override)) {
                return 0.0f;
            }

            Pair<Float, Float> safePitches = limit.getSafePitches(ComputerConfig.ProtectionMode::override);
            if (newPitch < oldPitch && safePitches.getLeft() > newPitch) {
                return 0.0f;
            }
            if (newPitch > oldPitch && safePitches.getRight() < newPitch) {
                return 0.0f;
            }
        }

        return pitchDelta;
    }
}
