import Lexico.AnalizadorLexico;
import Lexico.Tokens;
import Sintactico.AnalizadorSintactico;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.File;
import java.nio.file.Files;
import java.util.List;
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
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

public class InterfazGrafica extends JFrame {
    private JTextArea areaCodigo;
    private JTable tablaTokens;
    private DefaultTableModel modeloTabla;
    private JTextArea areaC3D;
    private JTextArea areaConsola;

    public InterfazGrafica() {
        super("Compilador Swift - Analizador Completo y Código Intermedio");
        this.configurarVentana();
        this.inicializarComponentes();
    }

    private void configurarVentana() {
        this.setSize(1100, 700);
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

        // --- 2. EDITOR DE CÓDIGO ---
        this.areaCodigo = new JTextArea();
        this.areaCodigo.setFont(new Font("Consolas", Font.PLAIN, 14));
        this.areaCodigo.setText("var edad: Int = 20\nlet nombre: String = \"Adrian\"\nvar promedio: Double = 9.5\nlet activo: Bool = true\n\nif edad >= 18 {\n    print(\"Acceso permitido\")\n} else {\n    print(\"Acceso denegado\")\n}\n\nwhile edad > 0 {\n    edad = edad - 1\n}\n");
        JScrollPane scrollEditor = new JScrollPane(this.areaCodigo);
        scrollEditor.setBorder(BorderFactory.createTitledBorder("Código Fuente Swift"));

        // --- 3. PESTAÑAS DERECHAS (TOKENS Y C3D) ---
        JTabbedPane tabsSalida = new JTabbedPane();

        // Pestaña 1: Tabla de Tokens
        String[] columnas = new String[]{"Línea", "Tipo de Token", "Lexema", "Valor Semántico"};
        this.modeloTabla = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int fila, int columna) { return false; }
        };
        this.tablaTokens = new JTable(this.modeloTabla);
        this.tablaTokens.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        this.tablaTokens.setRowHeight(22);
        JScrollPane scrollTabla = new JScrollPane(this.tablaTokens);
        tabsSalida.addTab("Tabla de Tokens", scrollTabla);

        // Pestaña 2: Código Intermedio (C3D)
        this.areaC3D = new JTextArea();
        this.areaC3D.setFont(new Font("Consolas", Font.PLAIN, 13));
        this.areaC3D.setEditable(false);
        this.areaC3D.setBackground(new Color(250, 250, 250));
        JScrollPane scrollC3D = new JScrollPane(this.areaC3D);
        tabsSalida.addTab("Código Intermedio (C3D)", scrollC3D);

        // División horizontal
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

    private void ejecutarAnalisis() {
        this.modeloTabla.setRowCount(0);
        this.areaC3D.setText("");
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
                this.modeloTabla.addRow(new Object[]{
                    t.linea, t.tipo, t.lexema, t.valor != null ? t.valor : "N/A"
                });
            }

            // Fase Sintáctica, Semántica y C3D
            AnalizadorSintactico parser = new AnalizadorSintactico(tokens);
            boolean esValido = parser.analizar();
            List<String> errores = parser.getListaErrores();

            if (esValido && errores.isEmpty()) {
                this.areaConsola.setForeground(new Color(39, 174, 96));
                this.areaConsola.setText("✔ COMPILACIÓN EXITOSA\n"
                        + "• Léxico: " + tokens.size() + " tokens generados.\n"
                        + "• Sintáctico: Estructura válida.\n"
                        + "• Semántico: Sin errores de tipos ni alcance.\n"
                        + "• Código intermedio generado en la pestaña C3D.");
                
                // Mostrar C3D generado
                this.areaC3D.setText(parser.getGeneradorC3D().obtenerCodigoTexto());
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