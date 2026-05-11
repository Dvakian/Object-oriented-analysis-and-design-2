import java.awt.*;
import javax.swing.*;

public class ProFlight extends FlightSimulatorApp {

    public ProFlight() {
        super("Моделирование полёта тела — Pro версия");
    }

    @Override
    protected JPanel buttonsPanel() {
        JPanel panel = cardPanel();
        panel.setLayout(new GridLayout(4, 1, 0, 10));

        addStyledButton(
            panel,
            "Запуск анимации",
            new Color(0, 105, 220),
            this::animate
        );
        addStyledButton(
            panel,
            "Расчёт графика",
            new Color(35, 150, 80),
            this::calculate
        );
        addStyledButton(
            panel,
            "Запустить все шаги",
            new Color(235, 150, 40),
            this::runAllSteps
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
        return true;
    }

    @Override
    protected boolean supportsMultipleSteps() {
        return true;
    }

    @Override
    protected boolean supportsCustomInput() {
        return true;
    }

    @Override
    protected String getVersionName() {
        return "Полная версия";
    }
}
