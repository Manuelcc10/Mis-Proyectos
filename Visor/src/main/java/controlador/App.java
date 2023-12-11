package controlador;

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
            String rutaInformeCompilado = "src/main/resources";
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
            String rutaJRXML = "src/main/resources/vista/informes/Informe_pdf.jrxml";
// Establecer conexión a la base de datos
            JasperPrint jasperPrint = cogerElReporte(rutaJRXML);


// Visualizar el informe (en este ejemplo, lo exportamos a un archivo PDF)
            JasperExportManager.exportReportToPdfFile(jasperPrint, "Informe.pdf");

            System.out.println("Informe generado exitosamente.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void CrearHtml() {
        try {
            String rutaJRXML = "src/main/java/com/practicag4/ddi_2practica_g4/Controller/ReporteMejorado.jrxml";
// Establecer conexión a la base de datos
            JasperPrint jasperPrint = cogerElReporte(rutaJRXML);

// Exportar el informe a un archivo HTML
            JasperExportManager.exportReportToHtmlFile(jasperPrint, "Informe.html");

            System.out.println("Informe HTML generado exitosamente.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JasperPrint cogerElReporte(String rutaJRXML) throws SQLException, JRException, FileNotFoundException {

        Connection conexion = DriverManager.getConnection(url, usuario, contrasena);


// Compilar el archivo JRXML y guardar el archivo compilado (.jasper)
        JasperReport jasperReport = JasperCompileManager.compileReport(new FileInputStream(rutaJRXML));

        String rutaJasper = "src/main/resources";
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
