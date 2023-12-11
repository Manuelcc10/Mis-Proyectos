package controlador;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class CuentaBater extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/vista/img/icono.png")));

        // Establecer la imagen como icono de la etapa principal
        stage.getIcons().add(icon);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/vista/visor.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("VISOR DE CUENTAS");
        stage.setScene(scene);
        stage.show();

    }



    public static void main(String[] args) {
        launch();
    }
}