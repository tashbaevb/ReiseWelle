package de.fhzwickau.reisewelle.dao;

import de.fhzwickau.reisewelle.config.JDBCConfig;
import de.fhzwickau.reisewelle.model.User;
import de.fhzwickau.reisewelle.model.UserRole;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserRepository {
    private final UserRoleRepository userRoleRepository = new UserRoleRepository();

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, email, password, user_role_id, created_at FROM [dbo].[User]";

        try (Connection conn = JDBCConfig.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String roleIdStr = rs.getString("user_role_id");
                UUID roleId = UUID.fromString(roleIdStr);
                UserRole userRole = userRoleRepository.findById(roleId);
                if (userRole == null) {
                    System.out.println("Warning: UserRole not found for roleId: " + roleIdStr);
                    continue;
                }
                User user = new User(
                        rs.getString("email"),
                        rs.getString("password"),
                        userRole,
                        rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null
                );
                user.setId(UUID.fromString(rs.getString("id")));
                users.add(user);
                System.out.println("Loaded user: email=" + user.getEmail() + ", role=" + userRole.getRoleName());
            }
            System.out.println("Total users loaded: " + users.size());
        } catch (SQLException e) {
            System.err.println("Error in UserRepository.findAll: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }

    public User findById(UUID id) {
        String sql = "SELECT id, email, password, user_role_id, created_at FROM [dbo].[User] WHERE id = ?";
        try (Connection conn = JDBCConfig.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String roleIdStr = rs.getString("user_role_id");
                    UUID roleId = UUID.fromString(roleIdStr);
                    UserRole userRole = userRoleRepository.findById(roleId);
                    if (userRole == null) {
                        System.out.println("Warning: UserRole not found for roleId: " + roleId);
                        return null;
                    }
                    User user = new User(
                            rs.getString("email"),
                            rs.getString("password"),
                            userRole,
                            rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null
                    );
                    user.setId(UUID.fromString(rs.getString("id")));
                    return user;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in UserRepository.findById: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<User> findCompanyReps() {
        List<User> companyReps = new ArrayList<>();
        String sql = "SELECT u.id, u.email, u.password, u.user_role_id, u.created_at " +
                "FROM [User] u " +
                "JOIN UserRole r ON u.user_role_id = r.id " +
                "WHERE r.role_name = 'company_rep'";

        try (Connection conn = JDBCConfig.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                UUID roleId = UUID.fromString(rs.getString("user_role_id"));
                UserRole userRole = userRoleRepository.findById(roleId);
                User user = new User(
                        rs.getString("email"),
                        rs.getString("password"),
                        userRole,
                        rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null
                );
                user.setId(UUID.fromString(rs.getString("id")));
                companyReps.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return companyReps;
    }

    public void save(User user) {
        String sql = user.getId() == null ?
                "INSERT INTO [dbo].[User] (id, email, password, user_role_id, created_at) VALUES (?, ?, ?, ?, ?)" :
                "UPDATE [dbo].[User] SET email = ?, password = ?, user_role_id = ?, created_at = ? WHERE id = ?";

        try (Connection conn = JDBCConfig.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (user.getId() == null) {
                user.setId(UUID.randomUUID());
                System.out.println("Generating new ID for user: " + user.getId());
                stmt.setString(1, user.getId().toString());
                stmt.setString(2, user.getEmail());
                stmt.setString(3, user.getPassword());
                stmt.setString(4, user.getUserRole().getId().toString());
                stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
                System.out.println("Executing INSERT: id=" + user.getId() + ", email=" + user.getEmail() + ", roleId=" + user.getUserRole().getId());
            } else {
                stmt.setString(1, user.getEmail());
                stmt.setString(2, user.getPassword());
                stmt.setString(3, user.getUserRole().getId().toString());
                stmt.setTimestamp(4, user.getCreatedAt() != null ? Timestamp.valueOf(user.getCreatedAt()) : null);
                stmt.setString(5, user.getId().toString());
                System.out.println("Executing UPDATE: id=" + user.getId() + ", email=" + user.getEmail() + ", roleId=" + user.getUserRole().getId());
            }
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);
        } catch (SQLException e) {
            System.err.println("Error in UserRepository.save: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to save user: " + e.getMessage(), e);
        }
    }

    public void delete(UUID id) {
        String sql = "DELETE FROM [dbo].[User] WHERE id = ?";
        try (Connection conn = JDBCConfig.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Rows deleted: " + rowsAffected);
        } catch (SQLException e) {
            System.err.println("Error in UserRepository.delete: " + e.getMessage());
            e.printStackTrace();
        }
    }
}