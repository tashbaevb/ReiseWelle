package de.fhzwickau.reisewelle.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;
import io.github.cdimascio.dotenv.Dotenv;

public class JDBCConfig {

    private static Connection connection;
    private static final Logger logger = Logger.getLogger(JDBCConfig.class.getName());
    private static final Dotenv dotenv = Dotenv.load();

    private JDBCConfig() {
    }

    public static synchronized Connection getInstance() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            return connection;
        }

        String CONNECTION_URL = dotenv.get("DB_URL");
        String USERNAME = dotenv.get("DB_USERNAME");
        String PASSWORD = dotenv.get("DB_PASSWORD");

        if (CONNECTION_URL == null || USERNAME == null || PASSWORD == null) {
            throw new IllegalStateException("Missing DB environment variables");
        }

        connection = DriverManager.getConnection(CONNECTION_URL, USERNAME, PASSWORD);
        logger.info("Database connection established: " + connection);
        return connection;
    }

    public static synchronized void close() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    logger.info("Database connection closed.");
                }
            } catch (SQLException e) {
                logger.severe("Error closing database connection: " + e.getMessage());
            }
        }
    }
}
