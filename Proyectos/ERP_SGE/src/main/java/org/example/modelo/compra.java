package org.example.modelo;

import javafx.beans.property.*;

import java.time.LocalDate;

public class compra {
    private IntegerProperty idCompra;
    private IntegerProperty idCliente;
    private StringProperty nombreProducto;
    private final ObjectProperty<java.sql.Date> fecha;
    private IntegerProperty cantidad;

    public compra(int idCompra, int idCliente, String nombreProducto, java.sql.Date fecha, int cantidad) {
        this.idCompra = new SimpleIntegerProperty(idCompra);
        this.idCliente = new SimpleIntegerProperty(idCliente);
        this.nombreProducto = new SimpleStringProperty(nombreProducto);
        this.fecha = new SimpleObjectProperty<>(fecha);
        this.cantidad = new SimpleIntegerProperty(cantidad);
    }

    public int getIdCompra() {
        return idCompra.get();
    }

    public IntegerProperty idCompraProperty() {
        return idCompra;
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
