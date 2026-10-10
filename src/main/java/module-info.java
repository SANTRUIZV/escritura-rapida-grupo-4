module com.univalle.fpoe.escriturarapida {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.univalle.fpoe.escriturarapida.controller to javafx.fxml;
    exports com.univalle.fpoe.escriturarapida;
}
