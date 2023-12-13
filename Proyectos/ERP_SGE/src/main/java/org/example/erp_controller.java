package org.example;

import javafx.application.Application;
import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;



import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

import javafx.scene.control.Button;

import javafx.scene.image.Image;



import javafx.scene.image.ImageView;



public class erp_controller extends Application  {
    @FXML
    private Button clientes_Id;

    @FXML
    private Button id_Prod;

    @FXML
    private Button prod_but;

    @FXML
    private Button ventas_but;

    @FXML
    private Button compras_but;

    @FXML
    private Button reporte_Id;




    @Override
    public void start(Stage primaryStage) {
        try {
            // Cargar el archivo FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/erp.fxml"));
            Parent root = loader.load();

            // Crear la escena
            Scene scene = new Scene(root, 650, 400);

            // Configurar el escenario principal (primaryStage)
            primaryStage.setScene(scene);
            primaryStage.setTitle("Vista del Controlador ERP");

            // Mostrar el escenario
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void launchApp(String[] args) {
        // Lanzar la aplicación JavaFX
        launch(args);
    }
public void action_Proveedores(){
    try {
        // Cargar el archivo FXML de la nueva vista
        FXMLLoader nuevaVistaLoader = new FXMLLoader(getClass().getResource("/vista/proveedores.fxml"));
        Parent nuevaVistaRoot = nuevaVistaLoader.load();

        // Crear el escenario para la nueva vista
        Stage nuevaVistaStage = new Stage();
        nuevaVistaStage.setScene(new Scene(nuevaVistaRoot, 600, 600));
        nuevaVistaStage.setTitle("Nueva Vista");

        // Mostrar el escenario de la nueva vista
        nuevaVistaStage.show();
    } catch (IOException e) {
        e.printStackTrace();
    }
}



    //Variables vista

    public void action_Prod() {
        try {
            // Cargar el archivo FXML de la nueva vista
            FXMLLoader nuevaVistaLoader = new FXMLLoader(getClass().getResource("/vista/productos.fxml"));
            Parent nuevaVistaRoot = nuevaVistaLoader.load();

            // Crear el escenario para la nueva vista
            Stage nuevaVistaStage = new Stage();
            nuevaVistaStage.setScene(new Scene(nuevaVistaRoot, 650, 500));
            nuevaVistaStage.setTitle("Nueva Vista");

            // Mostrar el escenario de la nueva vista
            nuevaVistaStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void initialize() {

        configurarBoton_up("/img/clientes.png", clientes_Id, "Clientes");
        configurarBoton_up("/img/mensajero.png", id_Prod, "Proveedores");
        configurarBoton_up("/img/frutas.png",prod_but, "Productos");
        configurarBoton("/img/ventas.png", ventas_but, "Ventas");
        configurarBoton("/img/compras.png", compras_but, "Compras");
        configurarBoton("/img/pdf.png", reporte_Id, "Reporte PDF");


    }

    private void configurarBoton(String imagePath, Button boton, String tooltipText) {
        try {
            InputStream inputStream = getClass().getResourceAsStream(imagePath);

            if (inputStream != null) {
                Image image = new Image(inputStream);

                // Establecer un tamaño fijo para la imagen (ajusta estos valores según tus necesidades)
                double anchoDeseado = 50; // Ancho deseado en píxeles
                double altoDeseado = 50; // Alto deseado en píxeles

                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(anchoDeseado);
                imageView.setFitHeight(altoDeseado);

                boton.setGraphic(imageView);
                boton.setTooltip(new Tooltip(tooltipText));
            } else {
                System.out.println("La ruta de la imagen no es válida: " + imagePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void configurarBoton_up(String imagePath, Button boton, String tooltipText) {
        try {
            InputStream inputStream = getClass().getResourceAsStream(imagePath);

            if (inputStream != null) {
                Image image = new Image(inputStream);

                // Establecer un tamaño fijo para la imagen (ajusta estos valores según tus necesidades)
                double anchoDeseado = 70; // Ancho deseado en píxeles
                double altoDeseado = 70; // Alto deseado en píxeles

                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(anchoDeseado);
                imageView.setFitHeight(altoDeseado);

                boton.setGraphic(imageView);
                boton.setTooltip(new Tooltip(tooltipText));
            } else {
                System.out.println("La ruta de la imagen no es válida: " + imagePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public void Action_clientesID() {
        try {
            // Cargar el archivo FXML de la nueva vista
            FXMLLoader nuevaVistaLoader = new FXMLLoader(getClass().getResource("/vista/login.fxml"));
            Parent nuevaVistaRoot = nuevaVistaLoader.load();

            // Crear el escenario para la nueva vista
            Stage nuevaVistaStage = new Stage();
            nuevaVistaStage.setScene(new Scene(nuevaVistaRoot, 600, 600));
            nuevaVistaStage.setTitle("Nueva Vista");

            // Mostrar el escenario de la nueva vista
            nuevaVistaStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void action_ventas(){
        try {
            // Cargar el archivo FXML de la nueva vista
            FXMLLoader nuevaVistaLoader = new FXMLLoader(getClass().getResource("/vista/ventas.fxml"));
            Parent nuevaVistaRoot = nuevaVistaLoader.load();

            // Crear el escenario para la nueva vista
            Stage nuevaVistaStage = new Stage();
            nuevaVistaStage.setScene(new Scene(nuevaVistaRoot, 700, 500));
            nuevaVistaStage.setTitle("Nueva Vista");

            // Mostrar el escenario de la nueva vista
            nuevaVistaStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void act_compras(){
        try {
            // Cargar el archivo FXML de la nueva vista
            FXMLLoader nuevaVistaLoader = new FXMLLoader(getClass().getResource("/vista/compras.fxml"));
            Parent nuevaVistaRoot = nuevaVistaLoader.load();

            // Crear el escenario para la nueva vista
            Stage nuevaVistaStage = new Stage();
            nuevaVistaStage.setScene(new Scene(nuevaVistaRoot, 700, 500));
            nuevaVistaStage.setTitle("Nueva Vista");

            // Mostrar el escenario de la nueva vista
            nuevaVistaStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void reporte_OnAction(ActionEvent actionEvent) {
        try {
            App app = new App();
            app.CrearPdf();

            mostrarAlerta(Alert.AlertType.INFORMATION, "Informe generado exitosamente", "Información");

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al generar el informe", "Error");
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String mensaje, String titulo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(mensaje);
        alert.showAndWait();
    }
}




