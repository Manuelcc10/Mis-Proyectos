package org.example;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.modelo.proveedor;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class proveedor_controller {

    private ObservableList<proveedor> listaProveedores = FXCollections.observableArrayList();

    public TextField txt_IdProv;
    public TextField txt_Nombre;
    public TextField txt_direccion;
    public TextField txt_Contacto;
    @FXML
    private TableView<proveedor> tabla_ID;

    @FXML
    private TableColumn<proveedor, Integer> colum_prov;

    @FXML
    private TableColumn<proveedor, String> colum_Nom;

    @FXML
    private TableColumn<proveedor, String> colum_Direc;

    @FXML
    private TableColumn<proveedor, String> colum_Contac;

    @FXML
    private Button act_id_entrar;

    @FXML
    private Button id_mod;

    @FXML
    private Button id_borrar;

    @FXML
    private TextField busca;



    public void initialize() {
        // Configurar el TextFormatter para aceptar solo dígitos en el campo de proveedor ID
        UnaryOperator<TextFormatter.Change> filter = change -> {
            if (Pattern.matches("\\d*", change.getControlNewText())) {
                return change;
            }
            return null;
        };
        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        txt_IdProv.setTextFormatter(textFormatter);

        // Configurar las columnas de la TableView
        tabla_ID.setItems(listaProveedores);
        // Asociar las propiedades de Proveedor con las columnas correspondientes
        colum_prov.setCellValueFactory(cellData -> cellData.getValue().idProveedorProperty().asObject());
        colum_Nom.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        colum_Direc.setCellValueFactory(cellData -> cellData.getValue().direccionProperty());
        colum_Contac.setCellValueFactory(cellData -> cellData.getValue().contactoProperty());
        cargarDatosDesdeBD();
        tabla_ID.setItems(listaProveedores);

        // Configurar el listener para la selección de la tabla
        tabla_ID.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<proveedor>() {
            @Override
            public void changed(ObservableValue<? extends proveedor> observable, proveedor oldValue, proveedor newValue) {
                // Manejar el evento de selección
                if (newValue != null) {
                    // Establecer los datos seleccionados en los TextField
                    txt_IdProv.setText(String.valueOf(newValue.getIdProveedor()));
                    txt_Nombre.setText(newValue.getNombre());
                    txt_direccion.setText(newValue.getDireccion());
                    txt_Contacto.setText(newValue.getContacto());
                }
            }
        });
        configurarBoton("/img/insertar.png", act_id_entrar, "Insertar");
        configurarBoton("/img/modificar.png", id_mod, "Modificar");
        configurarBoton("/img/basura.png", id_borrar, "Borrar");

        busca.textProperty().addListener((observable, oldValue, newValue) -> buscarProveedores());
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

        // Insertar un nuevo proveedor en la base de datos
        insertarNuevoProveedor();

        // Actualizar la tabla en la interfaz gráfica
        listaProveedores.add(new proveedor(
                Integer.parseInt(txt_IdProv.getText()),
                txt_Nombre.getText(),
                txt_direccion.getText(),
                txt_Contacto.getText()
        ));

        // Limpiar los TextField
        txt_IdProv.clear();
        txt_Nombre.clear();
        txt_direccion.clear();
        txt_Contacto.clear();
    }

    // Método para validar los campos antes de la inserción
    private boolean validarCampos() {
// Validar ID Proveedor
        String idProveedor = txt_IdProv.getText();
        if (!validarIDProveedor(idProveedor)) {
            mostrarAdvertencia("El ID del proveedor debe ser un número entero positivo.");
            return false;
        }

        // Validar Nombre
        String nombre = txt_Nombre.getText();
        if (!Pattern.matches("[A-Za-z]+", nombre) || nombre.length() > 20) {
            mostrarAdvertencia("El nombre debe contener solo letras y tener un máximo de 20 caracteres.");
            return false;
        }

        // Validar Dirección
        String direccion = txt_direccion.getText();
        if (!direccion.startsWith("C/")) {
            mostrarAdvertencia("La dirección debe comenzar con 'C/'.");
            return false;
        }

        // Validar Contacto
        String contacto = txt_Contacto.getText();
        if (!Pattern.matches("\\d{9}", contacto)) {
            mostrarAdvertencia("El contacto debe tener 9 números.");
            return false;
        }

        return true;
    }
    private boolean validarIDProveedor(String idProveedor) {
        // Verificar si es un número entero positivo
        return Pattern.matches("\\d+", idProveedor);
    }

    private void insertarNuevoProveedor() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = Conex_bd.obtenerConexion();
            String sql = "INSERT INTO Proveedores (id_proveedor, Nombre, Direccion, Contacto) VALUES (?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(sql);

            // Puedes ajustar estos valores según tus necesidades o proporcionarlos desde la interfaz gráfica
            preparedStatement.setInt(1, Integer.parseInt(txt_IdProv.getText())); // Proveedor ID
            preparedStatement.setString(2, txt_Nombre.getText());                // Nombre
            preparedStatement.setString(3, txt_direccion.getText());             // Direccion
            preparedStatement.setString(4, txt_Contacto.getText());               // Contacto

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



    private void mostrarAdvertencia(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advertencia");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void cargarDatosDesdeBD() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = Conex_bd.obtenerConexion();
            String sql = "SELECT id_proveedor, Nombre, Direccion, Contacto FROM Proveedores";
            preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int idProveedor = resultSet.getInt("id_proveedor");
                String nombre = resultSet.getString("Nombre");
                String direccion = resultSet.getString("Direccion");
                String contacto = resultSet.getString("Contacto");

                listaProveedores.add(new proveedor(idProveedor, nombre, direccion, contacto));
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

    public void act_Mod() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = Conex_bd.obtenerConexion();
            String sql = "UPDATE Proveedores SET Nombre = ?, Direccion = ?, Contacto = ? WHERE id_proveedor = ?";
            preparedStatement = connection.prepareStatement(sql);

            // Puedes ajustar estos valores según tus necesidades o proporcionarlos desde la interfaz gráfica
            preparedStatement.setString(1, txt_Nombre.getText());                // Nombre
            preparedStatement.setString(2, txt_direccion.getText());             // Direccion
            preparedStatement.setString(3, txt_Contacto.getText());               // Contacto
            preparedStatement.setInt(4, Integer.parseInt(txt_IdProv.getText())); // Proveedor ID

            preparedStatement.executeUpdate();
            actualizarTabla();

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
        // Limpiar los TextField
        txt_IdProv.clear();
        txt_Nombre.clear();
        txt_direccion.clear();
        txt_Contacto.clear();
    }

    public void Action_borrar() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = Conex_bd.obtenerConexion();
            String sql = "DELETE FROM Proveedores WHERE id_proveedor = ?";
            preparedStatement = connection.prepareStatement(sql);

            // Puedes ajustar estos valores según tus necesidades o proporcionarlos desde la interfaz gráfica
            preparedStatement.setInt(1, Integer.parseInt(txt_IdProv.getText())); // Proveedor ID

            preparedStatement.executeUpdate();
            actualizarTabla();

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
        // Limpiar los TextField
        txt_IdProv.clear();
        txt_Nombre.clear();
        txt_direccion.clear();
        txt_Contacto.clear();
    }

    private void actualizarTabla() {
        listaProveedores.clear(); // Limpiar la lista actual
        cargarDatosDesdeBD(); // Volver a cargar los datos desde la base de datos
        tabla_ID.setItems(listaProveedores); // Actualizar la TableView
    }
    public void buscador(javafx.scene.input.KeyEvent keyEvent) {
    }
    private void buscarProveedores() {
        String textoBuscado = busca.getText().toLowerCase();
        ObservableList<proveedor> listaProveedoresFiltrada = FXCollections.observableArrayList();

        // Realizar búsqueda en la lista de proveedores
        for (proveedor proveedor : listaProveedores) {
            if (String.valueOf(proveedor.getIdProveedor()).contains(textoBuscado)
                    || proveedor.getNombre().toLowerCase().contains(textoBuscado)
                    || proveedor.getDireccion().toLowerCase().contains(textoBuscado)
                    || proveedor.getContacto().toLowerCase().contains(textoBuscado)) {
                listaProveedoresFiltrada.add(proveedor);
            }
        }

        // Actualizar la tabla con los resultados de la búsqueda
        tabla_ID.setItems(listaProveedoresFiltrada);
    }
}
