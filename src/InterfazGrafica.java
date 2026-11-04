import Intermedio.GeneradorNotaciones;
import Intermedio.GeneradorNotaciones.ExpresionInfo;
import Intermedio.OptimizadorCodigo;
import Lexico.AnalizadorLexico;
import Lexico.TipoToken;
import Lexico.Tokens;
import Sintactico.AnalizadorSintactico;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

public class InterfazGrafica extends JFrame {
    private JTextArea areaCodigo;
    private JTextArea areaNumerosLinea;
    private JTable tablaTokens;
    private DefaultTableModel modeloTablaTokens;
    private JTextArea areaPrefija;
    private JTextArea areaPostfija;
    private JTextArea areaCodigoOptimizado;
    private JTextArea areaBitacora;
    private JTextArea areaConsola;

    public InterfazGrafica() {
        super("Compilador Swift - Notaciones, Cuádruplos y Optimización");
        this.configurarVentana();
        this.inicializarComponentes();
    }

    private void configurarVentana() {
        this.setSize(1200, 750);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout(10, 10));
    }

    private void inicializarComponentes() {
        // --- 1. BARRA SUPERIOR DE ACCIONES ---
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        JButton btnCompilar = new JButton("▶ Compilar Original");
        btnCompilar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCompilar.setBackground(new Color(24, 43, 73));
        btnCompilar.setForeground(Color.WHITE);
        btnCompilar.setOpaque(true);
        btnCompilar.setBorderPainted(false);
        btnCompilar.setFocusPainted(false);
        btnCompilar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCompilar.addActionListener((e) -> this.ejecutarAnalisis(this.areaCodigo.getText(), false));

        JButton btnRecompilarOptimizado = new JButton("🔄 Re-compilar Código Optimizado");
        btnRecompilarOptimizado.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnRecompilarOptimizado.setBackground(new Color(39, 174, 96));
        btnRecompilarOptimizado.setForeground(Color.WHITE);
        btnRecompilarOptimizado.setOpaque(true);
        btnRecompilarOptimizado.setBorderPainted(false);
        btnRecompilarOptimizado.setFocusPainted(false);
        btnRecompilarOptimizado.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRecompilarOptimizado.addActionListener((e) -> this.recompilarOptimizado());

        JButton btnAbrir = new JButton("📂 Abrir .swift");
        btnAbrir.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnAbrir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAbrir.addActionListener((e) -> this.abrirArchivoSwift());

        JButton btnGuardar = new JButton("💾 Guardar .swift");
        btnGuardar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGuardar.addActionListener((e) -> this.guardarArchivoSwift());

        panelSuperior.add(btnCompilar);
        panelSuperior.add(btnRecompilarOptimizado);
        panelSuperior.add(btnAbrir);
        panelSuperior.add(btnGuardar);
        this.add(panelSuperior, BorderLayout.NORTH);

        // --- 2. EDITOR PRINCIPAL ---
        this.areaCodigo = new JTextArea();
        this.areaCodigo.setFont(new Font("Consolas", Font.PLAIN, 14));

        this.areaNumerosLinea = new JTextArea("1 ");
        this.areaNumerosLinea.setFont(new Font("Consolas", Font.PLAIN, 14));
        this.areaNumerosLinea.setBackground(new Color(235, 237, 239));
        this.areaNumerosLinea.setForeground(new Color(120, 144, 156));
        this.areaNumerosLinea.setEditable(false);
        this.areaNumerosLinea.setFocusable(false);
        this.areaNumerosLinea.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        this.areaCodigo.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { actualizarNumerosLinea(); }
            @Override
            public void removeUpdate(DocumentEvent e) { actualizarNumerosLinea(); }
            @Override
            public void changedUpdate(DocumentEvent e) { actualizarNumerosLinea(); }
        });

        JScrollPane scrollEditor = new JScrollPane(this.areaCodigo);
        scrollEditor.setRowHeaderView(this.areaNumerosLinea);
        scrollEditor.setBorder(BorderFactory.createTitledBorder("Código Fuente Swift"));

        this.areaCodigo.setText("""
            var g2: Double = 10.0
            var h2: Double = 5.0
            var a2: Double = 8.0
            var b2: Double = 3.0
            var c2: Double = 4.0

            if false {
                print("Este bloque es codigo muerto")
            }

            var x = ((g2 + h2) * 3.0) / ((a2 - b2) + (c2 * 2.0))

            var a: Int = 12
            var b: Int = 4
            var c: Int = 2
            var d: Int = 3

            var resultado = (a + b * c) / (d + 1)
            
            print("Operaciones completadas")
            return

            var z1: Double = 100.0
            print("Linea inalcanzable tras return")
            """);
        this.actualizarNumerosLinea();

        // --- 3. PESTAÑAS DE SALIDA ---
        JTabbedPane tabsSalida = new JTabbedPane();

        String[] colTokens = new String[]{"Línea", "Tipo de Token", "Lexema", "Valor Semántico"};
        this.modeloTablaTokens = new DefaultTableModel(colTokens, 0) {
            public boolean isCellEditable(int fila, int columna) { return false; }
        };
        this.tablaTokens = new JTable(this.modeloTablaTokens);
        this.tablaTokens.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        this.tablaTokens.setRowHeight(22);
        JScrollPane scrollTablaTokens = new JScrollPane(this.tablaTokens);
        tabsSalida.addTab("Tabla de Tokens", scrollTablaTokens);

        this.areaPrefija = new JTextArea();
        this.areaPrefija.setFont(new Font("Consolas", Font.PLAIN, 13));
        this.areaPrefija.setEditable(false);
        this.areaPrefija.setBackground(new Color(250, 250, 250));
        JScrollPane scrollPrefija = new JScrollPane(this.areaPrefija);
        tabsSalida.addTab("Notación Prefija", scrollPrefija);

        this.areaPostfija = new JTextArea();
        this.areaPostfija.setFont(new Font("Consolas", Font.PLAIN, 13));
        this.areaPostfija.setEditable(false);
        this.areaPostfija.setBackground(new Color(250, 250, 250));
        JScrollPane scrollPostfija = new JScrollPane(this.areaPostfija);
        tabsSalida.addTab("Notación Postfija", scrollPostfija);

        this.areaCodigoOptimizado = new JTextArea();
        this.areaCodigoOptimizado.setFont(new Font("Consolas", Font.PLAIN, 13));
        this.areaCodigoOptimizado.setEditable(false);
        JScrollPane scrollOptimizado = new JScrollPane(this.areaCodigoOptimizado);
        scrollOptimizado.setBorder(BorderFactory.createTitledBorder("Código Sin Código Muerto"));

        this.areaBitacora = new JTextArea(8, 0);
        this.areaBitacora.setFont(new Font("Consolas", Font.PLAIN, 12));
        this.areaBitacora.setEditable(false);
        this.areaBitacora.setBackground(new Color(245, 245, 245));
        JScrollPane scrollBitacora = new JScrollPane(this.areaBitacora);
        scrollBitacora.setBorder(BorderFactory.createTitledBorder("Bitácora de Optimización y Re-compilación"));

        JPanel panelOptimizacion = new JPanel(new BorderLayout(5, 5));
        panelOptimizacion.add(scrollOptimizado, BorderLayout.CENTER);
        panelOptimizacion.add(scrollBitacora, BorderLayout.SOUTH);

        tabsSalida.addTab("Código Optimizado", panelOptimizacion);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollEditor, tabsSalida);
        split.setResizeWeight(0.45D);
        split.setDividerLocation(480);
        this.add(split, BorderLayout.CENTER);

        // --- 4. CONSOLA DE DIAGNÓSTICO ---
        this.areaConsola = new JTextArea(6, 0);
        this.areaConsola.setFont(new Font("Consolas", Font.PLAIN, 12));
        this.areaConsola.setEditable(false);
        this.areaConsola.setBackground(new Color(245, 245, 245));
        JScrollPane scrollConsola = new JScrollPane(this.areaConsola);
        scrollConsola.setBorder(BorderFactory.createTitledBorder("Consola de Diagnóstico"));
        this.add(scrollConsola, BorderLayout.SOUTH);
    }

    private void actualizarNumerosLinea() {
        int totalLineas = areaCodigo.getLineCount();
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= totalLineas; i++) {
            sb.append(i).append("\n");
        }
        areaNumerosLinea.setText(sb.toString());
    }

    private void recompilarOptimizado() {
        String codigoOpt = this.areaCodigoOptimizado.getText();
        if (codigoOpt.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Primero debes compilar el código original para generar el código optimizado.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Reemplaza el contenido del editor por el código optimizado
        this.areaCodigo.setText(codigoOpt);
        this.actualizarNumerosLinea();
        
        // Compila nuevamente el código que ya quedó limpio en el editor
        ejecutarAnalisis(codigoOpt, true);
    }

    private void ejecutarAnalisis(String codigoFuente, boolean esRecompilacion) {
        this.modeloTablaTokens.setRowCount(0);
        this.areaPrefija.setText("");
        this.areaPostfija.setText("");
        if (!esRecompilacion) {
            this.areaCodigoOptimizado.setText("");
            this.areaBitacora.setText("");
        }
        this.areaConsola.setText("");

        if (codigoFuente.trim().isEmpty()) {
            this.areaConsola.setForeground(Color.BLACK);
            this.areaConsola.setText("El editor está vacío. Ingresa código Swift para evaluar.");
            return;
        }

        long tiempoInicio = System.nanoTime();

        try {
            // 1. Fase Léxica
            AnalizadorLexico lexer = new AnalizadorLexico(codigoFuente);
            List<Tokens> tokens = lexer.analizar();

            for (Tokens t : tokens) {
                this.modeloTablaTokens.addRow(new Object[]{
                    t.linea, t.tipo, t.lexema, t.valor != null ? t.valor : "N/A"
                });
            }

            Map<String, Double> mapaValores = new HashMap<>();
            for (int i = 0; i < tokens.size() - 1; i++) {
                if (tokens.get(i).tipo == TipoToken.IDENTIFIER) {
                    String nombreVar = tokens.get(i).lexema;
                    for (int j = i + 1; j < tokens.size() - 1 && tokens.get(j).linea == tokens.get(i).linea; j++) {
                        if (tokens.get(j).tipo == TipoToken.ASSIGN) {
                            Tokens valorToken = tokens.get(j + 1);
                            if (valorToken.tipo == TipoToken.NUMBER_INT || valorToken.tipo == TipoToken.NUMBER_DOUBLE) {
                                try {
                                    mapaValores.put(nombreVar, Double.parseDouble(valorToken.lexema));
                                } catch (Exception ignored) {}
                            }
                            break;
                        }
                    }
                }
            }

            List<ExpresionInfo> listaExpresiones = new ArrayList<>();
            int numExpr = 1;

            for (int i = 0; i < tokens.size(); i++) {
                if (tokens.get(i).tipo == TipoToken.ASSIGN) {
                    int linea = tokens.get(i).linea;
                    List<Tokens> expr = new ArrayList<>();
                    boolean tieneOperador = false;

                    int j = i + 1;
                    while (j < tokens.size() && tokens.get(j).linea == linea && tokens.get(j).tipo != TipoToken.EOF) {
                        Tokens t = tokens.get(j);
                        expr.add(t);
                        if (t.tipo == TipoToken.PLUS || t.tipo == TipoToken.MINUS ||
                            t.tipo == TipoToken.MULTIPLY || t.tipo == TipoToken.DIVIDE ||
                            t.tipo == TipoToken.LPAREN) {
                            tieneOperador = true;
                        }
                        j++;
                    }

                    if (tieneOperador && !expr.isEmpty()) {
                        listaExpresiones.add(new ExpresionInfo(numExpr++, linea, expr));
                    }
                }
            }

            if (listaExpresiones.isEmpty()) {
                List<Tokens> expr = new ArrayList<>();
                for (Tokens t : tokens) {
                    if (t.tipo != TipoToken.EOF) expr.add(t);
                }
                if (!expr.isEmpty()) {
                    listaExpresiones.add(new ExpresionInfo(1, expr.get(0).linea, expr));
                }
            }

            // 2. Fase Sintáctica y Semántica
            AnalizadorSintactico parser = new AnalizadorSintactico(tokens);
            boolean esValido = parser.analizar();
            List<String> errores = parser.getListaErrores();

            long tiempoFin = System.nanoTime();
            double milisegundos = (tiempoFin - tiempoInicio) / 1e6;

            if (esValido && errores.isEmpty()) {
                String prefijoConsola = esRecompilacion ? "✔ RE-COMPILACIÓN DE CÓDIGO OPTIMIZADO EXITOSA" : "✔ COMPILACIÓN EXITOSA";
                this.areaConsola.setForeground(new Color(39, 174, 96));
                this.areaConsola.setText(String.format("%s (Tiempo: %.3f ms)\n"
                        + "• Léxico: %d tokens generados.\n"
                        + "• Sintáctico y Semántico: Válidos (0 errores).\n"
                        + "• Expresiones procesadas: %d encontradas.\n"
                        + "• Notaciones y Cuádruplos actualizados para este código.",
                        prefijoConsola, milisegundos, tokens.size(), listaExpresiones.size()));

                GeneradorNotaciones generador = new GeneradorNotaciones();
                this.areaPostfija.setText(generador.generarReportePostfijo(listaExpresiones, mapaValores));
                this.areaPrefija.setText(generador.generarReportePrefijo(listaExpresiones, mapaValores));

                // 3. Si es la primera compilación, generamos el código optimizado y guardamos el archivo .swift
                if (!esRecompilacion) {
                    OptimizadorCodigo optimizador = new OptimizadorCodigo();
                    OptimizadorCodigo.ResultadoOptimizacion optResult = optimizador.optimizar(codigoFuente, tokens);

                    String codigoNuevo = optResult.getCodigoOptimizado();
                    this.areaCodigoOptimizado.setText(codigoNuevo);

                    try {
                        File archivoOpt = new File("codigo_optimizado.swift");
                        Files.writeString(archivoOpt.toPath(), codigoNuevo);
                    } catch (Exception ignored) {}

                    StringBuilder sbBitacora = new StringBuilder();
                    sbBitacora.append("✔ Archivo generado automáticamente: 'codigo_optimizado.swift'\n\n");
                    sbBitacora.append("=== BITÁCORA DE OPTIMIZACIÓN DE CÓDIGO MUERTO ===\n");
                    for (String cambio : optResult.getBitacoraCambios()) {
                        sbBitacora.append("• ").append(cambio).append("\n");
                    }
                    this.areaBitacora.setText(sbBitacora.toString());
                } else {
                    this.areaBitacora.append("\n✔ [RE-COMPILACIÓN EJECUTADA CON ÉXITO A LAS " + java.time.LocalTime.now().toString().substring(0, 8) + "]");
                }

            } else {
                this.areaConsola.setForeground(new Color(192, 57, 43));
                StringBuilder mensajeErrores = new StringBuilder(String.format("✖ SE ENCONTRARON ERRORES (Tiempo: %.3f ms):\n", milisegundos));
                for (String err : errores) {
                    mensajeErrores.append(err).append("\n");
                }
                this.areaConsola.setText(mensajeErrores.toString());
            }
        } catch (Exception ex) {
            this.areaConsola.setForeground(Color.RED);
            this.areaConsola.setText("Excepción inesperada: " + ex.getMessage());
        }
    }

    private void abrirArchivoSwift() {
        JFileChooser selector = new JFileChooser();
        selector.setFileFilter(new FileNameExtensionFilter("Archivos Swift (*.swift)", "swift"));
        if (selector.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File archivo = selector.getSelectedFile();
                this.areaCodigo.setText(Files.readString(archivo.toPath()));
                this.actualizarNumerosLinea();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al abrir: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void guardarArchivoSwift() {
        JFileChooser selector = new JFileChooser();
        selector.setFileFilter(new FileNameExtensionFilter("Archivos Swift (*.swift)", "swift"));
        if (selector.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File archivo = selector.getSelectedFile();
                if (!archivo.getName().toLowerCase().endsWith(".swift")) {
                    archivo = new File(archivo.getAbsolutePath() + ".swift");
                }
                Files.writeString(archivo.toPath(), this.areaCodigo.getText());
                JOptionPane.showMessageDialog(this, "Guardado con éxito como " + archivo.getName(), "Información", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new InterfazGrafica().setVisible(true);
        });
    }
}