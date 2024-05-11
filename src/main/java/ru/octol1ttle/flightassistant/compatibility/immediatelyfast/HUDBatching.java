package ru.octol1ttle.flightassistant.compatibility.immediatelyfast;

import ru.octol1ttle.flightassistant.FlightAssistant;

public class HUDBatching {
    public static void tryBegin() {
        if (FlightAssistant.isHUDBatched()) {
            ImmediatelyFastInterface.beginDirect();
        }
    }

    public static void tryBeginIf(boolean condition) {
        if (condition) {
            tryBegin();
        }
    }

    public static void tryEnd() {
        if (FlightAssistant.isHUDBatched()) {
            ImmediatelyFastInterface.endDirect();
        }
    }

    public static void tryEndIf(boolean condition) {
        if (condition) {
            tryEnd();
        }
    }
}
