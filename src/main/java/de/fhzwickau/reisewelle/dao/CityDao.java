// src/main/java/de/fhzwickau/reisewelle/dao/CityDao.java
package de.fhzwickau.reisewelle.dao;

import de.fhzwickau.reisewelle.config.JDBCConfig;
import de.fhzwickau.reisewelle.model.City;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CityDao implements BaseDao<City> {

    @Override
    public City findById(UUID id) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        String sql = "SELECT id, name, country_id FROM City WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    City city = new City(rs.getString("name"), null);
                    city.setId(UUID.fromString(rs.getString("id")));
                    // TODO: load country via CountryDao
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
                City city = new City(rs.getString("name"), null);
                city.setId(UUID.fromString(rs.getString("id")));
                // TODO: load country via CountryDao
                list.add(city);
            }
        }
        return list;
    }

    @Override
    public void save(City entity) throws SQLException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void delete(UUID id) throws SQLException {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
