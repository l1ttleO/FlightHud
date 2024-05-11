package ru.octol1ttle.flightassistant.compatibility.doabarrelroll;

import org.jetbrains.annotations.ApiStatus;
import ru.octol1ttle.flightassistant.computers.api.IRollHandler;
import ru.octol1ttle.flightassistant.computers.impl.AirDataComputer;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;

@ApiStatus.Internal
public class DaBRRollHandler implements IRollHandler {
    private final AirDataComputer data = ComputerRegistry.resolve(AirDataComputer.class);

    @Override
    public float getRoll() {
        return DaBRInterface.getRoll(data.player());
    }

    @Override
    public void setRoll(float newRoll) {
        DaBRInterface.setRoll(data.player(), newRoll);
    }

    @Override
    public String getId() {
        return "dabr_roll";
    }

    @Override
    public void reset() {
    }
}
