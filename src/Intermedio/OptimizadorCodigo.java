package Intermedio;

//import Lexico.Tokens;
//import Lexico.TipoToken;
import java.util.ArrayList;
import java.util.List;

public class OptimizadorCodigo {

    public static class ResultadoOptimizacion {
        private final String codigoOptimizado;
        private final List<String> bitacoraCambios;

        public ResultadoOptimizacion(String codigoOptimizado, List<String> bitacoraCambios) {
            this.codigoOptimizado = codigoOptimizado;
            this.bitacoraCambios = bitacoraCambios;
        }

        public String getCodigoOptimizado() { return codigoOptimizado; }
        public List<String> getBitacoraCambios() { return bitacoraCambios; }
    }

    public ResultadoOptimizacion optimizar(String codigoOriginal) {
        List<String> bitacora = new ArrayList<>();
        String[] lineas = codigoOriginal.split("\n");
        StringBuilder codigoLimpio = new StringBuilder();

        boolean enBloqueMuerto = false;
        int nivelLlaves = 0;

        for (int i = 0; i < lineas.length; i++) {
            String lineaOriginal = lineas[i];
            String lineaTrimmed = lineaOriginal.trim();

            // A) Detección de inicio de bloque inalcanzable (if false / while false)
            if (lineaTrimmed.matches(".*\\b(if|while)\\s+false\\b.*")) {
                enBloqueMuerto = true;
                nivelLlaves = 0;
                
                // Contar llaves en la misma línea de apertura
                if (lineaTrimmed.contains("{")) {
                    nivelLlaves++;
                }

                bitacora.add("[Código Muerto] Línea " + (i + 1) + ": Se eliminó la estructura inalcanzable ('" + lineaTrimmed + "').");
                continue;
            }

            // B) Si estamos dentro de un bloque inalcanzable, omitir sus instrucciones
            if (enBloqueMuerto) {
                if (lineaTrimmed.contains("{") && !lineaTrimmed.matches(".*\\b(if|while)\\s+false\\b.*")) {
                    nivelLlaves++;
                }

                if (lineaTrimmed.contains("}")) {
                    if (nivelLlaves > 1) {
                        nivelLlaves--;
                    } else {
                        // Fin del bloque muerto principal
                        enBloqueMuerto = false;
                        bitacora.add("[Código Muerto] Línea " + (i + 1) + ": Cierre del bloque inalcanzable omitido.");
                        continue;
                    }
                }

                bitacora.add("[Código Muerto] Línea " + (i + 1) + ": Instrucción omitida ('" + lineaTrimmed + "').");
                continue;
            }

            // Si la línea es válida y alcanzable, se conserva
            codigoLimpio.append(lineaOriginal).append("\n");
        }

        if (bitacora.isEmpty()) {
            bitacora.add("No se detectó código muerto. El programa es 100% alcanzable.");
        }

        return new ResultadoOptimizacion(codigoLimpio.toString(), bitacora);
    }
}