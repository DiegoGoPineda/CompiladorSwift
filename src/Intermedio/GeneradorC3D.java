package Intermedio;

import java.util.ArrayList;
import java.util.List;

public class GeneradorC3D {
    private final List<String> instrucciones = new ArrayList<>();
    private final List<Cuadruplo> listaCuadruplos = new ArrayList<>();
    private int contadorTemporales = 1;
    private int contadorEtiquetas = 1;

    public String nuevoTemporal() {
        return "t" + (contadorTemporales++);
    }

    public String nuevaEtiqueta() {
        return "L" + (contadorEtiquetas++);
    }

    public void emitir(String instruccion) {
        instrucciones.add(instruccion);
        
        // Generar el cuádruplo equivalente automáticamente
        generarCuadruploDesdeInstruccion(instruccion);
    }

    public void emitirEtiqueta(String etiqueta) {
        instrucciones.add(etiqueta + ":");
        listaCuadruplos.add(new Cuadruplo("LABEL", "", "", etiqueta + ":"));
    }

    public List<String> getInstrucciones() {
        return instrucciones;
    }

    public List<Cuadruplo> getListaCuadruplos() {
        return listaCuadruplos;
    }

    // Parsea las instrucciones C3D estándar a Cuádruplos
    private void generarCuadruploDesdeInstruccion(String ins) {
        ins = ins.trim();
        
        // Caso: Asignación u Operación (res = arg1 op arg2 O res = arg1)
        if (ins.contains("=")) {
            String[] partes = ins.split("=");
            String res = partes[0].trim();
            String expr = partes[1].trim();

            String[] tokens = expr.split(" ");
            if (tokens.length == 3) {
                // Asignación binaria: t1 = a + b -> (+, a, b, t1)
                listaCuadruplos.add(new Cuadruplo(tokens[1], tokens[0], tokens[2], res));
            } else if (tokens.length == 1) {
                // Asignación simple: x = t1 -> (=, t1, , x)
                listaCuadruplos.add(new Cuadruplo("=", tokens[0], "", res));
            }
        } 
        // Caso: Salto condicional (if_false cond goto L1)
        else if (ins.startsWith("if_false")) {
            String[] partes = ins.split(" ");
            // (if_false, cond, , L1)
            listaCuadruplos.add(new Cuadruplo("if_false", partes[1], "", partes[3]));
        } 
        // Caso: Salto incondicional (goto L1)
        else if (ins.startsWith("goto")) {
            String[] partes = ins.split(" ");
            // (goto, , , L1)
            listaCuadruplos.add(new Cuadruplo("goto", "", "", partes[1]));
        }
        // Caso: Print o llamadas
        else if (ins.startsWith("param")) {
            String[] partes = ins.split(" ");
            listaCuadruplos.add(new Cuadruplo("param", partes[1], "", ""));
        } else if (ins.startsWith("call")) {
            listaCuadruplos.add(new Cuadruplo("call", "print", "1", ""));
        }
    }

    public String obtenerCodigoTexto() {
        StringBuilder sb = new StringBuilder();
        for (String ins : instrucciones) {
            if (ins.endsWith(":")) {
                sb.append(ins).append("\n");
            } else {
                sb.append("    ").append(ins).append("\n");
            }
        }
        return sb.toString();
    }

    public void limpiar() {
        instrucciones.clear();
        listaCuadruplos.clear();
        contadorTemporales = 1;
        contadorEtiquetas = 1;
    }
}