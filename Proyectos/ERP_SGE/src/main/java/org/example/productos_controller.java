package org.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.modelo.producto;


import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class productos_controller {

    private ObservableList<producto> listaProductos = FXCollections.observableArrayList();

    public TextField txt_Nombre_Prod;
    public TextField txt_prec;
    public TextField txt_Stock;
    public TextField txt_Descr;
    @FXML
    private TableView<producto> tabla_ID;

    @FXML
    private TableColumn<producto, String> colum_NomProd;

    @FXML
    private TableColumn<producto, String> colum_Des;

    @FXML
    private TableColumn<producto, Double> colum_Prec;

    @FXML
    private TableColumn<producto, Integer> colum_Stock;

    @FXML
    private TableColumn<producto, Integer> colum_IdProv;

    @FXML
    private ChoiceBox<Integer> prov_choose_Id;

    @FXML
    private Button act_id_entrar;

    @FXML
    private Button id_mod;

    @FXML
    private Button id_borrar;

    @FXML
    private TextField busca;

    public void initialize() {
        // Configurar el TextFormatter para aceptar solo dígitos en el campo de producto ID
        UnaryOperator<TextFormatter.Change> filter = change -> {
            if (Pattern.matches("\\d*", change.getControlNewText())) {
                return change;
            }
            return null;
        };
        TextFormatter<String> textFormatter = new TextFormatter<>(filter);

        // Configurar las columnas de la TableView
        tabla_ID.setItems(listaProductos);
        colum_NomProd.setCellValueFactory(cellData -> cellData.getValue().nombreProductoProperty());
        colum_Des.setCellValueFactory(cellData -> cellData.getValue().descripcionProperty());
        colum_Prec.setCellValueFactory(cellData -> cellData.getValue().precioProperty().asObject());
        colum_Stock.setCellValueFactory(cellData -> cellData.getValue().cantidadEnStockProperty().asObject());
        colum_IdProv.setCellValueFactory(cellData -> cellData.getValue().idProveedorProperty().asObject());

        cargarDatosDesdeBD();
        cargarProveedores();

        // Configurar el listener para la selección de la tabla
        tabla_ID.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // Manejar el evento de selección
            if (newValue != null) {
                // Establecer los datos seleccionados en los TextField
                txt_Nombre_Prod.setText(newValue.getNombreProducto());
                txt_Descr.setText(newValue.getDescripcion());
                txt_prec.setText(String.valueOf(newValue.getPrecio()));
                txt_Stock.setText(String.valueOf(newValue.getCantidadEnStock()));
                // Establecer el proveedor seleccionado en el ChoiceBox
                prov_choose_Id.setValue(newValue.getIdProveedor());
            }
        });
        configurarBoton("/img/insertar.png", act_id_entrar, "Insertar");
        configurarBoton("/img/modificar.png", id_mod, "Modificar");
        configurarBoton("/img/basura.png", id_borrar, "Borrar");

        busca.textProperty().addListener((observable, oldValue, newValue) -> buscarProductos());
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

    public void button_entrar_act() {
        // Verificar campos y mostrar advertencias si es necesario
        if (!validarCampos()) {
            return;
        }

        // Insertar un nuevo producto en la base de datos
        insertarNuevoProducto();

        // Actualizar la tabla en la interfaz gráfica
        listaProductos.add(new producto(
                // Ajustar según tus necesidades
                txt_Nombre_Prod.getText(),
                txt_Descr.getText(),
                Double.parseDouble(txt_prec.getText()),
                Integer.parseInt(txt_Stock.getText()),
                prov_choose_Id.getValue()
        ));

        // Limpiar los TextField
        txt_Nombre_Prod.clear();
        txt_prec.clear();
        txt_Stock.clear();
        txt_Descr.clear();
    }

    private void insertarNuevoProducto() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = Conex_bd.obtenerConexion();
            String sql = "INSERT INTO Productos (Nombre_producto, Descripcion, Precio, Cantidad_en_stock, Id_proveedor) VALUES (?, ?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, txt_Nombre_Prod.getText());
            preparedStatement.setString(2, txt_Descr.getText());
            preparedStatement.setDouble(3, Double.parseDouble(txt_prec.getText()));
            preparedStatement.setInt(4, Integer.parseInt(txt_Stock.getText()));
            preparedStatement.setInt(5, prov_choose_Id.getValue());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
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
    }



    private void cargarDatosDesdeBD() {
        // Ajustar según tu esquema de base de datos y las columnas necesarias
        // Esta es solo una implementación de ejemplo
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = Conex_bd.obtenerConexion();
            String sql = "SELECT Nombre_producto, Descripcion, Precio, Cantidad_en_stock, Id_proveedor FROM Productos";
            preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String Nombre_producto = resultSet.getString("Nombre_producto");
                String Descripcion = resultSet.getString("Descripcion");
                double Precio = resultSet.getDouble("Precio");
                int Cantidad_en_stock = resultSet.getInt("Cantidad_en_stock");
                int Id_proveedor = resultSet.getInt("id_proveedor");

                listaProductos.add(new producto(Nombre_producto, Descripcion, Precio, Cantidad_en_stock, Id_proveedor));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
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
    }

    private void cargarProveedores() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = Conex_bd.obtenerConexion();
            String sql = "SELECT Id_proveedor FROM Proveedores";
            preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            ObservableList<Integer> proveedores = FXCollections.observableArrayList();

            while (resultSet.next()) {
                int idProveedor = resultSet.getInt("id_proveedor");
                proveedores.add(idProveedor);
            }

            prov_choose_Id.setItems(proveedores);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
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
    }

    public void act_Mod() {

  modificarProducto();
        if (!validarCampos()) {
            return;
        }
    }
    private void modificarProducto() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = Conex_bd.obtenerConexion();
            String sql = "UPDATE Productos SET Nombre_producto = ?, Descripcion = ?, Precio = ?, Cantidad_en_stock = ?, Id_proveedor = ? WHERE Nombre_producto = ?";
            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, txt_Nombre_Prod.getText());
            preparedStatement.setString(2, txt_Descr.getText());
            preparedStatement.setDouble(3, Double.parseDouble(txt_prec.getText()));
            preparedStatement.setInt(4, Integer.parseInt(txt_Stock.getText()));
            preparedStatement.setInt(5, prov_choose_Id.getValue());
            preparedStatement.setString(6, tabla_ID.getSelectionModel().getSelectedItem().getNombreProducto());

            preparedStatement.executeUpdate();
            actualizarTabla();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cerrarRecursos(connection, preparedStatement);
        }

        limpiarCampos();
    }

    @FXML
    public void Action_borrar() {
        eliminarProducto();
    }

    private void eliminarProducto() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = Conex_bd.obtenerConexion();
            String sql = "DELETE FROM Productos WHERE Nombre_producto = ?";
            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, tabla_ID.getSelectionModel().getSelectedItem().getNombreProducto());

            preparedStatement.executeUpdate();
            actualizarTabla();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cerrarRecursos(connection, preparedStatement);
        }

        limpiarCampos();
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
    private void limpiarCampos() {
        // Limpiar los TextField
        txt_Nombre_Prod.clear();
        txt_Descr.clear();
        txt_prec.clear();
        txt_Stock.clear();
        prov_choose_Id.setValue(null);
    }
    private void actualizarTabla() {
        // Actualiza la tabla con los datos de la base de datos
        listaProductos.clear();
        cargarDatosDesdeBD();
        tabla_ID.setItems(listaProductos);
    }
    private boolean validarCampos() {
        // Validar el nombre del producto
        if (txt_Nombre_Prod.getText().isEmpty() || txt_Nombre_Prod.getText().length() > 30) {
            mostrarAdvertencia("Nombre del producto no válido. Debe tener entre 1 y 30 caracteres.");
            return false;
        }

        // Validar la descripción
        if (txt_Descr.getText().length() > 50) {
            mostrarAdvertencia("Descripción no válida. Debe tener menos de 50 caracteres.");
            return false;
        }

        // Validar el formato del precio


        // Validar el stock (solo números)
        if (!validarStock(txt_Stock.getText())) {
            mostrarAdvertencia("Stock no válido. Ingrese solo números.");
            return false;
        }

        // Agregar más validaciones según sea necesario

        return true;
    }



    private boolean validarStock(String stock) {
        // Verificar si es un número entero positivo
        return Pattern.matches("\\d+", stock);
    }

    private void mostrarAdvertencia(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advertencia");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    public void buscador(javafx.scene.input.KeyEvent keyEvent) {
    }
    private void buscarProductos() {
        String textoBuscado = busca.getText().toLowerCase();
        ObservableList<producto> listaProductosFiltrada = FXCollections.observableArrayList();

        // Realizar búsqueda en la lista de productos
        for (producto producto : listaProductos) {
            if (producto.getNombreProducto().toLowerCase().contains(textoBuscado)
                    || producto.getDescripcion().toLowerCase().contains(textoBuscado)
                    || String.valueOf(producto.getPrecio()).contains(textoBuscado)
                    || String.valueOf(producto.getCantidadEnStock()).contains(textoBuscado)
                    || String.valueOf(producto.getIdProveedor()).contains(textoBuscado)) {
                listaProductosFiltrada.add(producto);
            }
        }

        // Actualizar la tabla con los resultados de la búsqueda
        tabla_ID.setItems(listaProductosFiltrada);
    }
}