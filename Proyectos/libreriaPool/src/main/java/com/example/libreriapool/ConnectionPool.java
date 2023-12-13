package com.example.libreriapool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConnectionPool {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/libros";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";
    private static final int MAX_CONNECTIONS = 10;

    private List<Connection> availableConnections = new ArrayList<>();
    private List<Connection> busyConnections = new ArrayList<>();

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Error loading JDBC driver", e);
        }
    }

    public ConnectionPool() {
        // Inicializar el pool de conexiones
        for (int i = 0; i < MAX_CONNECTIONS; i++) {
            availableConnections.add(createConnection());
        }
    }

    private Connection createConnection() {
        try {
            return DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException("Error creating database connection", e);
        }
    }

    public synchronized Connection getConnection() throws SQLException {
        if (!availableConnections.isEmpty()) {
            Connection connection = availableConnections.remove(0);
            busyConnections.add(connection);
            return connection;
        } else if (busyConnections.size() < MAX_CONNECTIONS) {
            Connection newConnection = createConnection();
            busyConnections.add(newConnection);
            return newConnection;
        } else {
            throw new SQLException("No available connections in the pool");
        }
    }


    public synchronized void releaseConnection(Connection connection) {
        if (busyConnections.remove(connection)) {
            availableConnections.add(connection);
        } else {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace(); // Manejo de la excepción al cerrar la conexión
            }
            throw new IllegalArgumentException("Connection not in the busy pool");
        }
    }

    public synchronized void closeConnections() {
        for (Connection connection : availableConnections) {
            try {
                connection.close();
            } catch (SQLException e) {
                // Log or handle the exception
            }
        }
        for (Connection connection : busyConnections) {
            try {
                connection.close();
            } catch (SQLException e) {
                // Log or handle the exception
            }
        }
    }
}
