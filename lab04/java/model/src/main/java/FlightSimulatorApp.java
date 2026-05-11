import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.*;

public class FlightSimulatorApp extends JFrame {

    private final double g = 9.81;
    private final Map<Double, Result> resultsTable = new TreeMap<>();

    private final JTextField v0Field = new JTextField("50");
    private final JTextField angleField = new JTextField("45");
    private final JTextField mField = new JTextField("1.0");
    private final JTextField kField = new JTextField("0.02");
    private final JTextField dtField = new JTextField("0.05");

    private final JTextArea resultText = new JTextArea(12, 34);
    private final PlotPanel plotPanel = new PlotPanel();

    private javax.swing.Timer animationTimer;
    private boolean isAnimating = false;

    public FlightSimulatorApp() {
        super("Моделирование полёта тела в атмосфере");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        getContentPane().setBackground(new Color(245, 245, 245));

        addWindowListener(
            new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    //printTable();
                    dispose();
                    System.exit(0);
                }
            }
        );

        createInterface();

        setSize(1200, 720);
        setMinimumSize(new Dimension(1100, 650));
        setLocationRelativeTo(null);
    }

    private void createInterface() {
        JPanel main = new JPanel(new BorderLayout(14, 14));
        main.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        main.setBackground(new Color(245, 245, 245));

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setPreferredSize(new Dimension(360, 650));
        left.setBackground(new Color(245, 245, 245));

        left.add(parametersPanel());
        left.add(Box.createVerticalStrut(10));
        left.add(buttonsPanel());
        left.add(Box.createVerticalStrut(10));
        left.add(resultsPanel());

        JPanel right = cardPanel();
        right.setLayout(new BorderLayout());
        right.add(plotPanel, BorderLayout.CENTER);

        main.add(left, BorderLayout.WEST);
        main.add(right, BorderLayout.CENTER);

        add(main);
    }

    private JPanel parametersPanel() {
        JPanel panel = cardPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.insets = new Insets(0, 0, 12, 0);
        panel.add(titleLabel("Параметры моделирования"), c);

        addInput(panel, c, 1, "Начальная скорость (м/с):", v0Field);
        addInput(panel, c, 2, "Угол бросания (град):", angleField);
        addInput(panel, c, 3, "Масса тела (кг):", mField);
        addInput(panel, c, 4, "Коэфф. сопротивления (k):", kField);
        addInput(panel, c, 5, "Шаг моделирования (dt, с):", dtField);

        return panel;
    }

    private JPanel buttonsPanel() {
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

    private JPanel resultsPanel() {
        JPanel panel = cardPanel();
        panel.setLayout(new BorderLayout(8, 8));

        panel.add(titleLabel("Результаты:"), BorderLayout.NORTH);

        resultText.setFont(new Font("Consolas", Font.PLAIN, 13));
        resultText.setEditable(false);
        resultText.setMargin(new Insets(10, 10, 10, 10));

        panel.add(new JScrollPane(resultText), BorderLayout.CENTER);
        return panel;
    }

    private void addInput(
        JPanel panel,
        GridBagConstraints c,
        int row,
        String label,
        JTextField field
    ) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.BOLD, 14));

        field.setFont(new Font("Arial", Font.PLAIN, 15));
        field.setMargin(new Insets(6, 8, 6, 8));

        c.gridy = row;
        c.gridwidth = 1;
        c.insets = new Insets(7, 0, 7, 12);

        c.gridx = 0;
        c.weightx = 0.65;
        panel.add(lbl, c);

        c.gridx = 1;
        c.weightx = 0.35;
        c.insets = new Insets(7, 0, 7, 0);
        panel.add(field, c);
    }

    private void addStyledButton(
        JPanel parent,
        String text,
        Color bg,
        Runnable action
    ) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 15));
        button.setFocusPainted(false);
        button.setBackground(bg);
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(300, 44));
        button.addActionListener(e -> action.run());
        parent.add(button);
    }

    private JPanel cardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)
            )
        );
        return panel;
    }

    private JLabel titleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        return label;
    }

    private Params getParams() {
        try {
            return new Params(
                Double.parseDouble(v0Field.getText()),
                Double.parseDouble(angleField.getText()),
                Double.parseDouble(mField.getText()),
                Double.parseDouble(kField.getText()),
                Double.parseDouble(dtField.getText())
            );
        } catch (Exception e) {
            resultText.append("Ошибка ввода!\n");
            return null;
        }
    }

    private SimulationResult simulate(
        double v0,
        double angle,
        double m,
        double k,
        double dt
    ) {
        double x = 0.0;
        double y = 0.0;

        double rad = Math.toRadians(angle);
        double vx = v0 * Math.cos(rad);
        double vy = v0 * Math.sin(rad);

        List<Double> xs = new ArrayList<>();
        List<Double> ys = new ArrayList<>();

        xs.add(x);
        ys.add(y);

        double maxH = 0.0;

        while (y >= 0) {
            double xPrev = x;
            double yPrev = y;

            double v = Math.sqrt(vx * vx + vy * vy);
            double ax = -(k / m) * v * vx;
            double ay = -g - (k / m) * v * vy;

            x += vx * dt;
            y += vy * dt;
            vx += ax * dt;
            vy += ay * dt;

            if (y < 0) {
                double r = yPrev / (yPrev - y);
                double xHit = xPrev + r * (x - xPrev);

                xs.add(xHit);
                ys.add(0.0);

                x = xHit;
                y = 0.0;
                break;
            }

            xs.add(x);
            ys.add(y);

            maxH = Math.max(maxH, y);
        }

        double finalSpeed = Math.sqrt(vx * vx + vy * vy);
        return new SimulationResult(xs, ys, x, maxH, finalSpeed);
    }

    private void calculate() {
        Params p = getParams();
        if (p == null) return;

        SimulationResult sim = simulate(p.v0, p.angle, p.m, p.k, p.dt);
        updatePlot(sim, p.dt);
    }

    private void updatePlot(SimulationResult sim, double dt) {
        resultsTable.put(
            dt,
            new Result(sim.distance, sim.maxHeight, sim.finalSpeed)
        );
        plotPanel.addTrajectory(sim.xs, sim.ys, "dt=" + dt);
        showTable();
    }

    private void animate() {
        if (isAnimating) return;

        Params p = getParams();
        if (p == null) return;

        isAnimating = true;

        SimulationResult sim = simulate(p.v0, p.angle, p.m, p.k, p.dt);

        plotPanel.clear();
        plotPanel.setAnimationData(sim.xs, sim.ys, sim.distance, sim.maxHeight);

        final int[] frame = { 0 };

        animationTimer = new javax.swing.Timer(50, e -> {
            if (frame[0] < sim.xs.size()) {
                plotPanel.setAnimationFrame(frame[0]);
                frame[0]++;
            } else {
                frame[0] = 0;
            }
        });

        animationTimer.start();
    }

    private void runAllSteps() {
        Params p = getParams();
        if (p == null) return;

        resultsTable.clear();
        clearPlot();

        double[] steps = { 1, 0.1, 0.01, 0.001, 0.0001 };

        for (double dt : steps) {
            SimulationResult sim = simulate(p.v0, p.angle, p.m, p.k, dt);

            resultsTable.put(
                dt,
                new Result(sim.distance, sim.maxHeight, sim.finalSpeed)
            );
            plotPanel.addTrajectory(sim.xs, sim.ys, "dt=" + dt);
        }

        showTable();
        // resultText.append(
        //     "\nЗавершено. Результаты также выводятся в консоль при закрытии.\n"
        // );
    }

    private void clearPlot() {
        if (animationTimer != null) {
            animationTimer.stop();
            animationTimer = null;
        }

        isAnimating = false;
        plotPanel.clear();
        resultText.setText("");
    }

    private void showTable() {
        resultText.setText("");

        if (resultsTable.isEmpty()) return;

        resultText.append(String.format("%-18s", "Time step (dt)"));
        for (double dt : resultsTable.keySet()) {
            resultText.append(String.format("%12.4f", dt));
        }
        resultText.append("\n");

        appendMetric("Distance (m)", "distance");
        appendMetric("Max height (m)", "height");
        appendMetric("Speed (m/s)", "speed");
    }

    private void appendMetric(String title, String key) {
        resultText.append(String.format("%-18s", title));

        for (Result r : resultsTable.values()) {
            double value = switch (key) {
                case "distance" -> r.distance;
                case "height" -> r.maxHeight;
                case "speed" -> r.finalSpeed;
                default -> 0;
            };

            resultText.append(String.format("%12.2f", value));
        }

        resultText.append("\n");
    }

    private void printTable() {
        if (resultsTable.isEmpty()) return;

        System.out.println("Результаты моделирования для разных шагов (dt):");

        for (Map.Entry<Double, Result> entry : resultsTable.entrySet()) {
            double dt = entry.getKey();
            Result r = entry.getValue();

            System.out.printf(
                "dt = %.4f | Дальность = %.2f м | Макс. высота = %.2f м | Конечная скорость = %.2f м/с%n",
                dt,
                r.distance,
                r.maxHeight,
                r.finalSpeed
            );
        }
    }

    static class Params {

        double v0, angle, m, k, dt;

        Params(double v0, double angle, double m, double k, double dt) {
            this.v0 = v0;
            this.angle = angle;
            this.m = m;
            this.k = k;
            this.dt = dt;
        }
    }

    static class Result {

        double distance;
        double maxHeight;
        double finalSpeed;

        Result(double distance, double maxHeight, double finalSpeed) {
            this.distance = distance;
            this.maxHeight = maxHeight;
            this.finalSpeed = finalSpeed;
        }
    }

    static class SimulationResult {

        List<Double> xs;
        List<Double> ys;
        double distance;
        double maxHeight;
        double finalSpeed;

        SimulationResult(
            List<Double> xs,
            List<Double> ys,
            double distance,
            double maxHeight,
            double finalSpeed
        ) {
            this.xs = xs;
            this.ys = ys;
            this.distance = distance;
            this.maxHeight = maxHeight;
            this.finalSpeed = finalSpeed;
        }
    }

    static class PlotPanel extends JPanel {

        private final List<Trajectory> trajectories = new ArrayList<>();

        private final Color[] colors = {
            new Color(0, 90, 255),
            new Color(230, 80, 60),
            new Color(35, 160, 80),
            new Color(160, 80, 210),
            new Color(240, 150, 20),
        };

        private List<Double> animXs;
        private List<Double> animYs;
        private int animFrame = -1;

        private double maxX = 50;
        private double maxY = 50;

        PlotPanel() {
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(780, 620));
        }

        void addTrajectory(List<Double> xs, List<Double> ys, String label) {
            trajectories.add(new Trajectory(xs, ys, label));
            updateBounds(xs, ys);
            repaint();
        }

        void setAnimationData(
            List<Double> xs,
            List<Double> ys,
            double distance,
            double maxHeight
        ) {
            animXs = xs;
            animYs = ys;
            animFrame = 0;

            maxX = Math.max(distance + 10, 50);
            maxY = Math.max(maxHeight + 10, 50);

            repaint();
        }

        void setAnimationFrame(int frame) {
            animFrame = frame;
            repaint();
        }

        void clear() {
            trajectories.clear();
            animXs = null;
            animYs = null;
            animFrame = -1;
            maxX = 50;
            maxY = 50;
            repaint();
        }

        private void updateBounds(List<Double> xs, List<Double> ys) {
            for (double x : xs) maxX = Math.max(maxX, x + 10);
            for (double y : ys) maxY = Math.max(maxY, y + 10);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
            );

            int left = 80;
            int right = 40;
            int top = 70;
            int bottom = 80;

            int w = getWidth();
            int h = getHeight();

            int plotW = w - left - right;
            int plotH = h - top - bottom;

            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Arial", Font.BOLD, 17));
            drawCentered(g2, "Траектория полёта", w / 2, 32);

            g2.setFont(new Font("Arial", Font.PLAIN, 14));
            drawCentered(g2, "Дальность (x), м", left + plotW / 2, h - 25);
            Graphics2D gText = (Graphics2D) g2.create();

            gText.setFont(new Font("Arial", Font.PLAIN, 14));
            gText.setColor(Color.BLACK);

            gText.rotate(-Math.PI / 2);

            String yLabel = "Высота (y), м";
            FontMetrics fm = gText.getFontMetrics();

            int textX = -(top + plotH / 2 + fm.stringWidth(yLabel) / 2);
            int textY = 35;

            gText.drawString(yLabel, textX, textY);

            gText.dispose();
            g2.setColor(Color.BLACK);
            g2.drawRect(left, top, plotW, plotH);

            g2.setColor(new Color(215, 215, 215));
            for (int i = 1; i < 10; i++) {
                int x = left + (i * plotW) / 10;
                int y = top + (i * plotH) / 10;

                g2.drawLine(x, top, x, top + plotH);
                g2.drawLine(left, y, left + plotW, y);
            }

            g2.setStroke(new BasicStroke(2f));

            for (int i = 0; i < trajectories.size(); i++) {
                Trajectory t = trajectories.get(i);
                g2.setColor(colors[i % colors.length]);
                drawTrajectory(
                    g2,
                    t.xs,
                    t.ys,
                    t.xs.size(),
                    left,
                    top,
                    plotW,
                    plotH
                );
            }

            if (animXs != null && animYs != null && animFrame >= 0) {
                g2.setColor(new Color(0, 90, 255));
                drawTrajectory(
                    g2,
                    animXs,
                    animYs,
                    animFrame + 1,
                    left,
                    top,
                    plotW,
                    plotH
                );

                int safeFrame = Math.min(animFrame, animXs.size() - 1);

                int bx = toScreenX(animXs.get(safeFrame), left, plotW);
                int by = toScreenY(animYs.get(safeFrame), top, plotH);

                g2.setColor(new Color(230, 60, 60));
                g2.fillOval(bx - 6, by - 6, 12, 12);
            }

            g2.setStroke(new BasicStroke(1f));
            drawLegend(g2, left + plotW - 130, top + 15);
        }

        private void drawLegend(Graphics2D g2, int x, int y) {
            if (trajectories.isEmpty()) return;

            int height = trajectories.size() * 24 + 14;

            g2.setColor(new Color(255, 255, 255, 230));
            g2.fillRect(x, y, 120, height);
            g2.setColor(Color.BLACK);
            g2.drawRect(x, y, 120, height);

            g2.setFont(new Font("Arial", Font.PLAIN, 13));

            for (int i = 0; i < trajectories.size(); i++) {
                int yy = y + 22 + i * 24;

                g2.setColor(colors[i % colors.length]);
                g2.fillRect(x + 10, yy - 12, 14, 14);

                g2.setColor(Color.BLACK);
                g2.drawString(trajectories.get(i).label, x + 32, yy);
            }
        }

        private void drawTrajectory(
            Graphics2D g2,
            List<Double> xs,
            List<Double> ys,
            int count,
            int left,
            int top,
            int plotW,
            int plotH
        ) {
            for (int i = 1; i < count && i < xs.size(); i++) {
                int x1 = toScreenX(xs.get(i - 1), left, plotW);
                int y1 = toScreenY(ys.get(i - 1), top, plotH);
                int x2 = toScreenX(xs.get(i), left, plotW);
                int y2 = toScreenY(ys.get(i), top, plotH);

                g2.drawLine(x1, y1, x2, y2);
            }
        }

        private int toScreenX(double x, int left, int plotW) {
            return left + (int) ((x / maxX) * plotW);
        }

        private int toScreenY(double y, int top, int plotH) {
            return top + plotH - (int) ((y / maxY) * plotH);
        }

        private void drawCentered(Graphics2D g2, String text, int x, int y) {
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(text, x - fm.stringWidth(text) / 2, y);
        }

        static class Trajectory {

            List<Double> xs;
            List<Double> ys;
            String label;

            Trajectory(List<Double> xs, List<Double> ys, String label) {
                this.xs = xs;
                this.ys = ys;
                this.label = label;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FlightSimulatorApp app = new FlightSimulatorApp();
            app.setVisible(true);
        });
    }
}
