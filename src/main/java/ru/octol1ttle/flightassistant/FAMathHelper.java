package ru.octol1ttle.flightassistant;

public class FAMathHelper {
    public static float toDegrees(double angrad) {
        return (float) Math.toDegrees(angrad);
    }
    public static float toRadians(double degrees) {
        return (float) Math.toRadians(degrees);
    }

    public static int round(double a) {
        return (int) Math.round(a);
    }
}
