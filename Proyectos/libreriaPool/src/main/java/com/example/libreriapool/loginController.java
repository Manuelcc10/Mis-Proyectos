package com.example.libreriapool;


import com.example.libreriapool.Modelo.Usuarios;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class loginController {

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtContraseña;

    private final ConnectionPool connectionPool = new ConnectionPool();
    private Stage stage; // Añadir esta variable
    private Usuarios usuarios = new Usuarios();
    public Button btn_aceptarId;

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    private void mostrarMensajeError(String mensaje) {
        // Método para mostrar un cuadro de diálogo de error con un mensaje personalizado
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error de autenticación");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    @FXML
    public void btnAceptar_OnAction(ActionEvent event) {
        String usuario = txtUsuario.getText();
        String contraseña = txtContraseña.getText();

        // Validar longitud mínima del usuario y contraseña
        if (usuario.length() < 3 || contraseña.length() < 3) {
            mostrarMensajeError("El usuario y la contraseña deben tener al menos 3 caracteres.");
            return;
        }

        if (autenticarUsuario(usuario, contraseña)) {
            mostrarVentanaLibro(usuario);
            // Cerrar ventana de login
            stage.close();
        } else {
            mostrarMensajeError("Usuario o contraseña incorrectos.");
        }
    }

    private boolean autenticarUsuario(String usuario, String contraseña) {
        try (Connection connection = connectionPool.getConnection()) {
            // Consulta SQL para obtener la contraseña hasheada del usuario
            String sqlSelect = "SELECT password FROM usuarios WHERE username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlSelect)) {
                preparedStatement.setString(1, usuario);

                // Ejecutar la consulta
                ResultSet resultSet = preparedStatement.executeQuery();

                // Si hay un resultado, verificar la contraseña
                if (resultSet.next()) {
                    String hashedPassword = resultSet.getString("password");
                    return BCrypt.checkpw(contraseña, hashedPassword);
                } else {
                    // Si no hay resultados, intentamos insertar un nuevo usuario con contraseña hasheada
                    String hashedPassword = BCrypt.hashpw(contraseña, BCrypt.gensalt());
                    String sqlInsert = "INSERT INTO usuarios (username, password) VALUES (?, ?)";
                    try (PreparedStatement insertStatement = connection.prepareStatement(sqlInsert)) {
                        insertStatement.setString(1, usuario);
                        insertStatement.setString(2, hashedPassword);

                        // Ejecutar la inserción
                        int rowsAffected = insertStatement.executeUpdate();

                        // Verificar si se insertó correctamente
                        return rowsAffected > 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void mostrarVentanaLibro(String usuario) {
        try {
            // Cargar el archivo FXML de la ventana de libros (libro.fxml)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("libro.fxml"));
            Parent root = loader.load();

            // Configurar el nombre de usuario en el controlador de la ventana de libros
            usuarios.setUsuario(usuario);

            // Crear una nueva escena
            Scene scene = new Scene(root);

            // Configurar el nuevo stage
            Stage stage = new Stage();
            stage.setTitle("Libros");
            stage.setScene(scene);

            // Mostrar la nueva ventana
            stage.show();

            // Mostrar la alerta de bienvenida
            mostrarAlertaBienvenida(usuario);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mostrarAlertaBienvenida(String usuario) {
        // Mostrar un cuadro de diálogo de bienvenida
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Bienvenido");
        alert.setHeaderText("Bienvenido, " + usuario + "!");
        alert.setContentText("Disfruta de la aplicación.");
        alert.showAndWait();
    }




    public void initialize() {
        Image image = new Image("file:/C:/Users/mcast/Downloads/libreriaPool/src/main/resources/img/tic.png");
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(40);  // Reemplaza 40 con el ancho deseado
        imageView.setFitHeight(40); // Reemplaza 40 con el alto deseado
        imageView.setPreserveRatio(true); // Mantener la relación de aspecto

        btn_aceptarId.setGraphic(imageView);
    }

}


