package de.fhzwickau.reisewelle.dao;

import de.fhzwickau.reisewelle.config.JDBCConfig;
import de.fhzwickau.reisewelle.model.City;
import de.fhzwickau.reisewelle.model.Country;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CityDao implements BaseDao<City> {

    private final CountryDao countryDao = new CountryDao();

    @Override
    public City findById(UUID id) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        String sql = "SELECT id, name, country_id FROM City WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Country country = countryDao.findById(UUID.fromString(rs.getString("country_id")));
                    City city = new City(rs.getString("name"), country);
                    city.setId(UUID.fromString(rs.getString("id")));
                    return city;
                }
            }
        }
        return null;
    }

    @Override
    public List<City> findAll() throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        String sql = "SELECT id, name, country_id FROM City";
        List<City> list = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Country country = countryDao.findById(UUID.fromString(rs.getString("country_id")));
                City city = new City(rs.getString("name"), country);
                city.setId(UUID.fromString(rs.getString("id")));
                list.add(city);
            }
        }
        return list;
    }

    public void add(City entity) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        entity.setId(UUID.randomUUID());
        String sql = "INSERT INTO City (id, name, country_id) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entity.getId().toString());
            stmt.setString(2, entity.getName());
            stmt.setString(3, entity.getCountry().getId().toString());
            stmt.executeUpdate();
        }
    }

    public void update(City entity) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        String sql = "UPDATE City SET name = ?, country_id = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entity.getName());
            stmt.setString(2, entity.getCountry().getId().toString());
            stmt.setString(3, entity.getId().toString());
            stmt.executeUpdate();
        }
    }

    @Override
    public void save(City entity) throws SQLException {
        // По умолчанию делаем add, если нет id, иначе update (можно вообще убрать если используешь отдельно add/update)
        if (entity.getId() == null) {
            add(entity);
        } else {
            update(entity);
        }
    }

    @Override
    public void delete(UUID id) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM City WHERE id = ?")) {
            stmt.setString(1, id.toString());
            stmt.executeUpdate();
        }
    }
}
