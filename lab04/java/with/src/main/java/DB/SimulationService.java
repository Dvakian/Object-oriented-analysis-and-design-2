package DB;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class SimulationService {

    public static void save(
        String userLogin,
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
                    user_login,
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
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (
            Connection conn = DatabaseManager.connect();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, userLogin);
            stmt.setString(2, version);
            stmt.setDouble(3, v0);
            stmt.setDouble(4, y0);
            stmt.setDouble(5, angle);
            stmt.setDouble(6, mass);
            stmt.setDouble(7, k);
            stmt.setDouble(8, rho0);
            stmt.setDouble(9, H);
            stmt.setDouble(10, dt);
            stmt.setDouble(11, distance);
            stmt.setDouble(12, maxHeight);
            stmt.setDouble(13, finalSpeed);

            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
