package com.example.libreriapool;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;



public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);

        // Obtener el controlador de la ventana de inicio de sesión
        loginController loginController = loader.getController();
        loginController.setStage(primaryStage); // Pasar el Stage actual

        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


}
