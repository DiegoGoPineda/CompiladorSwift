import java.util.List;
import Lexico.AnalizadorLexico;
import Lexico.Tokens;
import Sintactico.AnalizadorSintactico;

public class App {
    public static void main(String[] args) {
        String codigoSwift = """
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
            """;

        System.out.println("--- FASE 1: ANÁLISIS LÉXICO ---");
        AnalizadorLexico lexer = new AnalizadorLexico(codigoSwift);
        List<Tokens> tokens = lexer.analizar();
        System.out.println("Léxico completado con " + tokens.size() + " tokens.");

        System.out.println("\n--- FASE 2: ANÁLISIS SINTÁCTICO ---");
        AnalizadorSintactico parser = new AnalizadorSintactico(tokens);
        boolean sintaxisCorrecta = parser.analizar();

        if (sintaxisCorrecta) {
            System.out.println("¡Análisis sintáctico exitoso! El código Swift tiene una estructura válida.");
        } else {
            System.out.println("Se detectaron errores de sintaxis en el código.");
        }
    }
}