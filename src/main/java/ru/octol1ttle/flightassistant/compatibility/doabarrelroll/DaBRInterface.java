package ru.octol1ttle.flightassistant.compatibility.doabarrelroll;

import net.minecraft.client.MinecraftClient;
import nl.enjarai.doabarrelroll.api.RollEntity;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class DaBRInterface {
    public static void setRoll(float newRoll) {
        if (MinecraftClient.getInstance().player != null) {
            ((RollEntity) MinecraftClient.getInstance().player).doABarrelRoll$setRoll(newRoll);
        }
    }
}
