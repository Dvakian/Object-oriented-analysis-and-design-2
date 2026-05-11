package DB;

import java.sql.Connection;
import java.sql.DriverManager;
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
                    CREATE TABLE IF NOT EXISTS users (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        login TEXT UNIQUE,
                        password TEXT,
                        is_pro INTEGER
                    )
                """
            );

            stmt.execute(
                """
                    CREATE TABLE IF NOT EXISTS simulations (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        user_login TEXT,
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
}
