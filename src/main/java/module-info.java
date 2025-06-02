module de.fhzwickau.reisewelle {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires java.sql;
    requires java.dotenv;
    requires org.slf4j;

    opens de.fhzwickau.reisewelle to javafx.fxml;
    exports de.fhzwickau.reisewelle;
    exports de.fhzwickau.reisewelle.controller;
    opens de.fhzwickau.reisewelle.controller to javafx.fxml;
    exports de.fhzwickau.reisewelle.controller.admin;
    opens de.fhzwickau.reisewelle.controller.admin to javafx.fxml;
    exports de.fhzwickau.reisewelle.model;
    exports de.fhzwickau.reisewelle.controller.user;
    opens de.fhzwickau.reisewelle.controller.user to javafx.fxml;
    exports de.fhzwickau.reisewelle.dto;
    exports de.fhzwickau.reisewelle.controller.admin.bus;
    opens de.fhzwickau.reisewelle.controller.admin.bus to javafx.fxml;
    exports de.fhzwickau.reisewelle.controller.admin.driver;
    opens de.fhzwickau.reisewelle.controller.admin.driver to javafx.fxml;
    exports de.fhzwickau.reisewelle.controller.admin.user;
    opens de.fhzwickau.reisewelle.controller.admin.user to javafx.fxml;
    exports de.fhzwickau.reisewelle.controller.admin.trip;
    opens de.fhzwickau.reisewelle.controller.admin.trip to javafx.fxml;
    exports de.fhzwickau.reisewelle.controller.admin.employee;
    opens de.fhzwickau.reisewelle.controller.admin.employee to javafx.fxml;

    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    // ДОБАВЬ вот это для Country CRUD:
    exports de.fhzwickau.reisewelle.controller.admin.country;
    opens de.fhzwickau.reisewelle.controller.admin.country to javafx.fxml;

    // Если будешь делать CRUD для города:
    exports de.fhzwickau.reisewelle.controller.admin.city;
    opens de.fhzwickau.reisewelle.controller.admin.city to javafx.fxml;

    // Для статусов (если отдельный пакет):
    exports de.fhzwickau.reisewelle.controller.admin.status;
    opens de.fhzwickau.reisewelle.controller.admin.status to javafx.fxml;

    // Для статусов трипа (если отдельный пакет):
//    exports de.fhzwickau.reisewelle.controller.admin.tripstatus;
//    opens de.fhzwickau.reisewelle.controller.admin.tripstatus to javafx.fxml;
    // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
}
