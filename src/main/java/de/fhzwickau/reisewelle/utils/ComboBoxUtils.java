package de.fhzwickau.reisewelle.utils;

import de.fhzwickau.reisewelle.dao.BaseDao;
import javafx.scene.control.ComboBox;

import java.sql.SQLException;

public class ComboBoxUtils {

    public static <T> void populate(ComboBox<T> comboBox, BaseDao<T> dao) {
        try {
            comboBox.getItems().setAll(dao.findAll());
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }
}
