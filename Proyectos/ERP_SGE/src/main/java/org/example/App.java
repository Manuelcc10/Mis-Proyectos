package org.example;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class App {

    String url = "jdbc:mysql://localhost:3306/cuentas";
    String usuario = "root";
    String contrasena = "root";
    public void jasperPdf() {
        try {
// Cargar el informe Jasper compilado (.jasper) que generaste
            String rutaInformeCompilado = "C:\\Users\\mcast\\IdeaProjects\\ERP_SGE\\src\\main\\resources\\Informes\\archivo_compilado.jasper";
            JasperReport jasperReport = (JasperReport) JRLoader.loadObjectFromFile(rutaInformeCompilado);

// Configurar el origen de datos (utilizando un JRDataSource)
            JRDataSource dataSource = new JREmptyDataSource(); // O utiliza tu propio JRDataSource si tienes datos

// Obtener la conexión JDBC
            Connection conexion = DriverManager.getConnection(url, usuario, contrasena);

// Agregar parámetros, incluyendo la conexión JDBC
            Map<String, Object> parametros = new HashMap<>();


// Crear un objeto JasperPrint
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, conexion);

// Visualizar el informe utilizando JasperViewer
            JasperViewer.viewReport(jasperPrint, false);

// También puedes exportar el informe a diferentes formatos
// JasperExportManager.exportReportToPdfFile(jasperPrint, "ruta/de/salida/informe.pdf");

        } catch (JRException | SQLException e) {
            e.printStackTrace();
        }
    }


    public void CrearPdf() {
        try {
            String rutaJRXML = "C:\\Users\\mcast\\IdeaProjects\\ERP_SGE\\src\\main\\resources\\Informes\\erp.jrxml";
// Establecer conexión a la base de datos
            JasperPrint jasperPrint = cogerElReporte(rutaJRXML);


// Visualizar el informe (en este ejemplo, lo exportamos a un archivo PDF)
            JasperExportManager.exportReportToPdfFile(jasperPrint, "C:\\Users\\mcast\\IdeaProjects\\ERP_SGE\\src\\main\\resources\\Informes\\ERP.pdf");

            System.out.println("Informe generado exitosamente.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private JasperPrint cogerElReporte(String rutaJRXML) throws SQLException, JRException, FileNotFoundException {

        Connection conexion = DriverManager.getConnection(url, usuario, contrasena);


// Compilar el archivo JRXML y guardar el archivo compilado (.jasper)
        JasperReport jasperReport = JasperCompileManager.compileReport(new FileInputStream(rutaJRXML));

        String rutaJasper = "C:\\Users\\mcast\\IdeaProjects\\ERP_SGE\\src\\main\\resources\\Informes\\archivo_compilado.jasper";
        JasperCompileManager.compileReportToFile(rutaJRXML, rutaJasper);

// Puedes agregar parámetros al informe si es necesario
        Map<String, Object> parametros = new HashMap<>();
// parametros.put("nombreParametro", valorParametro);

// Llenar el informe con datos desde la base de datos
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, conexion);

        jasperPdf();

        return jasperPrint;
    }
}