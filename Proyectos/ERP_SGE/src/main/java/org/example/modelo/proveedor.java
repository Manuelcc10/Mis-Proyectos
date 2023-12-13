package org.example.modelo;


import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class proveedor {
    private final SimpleIntegerProperty idProveedor;
    private final SimpleStringProperty nombre;
    private final SimpleStringProperty direccion;
    private final SimpleStringProperty contacto;

    public proveedor(int idProveedor, String nombre, String direccion, String contacto) {
        this.idProveedor = new SimpleIntegerProperty(idProveedor);
        this.nombre = new SimpleStringProperty(nombre);
        this.direccion = new SimpleStringProperty(direccion);
        this.contacto = new SimpleStringProperty(contacto);
    }

    // Métodos getter
    public int getIdProveedor() {
        return idProveedor.get();
    }

    public String getNombre() {
        return nombre.get();
    }

    public String getDireccion() {
        return direccion.get();
    }

    public String getContacto() {
        return contacto.get();
    }

    // Propiedades getter
    public SimpleIntegerProperty idProveedorProperty() {
        return idProveedor;
    }

    public SimpleStringProperty nombreProperty() {
        return nombre;
    }

    public SimpleStringProperty direccionProperty() {
        return direccion;
    }

    public SimpleStringProperty contactoProperty() {
        return contacto;
    }
}

