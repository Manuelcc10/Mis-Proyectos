package com.example.libreriapool.Modelo;

import javafx.beans.property.*;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Libro {

    private final IntegerProperty idLibro = new SimpleIntegerProperty();
    private final StringProperty titulo = new SimpleStringProperty();
    private final StringProperty autor = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> anioPublicacion = new SimpleObjectProperty<>();
    private final IntegerProperty cantidadDisponible = new SimpleIntegerProperty();

    // Constructor y métodos getter y setter

    public Libro() {
    }

    public Libro(int idLibro, String titulo, String autor, LocalDate anioPublicacion, int cantidadDisponible) {
        this.idLibro.set(idLibro);
        this.titulo.set(titulo);
        this.autor.set(autor);
        this.anioPublicacion.set(anioPublicacion);
        this.cantidadDisponible.set(cantidadDisponible);
    }

    public int getIdLibro() {
        return idLibro.get();
    }

    public IntegerProperty idLibroProperty() {
        return idLibro;
    }

    public void setIdLibro(int idLibro) {
        this.idLibro.set(idLibro);
    }

    public String getTitulo() {
        return titulo.get();
    }

    public StringProperty tituloProperty() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo.set(titulo);
    }

    public String getAutor() {
        return autor.get();
    }

    public StringProperty autorProperty() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor.set(autor);
    }

    public LocalDate getAnioPublicacion() {
        return anioPublicacion.get();
    }

    public ObjectProperty<LocalDate> anioPublicacionProperty() {
        return anioPublicacion;
    }

    public void setAnioPublicacion(LocalDate anioPublicacion) {
        this.anioPublicacion.set(anioPublicacion);
    }

    // Método específico para obtener una propiedad de cadena formateada del año
    public StringProperty anioPublicacionStringProperty() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
        String formattedDate = getAnioPublicacion().format(formatter);
        return new SimpleStringProperty(formattedDate);
    }

    public int getCantidadDisponible() {
        return cantidadDisponible.get();
    }

    public IntegerProperty cantidadDisponibleProperty() {
        return cantidadDisponible;
    }

    public void setCantidadDisponible(int cantidadDisponible) {
        this.cantidadDisponible.set(cantidadDisponible);
    }
}
