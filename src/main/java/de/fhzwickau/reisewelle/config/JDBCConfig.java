package de.fhzwickau.reisewelle.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import io.github.cdimascio.dotenv.Dotenv;

public class JDBCConfig {

    private static final Logger logger = Logger.getLogger(JDBCConfig.class.getName());
    private static final Dotenv dotenv = Dotenv.load();

    private JDBCConfig() {
    }

    public static Connection getInstance() throws SQLException {
        String CONNECTION_URL = dotenv.get("DB_URL");
        String USERNAME = dotenv.get("DB_USERNAME");
        String PASSWORD = dotenv.get("DB_PASSWORD");
        if (CONNECTION_URL == null || USERNAME == null || PASSWORD == null) {
            throw new IllegalStateException("Missing DB environment variables");
        }

        Connection connection = DriverManager.getConnection(CONNECTION_URL, USERNAME, PASSWORD);
        logger.info("Database connection established: " + connection);
        return connection;
    }
}