package ru.octol1ttle.flightassistant.computers.api;

public interface IRollHandler extends IComputer {
    float getRoll();
    void setRoll(float newRoll);
}
