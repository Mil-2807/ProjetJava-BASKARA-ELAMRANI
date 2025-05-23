module com.beginsecure.projetjavav6 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens com.beginsecure.projetjavav6 to javafx.fxml;
    exports com.beginsecure.projetjavav6;
}