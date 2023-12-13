package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conex_bd {

    // Datos de conexión a la base de datos
    private static final String URL = "jdbc:mysql://localhost:3306/empresa_erp";
    private static final String USUARIO = "root";
    private static final String CONTRASENA = "root";

    // Método para obtener la conexión a la base de datos
    public static Connection obtenerConexion() {
        try {
            // Registrar el controlador JDBC
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Obtener la conexión
            return DriverManager.getConnection(URL, USUARIO, CONTRASENA);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al conectar a la base de datos", e);
        }
    }

    // Método para cerrar la conexión
    public static void cerrarConexion(Connection conexion) {
        if (conexion != null) {
            try {
                conexion.close();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException("Error al cerrar la conexión", e);
            }
        }
    }
}
