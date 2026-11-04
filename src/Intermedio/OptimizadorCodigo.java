package Intermedio;

import Lexico.TipoToken;
import Lexico.Tokens;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        // Map para almacenar el valor en String formateado respetando si es Double (10.0) o Int (10)
        Map<String, String> mapaValoresTexto = new HashMap<>();
        Map<String, Double> mapaValoresNum = new HashMap<>();

        for (int i = 0; i < tokens.size() - 1; i++) {
            if (tokens.get(i).tipo == TipoToken.IDENTIFIER) {
                String nombreVar = tokens.get(i).lexema;
                for (int j = i + 1; j < tokens.size() - 1 && tokens.get(j).linea == tokens.get(i).linea; j++) {
                    if (tokens.get(j).tipo == TipoToken.ASSIGN) {
                        Tokens valorToken = tokens.get(j + 1);
                        if (valorToken.tipo == TipoToken.NUMBER_INT) {
                            mapaValoresTexto.put(nombreVar, valorToken.lexema);
                            try { mapaValoresNum.put(nombreVar, Double.parseDouble(valorToken.lexema)); } catch (Exception ignored) {}
                        } else if (valorToken.tipo == TipoToken.NUMBER_DOUBLE) {
                            String lex = valorToken.lexema;
                            if (!lex.contains(".")) lex += ".0";
                            mapaValoresTexto.put(nombreVar, lex);
                            try { mapaValoresNum.put(nombreVar, Double.parseDouble(lex)); } catch (Exception ignored) {}
                        }
                        break;
                    }
                }
            }
        }

        String[] lineas = codigoOriginal.split("\n");
        StringBuilder codigoLimpio = new StringBuilder();

        boolean enBloqueMuerto = false;
        boolean trasSalidaGlobal = false;
        int nivelLlavesMuertas = 0;

        for (int i = 0; i < lineas.length; i++) {
            String lineaOriginal = lineas[i];
            String lineaTrimmed = lineaOriginal.trim();

            // A) ELIMINACIÓN DE ESPACIOS EN BLANCO Y COMENTARIOS
            if (lineaTrimmed.isEmpty()) {
                bitacora.add("[Código Muerto] Línea " + (i + 1) + ": Se eliminó espacio en blanco inservible.");
                continue;
            }

            if (lineaTrimmed.startsWith("//") || lineaTrimmed.startsWith("/*")) {
                bitacora.add("[Código Muerto] Línea " + (i + 1) + ": Se eliminó comentario ('" + lineaTrimmed + "').");
                continue;
            }

            // B) RETORNOS DE CARRO (Código inalcanzable tras return)
            if (trasSalidaGlobal) {
                bitacora.add("[Código Muerto - Retorno de Carro] Línea " + (i + 1) + ": Instrucción inalcanzable tras salida omitida ('" + lineaTrimmed + "').");
                continue;
            }

            // C) CONDICIONES IMPOSIBLES (if false, if b == 5)
            boolean esCondicionFalsa = false;
            if (lineaTrimmed.matches(".*\\b(if|while)\\s+false\\b.*")) {
                esCondicionFalsa = true;
            } else if (lineaTrimmed.startsWith("if ")) {
                for (Map.Entry<String, Double> entry : mapaValoresNum.entrySet()) {
                    String varName = entry.getKey();
                    double val = entry.getValue();

                    if (lineaTrimmed.matches(".*\\b" + varName + "\\b.*")) {
                        String expresionFormateada = lineaTrimmed.replaceAll("\\b" + varName + "\\b", String.valueOf((int) val));
                        if (expresionFormateada.contains("==")) {
                            String[] partes = expresionFormateada.replace("if", "").replace("{", "").trim().split("==");
                            if (partes.length == 2) {
                                try {
                                    double izq = Double.parseDouble(partes[0].trim());
                                    double der = Double.parseDouble(partes[1].trim());
                                    if (izq != der) {
                                        esCondicionFalsa = true;
                                    }
                                } catch (Exception ignored) {}
                            }
                        }
                    }
                }
            }

            if (esCondicionFalsa) {
                enBloqueMuerto = true;
                nivelLlavesMuertas = 0;
                if (lineaTrimmed.contains("{")) nivelLlavesMuertas++;
                bitacora.add("[Código Muerto] Línea " + (i + 1) + ": Condición inalcanzable omitida ('" + lineaTrimmed + "').");
                continue;
            }

            if (enBloqueMuerto) {
                if (lineaTrimmed.contains("{")) nivelLlavesMuertas++;
                if (lineaTrimmed.contains("}")) {
                    if (nivelLlavesMuertas > 1) {
                        nivelLlavesMuertas--;
                    } else {
                        enBloqueMuerto = false;
                        bitacora.add("[Código Muerto] Línea " + (i + 1) + ": Cierre de bloque inalcanzable omitido.");
                        continue;
                    }
                }
                bitacora.add("[Código Muerto] Línea " + (i + 1) + ": Instrucción omitida dentro de bloque inalcanzable.");
                continue;
            }

            // D) OPTIMIZACIÓN LOCAL: Propagación de constantes respetando tipos exactos
            String lineaModificada = lineaOriginal;
            if (lineaTrimmed.startsWith("var ") || lineaTrimmed.startsWith("let ")) {
                if (lineaTrimmed.contains("=") && !lineaTrimmed.contains("print")) {
                    for (Map.Entry<String, String> entry : mapaValoresTexto.entrySet()) {
                        String vName = entry.getKey();
                        String vValStr = entry.getValue();

                        if (lineaModificada.matches(".*=\\s*.*\\b" + vName + "\\b.*")) {
                            lineaModificada = lineaModificada.replaceAll("\\b" + vName + "\\b", vValStr);
                            bitacora.add("[Optimización Local] Línea " + (i + 1) + ": Propagada constante '" + vName + "' = " + vValStr);
                        }
                    }
                }
            }

            // Detectar instrucción de retorno
            if (lineaTrimmed.equals("return") || lineaTrimmed.startsWith("return ") || lineaTrimmed.equals("break")) {
                trasSalidaGlobal = true;
                bitacora.add("[Código Muerto - Retorno de Carro] Línea " + (i + 1) + ": Detectada instrucción de salida ('" + lineaTrimmed + "'). Todo lo subsecuente es inalcanzable.");
                codigoLimpio.append(lineaModificada).append("\n");
                continue;
            }

            codigoLimpio.append(lineaModificada).append("\n");
        }

        if (bitacora.isEmpty()) {
            bitacora.add("No se detectó código muerto.");
        }

        return new ResultadoOptimizacion(codigoLimpio.toString(), bitacora);
    }
}