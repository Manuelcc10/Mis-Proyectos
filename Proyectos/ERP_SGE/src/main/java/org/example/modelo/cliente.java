package org.example.modelo;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class cliente {
    private final SimpleIntegerProperty idCliente;
    private final SimpleStringProperty dni;
    private final SimpleStringProperty nombre;
    private final SimpleStringProperty direccion;
    private final SimpleStringProperty contactos;

    public cliente(int idCliente, String dni, String nombre, String direccion, String contactos) {
        this.idCliente = new SimpleIntegerProperty(idCliente);
        this.dni = new SimpleStringProperty(dni);
        this.nombre = new SimpleStringProperty(nombre);
        this.direccion = new SimpleStringProperty(direccion);
        this.contactos = new SimpleStringProperty(contactos);
    }

    public int getIdCliente() {
        return idCliente.get();
    }

    public SimpleIntegerProperty idClienteProperty() {
        return idCliente;
    }

    public String getDni() {
        return dni.get();
    }

    public SimpleStringProperty dniProperty() {
        return dni;
    }

    public String getNombre() {
        return nombre.get();
    }

    public SimpleStringProperty nombreProperty() {
        return nombre;
    }

    public String getDireccion() {
        return direccion.get();
    }

    public SimpleStringProperty direccionProperty() {
        return direccion;
    }

    public String getContactos() {
        return contactos.get();
    }

    public SimpleStringProperty contactosProperty() {
        return contactos;
    }
}
