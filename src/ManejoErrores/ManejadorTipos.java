
/* CENTENO DOMINGUEZ EDGAR YAIR, RAMOS GUTIERREZ RODRIGO, RODRIGUEZ ROMERO ALEJANDRO */
package ManejoErrores;

/** Reglas de compatibilidad de tipos y expresiones para el subconjunto de C++. */
public class ManejadorTipos {
    public static class Expresion {
        public final String tipo;
        public final String texto;
        public final CodigoIntermedio.Nodo nodo;

        public Expresion(String tipo, String texto, CodigoIntermedio.Nodo nodo) {
            this.tipo = tipo;
            this.texto = texto;
            this.nodo = nodo;
        }
    }

    public static Expresion literal(String tipo, String texto) {
        return new Expresion(tipo, texto,
                "ERROR".equals(tipo) ? null : CodigoIntermedio.Nodo.literal(texto));
    }

    public static Expresion referencia(String tipo, String nombre) {
        return new Expresion(tipo, nombre, CodigoIntermedio.Nodo.variable(nombre));
    }

    
    /** Conserva el tipo y el nodo necesario para emitir parametros y call. */
    public static Expresion llamada(String tipo, String texto,
                                    CodigoIntermedio.Nodo nodo) {
        return new Expresion(tipo, texto, nodo);
    }


    public static Expresion agrupar(Expresion expresion) {
        return new Expresion(expresion.tipo, "(" + expresion.texto + ")", expresion.nodo);
    }

    public static Expresion identificador(TablaSimbolos tabla, String nombre, int linea, int columna) {
        Simbolo simbolo = tabla.buscarUso(nombre, linea, columna);
        if (simbolo == null) return literal("ERROR", nombre);
        if ("METODO".equals(simbolo.tiposimbolo)) {
            tabla.error(linea, columna, nombre, "El metodo '" + nombre + "' debe invocarse con parentesis.");
            return literal("ERROR", nombre);
        }
        return referencia(simbolo.tipodato, nombre);
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
        return ("float".equals(destino) && "int".equals(origen))
            || ("double".equals(destino) && ("int".equals(origen) || "float".equals(origen)));
    }

    private static Expresion operacion(String tipo, String texto, String operador,
            Expresion a, Expresion b) {
        CodigoIntermedio.Nodo nodo = CodigoIntermedio.Nodo.operacion(operador,
                a.nodo, b == null ? null : b.nodo);
        return new Expresion(tipo, texto, nodo);
    }

    public static Expresion aritmetica(TablaSimbolos tabla, String operador,
            Expresion a, Expresion b, int linea, int columna) {
        String texto = a.texto + " " + operador + " " + b.texto;
        if ("ERROR".equals(a.tipo) || "ERROR".equals(b.tipo)) return literal("ERROR", texto);
        if ("%".equals(operador) && "int".equals(a.tipo) && "int".equals(b.tipo)) {
            if (b.nodo != null && b.nodo.esCeroLiteral()) {
                tabla.error(linea, columna, operador, "Modulo entre cero.");
                return literal("ERROR", texto);
            }
            System.out.println("Tipos: int % int -> int");
            return operacion("int", texto, operador, a, b);
        }
        if (!"%".equals(operador) && numerico(a.tipo) && numerico(b.tipo)) {
            if ("/".equals(operador) && b.nodo != null && b.nodo.esCeroLiteral()) {
                tabla.error(linea, columna, operador, "Division entre cero.");
                return literal("ERROR", texto);
            }
            String tipo = promocionar(a.tipo, b.tipo);
            System.out.println("Tipos: " + a.tipo + " " + operador + " " + b.tipo + " -> " + tipo);
            return operacion(tipo, texto, operador, a, b);
        }
        tabla.error(linea, columna, operador, "El operador aritmetico '" + operador
                + "' no es aplicable a '" + a.tipo + "' y '" + b.tipo + "'.");
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
            return operacion("bool", texto, operador, a, b);
        }
        tabla.error(linea, columna, operador, "No se pueden comparar los tipos '" + a.tipo
                + "' y '" + b.tipo + "' con el operador '" + operador + "'.");
        return literal("ERROR", texto);
    }

    public static Expresion logica(TablaSimbolos tabla, String operador,
            Expresion a, Expresion b, int linea, int columna) {
        String texto = a.texto + " " + operador + " " + b.texto;
        if ("ERROR".equals(a.tipo) || "ERROR".equals(b.tipo)) return literal("ERROR", texto);
        if ("bool".equals(a.tipo) && "bool".equals(b.tipo)) {
            System.out.println("Tipos: bool " + operador + " bool -> bool");
            return operacion("bool", texto, operador, a, b);
        }
        tabla.error(linea, columna, operador, "El operador logico '" + operador
                + "' requiere booleanos; se recibieron '" + a.tipo + "' y '" + b.tipo + "'.");
        return literal("ERROR", texto);
    }

    public static Expresion negacion(TablaSimbolos tabla, Expresion valor,
            int linea, int columna) {
        if ("ERROR".equals(valor.tipo)) return literal("ERROR", "!" + valor.texto);
        if ("bool".equals(valor.tipo)) {
            System.out.println("Tipos: !bool -> bool");
            return operacion("bool", "!" + valor.texto, "!", valor, null);
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
