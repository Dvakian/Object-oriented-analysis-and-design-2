package core;

import calculate.*;

public class FlightPhysicsPlugin {

    public static FlightPhysics getPlugin(String method) {
        if (method == null) {
            return new EulerFlightPhysics();
        }

        return switch (method.toLowerCase()) {
            case "rk", "runge", "runge-kutta" -> new RKFlightPhysics();
            default -> new EulerFlightPhysics();
        };
    }
}
