package auth;

import DB.DatabaseManager;
import app.*;
import core.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.*;

public class LoginWindow extends JFrame {

    private final JTextField loginField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();

    public LoginWindow() {
        super("Авторизация");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        createInterface();
    }

    private void createInterface() {
        JPanel root = new JPanel(new BorderLayout());

        root.setBackground(new Color(245, 245, 245));
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel card = new JPanel(new GridBagLayout());

        card.setBackground(Color.WHITE);
        card.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
            )
        );

        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 10, 10, 10);

        JLabel title = new JLabel("Авторизация");
        title.setFont(new Font("Arial", Font.BOLD, 22));

        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;

        card.add(title, c);

        JLabel loginLabel = new JLabel("Логин:");
        loginLabel.setFont(new Font("Arial", Font.BOLD, 14));

        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;

        card.add(loginLabel, c);

        loginField.setFont(new Font("Arial", Font.PLAIN, 14));
        loginField.setPreferredSize(new Dimension(180, 34));

        c.gridx = 1;

        card.add(loginField, c);

        JLabel passwordLabel = new JLabel("Пароль:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));

        c.gridx = 0;
        c.gridy = 2;

        card.add(passwordLabel, c);

        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setPreferredSize(new Dimension(180, 34));

        c.gridx = 1;

        card.add(passwordField, c);

        JButton loginButton = createStyledButton(
            "Войти",
            new Color(110, 207, 198)
        );

        loginButton.addActionListener(e -> login());

        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 2;

        card.add(loginButton, c);

        root.add(card, BorderLayout.CENTER);

        add(root);
    }

    private void login() {
        String login = loginField.getText();
        String password = new String(passwordField.getPassword());

        String sql = """
                SELECT is_pro
                FROM users
                WHERE login = ?
                AND password = ?
            """;

        try (
            Connection conn = DatabaseManager.connect();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, login);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int accessLevel = rs.getInt("is_pro");

                UserSession.setLogin(login);
                UserSession.setAccessLevel(accessLevel);

                String method = switch (accessLevel) {
                    case 1 -> "rk";
                    case 2 -> "super";
                    default -> "aboba";
                };

                FlightPhysics physics = FlightPhysicsPlugin.getPlugin(method);

                FlightSimulatorApp app = new FlightSimulatorApp(physics);

                app.setVisible(true);

                dispose();
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Неверный логин или пароль"
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton button = new JButton(text);

        button.setFont(new Font("Arial", Font.BOLD, 15));
        button.setFocusPainted(false);

        button.setBackground(bg);
        button.setForeground(Color.BLACK);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.setPreferredSize(new Dimension(220, 42));

        return button;
    }
}
