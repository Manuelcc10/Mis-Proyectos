package com.example.libreriapool;

import com.example.libreriapool.Modelo.Libro;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.time.LocalDate;


public class contenidoController {
    @FXML
    private TextField txtLibro_id;
    @FXML
    private TextField txtTitulo_id;
    @FXML
    private TextField txtAutor_id;
    @FXML
    private TextField txtCantidad_id;
    @FXML
    private DatePicker dpAnio_id;
    @FXML
    private Button btnguardar_id;
    private Libro libroAModificar;
    private libroController libroController;



    private ConnectionPool connectionPool;

    public contenidoController() {
        // Inicializar el pool de conexiones
        this.connectionPool = new ConnectionPool();
    }

    public void setLibroController(libroController libroController) {
        this.libroController = libroController;
    }
    public void cargarDatos(Libro libro) {
        libroAModificar = libro;

        // Lógica para cargar los datos del libro en los campos correspondientes
        txtLibro_id.setEditable(false);
        txtLibro_id.setText(String.valueOf(libro.getIdLibro()));
        txtTitulo_id.setText(libro.getTitulo());
        txtAutor_id.setText(libro.getAutor());
        txtCantidad_id.setText(String.valueOf(libro.getCantidadDisponible()));
        LocalDate localDate = libro.getAnioPublicacion();
        dpAnio_id.setValue(localDate);
    }

    @FXML
    public void btnGuardar_OnAction(ActionEvent event) {
        // Obtener los valores de los campos
        String titulo = txtTitulo_id.getText();
        String autor = txtAutor_id.getText();
        String cantidadText = txtCantidad_id.getText();

        // Verificar que no haya campos en blanco
        if (titulo.trim().isEmpty() || autor.trim().isEmpty() || cantidadText.trim().isEmpty()) {
            mostrarMensaje("Todos los campos son obligatorios");
            return;  // Salir del método si hay campos en blanco
        }

        int cantidad;

        // Verificar que la cantidad sea un entero válido
        try {
            cantidad = Integer.parseInt(cantidadText);
        } catch (NumberFormatException e) {
            mostrarMensaje("La cantidad debe ser un número entero");
            return;  // Salir del método si la cantidad no es un entero válido
        }

        // Obtener la fecha de anioPublicacion
        java.sql.Date anioPublicacion = java.sql.Date.valueOf(dpAnio_id.getValue());

        int rowsAffected = 0;

        try (Connection connection = connectionPool.getConnection()) {
            if (libroAModificar == null) {
                // Es una nueva inserción
                String sqlInsert = "INSERT INTO libros (titulo, autor, anioPublicacion, cantidadDisponible) VALUES (?, ?, ?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(sqlInsert)) {
                    preparedStatement.setString(1, titulo);
                    preparedStatement.setString(2, autor);
                    preparedStatement.setDate(3, anioPublicacion);
                    preparedStatement.setInt(4, cantidad);

                    // Ejecutar la inserción
                    rowsAffected = preparedStatement.executeUpdate();
                }
            } else {
                // Es una modificación
                String sqlUpdate = "UPDATE libros SET titulo=?, autor=?, anioPublicacion=?, cantidadDisponible=? WHERE idLibro=?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdate)) {
                    preparedStatement.setString(1, titulo);
                    preparedStatement.setString(2, autor);
                    preparedStatement.setDate(3, anioPublicacion);
                    preparedStatement.setInt(4, cantidad);
                    preparedStatement.setInt(5, libroAModificar.getIdLibro());

                    // Ejecutar la actualización
                    rowsAffected = preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error de base de datos");
        }

        // Actualizar la tabla en libroController después de agregar o modificar un libro
        if (rowsAffected > 0) {
            mostrarMensajeYCerrar("Libro guardado correctamente");
        } else {
            mostrarMensaje("Error al guardar el libro");
        }

        // Actualizar la tabla en libroController después de agregar o modificar un libro
        if (libroController != null) {
            libroController.inicializarTabla();
        }
    }

    private void mostrarMensajeYCerrar(String mensaje) {
        mostrarMensaje(mensaje);

        // Verificar si btnGuardar_id y su Scene no son nulos
        if (btnguardar_id != null && btnguardar_id.getScene() != null) {
            // Obtener el Stage actual y cerrarlo
            Stage stage = (Stage) btnguardar_id.getScene().getWindow();
            stage.close();
        } else {
            System.out.println("Error: btnGuardar_id o su Scene es nulo");
        }
    }

    private void mostrarMensaje(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Mensaje");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    private void obtenerSiguienteIdLibro() {
        try (Connection connection = connectionPool.getConnection()) {
            String sql = "SELECT MAX(idLibro) + 1 AS siguienteId FROM libros";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int siguienteId = resultSet.getInt("siguienteId");
                    txtLibro_id.setText(String.valueOf(siguienteId));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al obtener el siguiente idLibro");
        }
    }

    @FXML
    public void initialize() {
        // Obtener y mostrar el siguiente idLibro al inicializar el controlador
        obtenerSiguienteIdLibro();
        Image image = new Image("file:/C:/Users/mcast/Downloads/libreriaPool/src/main/resources/img/insertar.png");
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(40);  // Reemplaza 40 con el ancho deseado
        imageView.setFitHeight(40); // Reemplaza 40 con el alto deseado
        imageView.setPreserveRatio(true); // Mantener la relación de aspecto

        btnguardar_id.setGraphic(imageView);

    }
    }

