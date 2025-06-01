package de.fhzwickau.reisewelle.dao;

import de.fhzwickau.reisewelle.config.JDBCConfig;
import de.fhzwickau.reisewelle.model.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PermissionDao implements BaseDao<Permission> {

    private static final Logger logger = LoggerFactory.getLogger(PermissionDao.class);

    @Override
    public Permission findById(UUID id) throws SQLException {
        Connection connection = JDBCConfig.getInstance();
        String sql = "SELECT * FROM Permission WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id.toString());
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) return mapPermission(rs);
            }
        } catch (SQLException sqle) {
            logger.warn("Error in PermissionDao.findById: " + sqle.getMessage());
        }

        return null;
    }

    @Override
    public List<Permission> findAll() throws SQLException {
        Connection connection = JDBCConfig.getInstance();
        String sql = "SELECT * FROM Permission";
        List<Permission> permissions = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) permissions.add(mapPermission(rs));
        } catch (SQLException sqle) {
            logger.warn("Error in PermissionDao.findAll: " + sqle.getMessage());
        }

        return permissions;
    }

    @Override
    public void save(Permission permission) throws SQLException {
        Connection connection = JDBCConfig.getInstance();
        boolean isNew = permission.getId() == null;
        String sql = isNew
                ? "INSERT INTO Permission (id, permission_name) VALUES (?, ?)"
                : "UPDATE Permission SET permission_name = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            if (isNew) {
                prepareInsert(statement, permission);
            } else {
                prepareUpdate(statement, permission);
            }

            statement.executeUpdate();
        } catch (SQLException sqle) {
            logger.warn("Error in PermissionDao.save: " + sqle.getMessage());
        }
    }

    @Override
    public void delete(UUID id) throws SQLException {
        Connection connection = JDBCConfig.getInstance();
        String sql = "DELETE FROM Permission WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id.toString());
            statement.executeUpdate();
        } catch (SQLException sqle) {
            logger.warn("Error in PermissionDao.delete: " + sqle.getMessage());
        }
    }

    private Permission mapPermission(ResultSet rs) throws SQLException {
        UUID id = UUID.fromString(rs.getString("id"));
        String permissionName = rs.getString("permission_name");
        return new Permission(id, permissionName);
    }

    private void prepareInsert(PreparedStatement statement, Permission permission) throws SQLException {
        permission.setId(UUID.randomUUID());
        statement.setString(1, permission.getId().toString());
        statement.setString(2, permission.getPermissionName());
    }

    private void prepareUpdate(PreparedStatement statement, Permission permission) throws SQLException {
        statement.setString(1, permission.getPermissionName());
        statement.setString(2, permission.getId().toString());
    }
}
