public class PluginFactory {

    private static final String PLUGIN_CLASS = "ProFlight"; // FreeFlight

    public static FlightSimulatorApp getApp() {
        try {
            Class<?> clazz = Class.forName(PLUGIN_CLASS);
            return (FlightSimulatorApp) clazz
                .getDeclaredConstructor()
                .newInstance();
        } catch (Exception e) {
            throw new RuntimeException(
                "Ошибка загрузки плагина: " + PLUGIN_CLASS,
                e
            );
        }
    }
}
