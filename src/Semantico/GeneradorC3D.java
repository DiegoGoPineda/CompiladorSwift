package Semantico;

import java.util.ArrayList;
import java.util.List;

public class GeneradorC3D {
    private final List<String> instrucciones = new ArrayList<>();
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
    }

    public void emitirEtiqueta(String etiqueta) {
        instrucciones.add(etiqueta + ":");
    }

    public List<String> getInstrucciones() {
        return instrucciones;
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
        contadorTemporales = 1;
        contadorEtiquetas = 1;
    }
}