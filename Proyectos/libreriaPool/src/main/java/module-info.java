module com.example.libreriapool {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jbcrypt;


    opens com.example.libreriapool to javafx.fxml;
    exports com.example.libreriapool;
}