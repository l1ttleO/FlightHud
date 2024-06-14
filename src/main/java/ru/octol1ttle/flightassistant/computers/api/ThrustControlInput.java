package ru.octol1ttle.flightassistant.computers.api;

public record ThrustControlInput(float target, float deltaTimeMultiplier, InputPriority priority) {
}
