package Semantico;

import java.util.HashMap;
import java.util.Map;

public class TablaSimbolos {
    private final Map<String, Simbolo> tabla = new HashMap<>();

    public boolean insertar(Simbolo simbolo) {
        if (tabla.containsKey(simbolo.getNombre())) {
            return false; // Ya existe en la tabla (redeclaración)
        }
        tabla.put(simbolo.getNombre(), simbolo);
        return true;
    }

    public Simbolo buscar(String nombre) {
        return tabla.get(nombre);
    }

    public boolean contiene(String nombre) {
        return tabla.containsKey(nombre);
    }

    public void limpiar() {
        tabla.clear();
    }
}