package modelo;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;

public class Cuenta {
    private String numeroCuenta;
    private String titular;
    private LocalDate fechaApertura;
    private double saldo;
    public String nacionalidad;
    private static final DecimalFormat formatoSaldo = new DecimalFormat("0.00");



    public Cuenta(String numeroCuenta, String titular, LocalDate fechaApertura, double saldo, String nacionalidad) {
        this.numeroCuenta = numeroCuenta;
        this.titular = titular;
        this.fechaApertura = fechaApertura;
        this.saldo = saldo;
        this.nacionalidad = nacionalidad;

    }

    public String getSaldoFormateado() {
        return formatoSaldo.format(saldo);
    }

    // Getters y setters para los atributos (acceso controlado)
    public String getNumeroCuenta() {
        return numeroCuenta;
    }
    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    public void setFechaApertura(LocalDate fechaApertura) {
        this.fechaApertura = fechaApertura;
    }

    public void setSaldo(Double saldo) {
        this.saldo = saldo;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }
    public String getTitular() {
        return titular;
    }

    public LocalDate getFechaApertura() {
        return fechaApertura;
    }

    public double getSaldo() {
        return saldo;
    }
    public class RegistroCuentas {
        private ArrayList<Cuenta> cuentas;

        public RegistroCuentas() {
            cuentas = new ArrayList<>();
        }

        // Agregar una cuenta al registro
        public void agregarCuenta(Cuenta cuenta) {
            cuentas.add(cuenta);
        }

        // Obtener una cuenta por su número de cuenta
        public Cuenta obtenerCuenta(String numeroCuenta) {
            for (Cuenta cuenta : cuentas) {
                if (cuenta.getNumeroCuenta().equals(numeroCuenta)) {
                    return cuenta;
                }
            }
            return null; // Devolver null si no se encuentra la cuenta
        }
    }

}


