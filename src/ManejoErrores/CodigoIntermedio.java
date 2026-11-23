
/* CENTENO DOMINGUEZ EDGAR YAIR, RAMOS GUTIERREZ RODRIGO, RODRIGUEZ ROMERO ALEJANDRO */
package ManejoErrores;

import java.util.ArrayList;
import java.util.List;

/** Codigo intermedio de tres direcciones para operaciones y asignaciones. */
public class CodigoIntermedio {
    public static class Nodo {
        public final String clase, valor;
        public final Nodo izquierdo, derecho;

        private Nodo(String clase, String valor, Nodo izquierdo, Nodo derecho) {
            this.clase = clase;
            this.valor = valor;
            this.izquierdo = izquierdo;
            this.derecho = derecho;
        }

        public static Nodo literal(String valor) {
            return new Nodo("LITERAL", valor, null, null);
        }

        public static Nodo variable(String nombre) {
            return new Nodo("VARIABLE", nombre, null, null);
        }

        public static Nodo operacion(String operador, Nodo izquierdo, Nodo derecho) {
            if (izquierdo == null || (derecho == null && !"!".equals(operador))) {
                return null;
            }
            return new Nodo("OPERACION", operador, izquierdo, derecho);
        }

        public boolean esCeroLiteral() {
            if (!"LITERAL".equals(clase)) return false;
            try { return Double.parseDouble(valor) == 0.0; }
            catch (NumberFormatException ex) { return false; }
        }
    }

    private final List<String> instrucciones = new ArrayList<>();
    private int consecutivo = 0;

    private String nuevoTemporal() {
        return "t" + (++consecutivo);
    }

    private String resolver(Nodo nodo, List<String> salida) {
        if ("VARIABLE".equals(nodo.clase)) return nodo.valor;
        if ("LITERAL".equals(nodo.clase)) {
            String temporal = nuevoTemporal();
            salida.add(temporal + " = " + nodo.valor);
            return temporal;
        }
        String izquierda = resolver(nodo.izquierdo, salida);
        String derecha = nodo.derecho == null ? null : resolver(nodo.derecho, salida);

        if (("/".equals(nodo.valor) || "%".equals(nodo.valor))
                && !nodo.derecho.esCeroLiteral()
                && !"LITERAL".equals(nodo.derecho.clase)) {
            salida.add("if " + derecha + " == 0 then error \"Division por cero\"");
        }

        String temporal = nuevoTemporal();
        if (derecha == null) salida.add(temporal + " = !" + izquierda);
        else salida.add(temporal + " = " + izquierda + " " + nodo.valor + " " + derecha);
        return temporal;
    }

    /** Confirma la traduccion solo de asignaciones semanticamente validas. */
    public void asignar(String destino, ManejadorTipos.Expresion expresion) {
        if (expresion == null || "ERROR".equals(expresion.tipo) || expresion.nodo == null) {
            return;
        }
        List<String> nuevas = new ArrayList<>();
        String origen = resolver(expresion.nodo, nuevas);
        nuevas.add(destino + " = " + origen);
        instrucciones.addAll(nuevas);
    }

    public void imprimir() {
        System.out.println("\n================ CODIGO INTERMEDIO (3AC) ================");
        for (String instruccion : instrucciones) System.out.println(instruccion);
        System.out.println("Total de instrucciones 3AC: " + instrucciones.size());
        System.out.println("Total de temporales: " + consecutivo);
        System.out.println("=========================================================\n");
    }
}
