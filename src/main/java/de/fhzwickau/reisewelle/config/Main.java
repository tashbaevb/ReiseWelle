package de.fhzwickau.reisewelle.config;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {

    public static void main(String[] args) throws SQLException {
        Connection connection = JDBCConfig.getInstance();
        if (connection != null) {
            String selectQuery = """
                    SELECT * FROM Eblan;
                    """;

            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(selectQuery)) {

                while (rs.next()) {
                    int id = rs.getInt("id");
                    String username = rs.getString("username");
                    System.out.println("ID: " + id + ", Username: " + username);
                }
            } catch (SQLException e) {
                System.err.println("Ошибка при выполнении запроса: " + e.getMessage());
            }
        }
    }
}
