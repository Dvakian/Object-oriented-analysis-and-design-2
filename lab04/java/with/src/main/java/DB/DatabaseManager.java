package DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseManager {

    private static final String URL = "jdbc:sqlite:flight.db";

    public static Connection connect() throws Exception {
        return DriverManager.getConnection(URL);
    }

    public static void initDatabase() {
        try (
            Connection conn = connect();
            Statement stmt = conn.createStatement()
        ) {
            stmt.execute(
                """
                    CREATE TABLE IF NOT EXISTS simulations (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        version TEXT,
                        v0 REAL,
                        y0 REAL,
                        angle REAL,
                        mass REAL,
                        k REAL,
                        rho0 REAL,
                        H REAL,
                        dt REAL,
                        distance REAL,
                        max_height REAL,
                        final_speed REAL,
                        created_at TEXT DEFAULT CURRENT_TIMESTAMP
                    )
                """
            );

            System.out.println("База данных подключена.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveSimulation(
        String version,
        double v0,
        double y0,
        double angle,
        double mass,
        double k,
        double rho0,
        double H,
        double dt,
        double distance,
        double maxHeight,
        double finalSpeed
    ) {
        String sql = """
                INSERT INTO simulations (
                    version, v0, y0, angle, mass, k, rho0, H, dt,
                    distance, max_height, final_speed
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (
            Connection conn = connect();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, version);
            stmt.setDouble(2, v0);
            stmt.setDouble(3, y0);
            stmt.setDouble(4, angle);
            stmt.setDouble(5, mass);
            stmt.setDouble(6, k);
            stmt.setDouble(7, rho0);
            stmt.setDouble(8, H);
            stmt.setDouble(9, dt);
            stmt.setDouble(10, distance);
            stmt.setDouble(11, maxHeight);
            stmt.setDouble(12, finalSpeed);

            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ResultSet loadHistory() throws Exception {
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
                ORDER BY id DESC
            """;

        Connection conn = connect();

        PreparedStatement stmt = conn.prepareStatement(sql);

        return stmt.executeQuery();
    }

    public static void clearHistory() throws Exception {
        String sql = "DELETE FROM simulations";

        try (
            Connection conn = connect();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.executeUpdate();
        }
    }
}
