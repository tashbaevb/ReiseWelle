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
}