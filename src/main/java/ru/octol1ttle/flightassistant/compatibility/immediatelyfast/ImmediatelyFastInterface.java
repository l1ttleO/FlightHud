package ru.octol1ttle.flightassistant.compatibility.immediatelyfast;

import net.raphimc.immediatelyfastapi.ImmediatelyFastApi;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class ImmediatelyFastInterface {
    public static void beginDirect() {
        ImmediatelyFastApi.getApiImpl().getBatching().beginHudBatching();
    }

    public static void endDirect() {
        ImmediatelyFastApi.getApiImpl().getBatching().endHudBatching();
    }
}
