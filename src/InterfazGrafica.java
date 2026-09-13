import Lexico.AnalizadorLexico;
import Lexico.Tokens;
import Sintactico.AnalizadorSintactico;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

public class InterfazGrafica extends JFrame {
    private JTextArea areaCodigo;
    private JTable tablaTokens;
    private DefaultTableModel modeloTabla;
    private JTextArea areaConsola;

    public InterfazGrafica() {
        super("Compilador Swift - Analizador Léxico y Sintáctico");
        this.configurarVentana();
        this.inicializarComponentes();
    }

    private void configurarVentana() {
        this.setSize(1000, 650);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout(10, 10));
    }

    private void inicializarComponentes() {
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnCompilar = new JButton("▶ Compilar / Analizar");
        btnCompilar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCompilar.setBackground(new Color(24, 43, 73));
        btnCompilar.setForeground(Color.WHITE);
        btnCompilar.setOpaque(true);
        btnCompilar.setBorderPainted(false);
        btnCompilar.setFocusPainted(false);
        btnCompilar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCompilar.addActionListener((e) -> this.ejecutarAnalisis());
        panelSuperior.add(btnCompilar);
        this.add(panelSuperior, BorderLayout.NORTH);

        this.areaCodigo = new JTextArea();
        this.areaCodigo.setFont(new Font("Consolas", Font.PLAIN, 14));
        this.areaCodigo.setText("var edad: Int = 20\nlet nombre: String = \"Adrian\"\nvar promedio: Double = 9.5\nlet activo: Bool = true\n\nif edad >= 18 {\n    print(\"Acceso permitido\")\n} else {\n    print(\"Acceso denegado\")\n}\n\nwhile edad > 0 {\n    edad = edad - 1\n}\n");
        JScrollPane scrollEditor = new JScrollPane(this.areaCodigo);
        scrollEditor.setBorder(BorderFactory.createTitledBorder("Código Swift"));

        String[] columnas = new String[]{"Línea", "Tipo de Token", "Lexema", "Valor Semántico"};
        this.modeloTabla = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        this.tablaTokens = new JTable(this.modeloTabla);
        this.tablaTokens.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        this.tablaTokens.setRowHeight(22);
        JScrollPane scrollTabla = new JScrollPane(this.tablaTokens);
        scrollTabla.setBorder(BorderFactory.createTitledBorder("Tabla de Tokens Generados"));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollEditor, scrollTabla);
        split.setResizeWeight(0.5D);
        split.setDividerLocation(480);
        this.add(split, BorderLayout.CENTER);

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
                    t.linea, 
                    t.tipo, 
                    t.lexema, 
                    t.valor != null ? t.valor : "N/A"
                });
            }

            // Fase Sintáctica
            AnalizadorSintactico parser = new AnalizadorSintactico(tokens);
            boolean esValido = parser.analizar();
            List<String> errores = parser.getListaErrores();

            if (esValido && errores.isEmpty()) {
                this.areaConsola.setForeground(new Color(39, 174, 96));
                this.areaConsola.setText("✔ ANÁLISIS COMPLETADO SIN ERRORES\n"
                        + "Léxico: " + tokens.size() + " tokens generados correctamente.\n"
                        + "Sintaxis: Estructura gramatical válida.");
            } else {
                this.areaConsola.setForeground(new Color(192, 57, 43));
                StringBuilder mensajeErrores = new StringBuilder("✖ SE ENCONTRARON ERRORES SINTÁCTICOS:\n");
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }

            new InterfazGrafica().setVisible(true);
        });
    }
}