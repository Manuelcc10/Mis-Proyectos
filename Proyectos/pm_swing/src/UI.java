import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class UI extends JFrame {
    private JPanel panelMain, estancia, colectivo, propuesta, suBillete;
    private JLabel dias, viajes, nViaj;
    private JSpinner ndias;
    private JSlider slViajes;
    private JButton aceptar, cancelar, cambiarModo; // Botón para cambiar el modo claro/oscuro
    private int dias_selec = 1;
    private int viajes_selec = 1;
    private String colect_selec;
    private Boolean flag = false;
    private JRadioButton sDescuento, jubilado, discapacitado, parado, estudiante;
    private ButtonGroup tipoBonos;
    private final JLabel imgfin = new JLabel();
    private static boolean modoOscuro = true;

    private final ActionListener ti = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            reiniciar();
        }
    };
    private final Timer tiempo = new Timer(7000, ti);
    private GridLayout gridLayout = new GridLayout();
    private JTextArea objetoFinal = new JTextArea();


    public UI() {
        IniciarVentana();
    }

    public void IniciarVentana() {
        setTitle("Aplicación movilidad");
        setLocation(500, 150);
        Font fuenteGrande = new Font("SansSerif", Font.PLAIN, 18);
        Font fuenteNegrita = new Font("SansSerif", Font.BOLD, 18);
        setUIFont(fuenteGrande);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ImageIcon icono = new ImageIcon(getClass().getClassLoader().getResource("img.png"));
        setIconImage(icono.getImage());

        panelMain = new JPanel(new GridLayout(0, 2, 20, 20));
        panelMain.setBorder(new EmptyBorder(7, 7, 7, 7));
        add(panelMain);

        Estancia();
        Colectivo();

        // Inicializa suBillete como un nuevo JPanel
        suBillete = new JPanel();

        pack();
        setVisible(true);
        estancia.setFont(fuenteNegrita);
        colectivo.setFont(fuenteNegrita);
        suBillete.setFont(fuenteNegrita); // Ahora puedes establecer la fuente para suBillete
    }



    public void setUIFont(Font font) {
        UIManager.put("Button.font", font);
        UIManager.put("Label.font", font);
        UIManager.put("TextField.font", font);
        UIManager.put("TextArea.font", font);
        UIManager.put("Spinner.font", font);
        UIManager.put("Slider.font", font);
        UIManager.put("RadioButton.font", font);

        for (Map.Entry<Object, Object> entry : UIManager.getLookAndFeelDefaults().entrySet()) {
            if (entry.getValue() instanceof Font) {
                UIManager.put(entry.getKey(), font);
            }
        }
    }

    public void tiempo() {
        tiempo.restart();
    }

    public void Estancia() {
        estancia = new JPanel(new GridLayout(4, 1, 0, 0)); // GridLayout con 4 filas y 1 columna
        estancia.setBorder(BorderFactory.createTitledBorder("Estancia"));

        // Fila 1: Días y JSpinner
        JPanel paneldias = new JPanel();
        dias = new JLabel("Días");

        ndias = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        ndias.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                dias_selec = (int) ndias.getValue();
                if (flag) {
                    CalculadorMovilidad c = new CalculadorMovilidad(dias_selec, viajes_selec, colect_selec);
                    ArrayList<Double> arr = c.calculaPreciosViaje();
                    objetoFinal.setText(c.mejorOpcion(arr));
                }
            }
        });

        paneldias.add(dias);
        paneldias.add(ndias);
        estancia.add(paneldias);

        // Fila 2: Barra de Viajes
        slViajes = new JSlider(JSlider.HORIZONTAL, 0, 100, 1);
        slViajes.setMinorTickSpacing(5);
        slViajes.setMajorTickSpacing(20);
        slViajes.setPaintTicks(true);
        slViajes.setPaintLabels(true);
        slViajes.setLabelTable(slViajes.createStandardLabels(20));
        estancia.add(slViajes);

        slViajes.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                viajes_selec = slViajes.getValue();
                nViaj.setText(String.valueOf(viajes_selec));
                if (flag) {
                    CalculadorMovilidad c = new CalculadorMovilidad(dias_selec, viajes_selec, colect_selec);
                    ArrayList<Double> arr = c.calculaPreciosViaje();
                    objetoFinal.setText(c.mejorOpcion(arr));
                }
            }
        });

        slViajes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int mouseX = e.getX();
                int width = slViajes.getWidth();
                int range = slViajes.getMaximum() - slViajes.getMinimum();
                double valuePerPixel = (double) range / (double) width;
                int newValue = (int) Math.round(mouseX * valuePerPixel) + slViajes.getMinimum();
                slViajes.setValue(newValue);
            }
        });

        // Fila 3: Etiqueta "Viajes" y número de viajes seleccionados
        JPanel panelviajes = new JPanel();
        viajes = new JLabel("Viajes");
        nViaj = new JLabel("1");
        panelviajes.add(viajes);
        panelviajes.add(nViaj);
        estancia.add(panelviajes);

        // Fila 4: Botón de la bombilla
        ImageIcon iconoModo = new ImageIcon(getClass().getClassLoader().getResource("bombilla.png"));
        Dimension dimensionBot = new Dimension(40, 40);
        Image bombillaImage = iconoModo.getImage().getScaledInstance(
                dimensionBot.width, dimensionBot.height, Image.SCALE_SMOOTH
        );
        iconoModo = new ImageIcon(bombillaImage);

        cambiarModo = new JButton(iconoModo);
        cambiarModo.setPreferredSize(dimensionBot);

// Establece un margen vacío para reducir el tamaño vertical del botón
        cambiarModo.setMargin(new Insets(5, 5, 5, 5));


        cambiarModo.setContentAreaFilled(false);
        cambiarModo.setToolTipText(modoOscuro ? "Tema: Oscuro o Claro" : "");

        cambiarModo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Cambiar el modo claro/oscuro
                modoOscuro = !modoOscuro;

                // Actualizar el Look and Feel
                actualizarLookAndFeel();

                // Reiniciar la interfaz
                reiniciar();
            }
        });
        estancia.add(cambiarModo);


        panelMain.add(estancia);
    }

    public void Colectivo() {
        colectivo = new JPanel(new BorderLayout()); // Usar BorderLayout para el panel Colectivo
        colectivo.setBorder(BorderFactory.createTitledBorder("Colectivo"));

        // Crear un JLabel para mostrar la imagen y cambiar su tamaño
        ImageIcon imagenIcon = new ImageIcon(getClass().getClassLoader().getResource("img.png"));
        Image imagen = imagenIcon.getImage();
        int nuevoAncho = 60; // Cambia el ancho deseado
        int nuevoAlto = 60; // Cambia el alto deseado
        Image imagenRedimensionada = imagen.getScaledInstance(nuevoAncho, nuevoAlto, Image.SCALE_SMOOTH);
        imagenIcon = new ImageIcon(imagenRedimensionada);
        JLabel imagenLabel = new JLabel(imagenIcon);

        // Agregar el icono en la esquina superior derecha (región norte)
        colectivo.add(imagenLabel, BorderLayout.NORTH);

        String[] bonos = {"Sin Descuento", "Jubilado", "Discapacitado", "Parado", "Estudiante"};
        JRadioButton[] radioButtons = new JRadioButton[bonos.length];
        tipoBonos = new ButtonGroup();
        JPanel radioPanel = new JPanel(new GridLayout(bonos.length, 1));

        for (int i = 0; i < bonos.length; i++) {
            radioButtons[i] = new JRadioButton(bonos[i]);
            radioButtons[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    AbstractButton aButton = (AbstractButton) e.getSource();
                    switch (aButton.getText()) {
                        case "Sin Descuento" -> colect_selec = "1";
                        case "Jubilado" -> colect_selec = "2";
                        case "Discapacitado" -> colect_selec = "4";
                        case "Parado" -> colect_selec = "3";
                        case "Estudiante" -> colect_selec = "5";
                    }
                    if (!flag) {
                        gridLayout.setRows(2);
                        Propuesta();
                        SuBillete();
                        panelMain.repaint();
                        panelMain.revalidate();
                        pack();
                        flag = true;
                    }
                    CalculadorMovilidad c = new CalculadorMovilidad(dias_selec, viajes_selec, colect_selec);
                    ArrayList<Double> arr = c.calculaPreciosViaje();
                    objetoFinal.setText(c.mejorOpcion(arr));
                }
            });

            tipoBonos.add(radioButtons[i]);
            radioPanel.add(radioButtons[i]);
        }

        // Agregar los botones de radio en la región central
        colectivo.add(radioPanel, BorderLayout.CENTER);

        panelMain.add(colectivo);
    }

    public void Propuesta() {
        propuesta = new JPanel(new GridLayout(2, 1, 10, 20));
        Dimension dimensionBot = new Dimension(70, 70);
        Dimension dimensionIco = new Dimension(50, 50);

        propuesta.setBorder(BorderFactory.createTitledBorder("Propuesta"));

        objetoFinal.setBorder(BorderFactory.createLoweredBevelBorder());
        objetoFinal.setLineWrap(true);
        objetoFinal.setWrapStyleWord(true);
        objetoFinal.setEditable(false);
        Font tipoFuente = new Font(objetoFinal.getFont().getName(), Font.PLAIN, 20);
        objetoFinal.setFont(tipoFuente);
        propuesta.add(objetoFinal);

        JPanel botones = new JPanel();

        ImageIcon aceptarIcon = new ImageIcon(getClass().getClassLoader().getResource("aceptar.png"));
        Image aceptarImage = aceptarIcon.getImage().getScaledInstance(
                dimensionIco.width, dimensionIco.height, Image.SCALE_SMOOTH
        );
        aceptarIcon = new ImageIcon(aceptarImage);
        aceptar = new JButton(aceptarIcon);
        aceptar.setPreferredSize(dimensionBot);
        aceptar.setContentAreaFilled(false);
        aceptar.setToolTipText(modoOscuro ? "Aceptar" : "Aceptar");


        aceptar.addActionListener(e -> {
            CalculadorMovilidad c = new CalculadorMovilidad(dias_selec, viajes_selec, colect_selec);
            ArrayList<Double> arr = c.calculaPreciosViaje();
            objetoFinal.setText(c.mejorOpcion(arr));
            ImageIcon ico = null;
            Image img;
            Image img2;
            switch (colect_selec) {
                case "1" -> {
                    ico = new ImageIcon(getClass().getClassLoader().getResource("sindesc.png"));
                    img = ico.getImage();
                    img2 = img.getScaledInstance(110, 110, Image.SCALE_SMOOTH);
                    ico.setImage(img2);
                }
                case "2" -> {
                    ico = new ImageIcon(getClass().getClassLoader().getResource("jubil.png"));
                    img = ico.getImage();
                    img2 = img.getScaledInstance(110, 110, Image.SCALE_SMOOTH);
                    ico.setImage(img2);
                }
                case "4" -> {
                    ico = new ImageIcon(getClass().getClassLoader().getResource("disc.png"));
                    img = ico.getImage();
                    img2 = img.getScaledInstance(110, 110, Image.SCALE_SMOOTH);
                    ico.setImage(img2);
                }
                case "3" -> {
                    ico = new ImageIcon(getClass().getClassLoader().getResource("par.png"));
                    img = ico.getImage();
                    img2 = img.getScaledInstance(110, 110, Image.SCALE_SMOOTH);
                    ico.setImage(img2);
                }
                case "5" -> {
                    ico = new ImageIcon(getClass().getClassLoader().getResource("estu.png"));
                    img = ico.getImage();
                    img2 = img.getScaledInstance(110, 110, Image.SCALE_SMOOTH);
                    ico.setImage(img2);
                }
            }
            imgfin.setIcon(ico);
            suBillete.add(imgfin);
            suBillete.repaint();
            tiempo();
        });

        ImageIcon cancelarIcon = new ImageIcon(getClass().getClassLoader().getResource("rechazar.png"));
        Image cancelarImage = cancelarIcon.getImage().getScaledInstance(
                dimensionIco.width, dimensionIco.height, Image.SCALE_SMOOTH
        );
        cancelarIcon = new ImageIcon(cancelarImage);
        cancelar = new JButton(cancelarIcon);
        cancelar.setPreferredSize(dimensionBot);
        cancelar.setContentAreaFilled(false);
        cancelar.setToolTipText(modoOscuro ? "Cancelar" : "Cancelar");

        cancelar.addActionListener(e -> {
            reiniciar();
        });

        botones.add(aceptar);
        botones.add(cancelar);
        propuesta.add(botones);
        panelMain.add(propuesta, BorderLayout.CENTER);
    }

    public void SuBillete() {
        suBillete = new JPanel();
        suBillete.setBorder(BorderFactory.createTitledBorder("Su Billete"));
        suBillete.setVisible(true);
        panelMain.add(suBillete);

    }
    public void reiniciar() {
        ndias.setValue(1);
        slViajes.setValue(1);
        tipoBonos.clearSelection();
        panelMain.remove(propuesta);
        panelMain.remove(suBillete);
        panelMain.revalidate();
        panelMain.repaint();
        flag = false;
        gridLayout.setRows(1);
        pack();
        tiempo.stop();
    }
    private void actualizarLookAndFeel() {
        try {
            if (modoOscuro) {
                UIManager.setLookAndFeel(new FlatDarculaLaf());
            } else {
                UIManager.setLookAndFeel(new FlatLightLaf());
            }
            SwingUtilities.updateComponentTreeUI(this); // Actualizar la interfaz
            pack();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            if (modoOscuro) {
                UIManager.setLookAndFeel(new FlatDarculaLaf());
            } else {
                UIManager.setLookAndFeel(new FlatLightLaf());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new UI());
    }
}

