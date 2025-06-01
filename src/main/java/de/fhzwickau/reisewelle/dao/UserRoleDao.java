package de.fhzwickau.reisewelle.dao;

import de.fhzwickau.reisewelle.config.JDBCConfig;
import de.fhzwickau.reisewelle.model.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserRoleDao implements BaseDao<UserRole> {

    private static final Logger logger = LoggerFactory.getLogger(BaseDao.class);

    @Override
    public List<UserRole> findAll() throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        String sql = "SELECT id, role_name FROM [dbo].[UserRole]";
        List<UserRole> userRoles = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                userRoles.add(mapUserRole(rs));
            }
            logger.info("Loaded roles count: " + userRoles.size());
        } catch (SQLException e) {
            logger.warn("Error in UserRoleRepository.findAll: " + e.getMessage());
        }

        return userRoles;
    }

    @Override
    public UserRole findById(UUID id) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        String sql = "SELECT id, role_name FROM [dbo].[UserRole] WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapUserRole(rs);
                }
                logger.warn("No role found for id: " + id);
            }
        } catch (SQLException e) {
            logger.warn("Error in UserRoleRepository.findById: " + e.getMessage());
        }

        return null;
    }

    @Override
    public void save(UserRole userRole) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        boolean isNew = userRole.getId() == null;
        String sql = isNew
                ? "INSERT INTO [dbo].[UserRole] (id, role_name) VALUES (?, ?)"
                : "UPDATE [dbo].[UserRole] SET role_name = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (isNew) {
                prepareInsert(stmt, userRole);
                logger.info("Executing INSERT: id=" + userRole.getId() + ", roleName=" + userRole.getRoleName());
            } else {
                prepareUpdate(stmt, userRole);
                logger.info("Executing UPDATE: id=" + userRole.getId() + ", roleName=" + userRole.getRoleName());
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error in UserRoleRepository.save: " + e.getMessage());
        }
    }

    @Override
    public void delete(UUID id) throws SQLException {
        // Not Realized
    }

    public UserRole findByName(String name) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        String sql = "SELECT * FROM UserRole WHERE role_name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                UUID id = UUID.fromString(rs.getString("id"));
                String roleName = rs.getString("role_name");
                return new UserRole(id, roleName);
            }
        }
        return null;
    }

    private UserRole mapUserRole(ResultSet rs) throws SQLException {
        UUID id = UUID.fromString(rs.getString("id"));
        String role = rs.getString("role_name");
        return new UserRole(id, role);
    }

    private void prepareInsert(PreparedStatement stmt, UserRole userRole) throws SQLException {
        userRole.setId(UUID.randomUUID());
        stmt.setString(1, userRole.getId().toString());
        stmt.setString(2, userRole.getRoleName());
    }

    private void prepareUpdate(PreparedStatement stmt, UserRole userRole) throws SQLException {
        stmt.setString(1, userRole.getRoleName());
        stmt.setString(2, userRole.getId().toString());
    }
}