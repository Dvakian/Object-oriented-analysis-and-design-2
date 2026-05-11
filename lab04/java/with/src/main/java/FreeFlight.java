import java.awt.*;
import javax.swing.*;

public class FreeFlight extends FlightSimulatorApp {

    private static final double FIXED_DT = 1.0;

    public FreeFlight() {
        super("Моделирование полёта тела — Free версия");
    }

    @Override
    protected JPanel buttonsPanel() {
        JPanel panel = cardPanel();
        panel.setLayout(new GridLayout(2, 1, 0, 10));

        addStyledButton(
            panel,
            "Расчёт графика",
            new Color(35, 150, 80),
            this::calculate
        );
        addStyledButton(
            panel,
            "Очистить график",
            new Color(185, 65, 65),
            this::clearPlot
        );

        return panel;
    }

    @Override
    protected boolean supportsAnimation() {
        return false;
    }

    @Override
    protected boolean supportsMultipleSteps() {
        return false;
    }

    @Override
    protected boolean supportsCustomInput() {
        return false;
    }

    @Override
    protected String getVersionName() {
        return "Бесплатная версия";
    }
}
