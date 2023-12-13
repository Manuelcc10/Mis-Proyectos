package com.example.libreriapool;

import com.example.libreriapool.Modelo.Libro;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class libroController {

    @FXML
    private TableView<Libro> tabla_id;

    @FXML
    private TableColumn<Libro, Integer> idLibro_tab;

    @FXML
    private TableColumn<Libro, String> titulo_tab;

    @FXML
    private TableColumn<Libro, String> autor_tab;

    @FXML
    private TableColumn<Libro, String> anioPublicacion_tab;

    @FXML
    private TableColumn<Libro, Integer> cantidadDisponibles_tab;

    @FXML
    private Button anadirId;

    @FXML
    private Button btonMod;


    ConnectionPool connectionPool = new ConnectionPool();
    // Agrega un ScheduledExecutorService


    // Método para detener la actualización periódica

    public libroController() {
        // Constructor vacío
    }

    public void initialize() {
        inicializarTabla();
        Image image = new Image("file:/C:/Users/mcast/Downloads/libreriaPool/src/main/resources/img/insertar.png");
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(40);  // Reemplaza 40 con el ancho deseado
        imageView.setFitHeight(40); // Reemplaza 40 con el alto deseado
        imageView.setPreserveRatio(true); // Mantener la relación de aspecto
        Image image2 = new Image("file:/C:/Users/mcast/Downloads/libreriaPool/src/main/resources/img/modificar.png");
        ImageView imageView2 = new ImageView(image2);
        imageView2.setFitWidth(40);  // Reemplaza 40 con el ancho deseado
        imageView2.setFitHeight(40); // Reemplaza 40 con el alto deseado
        imageView2.setPreserveRatio(true); // Mantener la relación de aspecto

        anadirId.setGraphic(imageView);
        btonMod.setGraphic(imageView2);

    }




    public void inicializarTabla() {
        // Limpiar la lista antes de agregar nuevos elementos
        tabla_id.getItems().clear();

        // Simulamos cargar datos de la base de datos
        List<Libro> listaLibros = obtenerDatosDesdeLaBaseDeDatos();

        // Configurar las columnas de la tabla
        idLibro_tab.setCellValueFactory(cellData -> cellData.getValue().idLibroProperty().asObject());
        titulo_tab.setCellValueFactory(cellData -> cellData.getValue().tituloProperty());
        autor_tab.setCellValueFactory(cellData -> cellData.getValue().autorProperty());
        anioPublicacion_tab.setCellValueFactory(cellData -> cellData.getValue().anioPublicacionStringProperty());
        cantidadDisponibles_tab.setCellValueFactory(cellData -> cellData.getValue().cantidadDisponibleProperty().asObject());

        // Cargar los datos en la tabla
        tabla_id.getItems().addAll(listaLibros);
    }

    private List<Libro> obtenerDatosDesdeLaBaseDeDatos() {
        List<Libro> listaLibros = new ArrayList<>();

        try (Connection connection = connectionPool.getConnection()) {
            String sql = "SELECT * FROM libros";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
                 ResultSet resultSet = preparedStatement.executeQuery()) {

                while (resultSet.next()) {
                    int idLibro = resultSet.getInt("idLibro");
                    String titulo = resultSet.getString("titulo");
                    String autor = resultSet.getString("autor");
                    LocalDate anioPublicacion = resultSet.getDate("anioPublicacion").toLocalDate();
                    int cantidadDisponible = resultSet.getInt("cantidadDisponible");

                    Libro libro = new Libro(idLibro, titulo, autor, anioPublicacion, cantidadDisponible);
                    listaLibros.add(libro);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return listaLibros;
    }

    @FXML
    public void btnAdd_OnAction(ActionEvent event) {
        abrirVentanaContenido(null);
    }

    private void abrirVentanaContenido(Libro libro) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("contenidoLibros.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle(libro == null ? "Añadir Libro" : "Modificar Libro");
            stage.setScene(new Scene(root));

            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(tabla_id.getScene().getWindow());

            contenidoController contenidoController = loader.getController();

            if (libro != null) {
                contenidoController.cargarDatos(libro);
            }

            contenidoController.setLibroController(this);

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void btnMod_OnAction(ActionEvent event) {
        Libro libroSeleccionado = tabla_id.getSelectionModel().getSelectedItem();

        if (libroSeleccionado != null) {
            abrirVentanaContenido(libroSeleccionado);
        } else {
            mostrarMensaje("Selecciona un libro para modificar.");
        }
    }

    private void mostrarMensaje(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public void buscador_titulo(KeyEvent keyEvent) {
        String tituloBusqueda = ((javafx.scene.control.TextField) keyEvent.getSource()).getText();
        inicializarTablaConBusqueda(tituloBusqueda);
    }

    private void inicializarTablaConBusqueda(String tituloBusqueda) {
        // Limpiar la lista antes de agregar nuevos elementos
        tabla_id.getItems().clear();

        // Simulamos cargar datos de la base de datos con búsqueda
        List<Libro> listaLibros = obtenerDatosDesdeLaBaseDeDatosConBusqueda(tituloBusqueda);

        // Configurar las columnas de la tabla principal
        idLibro_tab.setCellValueFactory(cellData -> cellData.getValue().idLibroProperty().asObject());
        titulo_tab.setCellValueFactory(cellData -> cellData.getValue().tituloProperty());
        autor_tab.setCellValueFactory(cellData -> cellData.getValue().autorProperty());
        anioPublicacion_tab.setCellValueFactory(cellData -> cellData.getValue().anioPublicacionStringProperty());
        cantidadDisponibles_tab.setCellValueFactory(cellData -> cellData.getValue().cantidadDisponibleProperty().asObject());

        // Cargar los datos en la tabla principal
        tabla_id.getItems().addAll(listaLibros);
    }

    private List<Libro> obtenerDatosDesdeLaBaseDeDatosConBusqueda(String tituloBusqueda) {
        List<Libro> listaLibros = new ArrayList<>();

        try (Connection connection = connectionPool.getConnection()) {
            String sql = "SELECT * FROM libros WHERE titulo LIKE ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, "%" + tituloBusqueda + "%");

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        int idLibro = resultSet.getInt("idLibro");
                        String titulo = resultSet.getString("titulo");
                        String autor = resultSet.getString("autor");
                        LocalDate anioPublicacion = resultSet.getDate("anioPublicacion").toLocalDate();
                        int cantidadDisponible = resultSet.getInt("cantidadDisponible");

                        Libro libro = new Libro(idLibro, titulo, autor, anioPublicacion, cantidadDisponible);
                        listaLibros.add(libro);
                    }
                } catch (SQLException e) {
                    e.printStackTrace(); // Handle the exception
                }
            } catch (SQLException e) {
                e.printStackTrace(); // Handle the exception
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception
        }

        return listaLibros;
    }



}
