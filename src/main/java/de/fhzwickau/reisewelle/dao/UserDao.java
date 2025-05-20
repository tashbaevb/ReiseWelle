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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserDao implements BaseDao<User> {

    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);
    private final UserRoleDao userRoleDao = new UserRoleDao();

    @Override
    public List<User> findAll() throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        String sql = "SELECT id, email, password, user_role_id, created_at FROM [dbo].[User]";
        List<User> users = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                User user = mapUser(rs);
                if (user != null) {
                    users.add(user);
                    logger.info("Loaded user: email={}, role={}", user.getEmail(), user.getUserRole().getRoleName());
                }
            }
        }

        return users;
    }

    @Override
    public User findById(UUID id) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        String sql = "SELECT id, email, password, user_role_id, created_at FROM [dbo].[User] WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        }

        return null;
    }

    @Override
    public void save(User user) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        boolean isNew = user.getId() == null;
        String sql =  isNew
                ? "INSERT INTO [dbo].[User] (id, email, password, user_role_id, created_at) VALUES (?, ?, ?, ?, ?)"
                : "UPDATE [dbo].[User] SET email = ?, password = ?, user_role_id = ?, created_at = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (isNew) {
                prepareInsert(stmt, user);
                logger.info("Executing INSERT: id=" + user.getId() + ", email=" + user.getEmail() + ", roleId=" + user.getUserRole().getId());
            } else {
                prepareUpdate(stmt, user);
                logger.info("Executing UPDATE: id=" + user.getId() + ", email=" + user.getEmail() + ", roleId=" + user.getUserRole().getId());
            }
            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(UUID id) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        String sql = "DELETE FROM [dbo].[User] WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            stmt.executeUpdate();
        }
    }

    public List<User> findEmployees() throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        String sql = """
                SELECT u.id, u.email, u.password, u.user_role_id, u.created_at
                FROM [User] u
                JOIN UserRole r ON u.user_role_id = r.id
                WHERE r.role_name = 'employee'
                """;
        List<User> companyReps = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                companyReps.add(mapUser(rs));
            }
        }

        return companyReps;
    }

    private User mapUser(ResultSet rs) throws SQLException {
        UUID roleId = UUID.fromString(rs.getString("user_role_id"));
        UserRole userRole = userRoleDao.findById(roleId);
        if (userRole == null) {
            logger.warn("UserRole not found for roleId: {}", roleId);
            return null;
        }

        User user = new User(
                rs.getString("email"),
                rs.getString("password"),
                userRole,
                rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null
        );
        user.setId(roleId);

        return user;
    }

    private void prepareInsert(PreparedStatement stmt, User user) throws SQLException {
        user.setId(UUID.randomUUID());
        stmt.setString(1, user.getId().toString());
        stmt.setString(2, user.getEmail());
        stmt.setString(3, user.getPassword());
        stmt.setString(4, user.getUserRole().getId().toString());
        stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
    }

    private void prepareUpdate(PreparedStatement stmt, User user) throws SQLException {
        stmt.setString(1, user.getEmail());
        stmt.setString(2, user.getPassword());
        stmt.setString(3, user.getUserRole().getId().toString());
        stmt.setTimestamp(4, user.getCreatedAt() != null ? Timestamp.valueOf(user.getCreatedAt()) : null);
        stmt.setString(5, user.getId().toString());
    }
}