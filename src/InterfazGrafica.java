import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import Lexico.AnalizadorLexico;
import Lexico.Tokens;
import Sintactico.AnalizadorSintactico;

public class InterfazGrafica extends JFrame {
    private JTextArea areaCodigo;
    private JTable tablaTokens;
    private DefaultTableModel modeloTabla;
    private JTextArea areaConsola;

    public InterfazGrafica() {
        super("Compilador Swift - Analizador Léxico y Sintáctico");
        configurarVentana();
        inicializarComponentes();
    }

    private void configurarVentana() {
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
    }

    private void inicializarComponentes() {
        // --- 1. BARRA SUPERIOR CON BOTÓN ---
        // --- 1. BARRA SUPERIOR CON BOTÓN ---
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnCompilar = new JButton("▶ Compilar / Analizar");
        btnCompilar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        // Estilo de alto contraste para que no se pierda en Windows:
        btnCompilar.setBackground(new Color(24, 43, 73));   // Azul marino oscuro
        btnCompilar.setForeground(Color.WHITE);             // Texto blanco nítido
        btnCompilar.setOpaque(true);                        // Fuerza a pintar el fondo en Swing
        btnCompilar.setBorderPainted(false);                // Quita el borde plano por defecto
        btnCompilar.setFocusPainted(false);
        btnCompilar.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Manita al pasar el mouse
        
        btnCompilar.addActionListener(e -> ejecutarAnalisis());
        panelSuperior.add(btnCompilar);
        add(panelSuperior, BorderLayout.NORTH);

        // --- 2. PANEL CENTRAL (EDITOR + TABLA DE TOKENS) ---
        // Editor de código
        areaCodigo = new JTextArea();
        areaCodigo.setFont(new Font("Consolas", Font.PLAIN, 14));
        areaCodigo.setText("""
            var edad: Int = 20
            let nombre: String = "Adrian"
            var promedio: Double = 9.5
            let activo: Bool = true

            if edad >= 18 {
                print("Acceso permitido")
            } else {
                print("Acceso denegado")
            }

            while edad > 0 {
                edad = edad - 1
            }
            """);
        JScrollPane scrollEditor = new JScrollPane(areaCodigo);
        scrollEditor.setBorder(BorderFactory.createTitledBorder("Código Swift"));

        // Tabla de tokens
        String[] columnas = {"Línea", "Tipo de Token", "Lexema", "Valor Semántico"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Solo lectura
            }
        };
        tablaTokens = new JTable(modeloTabla);
        tablaTokens.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaTokens.setRowHeight(22);
        JScrollPane scrollTabla = new JScrollPane(tablaTokens);
        scrollTabla.setBorder(BorderFactory.createTitledBorder("Tabla de Tokens Generados"));

        // División horizontal (50% código, 50% tabla)
        JSplitPane splitPrincipal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollEditor, scrollTabla);
        splitPrincipal.setResizeWeight(0.5);
        splitPrincipal.setDividerLocation(480);
        add(splitPrincipal, BorderLayout.CENTER);

        // --- 3. PANEL INFERIOR (CONSOLA DE RESULTADOS) ---
        areaConsola = new JTextArea(6, 0);
        areaConsola.setFont(new Font("Consolas", Font.PLAIN, 12));
        areaConsola.setEditable(false);
        areaConsola.setBackground(new Color(245, 245, 245));
        JScrollPane scrollConsola = new JScrollPane(areaConsola);
        scrollConsola.setBorder(BorderFactory.createTitledBorder("Consola de Diagnóstico"));
        add(scrollConsola, BorderLayout.SOUTH);
    }

    private void ejecutarAnalisis() {
        modeloTabla.setRowCount(0); // Limpiar tabla
        areaConsola.setText("");    // Limpiar consola

        String codigo = areaCodigo.getText();
        if (codigo.trim().isEmpty()) {
            areaConsola.setText("El editor está vacío. Ingresa código Swift para evaluar.");
            return;
        }

        // Redirigir errores de System.err a la consola gráfica
        ByteArrayOutputStream baosErrores = new ByteArrayOutputStream();
        PrintStream psErrores = new PrintStream(baosErrores);
        PrintStream salidaErrOriginal = System.err;
        System.setErr(psErrores);

        try {
            // FASE 1: Análisis Léxico
            AnalizadorLexico lexer = new AnalizadorLexico(codigo);
            List<Tokens> tokens = lexer.analizar();

            for (Tokens t : tokens) {
                modeloTabla.addRow(new Object[]{
                    t.linea,
                    t.tipo,
                    t.lexema,
                    t.valor != null ? t.valor : "N/A"
                });
            }

            // FASE 2: Análisis Sintáctico
            AnalizadorSintactico parser = new AnalizadorSintactico(tokens);
            boolean sintaxisValida = parser.analizar();

            System.err.flush();
            String erroresDetectados = baosErrores.toString();

            if (sintaxisValida && erroresDetectados.isEmpty()) {
                areaConsola.setForeground(new Color(39, 174, 96));
                areaConsola.setText("✔ ANÁLISIS COMPLETADO SIN ERRORES\n"
                        + "Léxico: " + tokens.size() + " tokens generados correctamente.\n"
                        + "Sintaxis: Estructura gramatical válida.");
            } else {
                areaConsola.setForeground(new Color(192, 57, 43));
                areaConsola.setText("✖ SE ENCONTRARON ERRORES:\n" + erroresDetectados);
            }

        } catch (Exception ex) {
            areaConsola.setForeground(Color.RED);
            areaConsola.setText("Excepción durante la ejecución: " + ex.getMessage());
        } finally {
            System.setErr(salidaErrOriginal);
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
