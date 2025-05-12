package de.fhzwickau.reisewelle.repository;

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
        String sql = "SELECT id, role_name FROM UserRole";

        try (Connection conn = JDBCConfig.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                UserRole userRole = new UserRole(rs.getString("role_name"));
                userRole.setId(UUID.fromString(rs.getString("id")));
                userRoles.add(userRole);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userRoles;
    }

    public UserRole findById(UUID id) {
        String sql = "SELECT id, role_name FROM UserRole WHERE id = ?";
        try (Connection conn = JDBCConfig.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    UserRole userRole = new UserRole(rs.getString("role_name"));
                    userRole.setId(UUID.fromString(rs.getString("id")));
                    return userRole;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void save(UserRole userRole){
        String sql = userRole.getId() == null ?
                "INSERT INTO UserRole (id, role_name) VALUES (?, ?)" :
                "UPDATE UserRole SET role_name = ? WHERE id = ?";

        try(Connection conn = JDBCConfig.getInstance();
        PreparedStatement stmt = conn.prepareStatement(sql);){
            if(userRole.getId() == null){
                userRole.setId(UUID.randomUUID());
                stmt.setString(1, userRole.getId().toString());
                stmt.setString(2, userRole.getRoleName());
            } else {
                stmt.setString(1, userRole.getRoleName());
                stmt.setString(2, userRole.getId().toString());
            }
            stmt.executeUpdate();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(UUID id){
        String sql = "DELETE FROM UserRole WHERE id = ?";

        try(Connection conn = JDBCConfig.getInstance();
        PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, id.toString());
            stmt.executeUpdate();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
