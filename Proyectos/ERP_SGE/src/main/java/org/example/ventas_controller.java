package org.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.modelo.venta;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

public class ventas_controller {
    @FXML
    private ChoiceBox<Integer> choise_IdCliente;

    @FXML
    private ChoiceBox<String> choise_NombreProd;

    @FXML
    private TableView<venta> tabla_ID;

    @FXML
    private TableColumn<venta, Integer> colum_IdVenta;

    @FXML
    private TableColumn<venta, Integer> colum_IdCliente;

    @FXML
    private TableColumn<venta, String> colum_NombreProd;

    @FXML
    private TableColumn<venta, Date> colum_Fecha;

    @FXML
    private TableColumn<venta, Integer> colum_Cant;

    @FXML
    private DatePicker id_fechaPicker;

    @FXML
    private TextField cantidad_txt;

    @FXML
    private TextField venta_id_txt;

    private ObservableList<Integer> clientes = FXCollections.observableArrayList();
    private ObservableList<String> productos = FXCollections.observableArrayList();
    private ObservableList<venta> listaVentas = FXCollections.observableArrayList();

    @FXML
    private Button id_butVender;

    @FXML
    private Button mod_Id;

    @FXML
    private Button borrar_Id;

    @FXML
    private TextField busca;



    public void initialize() {
        // Configurar las columnas de la TableView
        tabla_ID.setItems(listaVentas);
        colum_IdVenta.setCellValueFactory(cellData -> cellData.getValue().idVentaProperty().asObject());
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
                venta_id_txt.setText(String.valueOf(newValue.getIdVenta()));
                choise_IdCliente.setValue(newValue.getIdCliente());
                choise_NombreProd.setValue(newValue.getNombreProducto());
                id_fechaPicker.setValue(newValue.getFecha().toLocalDate());
                cantidad_txt.setText(String.valueOf(newValue.getCantidad()));
            }
        });
        configurarBoton("/img/ventas.png", id_butVender, "Vender");
        configurarBoton("/img/modificar.png", mod_Id, "Modificar");
        configurarBoton("/img/basura.png", borrar_Id, "Borrar");

        busca.textProperty().addListener((observable, oldValue, newValue) -> buscarVentas());
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

    public void Act_Vender() {
        // Verificar campos y mostrar advertencias si es necesario
        if (!validarCampos()) {
            return;
        }

        // Insertar una nueva venta en la base de datos
        insertarNuevaVenta();

        // Actualizar la tabla en la interfaz gráfica
        listaVentas.add(new venta(
                // Ajustar según tus necesidades
                Integer.parseInt(venta_id_txt.getText()),
                choise_IdCliente.getValue(),
                choise_NombreProd.getValue(),
                java.sql.Date.valueOf(id_fechaPicker.getValue()),
                Integer.parseInt(cantidad_txt.getText())
        ));

        // Limpiar los TextField
        venta_id_txt.clear();
        choise_IdCliente.setValue(null);
        choise_NombreProd.setValue(null);
        id_fechaPicker.setValue(null);
        cantidad_txt.clear();
    }

    private void insertarNuevaVenta() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = Conex_bd.obtenerConexion();
            String sql = "INSERT INTO Ventas (Id_cliente, Nombre_producto, Fecha, Cantidad) VALUES (?, ?, ?, ?)";
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
            String sql = "SELECT Id_venta, Id_cliente, Nombre_producto, Fecha, Cantidad FROM Ventas";
            preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int idVenta = resultSet.getInt("Id_venta");
                int idCliente = resultSet.getInt("Id_cliente");
                String nombreProducto = resultSet.getString("Nombre_producto");
                Date fecha = resultSet.getDate("Fecha");
                int cantidad = resultSet.getInt("Cantidad");

                listaVentas.add(new venta(idVenta, idCliente, nombreProducto, fecha, cantidad));
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
    public void act_mod(){
        modificarVenta();
    }
    private void modificarVenta() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = Conex_bd.obtenerConexion();
            String sql = "UPDATE Ventas SET Id_cliente = ?, Nombre_producto = ?, Fecha = ?, Cantidad = ? WHERE Id_venta = ?";
            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, choise_IdCliente.getValue());
            preparedStatement.setString(2, choise_NombreProd.getValue());
            preparedStatement.setDate(3, java.sql.Date.valueOf(id_fechaPicker.getValue()));
            preparedStatement.setInt(4, Integer.parseInt(cantidad_txt.getText()));
            preparedStatement.setInt(5, Integer.parseInt(venta_id_txt.getText()));

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
    public void act_Borrar(){
        eliminarVenta();
    }
    private void eliminarVenta() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = Conex_bd.obtenerConexion();
            String sql = "DELETE FROM Ventas WHERE Id_venta = ?";
            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, Integer.parseInt(venta_id_txt.getText()));

            preparedStatement.executeUpdate();

            listaVentas.remove(tabla_ID.getSelectionModel().getSelectedItem());

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
        venta_id_txt.clear();
        choise_IdCliente.setValue(null);
        choise_NombreProd.setValue(null);
        id_fechaPicker.setValue(null);
        cantidad_txt.clear();
    }
    private void actualizarTabla() {
        listaVentas.clear();
        cargarDatosDesdeBD();
    }
    public void buscador(javafx.scene.input.KeyEvent keyEvent) {
    }
    private void buscarVentas() {
        String textoBuscado = busca.getText().toLowerCase();
        ObservableList<venta> listaVentasFiltrada = FXCollections.observableArrayList();

        // Realizar búsqueda en la lista de ventas
        for (venta venta : listaVentas) {
            if (String.valueOf(venta.getIdVenta()).contains(textoBuscado)
                    || String.valueOf(venta.getIdCliente()).contains(textoBuscado)
                    || venta.getNombreProducto().toLowerCase().contains(textoBuscado)
                    || venta.getFecha().toString().contains(textoBuscado)
                    || String.valueOf(venta.getCantidad()).contains(textoBuscado)) {
                listaVentasFiltrada.add(venta);
            }
        }

        // Actualizar la tabla con los resultados de la búsqueda
        tabla_ID.setItems(listaVentasFiltrada);
    }
}


