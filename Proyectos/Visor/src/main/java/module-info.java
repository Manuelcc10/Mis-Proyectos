module controlador {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jasperreports;
    opens controlador to javafx.fxml;
    exports controlador;

}