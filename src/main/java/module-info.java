module de.fhzwickau.reisewelle {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires eu.hansolo.tilesfx;

    opens de.fhzwickau.reisewelle to javafx.fxml;
    exports de.fhzwickau.reisewelle;
}