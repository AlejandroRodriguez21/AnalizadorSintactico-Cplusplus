package ManejoErrores;

/** Reglas de compatibilidad de tipos para las expresiones de C++. */
public class ManejadorTipos {
    public static class Expresion {
        public final String tipo;
        public final String texto;

        public Expresion(String tipo, String texto) {
            this.tipo = tipo;
            this.texto = texto;
        }
    }

    public static Expresion literal(String tipo, String texto) {
        return new Expresion(tipo, texto);
    }

    public static Expresion identificador(TablaSimbolos tabla, String nombre, int linea, int columna) {
        Simbolo simbolo = tabla.buscarUso(nombre, linea, columna);
        if (simbolo == null) {
            return literal("ERROR", nombre);
        }
        if ("METODO".equals(simbolo.tiposimbolo)) {
            tabla.error(linea, columna, nombre, "El metodo '" + nombre + "' debe invocarse con parentesis.");
            return literal("ERROR", nombre);
        }
        return literal(simbolo.tipodato, nombre);
    }

    private static boolean numerico(String tipo) {
        return "int".equals(tipo) || "float".equals(tipo) || "double".equals(tipo);
    }

    private static String promocionar(String a, String b) {
        if ("double".equals(a) || "double".equals(b)) return "double";
        if ("float".equals(a) || "float".equals(b)) return "float";
        return "int";
    }

    public static boolean asignable(String destino, String origen) {
        if ("ERROR".equals(origen)) return false;
        if (destino.equals(origen)) return true;
        // Conversion numerica ampliativa: int -> float/double, float -> double.
        return ("float".equals(destino) && "int".equals(origen))
            || ("double".equals(destino) && ("int".equals(origen) || "float".equals(origen)));
    }

    public static Expresion aritmetica(TablaSimbolos tabla, String operador,
            Expresion a, Expresion b, int linea, int columna) {
        String texto = a.texto + " " + operador + " " + b.texto;
        if ("ERROR".equals(a.tipo) || "ERROR".equals(b.tipo)) return literal("ERROR", texto);
        if ("%".equals(operador) && "int".equals(a.tipo) && "int".equals(b.tipo)) {
            System.out.println("Tipos: int % int -> int");
            return literal("int", texto);
        }
        if (!"%".equals(operador) && numerico(a.tipo) && numerico(b.tipo)) {
            String tipo = promocionar(a.tipo, b.tipo);
            System.out.println("Tipos: " + a.tipo + " " + operador + " " + b.tipo + " -> " + tipo);
            return literal(tipo, texto);
        }
        tabla.error(linea, columna, operador,
                "El operador aritmetico '" + operador + "' no es aplicable a '"
                + a.tipo + "' y '" + b.tipo + "'.");
        return literal("ERROR", texto);
    }

    public static Expresion relacional(TablaSimbolos tabla, String operador,
            Expresion a, Expresion b, int linea, int columna) {
        String texto = a.texto + " " + operador + " " + b.texto;
        if ("ERROR".equals(a.tipo) || "ERROR".equals(b.tipo)) return literal("ERROR", texto);
        boolean compatible = numerico(a.tipo) && numerico(b.tipo);
        if ("==".equals(operador) || "!=".equals(operador)) {
            compatible = compatible || (a.tipo.equals(b.tipo)
                    && ("bool".equals(a.tipo) || "string".equals(a.tipo)));
        }
        if (compatible) {
            System.out.println("Tipos: " + a.tipo + " " + operador + " " + b.tipo + " -> bool");
            return literal("bool", texto);
        }
        tabla.error(linea, columna, operador,
                "No se pueden comparar los tipos '" + a.tipo + "' y '" + b.tipo
                + "' con el operador '" + operador + "'.");
        return literal("ERROR", texto);
    }

    public static Expresion logica(TablaSimbolos tabla, String operador,
            Expresion a, Expresion b, int linea, int columna) {
        String texto = a.texto + " " + operador + " " + b.texto;
        if ("ERROR".equals(a.tipo) || "ERROR".equals(b.tipo)) return literal("ERROR", texto);
        if ("bool".equals(a.tipo) && "bool".equals(b.tipo)) {
            System.out.println("Tipos: bool " + operador + " bool -> bool");
            return literal("bool", texto);
        }
        tabla.error(linea, columna, operador,
                "El operador logico '" + operador + "' requiere booleanos; se recibieron '"
                + a.tipo + "' y '" + b.tipo + "'.");
        return literal("ERROR", texto);
    }

    public static Expresion negacion(TablaSimbolos tabla, Expresion valor,
            int linea, int columna) {
        if ("ERROR".equals(valor.tipo)) return literal("ERROR", "!" + valor.texto);
        if ("bool".equals(valor.tipo)) {
            System.out.println("Tipos: !bool -> bool");
            return literal("bool", "!" + valor.texto);
        }
        tabla.error(linea, columna, "!", "El operador ! requiere un operando bool y recibio '"
                + valor.tipo + "'.");
        return literal("ERROR", "!" + valor.texto);
    }

    public static void validarCondicion(TablaSimbolos tabla, String estructura,
            Expresion expresion, int linea, int columna) {
        if (!"ERROR".equals(expresion.tipo) && !"bool".equals(expresion.tipo)) {
            tabla.error(linea, columna, estructura,
                    "La condicion de '" + estructura + "' debe ser bool y se obtuvo '"
                    + expresion.tipo + "'.");
        }
    }
}