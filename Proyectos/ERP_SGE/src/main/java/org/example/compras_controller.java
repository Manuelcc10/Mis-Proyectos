package org.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import org.example.modelo.compra;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

public class compras_controller {
    @FXML
    private ChoiceBox<Integer> choise_IdCliente;

    @FXML
    private ChoiceBox<String> choise_NombreProd;

    @FXML
    private TableView<compra> tabla_ID;

    @FXML
    private TableColumn<compra, Integer> colum_IdCompra;

    @FXML
    private TableColumn<compra, Integer> colum_IdCliente;

    @FXML
    private TableColumn<compra, String> colum_NombreProd;

    @FXML
    private TableColumn<compra, Date> colum_Fecha;

    @FXML
    private TableColumn<compra, Integer> colum_Cant;

    @FXML
    private DatePicker id_fechaPicker;

    @FXML
    private TextField cantidad_txt;

    @FXML
    private TextField compra_id_txt;

    @FXML
    private Button mod_Id;

    @FXML
    private Button borrar_Id;

    @FXML
    private Button id_butComprar;

    private ObservableList<Integer> clientes = FXCollections.observableArrayList();
    private ObservableList<String> productos = FXCollections.observableArrayList();
    private ObservableList<compra> listaCompras = FXCollections.observableArrayList();
    private ObservableList<compra> listaComprasfiltrada = FXCollections.observableArrayList();
    @FXML
    private TextField busca;


    public void initialize() {
        // Configurar las columnas de la TableView
        tabla_ID.setItems(listaCompras);
        colum_IdCompra.setCellValueFactory(cellData -> cellData.getValue().idCompraProperty().asObject());
        colum_IdCliente.setCellValueFactory(cellData -> cellData.getValue().idClienteProperty().asObject());
        colum_NombreProd.setCellValueFactory(cellData -> cellData.getValue().nombreProductoProperty());
        colum_Fecha.setCellValueFactory(cellData -> cellData.getValue().fechaProperty());
        colum_Cant.setCellValueFactory(cellData -> cellData.getValue().cantidadProperty().asObject());

        cargarDatosDesdeBD();
        cargarClientes();
        cargarProductos();

        // Configurar el listener para la selección de la tabla
        tabla_ID.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // Manejar el evento de selección
            if (newValue != null) {
                // Establecer los datos seleccionados en los campos correspondientes
                compra_id_txt.setText(String.valueOf(newValue.getIdCompra()));
                choise_IdCliente.setValue(newValue.getIdCliente());
                choise_NombreProd.setValue(newValue.getNombreProducto());
                id_fechaPicker.setValue(newValue.getFecha().toLocalDate());
                cantidad_txt.setText(String.valueOf(newValue.getCantidad()));
            }
        });
        configurarBoton("/img/compras.png", id_butComprar, "Comprar");
        configurarBoton("/img/modificar.png", mod_Id, "Modificar");
        configurarBoton("/img/basura.png", borrar_Id, "Modificar");

        busca.textProperty().addListener((observable, oldValue, newValue) -> buscarClientes());
    }
    private void configurarBoton(String imagePath, Button boton, String tooltipText) {
        try {
            InputStream inputStream = getClass().getResourceAsStream(imagePath);

            if (inputStream != null) {
                Image image = new Image(inputStream);

                // Establecer un tamaño fijo para la imagen (ajusta estos valores según tus necesidades)
                double anchoDeseado = 60; // Ancho deseado en píxeles
                double altoDeseado = 60; // Alto deseado en píxeles

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

    public void Act_Comprar() {
        // Verificar campos y mostrar advertencias si es necesario
        if (!validarCampos()) {
            return;
        }

        // Insertar una nueva compra en la base de datos
        insertarNuevaCompra();

        // Actualizar la tabla en la interfaz gráfica
        listaCompras.add(new compra(
                // Ajustar según tus necesidades
                Integer.parseInt(compra_id_txt.getText()),
                choise_IdCliente.getValue(),
                choise_NombreProd.getValue(),
                java.sql.Date.valueOf(id_fechaPicker.getValue()),
                Integer.parseInt(cantidad_txt.getText())
        ));

        // Limpiar los TextField
        compra_id_txt.clear();
        choise_IdCliente.setValue(null);
        choise_NombreProd.setValue(null);
        id_fechaPicker.setValue(null);
        cantidad_txt.clear();
    }

    private void insertarNuevaCompra() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = Conex_bd.obtenerConexion();
            String sql = "INSERT INTO Compras (Id_cliente, Nombre_producto, Fecha, Cantidad) VALUES (?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, choise_IdCliente.getValue());
            preparedStatement.setString(2, choise_NombreProd.getValue());
            preparedStatement.setDate(3, java.sql.Date.valueOf(id_fechaPicker.getValue()));
            preparedStatement.setInt(4, Integer.parseInt(cantidad_txt.getText()));

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Cierra los recursos
            cerrarRecursos(connection, preparedStatement);
        }
    }

    private boolean validarCampos() {
        // Agregar lógica de validación según tus necesidades
        return true;
    }

    private void cargarDatosDesdeBD() {
        // Ajustar según tu esquema de base de datos y las columnas necesarias
        // Esta es solo una implementación de ejemplo
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = Conex_bd.obtenerConexion();
            String sql = "SELECT Id_compra, Id_cliente, Nombre_producto, Fecha, Cantidad FROM Compras";
            preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int idCompra = resultSet.getInt("Id_compra");
                int idCliente = resultSet.getInt("Id_cliente");
                String nombreProducto = resultSet.getString("Nombre_producto");
                Date fecha = resultSet.getDate("Fecha");
                int cantidad = resultSet.getInt("Cantidad");

                listaCompras.add(new compra(idCompra, idCliente, nombreProducto, fecha, cantidad));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cerrarRecursos(connection, preparedStatement);
        }
    }

    private void cargarClientes() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = Conex_bd.obtenerConexion();
            String sql = "SELECT Id_cliente FROM Clientes";
            preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int idCliente = resultSet.getInt("Id_cliente");
                clientes.add(idCliente);
            }

            choise_IdCliente.setItems(clientes);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cerrarRecursos(connection, preparedStatement);
        }
    }

    private void cargarProductos() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = Conex_bd.obtenerConexion();
            String sql = "SELECT Nombre_producto FROM Productos";
            preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String nombreProducto = resultSet.getString("Nombre_producto");
                productos.add(nombreProducto);
            }

            choise_NombreProd.setItems(productos);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cerrarRecursos(connection, preparedStatement);
        }
    }

    private void cerrarRecursos(Connection connection, PreparedStatement preparedStatement) {
        // Cierra los recursos
        if (preparedStatement != null) {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void act_mod() {
        modificarCompra();
    }

    private void modificarCompra() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = Conex_bd.obtenerConexion();
            String sql = "UPDATE Compras SET Id_cliente = ?, Nombre_producto = ?, Fecha = ?, Cantidad = ? WHERE Id_compra = ?";
            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, choise_IdCliente.getValue());
            preparedStatement.setString(2, choise_NombreProd.getValue());
            preparedStatement.setDate(3, java.sql.Date.valueOf(id_fechaPicker.getValue()));
            preparedStatement.setInt(4, Integer.parseInt(cantidad_txt.getText()));
            preparedStatement.setInt(5, Integer.parseInt(compra_id_txt.getText()));

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Cierra los recursos
            cerrarRecursos(connection, preparedStatement);
        }
        actualizarTabla();
        limpiarCampos();
    }

    public void act_Borrar() {
        eliminarCompra();
    }

    private void eliminarCompra() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = Conex_bd.obtenerConexion();
            String sql = "DELETE FROM Compras WHERE Id_compra = ?";
            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, Integer.parseInt(compra_id_txt.getText()));

            preparedStatement.executeUpdate();

            listaCompras.remove(tabla_ID.getSelectionModel().getSelectedItem());

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Cierra los recursos
            cerrarRecursos(connection, preparedStatement);
        }
        actualizarTabla();
        limpiarCampos();
    }

    private void limpiarCampos() {
        compra_id_txt.clear();
        choise_IdCliente.setValue(null);
        choise_NombreProd.setValue(null);
        id_fechaPicker.setValue(null);
        cantidad_txt.clear();
    }

    private void actualizarTabla() {
        listaCompras.clear();
        cargarDatosDesdeBD();
    }
    public void buscador(javafx.scene.input.KeyEvent keyEvent) {
    }
    private void buscarClientes() {
        String textoBuscado = busca.getText().toLowerCase();
        listaComprasfiltrada.clear();

        // Realizar búsqueda en la lista de compras
        for (compra compra : listaCompras) {
            if (String.valueOf(compra.getIdCompra()).contains(textoBuscado)
                    || String.valueOf(compra.getIdCliente()).contains(textoBuscado)
                    || compra.getNombreProducto().toLowerCase().contains(textoBuscado)
                    || compra.getFecha().toString().contains(textoBuscado)
                    || String.valueOf(compra.getCantidad()).contains(textoBuscado)) {
                listaComprasfiltrada.add(compra);
            }
        }

        // Actualizar la tabla con los resultados de la búsqueda
        tabla_ID.setItems(listaComprasfiltrada);
    }
}