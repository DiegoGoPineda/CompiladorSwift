package Intermedio;

import Lexico.Tokens;
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

    public ResultadoOptimizacion optimizar(String codigoOriginal, List<Tokens> tokens) {
        List<String> bitacora = new ArrayList<>();
        String[] lineas = codigoOriginal.split("\n");
        StringBuilder codigoLimpio = new StringBuilder();

        boolean enBloqueMuerto = false;
        boolean trasRetorno = false;
        int nivelLlaves = 0;

        for (int i = 0; i < lineas.length; i++) {
            String lineaOriginal = lineas[i];
            String lineaTrimmed = lineaOriginal.trim();

            if (lineaTrimmed.isEmpty()) {
                if (!enBloqueMuerto && !trasRetorno) {
                    codigoLimpio.append("\n");
                }
                continue;
            }

            // A) ELIMINACIÓN DE CÓDIGO MUERTO (if false / while false)
            if (lineaTrimmed.matches(".*\\b(if|while)\\s+false\\b.*")) {
                enBloqueMuerto = true;
                nivelLlaves = 0;
                if (lineaTrimmed.contains("{")) nivelLlaves++;
                bitacora.add("[Código Muerto] Línea " + (i + 1) + ": Se eliminó bloque inalcanzable ('" + lineaTrimmed + "').");
                continue;
            }

            if (enBloqueMuerto) {
                if (lineaTrimmed.contains("{")) nivelLlaves++;
                if (lineaTrimmed.contains("}")) {
                    if (nivelLlaves > 1) {
                        nivelLlaves--;
                    } else {
                        enBloqueMuerto = false;
                        bitacora.add("[Código Muerto] Línea " + (i + 1) + ": Cierre de bloque inalcanzable omitido.");
                        continue;
                    }
                }
                bitacora.add("[Código Muerto] Línea " + (i + 1) + ": Instrucción omitida dentro de bloque inalcanzable.");
                continue;
            }

            // B) ELIMINACIÓN DE CÓDIGO INALCANZABLE TRAS RETORNO DE CARRO (return / break / continue)
            if (trasRetorno) {
                if (lineaTrimmed.equals("}")) {
                    trasRetorno = false;
                    codigoLimpio.append(lineaOriginal).append("\n");
                } else {
                    bitacora.add("[Retornos de Carro] Línea " + (i + 1) + ": Instrucción inalcanzable tras salto omitida ('" + lineaTrimmed + "').");
                }
                continue;
            }

            if (lineaTrimmed.startsWith("return") || lineaTrimmed.startsWith("break") || lineaTrimmed.startsWith("continue")) {
                trasRetorno = true;
                bitacora.add("[Retornos de Carro] Línea " + (i + 1) + ": Detectada instrucción de salida ('" + lineaTrimmed + "').");
            }

            codigoLimpio.append(lineaOriginal).append("\n");
        }

        if (bitacora.isEmpty()) {
            bitacora.add("No se detectó código muerto ni instrucciones inalcanzables tras retornos.");
        }

        return new ResultadoOptimizacion(codigoLimpio.toString(), bitacora);
    }
}