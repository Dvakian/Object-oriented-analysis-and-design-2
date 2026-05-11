import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FlightSimulatorApp app = PluginFactory.getApp();
            app.setVisible(true);
        });
    }
}
