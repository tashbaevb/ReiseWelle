package de.fhzwickau.reisewelle.dao;

import de.fhzwickau.reisewelle.config.JDBCConfig;
import de.fhzwickau.reisewelle.model.Country;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CountryDao implements BaseDao<Country> {

    @Override
    public Country findById(UUID id) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        String sql = "SELECT id, name FROM Country WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapCountry(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Country> findAll() throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        String sql = "SELECT id, name FROM Country ORDER BY name ASC";
        List<Country> countries = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                countries.add(mapCountry(rs));
            }
        }
        return countries;
    }

    public void save(Country country) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        String sql = "INSERT INTO Country (id, name) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (country.getId() == null) country.setId(UUID.randomUUID());
            stmt.setString(1, country.getId().toString());
            stmt.setString(2, country.getName());
            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(UUID id) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM Country WHERE id = ?")) {
            stmt.setString(1, id.toString());
            stmt.executeUpdate();
        }
    }

    public void update(Country country) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        String sql = "UPDATE Country SET name = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, country.getName());
            stmt.setString(2, country.getId().toString());
            stmt.executeUpdate();
        }
    }


    // Вспомогательный метод для преобразования ResultSet в Country
    private Country mapCountry(ResultSet rs) throws SQLException {
        UUID id = UUID.fromString(rs.getString("id"));
        String name = rs.getString("name");
        Country country = new Country(name);
        country.setId(id); // Устанавливаем id вручную, чтобы не сгенерировался новый
        return country;
    }
}
