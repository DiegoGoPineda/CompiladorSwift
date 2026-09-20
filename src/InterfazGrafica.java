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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

public class InterfazGrafica extends JFrame {
    private JTextArea areaCodigo;
    private JTextArea areaNumerosLinea;
    private JTable tablaTokens;
    private DefaultTableModel modeloTabla;
    private JTextArea areaC3D;
    private JTextArea areaConsola;

    public InterfazGrafica() {
        super("Compilador Swift");
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

        // Columna lateral para enumerar las líneas
        this.areaNumerosLinea = new JTextArea("1 ");
        this.areaNumerosLinea.setFont(new Font("Consolas", Font.PLAIN, 14));
        this.areaNumerosLinea.setBackground(new Color(235, 237, 239));
        this.areaNumerosLinea.setForeground(new Color(120, 144, 156));
        this.areaNumerosLinea.setEditable(false);
        this.areaNumerosLinea.setFocusable(false);
        this.areaNumerosLinea.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        // Listener para recalcular números de línea al escribir o borrar
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

        // Código de prueba limpio de más de 100 líneas
        this.areaCodigo.setText(obtenerCodigoPruebaInicial());
        this.actualizarNumerosLinea();

        // --- 3. PESTAÑAS DERECHAS (TOKENS Y C3D) ---
        JTabbedPane tabsSalida = new JTabbedPane();

        String[] columnas = new String[]{"Línea", "Tipo de Token", "Lexema", "Valor Semántico"};
        this.modeloTabla = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int fila, int columna) { return false; }
        };
        this.tablaTokens = new JTable(this.modeloTabla);
        this.tablaTokens.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        this.tablaTokens.setRowHeight(22);
        JScrollPane scrollTabla = new JScrollPane(this.tablaTokens);
        tabsSalida.addTab("Tabla de Tokens", scrollTabla);

        this.areaC3D = new JTextArea();
        this.areaC3D.setFont(new Font("Consolas", Font.PLAIN, 13));
        this.areaC3D.setEditable(false);
        this.areaC3D.setBackground(new Color(250, 250, 250));
        JScrollPane scrollC3D = new JScrollPane(this.areaC3D);
        tabsSalida.addTab("Código Intermedio (C3D)", scrollC3D);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollEditor, tabsSalida);
        split.setResizeWeight(0.5D);
        split.setDividerLocation(520);
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
            AnalizadorLexico lexer = new AnalizadorLexico(codigo);
            List<Tokens> tokens = lexer.analizar();

            for (Tokens t : tokens) {
                this.modeloTabla.addRow(new Object[]{
                    t.linea, t.tipo, t.lexema, t.valor != null ? t.valor : "N/A"
                });
            }

            AnalizadorSintactico parser = new AnalizadorSintactico(tokens);
            boolean esValido = parser.analizar();
            List<String> errores = parser.getListaErrores();

            if (esValido && errores.isEmpty()) {
                this.areaConsola.setForeground(new Color(39, 174, 96));
                this.areaConsola.setText("COMPILACIÓN EXITOSA\n"
                        + "• Léxico: " + tokens.size() + " tokens generados.\n"
                        + "• Sintáctico: Estructura válida.\n"
                        + "• Semántico: Sin errores de tipos ni alcance.");

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
                JOptionPane.showMessageDialog(this, "Guardado con exito como " + archivo.getName(), "Información", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String obtenerCodigoPruebaInicial() {
        return """
            let institucion: String = "Control Escolar"
            let anio: Int = 2026
            let cupoMaximo: Int = 50
            let minimaAprobatoria: Double = 70.0

            var alumnosInscritos: Int = 0
            var sumaNotas: Double = 0.0
            var promedio: Double = 0.0
            var sistemaActivo: Bool = true

            print("Iniciando operaciones del ciclo:")
            print(institucion)

            var id1: Int = 101
            var nota1: Double = 85.0
            var aprobado1: Bool = false

            if nota1 >= minimaAprobatoria {
                aprobado1 = true
                print("Estudiante 101 aprobado")
            } else {
                aprobado1 = false
                print("Estudiante 101 reprobado")
            }

            alumnosInscritos = alumnosInscritos + 1
            sumaNotas = sumaNotas + nota1

            var id2: Int = 102
            var nota2: Double = 62.5
            var aprobado2: Bool = false

            if nota2 >= minimaAprobatoria {
                aprobado2 = true
                print("Estudiante 102 aprobado")
            } else {
                aprobado2 = false
                print("Estudiante 102 reprobado")
            }

            alumnosInscritos = alumnosInscritos + 1
            sumaNotas = sumaNotas + nota2

            var id3: Int = 103
            var nota3: Double = 94.0
            var aprobado3: Bool = false

            if nota3 >= minimaAprobatoria {
                aprobado3 = true
                print("Estudiante 103 aprobado")
            } else {
                aprobado3 = false
                print("Estudiante 103 reprobado")
            }

            alumnosInscritos = alumnosInscritos + 1
            sumaNotas = sumaNotas + nota3

            var id4: Int = 104
            var nota4: Double = 78.0
            var aprobado4: Bool = false

            if nota4 >= minimaAprobatoria {
                aprobado4 = true
                print("Estudiante 104 aprobado")
            } else {
                aprobado4 = false
                print("Estudiante 104 reprobado")
            }

            alumnosInscritos = alumnosInscritos + 1
            sumaNotas = sumaNotas + nota4

            promedio = sumaNotas / 4.0
            print("Promedio general calculado:")
            print(promedio)

            if promedio >= 90.0 {
                print("Desempenio de excelencia")
            } else {
                if promedio >= 70.0 {
                    print("Desempenio satisfactorio")
                } else {
                    print("Desempenio insuficiente")
                }
            }

            var lugaresRestantes: Int = cupoMaximo - alumnosInscritos
            print("Asignando cupos adicionales...")

            while lugaresRestantes > 40 {
                print("Bloque de alta disponibilidad")
                lugaresRestantes = lugaresRestantes - 2
            }

            while lugaresRestantes > 30 {
                lugaresRestantes = lugaresRestantes - 1
            }

            var auditoriaCompleta: Bool = false

            if lugaresRestantes >= 30 {
                auditoriaCompleta = true
                print("Auditoria finalizada con exito")
            } else {
                auditoriaCompleta = false
                print("Capacidad no coincidente")
            }

            var pasoVerificacion: Int = 3
            while pasoVerificacion > 0 {
                print(pasoVerificacion)
                pasoVerificacion = pasoVerificacion - 1
            }

            sistemaActivo = false
            print("Cierre de sesion completado")
            """;
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