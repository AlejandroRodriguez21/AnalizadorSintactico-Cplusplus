
/* CENTENO DOMINGUEZ EDGAR YAIR, RAMOS GUTIERREZ RODRIGO, RODRIGUEZ ROMERO ALEJANDRO */
package ManejoErrores;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/** Traduccion de expresiones y flujo de control a codigo de tres direcciones. */
public class CodigoIntermedio {
    public static class Nodo {
        public final String clase, valor;
        public final Nodo izquierdo, derecho;
        public final List<Nodo> argumentos;
        public final String objeto;

        private Nodo(String clase, String valor, Nodo izq, Nodo der,
                     List<Nodo> argumentos, String objeto) {
            this.clase = clase;
            this.valor = valor;
            this.izquierdo = izq;
            this.derecho = der;
            this.argumentos = argumentos;
            this.objeto = objeto;
        }

        public static Nodo literal(String valor) {
            return new Nodo("LITERAL", valor, null, null, null, null);
        }

        public static Nodo variable(String nombre) {
            return new Nodo("VARIABLE", nombre, null, null, null, null);
        }

        public static Nodo operacion(String operador, Nodo izq, Nodo der) {
            if (izq == null || (der == null && !"!".equals(operador))) return null;
            return new Nodo("OPERACION", operador, izq, der, null, null);
        }

        public static Nodo llamada(String etiqueta,
                List<ManejadorTipos.Expresion> expresiones, String objeto) {
            List<Nodo> nodos = new ArrayList<>();
            for (ManejadorTipos.Expresion e : expresiones) {
                if (e == null || e.nodo == null) return null;
                nodos.add(e.nodo);
            }
            return new Nodo("LLAMADA", etiqueta, null, null, nodos, objeto);
        }

        public boolean esCeroLiteral() {
            if (!"LITERAL".equals(clase)) return false;
            try {
                return Double.parseDouble(valor) == 0.0;
            } catch (NumberFormatException ex) {
                return false;
            }
        }
    }

    private static class Salto {
        final String fin, continuar;
        final boolean ciclo;

        Salto(String fin, String continuar, boolean ciclo) {
            this.fin = fin;
            this.continuar = continuar;
            this.ciclo = ciclo;
        }
    }

    private static class CicloFor {
        String inicio, actualizacion, fin;
        List<String> inicializacion, incremento;
    }

    private static class Caso {
        String valor, etiqueta;
        List<String> cuerpo;

        Caso(String v, String e, List<String> b) {
            valor = v;
            etiqueta = e;
            cuerpo = b;
        }
    }

    private static class Seleccion {
        String fin, porDefecto;
        ManejadorTipos.Expresion expresion;
        List<Caso> casos = new ArrayList<>();
        List<String> cuerpoDefecto;
    }

    private final List<String> instrucciones = new ArrayList<>();
    private final Deque<List<String>> capturas = new ArrayDeque<>();
    private final Deque<Salto> saltos = new ArrayDeque<>();
    private final Deque<CicloFor> ciclosFor = new ArrayDeque<>();
    private final Deque<Seleccion> selecciones = new ArrayDeque<>();
    private final Deque<String> etiquetasMetodo = new ArrayDeque<>();
    private final Deque<String> iniciosDo = new ArrayDeque<>();
    private int temporal = 0, etiqueta = 0;

    public String nuevaEtiqueta() {
        return "L" + (++etiqueta);
    }

    private String nuevoTemporal() {
        return "t" + (++temporal);
    }

    private List<String> salidaActual() {
        return capturas.isEmpty() ? instrucciones : capturas.peek();
    }

    private void agregar(String linea) {
        salidaActual().add(linea);
    }

    private void agregarTodo(List<String> lineas) {
        salidaActual().addAll(lineas);
    }

    private void capturar() {
        capturas.push(new ArrayList<>());
    }

    private List<String> terminarCaptura() {
        return capturas.pop();
    }

    private String resolver(Nodo nodo, List<String> salida) {
        if (nodo == null) return null;

        if ("VARIABLE".equals(nodo.clase)) return nodo.valor;

        if ("LITERAL".equals(nodo.clase)) {
            String t = nuevoTemporal();
            salida.add(t + " = " + nodo.valor);
            return t;
        }

        if ("LLAMADA".equals(nodo.clase)) {
            if (nodo.objeto != null)
                salida.add("param_this " + nodo.objeto);

            for (Nodo argumento : nodo.argumentos) {
                String valor = resolver(argumento, salida);
                if (valor == null) return null;
                salida.add("param " + valor);
            }

            salida.add("call " + nodo.valor + ", " + nodo.argumentos.size());
            String t = nuevoTemporal();
            salida.add(t + " = return_value");
            return t;
        }

        String a = resolver(nodo.izquierdo, salida);
        String b = nodo.derecho == null ? null : resolver(nodo.derecho, salida);

        if (a == null || (nodo.derecho != null && b == null))
            return null;

        if (("/".equals(nodo.valor) || "%".equals(nodo.valor))
                && !nodo.derecho.esCeroLiteral()
                && !"LITERAL".equals(nodo.derecho.clase)) {
            salida.add("if " + b + " == 0 then error \"Division por cero\"");
        }

        String t = nuevoTemporal();
        salida.add(t + " = " +
                (b == null ? "!" + a : a + " " + nodo.valor + " " + b));
        return t;
    }

    private String operando(ManejadorTipos.Expresion expr, List<String> lineas) {
        if (expr == null || "ERROR".equals(expr.tipo)) return null;
        return resolver(expr.nodo, lineas);
    }

    public void asignar(String destino, ManejadorTipos.Expresion expr) {
        List<String> partes = new ArrayList<>();
        String fuente = operando(expr, partes);
        if (fuente == null) return;

        partes.add(destino + " = " + fuente);
        agregarTodo(partes);
    }

    public void invocar(ManejadorTipos.Expresion expr) {
        if (expr == null || "ERROR".equals(expr.tipo)
                || expr.nodo == null) return;

        if (!"LLAMADA".equals(expr.nodo.clase)) return;

        List<String> partes = new ArrayList<>();
        Nodo n = expr.nodo;

        if (n.objeto != null)
            partes.add("param_this " + n.objeto);

        for (Nodo arg : n.argumentos) {
            String p = resolver(arg, partes);
            if (p == null) return;
            partes.add("param " + p);
        }

        partes.add("call " + n.valor + ", " + n.argumentos.size());
        agregarTodo(partes);
    }

    public void abrirClase(String nombre) {
        agregar("class " + nombre);
    }

    public void cerrarClase(String nombre) {
        agregar("end_class " + nombre);
    }

    public void abrirMetodo(String etiquetaMetodo) {
        etiquetasMetodo.push(etiquetaMetodo);
        capturar();
    }

    public void parametro(String nombre, String tipo) {
        agregar("parametro " + tipo + " " + nombre);
    }

    public void retornar(ManejadorTipos.Expresion expr) {
        if (expr == null) {
            agregar("return");
            return;
        }

        List<String> partes = new ArrayList<>();
        String resultado = operando(expr, partes);

        if (resultado != null) {
            partes.add("return " + resultado);
            agregarTodo(partes);
        }
    }

    public void cerrarMetodo() {
        List<String> cuerpo = terminarCaptura();
        String metodo = etiquetasMetodo.pop();

        agregar(metodo + ":");
        agregarTodo(cuerpo);
        agregar("end_method " + metodo);
    }

    public void crearObjeto(String nombre, String clase) {
        agregar(nombre + " = create " + clase);
    }

    public void iniciarIf() {
        capturar();
    }

    public List<String> iniciarElse() {
        List<String> verdadero = terminarCaptura();
        capturar();
        return verdadero;
    }

    public void finalizarIf(ManejadorTipos.Expresion condicion) {
        List<String> verdadero = terminarCaptura();
        List<String> partes = new ArrayList<>();
        String t = operando(condicion, partes);

        if (t == null) return;

        String fin = nuevaEtiqueta();

        agregarTodo(partes);
        agregar("if not " + t + " goto " + fin);
        agregarTodo(verdadero);
        agregar(fin + ":");
    }

    public void finalizarIfElse(ManejadorTipos.Expresion condicion,
                               List<String> verdadero) {
        List<String> falso = terminarCaptura();
        List<String> partes = new ArrayList<>();
        String t = operando(condicion, partes);

        if (t == null) return;

        String lFalso = nuevaEtiqueta();
        String fin = nuevaEtiqueta();

        agregarTodo(partes);
        agregar("if not " + t + " goto " + lFalso);
        agregarTodo(verdadero);
        agregar("goto " + fin);
        agregar(lFalso + ":");
        agregarTodo(falso);
        agregar(fin + ":");
    }

    public void iniciarWhile() {
        String inicio = nuevaEtiqueta();
        String fin = nuevaEtiqueta();

        saltos.push(new Salto(fin, inicio, true));
        capturar();
    }

    public void finalizarWhile(ManejadorTipos.Expresion condicion) {
        List<String> cuerpo = terminarCaptura();
        Salto s = saltos.pop();

        List<String> partes = new ArrayList<>();
        String t = operando(condicion, partes);

        if (t == null) return;

        agregar(s.continuar + ":");
        agregarTodo(partes);
        agregar("if not " + t + " goto " + s.fin);
        agregarTodo(cuerpo);
        agregar("goto " + s.continuar);
        agregar(s.fin + ":");
    }

    public void iniciarDoWhile() {
        String principio = nuevaEtiqueta();
        String condicion = nuevaEtiqueta();
        String fin = nuevaEtiqueta();

        saltos.push(new Salto(fin, condicion, true));
        iniciosDo.push(principio);
        capturar();
    }

    public void finalizarDoWhile(ManejadorTipos.Expresion condicion) {
        List<String> cuerpo = terminarCaptura();
        Salto s = saltos.pop();
        String inicio = iniciosDo.pop();

        List<String> partes = new ArrayList<>();
        String t = operando(condicion, partes);

        if (t == null) return;

        agregar(inicio + ":");
        agregarTodo(cuerpo);
        agregar(s.continuar + ":");
        agregarTodo(partes);
        agregar("if " + t + " goto " + inicio);
        agregar(s.fin + ":");
    }

    public void iniciarFor() {
        capturar();
    }

    public void iniciarActualizacionFor() {
        CicloFor f = new CicloFor();
        f.inicializacion = terminarCaptura();
        f.inicio = nuevaEtiqueta();
        f.actualizacion = nuevaEtiqueta();
        f.fin = nuevaEtiqueta();

        ciclosFor.push(f);
        saltos.push(new Salto(f.fin, f.actualizacion, true));
        capturar();
    }

    public void iniciarCuerpoFor() {
        ciclosFor.peek().incremento = terminarCaptura();
        capturar();
    }

    public void finalizarFor(ManejadorTipos.Expresion condicion) {
        List<String> cuerpo = terminarCaptura();
        CicloFor f = ciclosFor.pop();
        saltos.pop();

        List<String> partes = new ArrayList<>();
        String t = operando(condicion, partes);

        if (t == null) return;

        agregarTodo(f.inicializacion);
        agregar(f.inicio + ":");
        agregarTodo(partes);
        agregar("if not " + t + " goto " + f.fin);
        agregarTodo(cuerpo);
        agregar(f.actualizacion + ":");
        agregarTodo(f.incremento);
        agregar("goto " + f.inicio);
        agregar(f.fin + ":");
    }

    public void iniciarSwitch(ManejadorTipos.Expresion expresion) {
        Seleccion seleccion = new Seleccion();
        seleccion.expresion = expresion;
        seleccion.fin = nuevaEtiqueta();

        selecciones.push(seleccion);
        saltos.push(new Salto(seleccion.fin, null, false));
    }

    public void iniciarCaso() {
        capturar();
    }

    public void finalizarCaso(String valor) {
        List<String> cuerpo = terminarCaptura();
        selecciones.peek().casos.add(
                new Caso(valor, nuevaEtiqueta(), cuerpo));
    }

    public void iniciarDefecto() {
        capturar();
    }

    public void finalizarDefecto() {
        Seleccion s = selecciones.peek();
        s.cuerpoDefecto = terminarCaptura();
        s.porDefecto = nuevaEtiqueta();
    }

    public void finalizarSwitch() {
        Seleccion s = selecciones.pop();
        saltos.pop();

        List<String> partes = new ArrayList<>();
        String t = operando(s.expresion, partes);

        if (t == null) return;

        agregarTodo(partes);

        for (Caso caso : s.casos) {
            agregar("if " + t + " == " + caso.valor
                    + " goto " + caso.etiqueta);
        }

        agregar("goto " + (s.porDefecto == null ? s.fin : s.porDefecto));

        for (Caso caso : s.casos) {
            agregar(caso.etiqueta + ":");
            agregarTodo(caso.cuerpo);
        }

        if (s.porDefecto != null) {
            agregar(s.porDefecto + ":");
            agregarTodo(s.cuerpoDefecto);
        }

        agregar(s.fin + ":");
    }

    public void romper() {
        if (!saltos.isEmpty())
            agregar("goto " + saltos.peek().fin);
    }

    public void continuar() {
        for (Salto s : saltos) {
            if (s.ciclo) {
                agregar("goto " + s.continuar);
                return;
            }
        }
    }

    public void imprimir() {
        System.out.println(
                "\n================ CODIGO INTERMEDIO (3AC) ================");

        for (String linea : instrucciones)
            System.out.println(linea);

        System.out.println("Total de instrucciones 3AC: "
                + instrucciones.size());
        System.out.println("Total de temporales: " + temporal);
        System.out.println("Total de etiquetas: " + etiqueta);
        System.out.println("=========================================================");
    }
}
