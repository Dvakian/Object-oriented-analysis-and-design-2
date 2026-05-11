package history;

import DB.DatabaseManager;
import app.FlightSimulatorApp;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class History extends JFrame {

    private final FlightSimulatorApp app;
    private final String userLogin;

    private final DefaultTableModel tableModel = new DefaultTableModel(
        new String[] {
            "Дата",
            "Версия",
            "v0",
            "y0",
            "Угол",
            "Масса",
            "k",
            "rho0",
            "H",
            "dt",
            "Дальность",
            "Высота",
            "Скорость",
        },
        0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(tableModel);

    public History(String userLogin, FlightSimulatorApp app) {
        super("История моделирований — " + userLogin);
        this.userLogin = userLogin;
        this.app = app;

        setSize(1250, 550);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        createInterface();
        loadHistory();
    }

    private void createInterface() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 4, 12));

        JLabel title = new JLabel("История моделирований");
        title.setFont(new Font("Arial", Font.BOLD, 22));

        JLabel hint = new JLabel(
            "Выберите строку и нажмите «Импортировать параметры»"
        );
        hint.setFont(new Font("Arial", Font.PLAIN, 14));

        JPanel titleBox = new JPanel(new GridLayout(2, 1));
        titleBox.add(title);
        titleBox.add(hint);

        topPanel.add(titleBox, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);

        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 12, 12)
        );

        JButton importButton = new JButton("Импортировать параметры");
        importButton.setFont(new Font("Arial", Font.BOLD, 14));
        importButton.addActionListener(e -> importSelectedRow());

        JButton clearButton = new JButton("Очистить историю");
        clearButton.setFont(new Font("Arial", Font.PLAIN, 14));
        clearButton.addActionListener(e -> clearHistory());

        JButton closeButton = new JButton("Закрыть");
        closeButton.setFont(new Font("Arial", Font.PLAIN, 14));
        closeButton.addActionListener(e -> dispose());

        bottomPanel.add(importButton);
        bottomPanel.add(clearButton);
        bottomPanel.add(closeButton);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadHistory() {
        String sql = """
                SELECT
                    created_at,
                    version,
                    v0,
                    y0,
                    angle,
                    mass,
                    k,
                    rho0,
                    H,
                    dt,
                    distance,
                    max_height,
                    final_speed
                FROM simulations
                WHERE user_login = ?
                ORDER BY id DESC
            """;

        try (
            Connection conn = DatabaseManager.connect();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, userLogin);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                tableModel.addRow(
                    new Object[] {
                        rs.getString("created_at"),
                        rs.getString("version"),
                        rs.getDouble("v0"),
                        rs.getDouble("y0"),
                        rs.getDouble("angle"),
                        rs.getDouble("mass"),
                        rs.getDouble("k"),
                        rs.getDouble("rho0"),
                        rs.getDouble("H"),
                        rs.getDouble("dt"),
                        rs.getDouble("distance"),
                        rs.getDouble("max_height"),
                        rs.getDouble("final_speed"),
                    }
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Ошибка загрузки истории.");
        }
    }

    private void importSelectedRow() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Сначала выберите строку из истории."
            );
            return;
        }

        double v0 = getDouble(row, 2);
        double y0 = getDouble(row, 3);
        double angle = getDouble(row, 4);
        double mass = getDouble(row, 5);
        double k = getDouble(row, 6);
        double rho0 = getDouble(row, 7);
        double H = getDouble(row, 8);
        double dt = getDouble(row, 9);

        app.importParamsFromHistory(v0, y0, angle, mass, k, rho0, H, dt);

        JOptionPane.showMessageDialog(
            this,
            "Параметры импортированы в главное окно."
        );

        dispose();
    }

    private double getDouble(int row, int column) {
        Object value = tableModel.getValueAt(row, column);
        return Double.parseDouble(value.toString());
    }

    private void clearHistory() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Удалить всю историю моделирований?",
            "Подтверждение",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = """
                DELETE FROM simulations
                WHERE user_login = ?
            """;

        try (
            Connection conn = DatabaseManager.connect();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, userLogin);
            stmt.executeUpdate();

            tableModel.setRowCount(0);

            JOptionPane.showMessageDialog(this, "История успешно очищена.");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Ошибка очистки истории.");
        }
    }
}
