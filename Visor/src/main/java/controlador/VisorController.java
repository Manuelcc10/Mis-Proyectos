package controlador;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import modelo.Cuenta;


import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;

import java.util.*;
import java.sql.*;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;
import net.sf.jasperreports.engine.JRException;


public class VisorController {

    @FXML
    private TextField txtTitular;

    @FXML
    private TextField txtNum;

    @FXML
    private Button btnSig;

    @FXML
    private Button btnAnterior;

    @FXML
    private Button btnNuevo;

    @FXML
    private TextField txtSaldo;

    @FXML
    private DatePicker txtFecha;
    private int indiceActual;

    private List<Cuenta> cuentas;

    @FXML
    private Pane lblTitulo;

    @FXML
    private Pane tituloinicial;

    @FXML
    private Button btnAceptar;

    @FXML
    private Button btnCancelar;

    @FXML
    private Button primer_report;

    @FXML
    private Button segund_report;

    private Connection connection;

    @FXML
    private TextField txtnacion;

    @FXML
    private Button idbutton_borrar;

    @FXML
    private Button buttonMod_Id;

    @FXML
    private Button buton_cancelarMod;

    @FXML
    private Button but_AceptarMod;


    private DecimalFormat formatoSaldo = new DecimalFormat("0.00\u20AC");






    public VisorController() {
        try {
            configurarConexion();
            cargarCuentasDesdeDB();
        } catch (SQLException e) {
            e.printStackTrace();
            // Manejar la excepción aquí, por ejemplo, mostrar un mensaje de error.
            return;  // Termina la inicialización si hay un error en la conexión.
        }

        if (cuentas == null) {
            cuentas = new ArrayList<>();
        }

        // La siguiente lógica solo se ejecutará si la tabla está vacía
        if (cuentas.isEmpty()) {
            cuentas.add(new Cuenta("1", "Juan", LocalDate.parse("2023-10-20"), 1000.00, "Mexicana"));
            cuentas.add(new Cuenta("2", "María", LocalDate.parse("2022-09-12"), 1500.00, "Española"));
            cuentas.add(new Cuenta("3", "Pedro", LocalDate.parse("2021-07-04"), 800.00, "Argentina"));
            cuentas.add(new Cuenta("4", "Ana", LocalDate.parse("2023-06-01"), 2000.00, "Colombiana"));
            cuentas.add(new Cuenta("5", "Luis", LocalDate.parse("2024-02-08"), 3000.00, "Chilena"));

            // Insertar datos desde el ArrayList a la base de datos
            for (Cuenta cuenta : cuentas) {
                insertarCuentaEnDB(cuenta);
            }
        }
    }


    private void configurarConexion() throws SQLException {
        // Reemplaza "jdbc:mysql://localhost:3306/cuentas_db" con la URL de tu base de datos MySQL
        String url = "jdbc:mysql://localhost:3306/cuentas";
        String usuario = "root";
        String contraseña = "root";
        connection = DriverManager.getConnection(url, usuario, contraseña);
    }

    private void insertarCuentaEnDB(Cuenta cuenta) {
        String insertQuery = "INSERT INTO cuenta (numero_cuenta, titular, fecha_apertura, saldo, nacionalidad) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
            insertStatement.setString(1, cuenta.getNumeroCuenta());
            insertStatement.setString(2, cuenta.getTitular());
            insertStatement.setDate(3, java.sql.Date.valueOf(cuenta.getFechaApertura()));
            insertStatement.setDouble(4, cuenta.getSaldo());
            insertStatement.setString(5, cuenta.getNacionalidad());

            // Ejecutar la inserción
            insertStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            // Manejar la excepción aquí, por ejemplo, mostrar un mensaje de error.
        }
    }
    private void cargarCuentasDesdeDB() {
        try {
            // Consulta para verificar si la tabla está vacía
            String checkQuery = "SELECT COUNT(*) FROM cuenta";
            try (Statement checkStatement = connection.createStatement();
                 ResultSet checkResultSet = checkStatement.executeQuery(checkQuery)) {

                checkResultSet.next();
                int rowCount = checkResultSet.getInt(1);

                // Cargar datos desde la base de datos solo si la tabla está vacía
                if (rowCount == 0) {
                    cargarDatosIniciales();
                } else {
                    cargarDatosDesdeDB();
                }
            }
            cargarDatosDesdeDB();
        } catch (SQLException e) {
            e.printStackTrace();
            // Manejar la excepción aquí, por ejemplo, mostrar un mensaje de error.
        }
    }
    private void cargarDatosIniciales() {
        // Insertar datos desde el ArrayList a la base de datos
        if (cuentas == null) {
            cuentas = new ArrayList<>(); // o la clase concreta que estés utilizando
        }
        for (Cuenta cuenta : cuentas) {
            insertarCuentaEnDB(cuenta);
        }

        // Ahora puedes cargar los datos desde la base de datos
        cargarDatosDesdeDB();
    }

    private void cargarDatosDesdeDB() {
        try {
            // Consulta para obtener todos los datos de la tabla
            String query = "SELECT * FROM cuenta";
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {

                cuentas = new ArrayList<>(); // Inicializar la lista antes de cargar desde la base de datos

                while (resultSet.next()) {
                    String numeroCuenta = resultSet.getString("numero_cuenta");
                    String titular = resultSet.getString("titular");
                    LocalDate fechaApertura = resultSet.getDate("fecha_apertura").toLocalDate();
                    double saldo = resultSet.getDouble("saldo");
                    String nacionalidad = resultSet.getString("nacionalidad");

                    // Agregar la cuenta a la lista
                    cuentas.add(new Cuenta(numeroCuenta, titular, fechaApertura, saldo, nacionalidad));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Manejar la excepción aquí, por ejemplo, mostrar un mensaje de error.
        }
    }

    @FXML
    private void initialize() {
        mostrarCuenta();
        tituloinicial.setVisible(true);
        colocarimg();
        actualizarBotones();


        txtNum.textProperty().addListener((observable, oldValue, newValue) -> quitarResaltadoError(txtNum));
        txtTitular.textProperty().addListener((observable, oldValue, newValue) -> quitarResaltadoError(txtTitular));
        txtNum.setTextFormatter(formatoNumerico());

        txtSaldo.textProperty().addListener((observable, oldValue, newValue) -> quitarResaltadoError(txtSaldo));
        txtnacion.textProperty().addListener((observable, oldValue, newValue) -> quitarResaltadoError(txtnacion));
        buton_cancelarMod.setVisible(false);
        but_AceptarMod.setVisible(false);

        formatoSaldo = new DecimalFormat("0.00\u20AC");

    }

    @FXML
    private void Action_anterior() {
        if (indiceActual > 0) {
            indiceActual--;
            mostrarCuenta();
            actualizarBotones();
        }
    }

    @FXML
    private void Action_siguiente() {
        if (indiceActual < cuentas.size() - 1) {
            indiceActual++;
            mostrarCuenta();
            actualizarBotones();
        }
    }

    @FXML
    private void Action_aceptar() {
        // Obtener valores de los campos de texto
        String numeroCuenta = txtNum.getText();
        String titular = txtTitular.getText();
        LocalDate fechaApertura = txtFecha.getValue();
        double saldo;
        String nacionalidad = txtnacion.getText();;
        try {
            saldo = Double.parseDouble(txtSaldo.getText());
        } catch (NumberFormatException e) {
            saldo = -1;  // Valor no válido, se puede cambiar a otro valor por defecto
        }

        boolean camposValidos = validarCampos(numeroCuenta, titular, fechaApertura, saldo);

        if (!camposValidos) {
            mostrarMensajeError("Por favor, complete todos los campos correctamente.");
            return;
        }

        // Comprobar si el número de cuenta ya existe en la base de datos
        if (existeNumeroCuentaEnDB(numeroCuenta)) {
            resaltarErrorCampo(txtNum);
            mostrarMensajeError("El número de cuenta ya existe.");
            return;
        }

        // Insertar la nueva cuenta en la base de datos
        insertarCuentaEnDB(numeroCuenta, titular, fechaApertura, saldo, nacionalidad);

        // Volver a cargar las cuentas desde la base de datos
        cargarCuentasDesdeDB();

        // Actualizar la interfaz con la nueva cuenta
        indiceActual = cuentas.size() - 1;
        mostrarCuenta();
        numeroCuentas();

        // Restaurar la visibilidad de los botones
        btnNuevo.setVisible(true);
        btnAnterior.setVisible(true);
        btnAceptar.setVisible(false);
        btnCancelar.setVisible(false);
        tituloinicial.setVisible(true);
        lblTitulo.setVisible(false);
        btnNuevo.setDisable(false);
        buttonMod_Id.setDisable(false);
        idbutton_borrar.setDisable(false);

        txtNum.setTooltip(null);
        txtTitular.setTooltip(null);
        txtFecha.setTooltip(null);
        txtSaldo.setTooltip(null);

        txtNum.setEditable(true);
        txtTitular.setEditable(true);
        txtFecha.setEditable(true);
        txtSaldo.setEditable(true);
    }

    // Método para validar los campos
    private boolean validarCampos(String numeroCuenta, String titular, LocalDate fechaApertura, double saldo) {
        boolean camposValidos = true;

        if (numeroCuenta.isEmpty() || !validarNum(numeroCuenta)) {
            resaltarErrorCampo(txtNum);
            camposValidos = false;
        } else {
            quitarResaltadoError(txtNum);
        }

        if (titular.isEmpty() || !validarTit(titular)) {
            resaltarErrorCampo(txtTitular);
            camposValidos = false;
        } else {
            quitarResaltadoError(txtTitular);
        }

        if (fechaApertura == null) {
            resaltarErrorCampo(txtFecha.getEditor());
            camposValidos = false;
        } else {
            quitarResaltadoError(txtFecha.getEditor());
        }

        if (saldo <= 0) {
            resaltarErrorCampo(txtSaldo);
            camposValidos = false;
        } else {
            quitarResaltadoError(txtSaldo);
        }

        return camposValidos;
    }

    // Método para comprobar si el número de cuenta ya existe en la base de datos
    private boolean existeNumeroCuentaEnDB(String numeroCuenta) {
        // Consulta para verificar si el número de cuenta ya existe
        String query = "SELECT COUNT(*) FROM cuenta WHERE numero_cuenta = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, numeroCuenta);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                int rowCount = resultSet.getInt(1);
                return rowCount > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Manejar la excepción aquí, por ejemplo, mostrar un mensaje de error.
            return false;
        }
    }

    // Método para insertar una nueva cuenta en la base de datos
    private void insertarCuentaEnDB(String numeroCuenta, String titular, LocalDate fechaApertura, double saldo, String nacionalidad) {
        String insertQuery = "INSERT INTO cuenta (numero_cuenta, titular, fecha_apertura, saldo, nacionalidad) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
            insertStatement.setString(1, numeroCuenta);
            insertStatement.setString(2, titular);
            insertStatement.setDate(3, java.sql.Date.valueOf(fechaApertura));
            insertStatement.setDouble(4, saldo);
            insertStatement.setString(5, nacionalidad);

            // Ejecutar la inserción
            insertStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            // Manejar la excepción aquí, por ejemplo, mostrar un mensaje de error.
        }
    }
    private void mostrarMensajeError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void Action_cancelar() {
        quitarResaltadoError(txtNum);
        quitarResaltadoError(txtTitular);
        quitarResaltadoError(txtFecha.getEditor());
        quitarResaltadoError(txtSaldo);

        btnNuevo.setVisible(true);
        btnAnterior.setVisible(true);
        btnAceptar.setVisible(false);
        btnCancelar.setVisible(false);
        btnSig.setVisible(true);
        btnNuevo.setDisable(false);
        buttonMod_Id.setDisable(false);
        idbutton_borrar.setDisable(false);

        txtNum.setTooltip(null);
        txtTitular.setTooltip(null);
        txtFecha.setTooltip(null);
        txtSaldo.setTooltip(null);

        txtNum.setEditable(true);
        txtTitular.setEditable(true);
        txtFecha.setEditable(true);
        txtSaldo.setEditable(true);

        lblTitulo.setVisible(false);
        tituloinicial.setVisible(true);

        // Mostrar la última cuenta en la interfaz
        mostrarCuenta();
    }

    @FXML
    private void Action_nuevo() {
        lblTitulo.setVisible(true); // Muestra el nuevo título
        btnNuevo.setVisible(false);
        btnAceptar.setVisible(true);
        btnCancelar.setVisible(true);
        tituloinicial.setVisible(false);
        btnAnterior.setVisible(false);
        btnSig.setVisible(false);
        btnNuevo.setDisable(true);
        buttonMod_Id.setDisable(true);
        idbutton_borrar.setDisable(true);

        txtNum.setTooltip(new Tooltip("Introduzca número\nEnter number\nEntrez un numéro\nDigite um número"));
        txtTitular.setTooltip(new Tooltip("Introduzca titular\nEnter account holder\nEntrez le titulaire\nDigite o titular"));
        txtFecha.setTooltip(new Tooltip("Introduzca fecha\nEnter date\nEntrez la date\nDigite a data"));
        txtSaldo.setTooltip(new Tooltip("Introduzca saldo\nEnter balance\nEntrez le solde\nDigite o saldo"));

        txtNum.setEditable(true);  // Hacer el campo editable
        txtTitular.setEditable(true);  // Hacer el campo editable
        txtFecha.setEditable(true); // Hacer el DatePicker editable

        txtSaldo.setEditable(true);
        txtnacion.setEditable(true);// Hacer el campo editable
        // Limpia todos los campos de texto
        txtNum.clear();
        txtTitular.clear();
        txtFecha.setValue(null);
        txtSaldo.clear();
        txtnacion.clear();

        LocalDate fechaActual = LocalDate.now();
        txtFecha.setValue(fechaActual);
        numeroCuentas();
    }

    @FXML
    private void reportuno_action() {
        App app = new App();
        app.CrearPdf();

    }


    @FXML
    private void reportdos_action() {

    }

    public void mostrarCuenta() {
        System.out.println("Mostrando cuenta...");

        // Asegúrate de que cuentas no esté vacío y el índiceActual sea válido
        if (!cuentas.isEmpty() && indiceActual >= 0 && indiceActual < cuentas.size()) {
            Cuenta cuentaActual = cuentas.get(indiceActual);

            System.out.println("Número de cuenta: " + cuentaActual.getNumeroCuenta());
            System.out.println("Titular: " + cuentaActual.getTitular());
            System.out.println("Fecha de apertura: " + cuentaActual.getFechaApertura());
            System.out.println("Saldo: " + cuentaActual.getSaldo());
            System.out.println("Nacionalidad: " + cuentaActual.getNacionalidad());

            // Actualizar la interfaz con los valores de la cuenta actual
            txtNum.setText(cuentaActual.getNumeroCuenta());
            txtTitular.setText(cuentaActual.getTitular());
            txtFecha.setValue(cuentaActual.getFechaApertura());

            // Verificar si formatoSaldo es nulo antes de formatear y mostrar el saldo
            if (formatoSaldo != null) {
                txtSaldo.setText(formatoSaldo.format(cuentaActual.getSaldo()));
            } else {
                txtSaldo.setText(String.valueOf(cuentaActual.getSaldo()));
            }

            txtnacion.setText(cuentaActual.getNacionalidad());

        } else {
            System.out.println("Lista de cuentas vacía o índiceActual no válido.");
        }
    }
    public void actualizarBotones() {
        btnAnterior.setVisible(indiceActual != 0);

        btnSig.setVisible(indiceActual != cuentas.size() - 1);
    }

    private void colocarimg() {
        btnAnterior.setTooltip(new Tooltip("Anterior\nPrevious\nPrécédente\nAnterior"));
        Image imgant = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/vista/img/flechaAnterior.png")));
        ImageView imgviewant = new ImageView(imgant);
        imgviewant.setFitWidth(45);
        imgviewant.setFitHeight(45);
        btnAnterior.setGraphic(imgviewant);

        btnSig.setTooltip(new Tooltip("Siguiente\nNext\nSuivant\nSeguinte"));
        Image imgsig = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/vista/img/flechaSiguiente.png")));
        ImageView imgviewsig = new ImageView(imgsig);
        imgviewsig.setFitWidth(45);
        imgviewsig.setFitHeight(45);
        btnSig.setGraphic(imgviewsig);

        btnNuevo.setTooltip(new Tooltip("Añadir Cuenta\nAdd Account\nAjouter un compte\nAdicionar Conta"));
        Image imgnuv = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/vista/img/nuevo.png")));
        ImageView imgviewnuv = new ImageView(imgnuv);
        imgviewnuv.setFitWidth(45);
        imgviewnuv.setFitHeight(45);
        btnNuevo.setGraphic(imgviewnuv);

        btnCancelar.setTooltip(new Tooltip("Cancelar\nCancel\nAnnuler\nCancelar"));
        Image imgcanc = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/vista/img/cancelar.png")));
        ImageView imgviewcan = new ImageView(imgcanc);
        imgviewcan.setFitWidth(45);
        imgviewcan.setFitHeight(45);
        btnCancelar.setGraphic(imgviewcan);

        btnAceptar.setTooltip(new Tooltip("Aceptar\nAccept\nAccepter\nAceitar"));
        Image imgacep = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/vista/img/aceptar.png")));
        ImageView imgviewacep = new ImageView(imgacep);
        imgviewacep.setFitWidth(45);
        imgviewacep.setFitHeight(45);
        btnAceptar.setGraphic(imgviewacep);

        primer_report.setTooltip(new Tooltip("REPORTE SIMPLE"));
        Image imgarep = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/vista/img/primero.png")));
        ImageView imgviewarep = new ImageView(imgarep);
        imgviewarep.setFitWidth(45);
        imgviewarep.setFitHeight(45);
        primer_report.setGraphic(imgviewarep);

        segund_report.setTooltip(new Tooltip("REPORTE COMPLETADO"));
        Image imgarep2 = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/vista/img/html.png")));
        ImageView imgviewarep2 = new ImageView(imgarep2);
        imgviewarep2.setFitWidth(45);
        imgviewarep2.setFitHeight(45);
        segund_report.setGraphic(imgviewarep2);

        idbutton_borrar.setTooltip(new Tooltip("BORRAR"));
        Image imgbor = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/vista/img/borrar.png")));
        ImageView imgbor2 = new ImageView(imgbor);
        imgbor2.setFitWidth(45);
        imgbor2.setFitHeight(45);
        idbutton_borrar.setGraphic(imgbor2);

        buttonMod_Id.setTooltip(new Tooltip("MODIFICAR"));
        Image imgmod = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/vista/img/editar.png")));
        ImageView imgmod2 = new ImageView(imgmod);
        imgmod2.setFitWidth(45);
        imgmod2.setFitHeight(45);
        buttonMod_Id.setGraphic(imgmod2);

        buton_cancelarMod.setTooltip(new Tooltip("CANCELAR MODIFICACIÓN"));
        Image imgmodcan = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/vista/img/cancelar.png")));
        ImageView imgmodcan2 = new ImageView(imgmodcan);
        imgmodcan2.setFitWidth(45);
        imgmodcan2.setFitHeight(45);
        buton_cancelarMod.setGraphic(imgmodcan2);

        but_AceptarMod.setTooltip(new Tooltip("CANCELAR MODIFICACIÓN"));
        Image imgmodacep = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/vista/img/aceptar.png")));
        ImageView imgmodacep2 = new ImageView(imgmodacep);
        imgmodacep2.setFitWidth(45);
        imgmodacep2.setFitHeight(45);
        but_AceptarMod.setGraphic(imgmodacep2);

    }

    @FXML
    private void action_borrar() {
        if (cuentas.isEmpty()) {
            mostrarMensajeError("No hay cuentas para borrar.");
            return;
        }

        // Obtener el número de cuenta en la posición actual
        String numeroCuenta = cuentas.get(indiceActual).getNumeroCuenta();

        // Confirmar con el usuario antes de borrar
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación de Borrado");
        alert.setHeaderText(null);
        alert.setContentText("¿Está seguro de que desea borrar la cuenta con número " + numeroCuenta + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Borrar la cuenta de la base de datos
            borrarCuentaEnDB(numeroCuenta);

            // Borrar la cuenta del ArrayList
            cuentas.remove(indiceActual);

            // Actualizar la interfaz
            if (cuentas.isEmpty()) {
                // Si no hay más cuentas, mostrar un mensaje y limpiar la interfaz
                mostrarMensajeError("No hay más cuentas.");
                limpiarInterfaz();
            } else {
                // Si hay más cuentas, actualizar la interfaz con la siguiente cuenta
                if (indiceActual >= cuentas.size()) {
                    // Si hemos borrado la última cuenta, mover el índice al último elemento restante
                    indiceActual = cuentas.size() - 1;
                }
                mostrarCuenta();
                numeroCuentas();
                actualizarBotones();
            }
        }
    }

    private void borrarCuentaEnDB(String numeroCuenta) {
        try {
            // Crear la consulta de borrado
            String deleteQuery = "DELETE FROM cuenta WHERE numero_cuenta = ?";

            // Preparar la declaración con la conexión
            try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
                // Establecer el parámetro del número de cuenta
                deleteStatement.setString(1, numeroCuenta);

                // Ejecutar la consulta de borrado
                deleteStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Manejar la excepción aquí, por ejemplo, mostrar un mensaje de error.
        }
    }
    @FXML
    private void action_Modif() {
        if (cuentas.isEmpty()) {
            mostrarMensajeError("No hay cuentas para modificar.");
            return;
        }

        // Obtener la cuenta actual
        Cuenta cuentaActual = cuentas.get(indiceActual);

        // Mostrar una alerta de confirmación
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación de Modificación");
        alert.setHeaderText(null);
        alert.setContentText("¿Está seguro de que desea modificar la cuenta?");

        // Personalizar los botones de la alerta
        ButtonType botonAceptar = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
        ButtonType botonCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(botonAceptar, botonCancelar);

        // Obtener la respuesta del usuario
        alert.showAndWait().ifPresent(response -> {
            if (response == botonAceptar) {
                // El usuario hizo clic en "Aceptar", realizar la modificación
                modificarCuenta(cuentaActual);
                // Puedes agregar más lógica aquí si es necesario
                // ...
            } else {
                // El usuario hizo clic en "Cancelar" o cerró la alerta
                // Revertir los cambios y salir del modo de modificación
                cancelarModificacion();
            }

        });
        if (txtNum.getText().isEmpty() || txtTitular.getText().isEmpty() || txtFecha.getValue() == null || txtSaldo.getText().isEmpty() || txtnacion.getText().isEmpty()) {
            // Muestra una alerta indicando que se deben ingresar datos antes de modificar
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Advertencia");
            alerta.setHeaderText(null);
            alerta.setContentText("Debes ingresar datos antes de modificar.");
            alerta.showAndWait();
            return;
        }
        txtNum.setEditable(false);

        idbutton_borrar.setDisable(true);

    }

    private void modificarCuenta(Cuenta cuenta) {
        // Habilitar la edición de campos
        txtNum.setEditable(true);
        txtTitular.setEditable(true);
        txtFecha.setEditable(true);
        txtSaldo.setEditable(true);
        txtnacion.setEditable(true);

        // Mostrar los botones de aceptar y cancelar
        buton_cancelarMod.setVisible(true);
        but_AceptarMod.setVisible(true);

        // Ocultar otros botones
        btnNuevo.setVisible(false);
        btnAnterior.setVisible(false);
        btnSig.setVisible(false);

        // Resaltar los campos que se pueden modificar
        resaltarCampoEditable(txtTitular);
        resaltarCampoEditable(txtFecha.getEditor());
        resaltarCampoEditable(txtSaldo);
        resaltarCampoEditable(txtnacion);
    }

    @FXML
    private void AceptarMod_Act() {
        // Obtener la cuenta actual
        Cuenta cuentaActual = cuentas.get(indiceActual);

        // Obtener los nuevos valores de los campos de texto
        String nuevoTitular = txtTitular.getText();
        LocalDate nuevaFecha = txtFecha.getValue();
        double nuevoSaldo;
        String nuevaNacionalidad = txtnacion.getText();

        try {
            nuevoSaldo = Double.parseDouble(txtSaldo.getText());
        } catch (NumberFormatException e) {
            nuevoSaldo = -1;  // Valor no válido, se puede cambiar a otro valor por defecto
        }

        // Validar los nuevos campos
        // Dentro de AceptarMod_Act()
        System.out.println("Nuevo Titular: " + nuevoTitular);


        // Guardar los cambios en la base de datos
        modificarCuentaEnDB(cuentaActual, nuevoTitular, nuevaFecha, nuevoSaldo, nuevaNacionalidad);



        // Ocultar los botones de aceptar y cancelar
        buton_cancelarMod.setVisible(false);
        but_AceptarMod.setVisible(false);

        // Mostrar otros botones
        btnNuevo.setVisible(true);
        btnAnterior.setVisible(true);
        btnSig.setVisible(true);
        idbutton_borrar.setDisable(false);

        // Actualizar la interfaz con la cuenta modificada

    }
    private void modificarCuentaEnDB(Cuenta cuenta, String nuevoTitular, LocalDate nuevaFecha, double nuevoSaldo, String nuevaNacionalidad) {
        try {
            // Crear la consulta de actualización
            String updateQuery = "UPDATE cuenta SET titular = ?, fecha_apertura = ?, saldo = ?, nacionalidad = ? WHERE numero_cuenta = ?";

            // Preparar la declaración con la conexión
            try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                // Establecer los parámetros de la actualización
                updateStatement.setString(1, nuevoTitular);
                updateStatement.setDate(2, java.sql.Date.valueOf(nuevaFecha));
                updateStatement.setDouble(3, nuevoSaldo);
                updateStatement.setString(4, nuevaNacionalidad);
                updateStatement.setString(5, cuenta.getNumeroCuenta());

                // Ejecutar la consulta de actualización
                updateStatement.executeUpdate();
                cuenta.setTitular(nuevoTitular);
                cuenta.setFechaApertura(nuevaFecha);
                cuenta.setSaldo(nuevoSaldo);
                cuenta.setNacionalidad(nuevaNacionalidad);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Manejar la excepción aquí, por ejemplo, mostrar un mensaje de error.
        }

    }

    @FXML
    private void cancelarMod_Act() {
        cancelarModificacion();

    }

    private void cancelarModificacion() {


        // Ocultar los botones de aceptar y cancelar
        buton_cancelarMod.setVisible(false);
        but_AceptarMod.setVisible(false);

        // Mostrar otros botones
        btnNuevo.setVisible(true);
        btnAnterior.setVisible(true);
        btnSig.setVisible(true);
        idbutton_borrar.setDisable(false);

        // Actualizar la interfaz sin aplicar los cambios
        mostrarCuenta();
    }



    private void resaltarCampoEditable(Node campo) {
        campo.setStyle("-fx-border-color: blue;");
    }

    private void limpiarInterfaz() {
        // Lógica para limpiar la interfaz cuando no hay más cuentas
        // Puedes ajustar esto según tus necesidades
        txtNum.clear();
        txtTitular.clear();
        txtFecha.setValue(null);
        txtSaldo.clear();
        txtnacion.clear();
    }

    private boolean validarNum(String num) {
        // Patrón para verificar que la cadena contiene solo dígitos
        String pattern = "^[0-9]+$";
        return num.matches(pattern);
    }

    private boolean validarTit(String titular) {
        // Patrón para verificar que la cadena contiene solo dígitos
        String pattern = "^[a-zA-Z]+( [A-Z][a-zA-Z]+)*$";
        return titular.matches(pattern);
    }


    private TextFormatter<String> formatoNumerico() {
        return new TextFormatter<>(change -> {
            String nuevoText = change.getControlNewText();
            if (nuevoText.matches("([0-9]*\\.?[0-9]*)?")) {
                return change;
            } else {
                return null;
            }
        });
    }

    // Método para resaltar un campo con error
    private void resaltarErrorCampo(Node campo) {
        campo.setStyle("-fx-border-color: red;");
    }

    // Método para quitar el resaltado de error de un campo
    private void quitarResaltadoError(Node campo) {
        campo.setStyle(""); // Restaura el estilo predeterminado
    }


    public void numeroCuentas() {
        int cont = 0;

        System.out.println("El número de cuentas es de: " + cuentas.size());

        for (Cuenta cuenta : cuentas) {
            if (cuenta.getSaldo() >= 50000.0) {
                System.out.println("Número de cuenta: " + cuenta.getNumeroCuenta() +
                        ", Titular: " + cuenta.getTitular() +
                        ", Saldo: " + cuenta.getSaldo());
                cont++;
            }
        }

        if (cont == 0) {
            System.out.println("No hay cuentas con saldo igual o superior a 50,000.");
        } else {
            System.out.println("Total de cuentas con saldo igual o superior a 50,000: " + cont);
        }
    }
    public void modentered() {
        // Crear una transición de escala
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), buttonMod_Id);

        // Establecer los valores de escala inicial y final
        scaleTransition.setFromX(1.0);
        scaleTransition.setFromY(1.0);
        scaleTransition.setToX(1.2);
        scaleTransition.setToY(1.2);

        // Reproducir la transición
        scaleTransition.play();

    }
    public void nuevoentered() {
        // Crear una transición de escala
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), btnNuevo);

        // Establecer los valores de escala inicial y final
        scaleTransition.setFromX(1.0);
        scaleTransition.setFromY(1.0);
        scaleTransition.setToX(1.2);
        scaleTransition.setToY(1.2);

        // Reproducir la transición
        scaleTransition.play();

    }
    public void borrentered() {
        // Crear una transición de escala
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), idbutton_borrar);

        // Establecer los valores de escala inicial y final
        scaleTransition.setFromX(1.0);
        scaleTransition.setFromY(1.0);
        scaleTransition.setToX(1.2);
        scaleTransition.setToY(1.2);

        // Reproducir la transición
        scaleTransition.play();

    }

    public void reportbasentered() {
        // Crear una transición de escala
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), primer_report);

        // Establecer los valores de escala inicial y final
        scaleTransition.setFromX(1.0);
        scaleTransition.setFromY(1.0);
        scaleTransition.setToX(1.2);
        scaleTransition.setToY(1.2);

        // Reproducir la transición
        scaleTransition.play();

    }

    public void reporthtmlentered() {
        // Crear una transición de escala
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), segund_report);

        // Establecer los valores de escala inicial y final
        scaleTransition.setFromX(1.0);
        scaleTransition.setFromY(1.0);
        scaleTransition.setToX(1.2);
        scaleTransition.setToY(1.2);

        // Reproducir la transición
        scaleTransition.play();

    }

}