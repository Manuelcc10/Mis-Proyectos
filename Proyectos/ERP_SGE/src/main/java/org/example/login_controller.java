package org.example;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.example.modelo.cliente;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class login_controller  {

    private ObservableList<cliente> listaClientes = FXCollections.observableArrayList();

    private ObservableList<cliente> listaClientesFiltrada = FXCollections.observableArrayList();


    public TextField text_usur;
    public PasswordField txt_contra;
    public TextField txt_Nombre;
    public TextField txt_IdCliente;
    public TextField txt_direccion;
    public TextField txt_Contacto;
    public TextField txt_DNI;
    @FXML
    private TableView<cliente> tabla_ID;

    @FXML
    private TableColumn<cliente, Integer> colum_clienteID;

    @FXML
    private TableColumn<cliente, String> colum_DNI;

    @FXML
    private TableColumn<cliente, String> colum_Nom;

    @FXML
    private TableColumn<cliente, String> colum_Direc;

    @FXML
    private TableColumn<cliente, String> colum_Contac;

    @FXML
    private Button act_id_entrar;

    @FXML
    private Button id_mod;

    @FXML
    private Button id_borrar;

    @FXML
    private TextField busca;




    public void initialize() {
        // Configurar el TextFormatter para aceptar solo dígitos en el campo de cliente ID
        UnaryOperator<TextFormatter.Change> filter = change -> {
            if (Pattern.matches("\\d*", change.getControlNewText())) {
                return change;
            }
            return null;
        };
        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        txt_IdCliente.setTextFormatter(textFormatter);

        // Configurar las columnas de la TableView
        tabla_ID.setItems(listaClientes);
        // Asociar las propiedades de Cliente con las columnas correspondientes
        colum_clienteID.setCellValueFactory(cellData -> cellData.getValue().idClienteProperty().asObject());
        colum_DNI.setCellValueFactory(cellData -> cellData.getValue().dniProperty());
        colum_Nom.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        colum_Direc.setCellValueFactory(cellData -> cellData.getValue().direccionProperty());
        colum_Contac.setCellValueFactory(cellData -> cellData.getValue().contactosProperty());
        cargarDatosDesdeBD();
        tabla_ID.setItems(listaClientes);
        // Asociar las propiedades de Cliente con las columnas correspondientes
        colum_clienteID.setCellValueFactory(cellData -> cellData.getValue().idClienteProperty().asObject());
        colum_DNI.setCellValueFactory(cellData -> cellData.getValue().dniProperty());
        colum_Nom.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        colum_Direc.setCellValueFactory(cellData -> cellData.getValue().direccionProperty());
        colum_Contac.setCellValueFactory(cellData -> cellData.getValue().contactosProperty());

        // Configurar el listener para la selección de la tabla
        tabla_ID.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<cliente>() {
            @Override
            public void changed(ObservableValue<? extends cliente> observable, cliente oldValue, cliente newValue) {
                // Manejar el evento de selección
                if (newValue != null) {
                    // Establecer los datos seleccionados en los TextField
                    txt_IdCliente.setText(String.valueOf(newValue.getIdCliente()));
                    txt_DNI.setText(newValue.getDni());
                    txt_Nombre.setText(newValue.getNombre());
                    txt_direccion.setText(newValue.getDireccion());
                    txt_Contacto.setText(newValue.getContactos());
                }
            }
        });
        configurarBoton("/img/insertar.png", act_id_entrar, "Insertar");
        configurarBoton("/img/modificar.png", id_mod, "Modificar");
        configurarBoton("/img/basura.png", id_borrar, "Borrar");

        busca.textProperty().addListener((observable, oldValue, newValue) -> buscarClientes());
    }
    private void buscarClientes() {
        String textoBuscado = busca.getText().toLowerCase();
        listaClientesFiltrada.clear();

        // Realizar búsqueda en la lista de clientes
        for (cliente cliente : listaClientes) {
            if (cliente.getNombre().toLowerCase().contains(textoBuscado)
                    || cliente.getDni().toLowerCase().contains(textoBuscado)
                    || cliente.getDireccion().toLowerCase().contains(textoBuscado)
                    || cliente.getContactos().toLowerCase().contains(textoBuscado)) {
                listaClientesFiltrada.add(cliente);
            }
        }

        // Actualizar la tabla con los resultados de la búsqueda
        tabla_ID.setItems(listaClientesFiltrada);
    }


    public void text_act_Usur() {
        // Aquí puedes agregar lógica para manejar eventos en el campo de usuario
    }

    public void txt_Act_Contra() {
        // Aquí puedes agregar lógica para manejar eventos en el campo de contraseña
    }

    public void button_entrar_act() {


        // Verificar campos y mostrar advertencias si es necesario
        if (!validarCampos()) {
            return;
        }

        // Insertar un nuevo cliente en la base de datos
        insertarNuevoCliente();

        // Actualizar la tabla en la interfaz gráfica
        listaClientes.add(new cliente(
                Integer.parseInt(txt_IdCliente.getText()),
                txt_DNI.getText(),
                txt_Nombre.getText(),
                txt_direccion.getText(),
                txt_Contacto.getText()
        ));
        // Limpiar los TextField
        txt_IdCliente.clear();
        txt_DNI.clear();
        txt_Nombre.clear();
        txt_direccion.clear();
        txt_Contacto.clear();
    }



        // Puedes realizar otras acciones después de insertar el cliente, como abrir la ventana principal


    // Método para insertar un nuevo cliente en la base de datos
    private void insertarNuevoCliente() {
        // Aquí deberías implementar la lógica para insertar un nuevo cliente en la base de datos
        // Puedes usar la conexión a la base de datos (conexionBD) y ejecutar consultas SQL

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = Conex_bd.obtenerConexion();
            String sql = "INSERT INTO Clientes (id_cliente, Dni, Nombre, Direccion, Contactos) VALUES (?, ?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(sql);

            // Puedes ajustar estos valores según tus necesidades o proporcionarlos desde la interfaz gráfica
            preparedStatement.setInt(1, Integer.parseInt(txt_IdCliente.getText())); // Cliente ID
            preparedStatement.setString(2, txt_DNI.getText());                   // Nombre
            preparedStatement.setString(3, txt_Nombre.getText());                // Direccion
            preparedStatement.setString(4, txt_direccion.getText());                 // Contacto
            preparedStatement.setString(5, txt_Contacto.getText());                       // DNI

              // Contraseña

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

    // Método para validar los campos antes de la inserción


    // Método para mostrar una advertencia
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
            String sql = "SELECT id_cliente, Dni, Nombre, Direccion, Contactos FROM Clientes";
            preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int idCliente = resultSet.getInt("id_cliente");
                String dni = resultSet.getString("Dni");
                String nombre = resultSet.getString("Nombre");
                String direccion = resultSet.getString("Direccion");
                String contactos = resultSet.getString("Contactos");

                listaClientes.add(new cliente(idCliente, dni, nombre, direccion, contactos));
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

public void act_Mod(){
Connection connection = null;
    PreparedStatement preparedStatement = null;

    try {
        connection = Conex_bd.obtenerConexion();
        String sql = "UPDATE Clientes SET Dni = ?, Nombre = ?, Direccion = ?, Contactos = ? WHERE id_cliente = ?";
        preparedStatement = connection.prepareStatement(sql);

        // Puedes ajustar estos valores según tus necesidades o proporcionarlos desde la interfaz gráfica
        preparedStatement.setString(1, txt_DNI.getText());                   // Nombre
        preparedStatement.setString(2, txt_Nombre.getText());                // Direccion
        preparedStatement.setString(3, txt_direccion.getText());                 // Contacto
        preparedStatement.setString(4, txt_Contacto.getText());                       // DNI
        preparedStatement.setInt(5, Integer.parseInt(txt_IdCliente.getText())); // Cliente ID

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
        if (!validarCampos()) {
            return;
        }

    }
    // Limpiar los TextField
    txt_IdCliente.clear();
    txt_DNI.clear();
    txt_Nombre.clear();
    txt_direccion.clear();
    txt_Contacto.clear();
}
    private void actualizarTabla() {
        listaClientes.clear(); // Limpiar la lista actual
        cargarDatosDesdeBD(); // Volver a cargar los datos desde la base de datos
        tabla_ID.setItems(listaClientes); // Actualizar la TableView
    }
    // Método para validar los campos antes de la inserción
    private boolean validarCampos() {
        if (txt_IdCliente.getText().isEmpty() || txt_Nombre.getText().isEmpty() || txt_direccion.getText().isEmpty() ||
                txt_Contacto.getText().isEmpty() || txt_DNI.getText().isEmpty()) {
            mostrarAdvertencia("Inserte todos los campos");
            return false;
        }

        // Validar DNI
        String dni = txt_DNI.getText();
        if (!Pattern.matches("\\d{8}[A-Z]", dni)) {
            mostrarAdvertencia("El DNI debe tener 8 números seguidos de una letra mayúscula.");
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
public void Action_borrar() {
    Connection connection = null;
    PreparedStatement preparedStatement = null;

    try {
        connection = Conex_bd.obtenerConexion();
        String sql = "DELETE FROM Clientes WHERE id_cliente = ?";
        preparedStatement = connection.prepareStatement(sql);

        // Puedes ajustar estos valores según tus necesidades o proporcionarlos desde la interfaz gráfica
        preparedStatement.setInt(1, Integer.parseInt(txt_IdCliente.getText())); // Cliente ID

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
    txt_IdCliente.clear();
    txt_DNI.clear();
    txt_Nombre.clear();
    txt_direccion.clear();
    txt_Contacto.clear();
}

    public void buscador(javafx.scene.input.KeyEvent keyEvent) {
    }
}
