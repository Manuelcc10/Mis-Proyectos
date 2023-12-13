package org.example.modelo;

import javafx.beans.property.*;

public class producto {
    private final StringProperty nombreProducto;
    private final StringProperty descripcion;
    private final DoubleProperty precio;
    private final IntegerProperty cantidadEnStock;
    private final IntegerProperty idProveedor;

    public producto(String nombreProducto, String descripcion, double precio, int cantidadEnStock, int idProveedor) {
        this.nombreProducto = new SimpleStringProperty(nombreProducto);
        this.descripcion = new SimpleStringProperty(descripcion);
        this.precio = new SimpleDoubleProperty(precio);
        this.cantidadEnStock = new SimpleIntegerProperty(cantidadEnStock);
        this.idProveedor = new SimpleIntegerProperty(idProveedor);
    }

    // Métodos Getter para propiedades

    public String getNombreProducto() {
        return nombreProducto.get();
    }

    public StringProperty nombreProductoProperty() {
        return nombreProducto;
    }

    public String getDescripcion() {
        return descripcion.get();
    }

    public StringProperty descripcionProperty() {
        return descripcion;
    }

    public double getPrecio() {
        return precio.get();
    }

    public DoubleProperty precioProperty() {
        return precio;
    }

    public int getCantidadEnStock() {
        return cantidadEnStock.get();
    }

    public IntegerProperty cantidadEnStockProperty() {
        return cantidadEnStock;
    }

    public int getIdProveedor() {
        return idProveedor.get();
    }

    public IntegerProperty idProveedorProperty() {
        return idProveedor;
    }
}