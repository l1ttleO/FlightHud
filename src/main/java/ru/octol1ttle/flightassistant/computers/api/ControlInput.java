package ru.octol1ttle.flightassistant.computers.api;

public record ControlInput(float target, float deltaTimeMultiplier, InputPriority priority) {
}
