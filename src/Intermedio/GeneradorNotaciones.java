package Intermedio;

import Lexico.Tokens;
import Lexico.TipoToken;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeneradorNotaciones {

    private int precedencia(Tokens t) {
        switch (t.tipo) {
            case MULTIPLY: case DIVIDE: return 3;
            case PLUS: case MINUS: return 2;
            case GREATER: case LESS: case GREATER_EQUAL: 
            case LESS_EQUAL: case EQUALS: case NOT_EQUALS: return 1;
            default: return -1;
        }
    }

    private boolean esOperando(Tokens t) {
        return t.tipo == TipoToken.IDENTIFIER || 
               t.tipo == TipoToken.NUMBER_INT || 
               t.tipo == TipoToken.NUMBER_DOUBLE;
    }

    private boolean esOpToken(Tokens t) {
        return precedencia(t) > 0;
    }

    public List<Tokens> infijaAPosfija(List<Tokens> infija) {
        List<Tokens> posfija = new ArrayList<>();
        Deque<Tokens> pila = new ArrayDeque<>();

        for (Tokens t : infija) {
            if (esOperando(t)) {
                posfija.add(t);
            } else if (t.tipo == TipoToken.LPAREN) {
                pila.push(t);
            } else if (t.tipo == TipoToken.RPAREN) {
                while (!pila.isEmpty() && pila.peek().tipo != TipoToken.LPAREN) {
                    posfija.add(pila.pop());
                }
                if (!pila.isEmpty()) pila.pop();
            } else if (esOpToken(t)) {
                while (!pila.isEmpty() && precedencia(pila.peek()) >= precedencia(t)) {
                    posfija.add(pila.pop());
                }
                pila.push(t);
            }
        }
        while (!pila.isEmpty()) {
            posfija.add(pila.pop());
        }
        return posfija;
    }

    public List<Tokens> infijaAPrefija(List<Tokens> infija) {
        List<Tokens> invertida = new ArrayList<>(infija);
        Collections.reverse(invertida);

        for (int i = 0; i < invertida.size(); i++) {
            Tokens t = invertida.get(i);
            if (t.tipo == TipoToken.LPAREN) {
                invertida.set(i, new Tokens(TipoToken.RPAREN, ")", t.valor, t.linea));
            } else if (t.tipo == TipoToken.RPAREN) {
                invertida.set(i, new Tokens(TipoToken.LPAREN, "(", t.valor, t.linea));
            }
        }

        List<Tokens> posfijaInvertida = infijaAPosfija(invertida);
        Collections.reverse(posfijaInvertida);
        return posfijaInvertida;
    }

    // --- EVALUADOR DE EXPRESIONES (Cálculo del resultado real) ---
    public double evaluarPosfija(List<Tokens> posfija, Map<String, Double> valoresVariables) {
        Deque<Double> pila = new ArrayDeque<>();

        for (Tokens t : posfija) {
            if (esOperando(t)) {
                double val = 0.0;
                if (t.tipo == TipoToken.IDENTIFIER) {
                    val = valoresVariables.getOrDefault(t.lexema, 0.0);
                } else {
                    val = Double.parseDouble(t.lexema);
                }
                pila.push(val);
            } else if (esOpToken(t)) {
                if (pila.size() < 2) continue;
                double b = pila.pop();
                double a = pila.pop();
                switch (t.tipo) {
                    case PLUS: pila.push(a + b); break;
                    case MINUS: pila.push(a - b); break;
                    case MULTIPLY: pila.push(a * b); break;
                    case DIVIDE: pila.push(b != 0 ? a / b : 0.0); break;
                    default: break;
                }
            }
        }
        return pila.isEmpty() ? 0.0 : pila.pop();
    }

    public List<String[]> cuadruplosPostfijo(List<Tokens> post) {
        List<String[]> lista = new ArrayList<>();
        Deque<String> pila = new ArrayDeque<>();
        int tmp = 1;

        for (Tokens t : post) {
            if (esOperando(t)) {
                pila.push(t.lexema);
            } else if (esOpToken(t)) {
                String op2 = pila.isEmpty() ? "?" : pila.pop();
                String op1 = pila.isEmpty() ? "?" : pila.pop();
                String res = "t" + tmp++;
                lista.add(new String[]{t.lexema, op1, op2, res});
                pila.push(res);
            }
        }
        return lista;
    }

    public List<String[]> cuadruplosPrefija(List<Tokens> pre) {
        List<String[]> lista = new ArrayList<>();
        Deque<String> pila = new ArrayDeque<>();
        int tmp = 1;

        for (int i = pre.size() - 1; i >= 0; i--) {
            Tokens t = pre.get(i);
            if (esOperando(t)) {
                pila.push(t.lexema);
            } else if (esOpToken(t)) {
                String op1 = pila.isEmpty() ? "?" : pila.pop();
                String op2 = pila.isEmpty() ? "?" : pila.pop();
                String res = "T" + tmp++;
                lista.add(new String[]{t.lexema, op1, op2, res});
                pila.push(res);
            }
        }
        return lista;
    }

    // --- GENERACIÓN DE REPORTES CON RESULTADO INTEGRADO ---

    public String generarReportePostfijo(List<Tokens> infija, int numExpresion, int linea, Map<String, Double> mapaValores) {
        List<Tokens> post = infijaAPosfija(infija);
        List<String[]> cuadruplos = cuadruplosPostfijo(post);
        double resultadoNum = evaluarPosfija(post, mapaValores);

        StringBuilder sb = new StringBuilder();
        sb.append("=========================================================\n");
        sb.append("            CONVERSIÓN A NOTACIÓN POSTFIJA               \n");
        sb.append("=========================================================\n\n");
        sb.append("Total de expresiones encontradas: 1\n\n");
        sb.append("---------------------------------------------------------\n");
        sb.append("Expresión #").append(numExpresion).append("   [Línea ").append(linea).append("]\n");
        sb.append("---------------------------------------------------------\n");

        sb.append("► Infija   : ");
        for (Tokens t : infija) sb.append(t.lexema).append(" ");
        sb.append("\n");

        sb.append("► Postfija : ");
        for (Tokens t : post) sb.append(t.lexema).append(" ");
        sb.append("\n");

        sb.append("► Resultado: ").append(resultadoNum).append("\n\n");

        sb.append("CUÁDRUPLOS (Postfijo):\n");
        sb.append(String.format("%-5s %-10s %-12s %-12s %-10s\n", "No.", "Operador", "Operando1", "Operando2", "Resultado"));
        sb.append("---------------------------------------------------------\n");

        int num = 1;
        for (String[] c : cuadruplos) {
            sb.append(String.format("%-5d %-10s %-12s %-12s %-10s\n", num++, c[0], c[1], c[2], c[3]));
        }
        sb.append("---------------------------------------------------------\n");
        return sb.toString();
    }

    public String generarReportePrefijo(List<Tokens> infija, int numExpresion, int linea, Map<String, Double> mapaValores) {
        List<Tokens> pre = infijaAPrefija(infija);
        List<Tokens> post = infijaAPosfija(infija);
        List<String[]> cuadruplos = cuadruplosPrefija(pre);
        double resultadoNum = evaluarPosfija(post, mapaValores);

        StringBuilder sb = new StringBuilder();
        sb.append("=========================================================\n");
        sb.append("            CONVERSIÓN A NOTACIÓN PREFIJA                \n");
        sb.append("=========================================================\n\n");
        sb.append("Total de expresiones encontradas: 1\n\n");
        sb.append("---------------------------------------------------------\n");
        sb.append("Expresión #").append(numExpresion).append("   [Línea ").append(linea).append("]\n");
        sb.append("---------------------------------------------------------\n");

        sb.append("► Infija   : ");
        for (Tokens t : infija) sb.append(t.lexema).append(" ");
        sb.append("\n");

        sb.append("► Prefija  : ");
        for (Tokens t : pre) sb.append(t.lexema).append(" ");
        sb.append("\n");

        sb.append("► Resultado: ").append(resultadoNum).append("\n\n");

        sb.append("CUÁDRUPLOS (Prefijo):\n");
        sb.append(String.format("%-5s %-10s %-12s %-12s %-10s\n", "No.", "Operador", "Operando1", "Operando2", "Resultado"));
        sb.append("---------------------------------------------------------\n");

        int num = 1;
        for (String[] c : cuadruplos) {
            sb.append(String.format("%-5d %-10s %-12s %-12s %-10s\n", num++, c[0], c[1], c[2], c[3]));
        }
        sb.append("---------------------------------------------------------\n");
        return sb.toString();
    }
}