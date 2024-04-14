package ru.octol1ttle.flightassistant.computers.impl.navigation;

public record LandingMinimums(AltitudeType type, int altitude) {
    public enum AltitudeType {
        ABSOLUTE("Absolute"),
        ABOVE_GROUND("AboveGround");

        public final String serializedName;
        AltitudeType(String serializedName) {
            this.serializedName = serializedName;
        }

        public static AltitudeType fromSerializedName(String nbtName) {
            for (AltitudeType type : AltitudeType.values()) {
                if (type.serializedName.equals(nbtName)) {
                    return type;
                }
            }

            throw new IllegalStateException("Unknown altitude type: %s".formatted(nbtName));
        }
    }
}