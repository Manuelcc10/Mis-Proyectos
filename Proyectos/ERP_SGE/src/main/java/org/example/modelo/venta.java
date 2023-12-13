package org.example.modelo;

import javafx.beans.property.*;

public class venta {
    private final IntegerProperty idVenta;
    private final IntegerProperty idCliente;
    private final StringProperty nombreProducto;
    private final ObjectProperty<java.sql.Date> fecha;
    private final IntegerProperty cantidad;

    public venta(int idVenta, int idCliente, String nombreProducto, java.sql.Date fecha, int cantidad) {
        this.idVenta = new SimpleIntegerProperty(idVenta);
        this.idCliente = new SimpleIntegerProperty(idCliente);
        this.nombreProducto = new SimpleStringProperty(nombreProducto);
        this.fecha = new SimpleObjectProperty<>(fecha);
        this.cantidad = new SimpleIntegerProperty(cantidad);
    }

    // Métodos Getter para propiedades

    public int getIdVenta() {
        return idVenta.get();
    }

    public IntegerProperty idVentaProperty() {
        return idVenta;
    }

    public int getIdCliente() {
        return idCliente.get();
    }

    public IntegerProperty idClienteProperty() {
        return idCliente;
    }

    public String getNombreProducto() {
        return nombreProducto.get();
    }

    public StringProperty nombreProductoProperty() {
        return nombreProducto;
    }

    public java.sql.Date getFecha() {
        return fecha.get();
    }

    public ObjectProperty<java.sql.Date> fechaProperty() {
        return fecha;
    }

    public int getCantidad() {
        return cantidad.get();
    }

    public IntegerProperty cantidadProperty() {
        return cantidad;
    }
}