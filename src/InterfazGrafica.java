import Intermedio.GeneradorNotaciones;
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
    private JTextArea areaConsola;

    public InterfazGrafica() {
        super("Compilador Swift - Notaciones y Cuádruplos");
        this.configurarVentana();
        this.inicializarComponentes();
    }

    private void configurarVentana() {
        this.setSize(1150, 720);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout(10, 10));
    }

    private void inicializarComponentes() {
        // --- 1. BARRA SUPERIOR DE ACCIONES ---
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        JButton btnCompilar = new JButton("▶ Compilar / Analizar");
        btnCompilar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCompilar.setBackground(new Color(24, 43, 73));
        btnCompilar.setForeground(Color.WHITE);
        btnCompilar.setOpaque(true);
        btnCompilar.setBorderPainted(false);
        btnCompilar.setFocusPainted(false);
        btnCompilar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCompilar.addActionListener((e) -> this.ejecutarAnalisis());

        JButton btnAbrir = new JButton("📂 Abrir .swift");
        btnAbrir.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnAbrir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAbrir.addActionListener((e) -> this.abrirArchivoSwift());

        JButton btnGuardar = new JButton("💾 Guardar .swift");
        btnGuardar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGuardar.addActionListener((e) -> this.guardarArchivoSwift());

        panelSuperior.add(btnCompilar);
        panelSuperior.add(btnAbrir);
        panelSuperior.add(btnGuardar);
        this.add(panelSuperior, BorderLayout.NORTH);

        // --- 2. EDITOR DE CÓDIGO CON NÚMEROS DE LÍNEA ---
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

            var x = ((g2 + h2) * 3.0) / ((a2 - b2) + (c2 * 2.0))
            """);
        this.actualizarNumerosLinea();

        // --- 3. PESTAÑAS DERECHAS (TOKENS, PREFIJA Y POSTFIJA) ---
        JTabbedPane tabsSalida = new JTabbedPane();

        // Pestaña 1: Tabla de Tokens
        String[] colTokens = new String[]{"Línea", "Tipo de Token", "Lexema", "Valor Semántico"};
        this.modeloTablaTokens = new DefaultTableModel(colTokens, 0) {
            public boolean isCellEditable(int fila, int columna) { return false; }
        };
        this.tablaTokens = new JTable(this.modeloTablaTokens);
        this.tablaTokens.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        this.tablaTokens.setRowHeight(22);
        JScrollPane scrollTablaTokens = new JScrollPane(this.tablaTokens);
        tabsSalida.addTab("Tabla de Tokens", scrollTablaTokens);

        // Pestaña 2: Notación Prefija
        this.areaPrefija = new JTextArea();
        this.areaPrefija.setFont(new Font("Consolas", Font.PLAIN, 13));
        this.areaPrefija.setEditable(false);
        this.areaPrefija.setBackground(new Color(250, 250, 250));
        JScrollPane scrollPrefija = new JScrollPane(this.areaPrefija);
        tabsSalida.addTab("Notación Prefija", scrollPrefija);

        // Pestaña 3: Notación Postfija
        this.areaPostfija = new JTextArea();
        this.areaPostfija.setFont(new Font("Consolas", Font.PLAIN, 13));
        this.areaPostfija.setEditable(false);
        this.areaPostfija.setBackground(new Color(250, 250, 250));
        JScrollPane scrollPostfija = new JScrollPane(this.areaPostfija);
        tabsSalida.addTab("Notación Postfija", scrollPostfija);

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

    private void ejecutarAnalisis() {
        this.modeloTablaTokens.setRowCount(0);
        this.areaPrefija.setText("");
        this.areaPostfija.setText("");
        this.areaConsola.setText("");

        String codigo = this.areaCodigo.getText();
        if (codigo.trim().isEmpty()) {
            this.areaConsola.setForeground(Color.BLACK);
            this.areaConsola.setText("El editor está vacío. Ingresa código Swift para evaluar.");
            return;
        }

        try {
            // Fase Léxica
            AnalizadorLexico lexer = new AnalizadorLexico(codigo);
            List<Tokens> tokens = lexer.analizar();

            for (Tokens t : tokens) {
                this.modeloTablaTokens.addRow(new Object[]{
                    t.linea, t.tipo, t.lexema, t.valor != null ? t.valor : "N/A"
                });
            }

            // Extraer únicamente los tokens pertenecientes a la expresión principal (tras la asignación '=')
            List<Tokens> expresionTokens = new ArrayList<>();
            int inicioExpr = -1;
            int lineaExpr = 1;

            for (int i = 0; i < tokens.size(); i++) {
                if (tokens.get(i).tipo == TipoToken.ASSIGN) {
                    inicioExpr = i + 1;
                    lineaExpr = tokens.get(i).linea;
                }
            }

            if (inicioExpr != -1) {
                for (int i = inicioExpr; i < tokens.size(); i++) {
                    Tokens t = tokens.get(i);
                    if (t.tipo == TipoToken.EOF) break;
                    expresionTokens.add(t);
                }
            } else {
                for (Tokens t : tokens) {
                    if (t.tipo != TipoToken.EOF) expresionTokens.add(t);
                }
                if (!expresionTokens.isEmpty()) {
                    lineaExpr = expresionTokens.get(0).linea;
                }
            }

            // Fase Sintáctica y Semántica
            AnalizadorSintactico parser = new AnalizadorSintactico(tokens);
            boolean esValido = parser.analizar();
            List<String> errores = parser.getListaErrores();

            if (esValido && errores.isEmpty()) {
                this.areaConsola.setForeground(new Color(39, 174, 96));
                this.areaConsola.setText("✔ COMPILACIÓN EXITOSA\n"
                        + "• Léxico: " + tokens.size() + " tokens generados.\n"
                        + "• Sintáctico: Estructura válida.\n"
                        + "• Notaciones, Cuádruplos y Evaluación de la expresión calculados con éxito.");

                // Mapear los valores de variables declaradas en el programa (ej. var g2: Double = 10.0)
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

                // Generar reportes con evaluación
                GeneradorNotaciones generador = new GeneradorNotaciones();
                this.areaPostfija.setText(generador.generarReportePostfijo(expresionTokens, 1, lineaExpr, mapaValores));
                this.areaPrefija.setText(generador.generarReportePrefijo(expresionTokens, 1, lineaExpr, mapaValores));

            } else {
                this.areaConsola.setForeground(new Color(192, 57, 43));
                StringBuilder mensajeErrores = new StringBuilder("✖ SE ENCONTRARON ERRORES:\n");
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