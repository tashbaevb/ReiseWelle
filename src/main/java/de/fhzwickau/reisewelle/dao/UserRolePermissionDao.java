package de.fhzwickau.reisewelle.dao;

import de.fhzwickau.reisewelle.config.JDBCConfig;
import de.fhzwickau.reisewelle.model.Permission;
import de.fhzwickau.reisewelle.model.UserRole;
import de.fhzwickau.reisewelle.model.UserRolePermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserRolePermissionDao implements BaseDao<UserRolePermission> {

    private static final Logger logger = LoggerFactory.getLogger(UserRolePermissionDao.class);
    private final BaseDao<UserRole> userRoleDao = new UserRoleDao();
    private final BaseDao<Permission> permissionDao = new PermissionDao();

    @Override
    public UserRolePermission findById(UUID id) throws SQLException {
        Connection connection = JDBCConfig.getInstance();
        String sql = "SELECT * FROM UserRolePermission WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id.toString());
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) return mapUserRolePermission(rs);
            }
        } catch (SQLException sqle) {
            logger.warn("Error in UserRolePermissionDao.findById: " + sqle.getMessage());
        }

        return null;
    }

    @Override
    public List<UserRolePermission> findAll() throws SQLException {
        Connection connection = JDBCConfig.getInstance();
        String sql = "SELECT * FROM UserRolePermission";
        List<UserRolePermission> userRolePermissions = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) userRolePermissions.add(mapUserRolePermission(rs));
        } catch (SQLException sqle) {
            logger.warn("Error in UserRolePermissionDao.findAll: " + sqle.getMessage());
        }

        return userRolePermissions;
    }

    @Override
    public void save(UserRolePermission userRolePermission) throws SQLException {
        Connection connection = JDBCConfig.getInstance();
        boolean isNew = userRolePermission.getId() == null;
        String sql = isNew
                ? "INSERT INTO UserRolePermission (role_id, permission_id) VALUES (?, ?)"
                : "UPDATE UserRolePermission SET role_id = ?, permission_id = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            if (isNew) {
                prepareInsert(statement, userRolePermission);
            } else {
                prepareUpdate(statement, userRolePermission);
            }

            statement.executeUpdate();
        } catch (SQLException sqle) {
            logger.warn("Error in UserRolePermissionDao.save: " + sqle.getMessage());
        }

    }

    @Override
    public void delete(UUID id) throws SQLException {
    }

    private UserRolePermission mapUserRolePermission(ResultSet rs) throws SQLException {
        UUID roleId = UUID.fromString(rs.getString("role_id"));
        UUID permissionId = UUID.fromString(rs.getString("permission_id"));
        UserRole userRole = userRoleDao.findById(roleId);
        Permission permission = permissionDao.findById(permissionId);

        return new UserRolePermission(userRole, permission);
    }

    private void prepareInsert(PreparedStatement statement, UserRolePermission userRolePermission) throws SQLException {
        userRolePermission.setId(UUID.randomUUID());
        statement.setString(1, userRolePermission.getUserRole().getId().toString());
        statement.setString(2, userRolePermission.getPermission().getId().toString());
    }

    private void prepareUpdate(PreparedStatement statement, UserRolePermission userRolePermission) {
    }
}
