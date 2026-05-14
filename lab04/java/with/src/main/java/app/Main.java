package app;

import DB.DatabaseManager;
import core.FlightPhysics;
import core.FlightPhysicsPlugin;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DatabaseManager.initDatabase();

            FlightPhysics physics = FlightPhysicsPlugin.getPlugin(
                "config.properties"
            );

            FlightSimulatorApp app = new FlightSimulatorApp(physics);

            app.setVisible(true);
        });
    }
}
