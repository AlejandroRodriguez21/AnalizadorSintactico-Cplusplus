
package ManejoErrores;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Map;

/** Registro dinamico de identificadores y sus ambitos. */
public class TablaSimbolos {
    public static class Referencia {
        public final String id;
        public final int linea;
        public final int columna;

        public Referencia(String id, int linea, int columna) {
            this.id = id;
            this.linea = linea;
            this.columna = columna;
        }
    }

    private final Map<String, Simbolo> simbolos = new LinkedHashMap<>();
    private final Deque<String> ambitos = new ArrayDeque<>();
    private final Deque<Boolean> aperturasBloque = new ArrayDeque<>();
    private boolean primerBloqueMetodo = false;
    private int numeroBloque = 0;
    private int errores = 0;

    public TablaSimbolos() {
        ambitos.push("GLOBAL");
    }

    public String ambitoActual() {
        return ambitos.peek();
    }

    public void abrirClase(String id, int linea, int columna) {
        registrar(id, "CLASE", "void", "N/A", linea, columna);
        ambitos.push(id);
    }

    public void cerrarClase() {
        ambitos.pop();
    }

    public void abrirMetodo(String id, String tipo, int linea, int columna) {
        registrar(id, "METODO", tipo, "N/A", linea, columna);
        ambitos.push(ambitoActual() + "." + id);
        primerBloqueMetodo = true;
    }

    public void cerrarMetodo() {
        ambitos.pop();
        primerBloqueMetodo = false;
    }

    public void abrirBloque() {
        if (primerBloqueMetodo) {
            primerBloqueMetodo = false;
            aperturasBloque.push(false);
        } else {
            ambitos.push(ambitoActual() + ".bloque" + (++numeroBloque));
            aperturasBloque.push(true);
        }
    }

    public void cerrarBloque() {
        if (aperturasBloque.pop()) {
            ambitos.pop();
        }
    }

    public void registrar(String id, String categoria, String tipo,
                         String valor, int linea, int columna) {
        if (categoria.equals("CLASE")) {
            if (!id.matches("[A-Z][a-zA-Z0-9]*")) {
                error(linea, columna, "La clase '" + id + "' debe usar PascalCase.");
            }
        } else if (categoria.equals("CONSTANTE")) {
            if (!id.matches("[A-Z][A-Z0-9]*(?:_[A-Z0-9]+)*")
                    && !id.matches("[a-z][a-zA-Z0-9]*")) {
                error(linea, columna, "La constante '" + id + "' debe usar SNAKE_CASE o camelCase.");
            }
        } else if (!id.matches("[a-z][a-zA-Z0-9]*")) {
            error(linea, columna, "El identificador '" + id + "' de "
                    + categoria.toLowerCase() + " debe usar camelCase.");
        }

        String clave = ambitoActual() + "|" + id;
        if (simbolos.containsKey(clave)) {
            error(linea, columna, "El identificador '" + id
                    + "' ya fue declarado en el ambito '" + ambitoActual() + "'.");
            return;
        }

        simbolos.put(clave, new Simbolo(id, categoria, tipo, valor,
                ambitoActual(), linea, columna, "IDENTIFICADOR"));
    }

    public void verificarUso(String id, int linea, int columna) {
        for (String ambito : ambitos) {
            if (simbolos.containsKey(ambito + "|" + id)) {
                return;
            }
        }
        error(linea, columna, "El identificador '" + id
                + "' NO ha sido declarado en el ambito '" + ambitoActual() + "'.");
    }

    public void error(int linea, int columna, String motivo) {
        errores++;
        System.err.println("Error semantico en linea " + linea
                + ", columna " + columna + ": " + motivo);
    }

    public int getErrores() {
        return errores;
    }

    public void imprimir() {
        System.out.println("\n========================= TABLA DE SIMBOLOS =========================");
        System.out.printf("%-20s %-11s %-10s %-18s %-29s %5s %7s %s%n",
                "ID", "Categoria", "Tipo", "Valor", "Ambito", "Linea", "Columna", "Token");
        System.out.println("-----------------------------------------------------------------------------------------------");
        for (Simbolo s : simbolos.values()) {
            System.out.printf("%-20s %-11s %-10s %-18s %-29s %5d %7d %s%n",
                    s.id, s.tiposimbolo, s.tipodato, s.valor, s.ambito,
                    s.linea, s.columna, s.token);
        }
        System.out.println("Total de simbolos registrados: " + simbolos.size());
        System.out.println("Total de errores semanticos: " + errores);
        System.out.println("=====================================================================\n");
    }
}
