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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
        boolean isNew = findById(userRolePermission.getId()) == null;
        String sql = isNew
                ? "INSERT INTO UserRolePermission (id, role_id, permission_id) VALUES (?, ?, ?)"
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
        Connection connection = JDBCConfig.getInstance();
        String sql = "DELETE FROM UserRolePermission WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id.toString());
            statement.executeUpdate();
        } catch (SQLException sqle) {
            logger.warn("Error in UserRolePermissionDao.delete: " + sqle.getMessage());
            throw sqle;
        }
    }

    public Set<String> findPermissionNamesByUserId(UUID id) throws SQLException {
        Connection connection = JDBCConfig.getInstance();
        Set<String> permissions = new HashSet<>();
        String sql = """
                SELECT p.permission_name
                FROM UserRolePermission urp
                JOIN UserRole ur ON urp.role_id = ur.id
                JOIN Permission p ON urp.permission_id = p.id
                JOIN (
                  SELECT id, user_role_id FROM [dbo].[User]
                  UNION ALL
                  SELECT id, user_role_id FROM [dbo].[Mitarbeiter]
                ) u ON u.user_role_id = ur.id
                WHERE u.id = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id.toString());
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) permissions.add(rs.getString("permission_name"));
            }
        } catch (SQLException sqle) {
            logger.warn("Error in UserRolePermissionDao.findPermissionNamesByUserId: " + sqle.getMessage());
        }

        for (String s: permissions) {
            System.out.println(s);
        }
        System.out.println(permissions.isEmpty());
        return permissions;
    }

    private UserRolePermission mapUserRolePermission(ResultSet rs) throws SQLException {
        UUID id = UUID.fromString(rs.getString("id"));
        UUID roleId = UUID.fromString(rs.getString("role_id"));
        UUID permissionId = UUID.fromString(rs.getString("permission_id"));
        UserRole userRole = userRoleDao.findById(roleId);
        Permission permission = permissionDao.findById(permissionId);

        return new UserRolePermission(id, userRole, permission);
    }

    private void prepareInsert(PreparedStatement statement, UserRolePermission userRolePermission) throws SQLException {
        userRolePermission.setId(UUID.randomUUID());
        statement.setString(1, userRolePermission.getId().toString());
        statement.setString(2, userRolePermission.getUserRole().getId().toString());
        statement.setString(3, userRolePermission.getPermission().getId().toString());
    }

    private void prepareUpdate(PreparedStatement statement, UserRolePermission userRolePermission) throws SQLException {
        statement.setString(1, userRolePermission.getUserRole().getId().toString());
        statement.setString(2, userRolePermission.getPermission().getId().toString());
        statement.setString(3, userRolePermission.getId().toString());
    }
}
