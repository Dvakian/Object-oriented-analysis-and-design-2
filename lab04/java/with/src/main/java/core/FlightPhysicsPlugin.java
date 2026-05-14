package core;

import java.io.FileInputStream;
import java.util.Properties;

public class FlightPhysicsPlugin {

    public static FlightPhysics getPlugin(String configFile) {
        try {
            Properties props = new Properties();

            try (FileInputStream input = new FileInputStream(configFile)) {
                props.load(input);
            }

            String className = props.getProperty("FlightPhysics");

            Class<?> pluginClass = Class.forName(className);

            return (FlightPhysics) pluginClass
                .getDeclaredConstructor()
                .newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
