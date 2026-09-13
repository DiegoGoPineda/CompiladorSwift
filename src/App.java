import java.util.List;
import Lexico.AnalizadorLexico;
import Lexico.Tokens;
import Sintactico.AnalizadorSintactico;

public class App {
    public static void main(String[] args) {
        String codigoSwift = """
            let institucion: String = "Instituto Tecnologico"
            let anioActual: Int = 2026
            let calificacionMinima: Double = 70.0
            let cupoMaximo: Int = 30

            var alumnosInscritos: Int = 0
            var promedioGeneral: Double = 0.0
            var sumaCalificaciones: Double = 0.0
            var sistemaAbierto: Bool = true

            print("Iniciando sistema de control escolar...")
            print(institucion)

            var matricula1: Int = 101
            var calif1: Double = 85.5
            var aprobado1: Bool = false

            if calif1 >= calificacionMinima {
                aprobado1 = true
                print("Alumno 101 aprobado")
            } else {
                aprobado1 = false
                print("Alumno 101 reprobado")
            }

            alumnosInscritos = alumnosInscritos + 1
            sumaCalificaciones = sumaCalificaciones + calif1

            var matricula2: Int = 102
            var calif2: Double = 64.0
            var aprobado2: Bool = false

            if calif2 >= calificacionMinima {
                aprobado2 = true
                print("Alumno 102 aprobado")
            } else {
                aprobado2 = false
                print("Alumno 102 reprobado")
            }

            alumnosInscritos = alumnosInscritos + 1
            sumaCalificaciones = sumaCalificaciones + calif2

            var matricula3: Int = 103
            var calif3: Double = 92.0
            var aprobado3: Bool = false

            if calif3 >= calificacionMinima {
                aprobado3 = true
                print("Alumno 103 aprobado")
            } else {
                aprobado3 = false
                print("Alumno 103 reprobado")
            }

            alumnosInscritos = alumnosInscritos + 1
            sumaCalificaciones = sumaCalificaciones + calif3

            promedioGeneral = sumaCalificaciones / 3.0
            print("Resumen de calificaciones:")
            print(promedioGeneral)

            if promedioGeneral >= 90.0 {
                print("Rendimiento del grupo: Excelente")
            } else {
                if promedioGeneral >= 70.0 {
                    print("Rendimiento del grupo: Regular")
                } else {
                    print("Rendimiento del grupo: Deficiente")
                }
            }

            var cuposDisponibles: Int = cupoMaximo - alumnosInscritos
            print("Calculando lugares libres en las aulas...")

            while cuposDisponibles > 20 {
                print("Capacidad disponible alta")
                cuposDisponibles = cuposDisponibles - 5
            }

            while cuposDisponibles > 0 {
                print("Asignando banco individual...")
                cuposDisponibles = cuposDisponibles - 1
            }

            var procesoTerminado: Bool = false
            if cuposDisponibles == 0 {
                procesoTerminado = true
                print("Cupo lleno para el ciclo escolar")
            } else {
                procesoTerminado = false
                print("Aun quedan lugares disponibles")
            }

            var contadorCierre: Int = 3
            while contadorCierre > 0 {
                print(contadorCierre)
                contadorCierre = contadorCierre - 1
            }

            sistemaAbierto = false
            print("Sistema finalizado con exito")
            """;

        System.out.println("--- FASE 1: ANALISIS LEXICO ---");
        AnalizadorLexico lexer = new AnalizadorLexico(codigoSwift);
        List<Tokens> tokens = lexer.analizar();
        System.out.println("Lexico completado con " + tokens.size() + " tokens.");

        System.out.println("\n--- FASE 2: ANALISIS SINTACTICO ---");
        AnalizadorSintactico parser = new AnalizadorSintactico(tokens);
        boolean sintaxisCorrecta = parser.analizar();

        if (sintaxisCorrecta) {
            System.out.println("¡Analisis sintáctico exitoso! El codigo Swift tiene una estructura valida.");
        } else {
            System.out.println("Se detectaron errores de sintaxis en el codigo.");
        }
    }
}