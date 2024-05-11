package ru.octol1ttle.flightassistant.compatibility.doabarrelroll;

import net.minecraft.entity.player.PlayerEntity;
import nl.enjarai.doabarrelroll.api.RollEntity;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class DaBRInterface {
    static float getRoll(PlayerEntity player) {
        return ((RollEntity) player).doABarrelRoll$getRoll();
    }

    static void setRoll(PlayerEntity player, float newRoll) {
        ((RollEntity) player).doABarrelRoll$setRoll(newRoll);
    }
}
