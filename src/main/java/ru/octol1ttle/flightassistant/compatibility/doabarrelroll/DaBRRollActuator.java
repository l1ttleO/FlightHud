package ru.octol1ttle.flightassistant.compatibility.doabarrelroll;

import org.jetbrains.annotations.ApiStatus;
import ru.octol1ttle.flightassistant.computers.api.IRollActuator;

@ApiStatus.Internal
public class DaBRRollActuator implements IRollActuator {
    @Override
    public void setRoll(float newRoll) {
        DaBRInterface.setRoll(newRoll);
    }

    @Override
    public String getId() {
        return "dabr_roll";
    }

    @Override
    public void reset() {
    }
}
