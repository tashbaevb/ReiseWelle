package de.fhzwickau.reisewelle.dao;

import de.fhzwickau.reisewelle.config.JDBCConfig;
import de.fhzwickau.reisewelle.model.UserRole;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserRoleRepository {

    public List<UserRole> findAll() {
        List<UserRole> userRoles = new ArrayList<>();
        String sql = "SELECT id, role_name FROM [dbo].[UserRole]";

        try (Connection conn = JDBCConfig.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String roleId = rs.getString("id");
                System.out.println("Found role: id=" + roleId + ", name=" + rs.getString("role_name"));
                UserRole userRole = new UserRole(rs.getString("role_name"));
                userRole.setId(UUID.fromString(roleId));
                userRoles.add(userRole);
            }
            System.out.println("Loaded roles count: " + userRoles.size());
        } catch (SQLException e) {
            System.err.println("Error in UserRoleRepository.findAll: " + e.getMessage());
            e.printStackTrace();
        }
        return userRoles;
    }

    public UserRole findById(UUID id) {
        String sql = "SELECT id, role_name FROM [dbo].[UserRole] WHERE id = ?";
        try (Connection conn = JDBCConfig.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            System.out.println("Searching for role with id: " + id.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String roleId = rs.getString("id");
                    System.out.println("Found role: id=" + roleId + ", name=" + rs.getString("role_name"));
                    UserRole userRole = new UserRole(rs.getString("role_name"));
                    userRole.setId(UUID.fromString(roleId));
                    return userRole;
                } else {
                    System.out.println("No role found for id: " + id.toString());
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in UserRoleRepository.findById: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public void save(UserRole userRole) {
        String sql = userRole.getId() == null ?
                "INSERT INTO [dbo].[UserRole] (id, role_name) VALUES (?, ?)" :
                "UPDATE [dbo].[UserRole] SET role_name = ? WHERE id = ?";

        try (Connection conn = JDBCConfig.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (userRole.getId() == null) {
                userRole.setId(UUID.randomUUID());
                System.out.println("Generating new ID for userRole: " + userRole.getId());
                stmt.setString(1, userRole.getId().toString());
                stmt.setString(2, userRole.getRoleName());
                System.out.println("Executing INSERT: id=" + userRole.getId() + ", roleName=" + userRole.getRoleName());
            } else {
                stmt.setString(1, userRole.getRoleName());
                stmt.setString(2, userRole.getId().toString());
                System.out.println("Executing UPDATE: id=" + userRole.getId() + ", roleName=" + userRole.getRoleName());
            }
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);
        } catch (SQLException e) {
            System.err.println("Error in UserRoleRepository.save: " + e.getMessage());
            e.printStackTrace();
        }
    }
}