
/* CENTENO DOMINGUEZ EDGAR YAIR, RAMOS GUTIERREZ RODRIGO, RODRIGUEZ ROMERO ALEJANDRO */
package ManejoErrores;

import java.util.*;

/** Tabla dinamica: identificadores, clases, firmas, visibilidad y ambitos. */
public class TablaSimbolos {
    public static class Referencia {
        public final String id;
        public final int linea, columna;
        public Referencia(String id, int linea, int columna) {
            this.id=id; this.linea=linea; this.columna=columna;
        }
    }
    public static class FirmaMetodo {
        public final String nombre, clase, retorno, acceso;
        public String etiqueta;
        public final int linea, columna;
        public final List<String> parametros = new ArrayList<>();
        public FirmaMetodo(String n, String c, String t, String a, int l, int col) {
            nombre=n; clase=c; retorno=t; acceso=a; linea=l; columna=col;
        }
    }
    private final Map<String, Simbolo> simbolos = new LinkedHashMap<>();
    private final CodigoIntermedio codigoIntermedio = new CodigoIntermedio();
    public CodigoIntermedio getCodigoIntermedio() { return codigoIntermedio; }
    private final Map<String, String> accesos = new LinkedHashMap<>();
    private final Map<String, List<FirmaMetodo>> firmas = new LinkedHashMap<>();
    private final Deque<String> ambitos = new ArrayDeque<>();
    private final Deque<Boolean> aperturasBloque = new ArrayDeque<>();
    private final Deque<Set<String>> casosSwitch = new ArrayDeque<>();
    private final Deque<String> tiposSwitch = new ArrayDeque<>();
    private FirmaMetodo metodoActual;
    private String claseActual;
    private String accesoActual="private"; // En class, C++ usa private por defecto.
    private boolean primerBloqueMetodo=false;
    private int bloques=0, ciclos=0, errores=0;

    public TablaSimbolos() { ambitos.push("GLOBAL"); }
    public String ambitoActual() { return ambitos.peek(); }
    public String claseActual() { return claseActual; }
    public void cambiarAcceso(String acceso) { accesoActual=acceso; }
    
    public void abrirClase(String id, int linea, int columna) {
        registrar(id, "CLASE", id, "N/A", linea, columna);
        codigoIntermedio.abrirClase(id);
        claseActual = id;
        ambitos.push(id);
        accesoActual = "private";
    }

    public void cerrarClase() {
        codigoIntermedio.cerrarClase(claseActual);
        ambitos.pop();
        claseActual = null;
    }

    public void abrirMetodo(String nombre, String retorno,
                            int linea, int columna) {
        String clave = claseActual + "|" + nombre;

        List<FirmaMetodo> versiones =
                firmas.computeIfAbsent(clave, k -> new ArrayList<>());

        metodoActual = new FirmaMetodo(
                nombre, claseActual, retorno, accesoActual, linea, columna);

        versiones.add(metodoActual);

        metodoActual.etiqueta = "M_" + claseActual + "_"
                + nombre + "_" + versiones.size();

        codigoIntermedio.abrirMetodo(metodoActual.etiqueta);

        if (simbolos.containsKey(claseActual + "|" + nombre)
                && versiones.size() == 1) {
            error(linea, columna, nombre,
                    "El nombre del metodo ya pertenece a un atributo de la clase.");
        }

        String key = clave + (versiones.size() == 1
                ? "" : "#" + versiones.size());

        simbolos.put(key, new Simbolo(
                nombre, "METODO", retorno, "N/A", claseActual,
                linea, columna, "IDENTIFICADOR"));

        accesos.put(key, accesoActual);

        String scope = claseActual + "." + nombre
                + (versiones.size() == 1 ? "" : "#" + versiones.size());

        ambitos.push(scope);
        primerBloqueMetodo = true;
    }

    public void registrarParametro(String nombre, String tipo,
                                   int linea, int columna) {
        if (metodoActual != null)
            metodoActual.parametros.add(tipo);

        registrar(nombre, "VARIABLE", tipo,
                "N/A (Parametro)", linea, columna);

        codigoIntermedio.parametro(nombre, tipo);
    }

    public void finalizarFirma() {
        if (metodoActual==null) return;
        List<FirmaMetodo> lista=firmas.get(metodoActual.clase+"|"+metodoActual.nombre);
        for(FirmaMetodo f:lista) {
            if(f!=metodoActual && f.parametros.equals(metodoActual.parametros)) {
                error(metodoActual.linea,metodoActual.columna,metodoActual.nombre,
                    "Firma duplicada del metodo '"+metodoActual.nombre+"' en la misma clase.");
                break;
            }
        }
    }
    public void validarRetorno(ManejadorTipos.Expresion expr,int linea,int columna) {
        if(metodoActual==null) {
            error(linea,columna,"retornar","La instruccion retornar debe estar dentro de un metodo.");
            return;
        }
        if(expr==null) {
            if(!"void".equals(metodoActual.retorno))
                error(linea,columna,"retornar","El metodo '"+metodoActual.nombre+"' debe devolver '"+metodoActual.retorno+"'.");
        } else if("void".equals(metodoActual.retorno)) {
            error(linea,columna,"retornar","El metodo void '"+metodoActual.nombre+"' no debe devolver un valor.");
        } else if(!"ERROR".equals(expr.tipo) && !ManejadorTipos.asignable(metodoActual.retorno,expr.tipo)) {
            error(linea,columna,"retornar","Retorno incompatible en '"+metodoActual.nombre+"': se esperaba '"+
                metodoActual.retorno+"' y se obtuvo '"+expr.tipo+"'.");
        }
    }
    public boolean retornoValido(ManejadorTipos.Expresion expr) {
        // Un return escrito cubre ese camino; su incompatibilidad ya se reporto.
        return metodoActual!=null;
    }
    
    public void cerrarMetodo(boolean retorna) {
        if (metodoActual != null
                && !"void".equals(metodoActual.retorno)
                && !retorna) {
            error(metodoActual.linea, metodoActual.columna,
                    metodoActual.nombre,
                    "El metodo '" + metodoActual.nombre
                    + "' requiere retorno valido en todos los caminos de ejecucion.");
        }

        codigoIntermedio.cerrarMetodo();

        ambitos.pop();
        primerBloqueMetodo = false;
        metodoActual = null;
    }


    public ManejadorTipos.Expresion llamarMetodo(String objeto,String nombre,
            List<ManejadorTipos.Expresion> args,int linea,int columna) {
        String clase=claseActual;
        if(objeto!=null) {
            Simbolo o=buscarObjeto(objeto,linea,columna);
            if(o==null) return ManejadorTipos.literal("ERROR",objeto+"."+nombre+"(...)");
            clase=o.tipodato;
        }
        List<FirmaMetodo> candidatos=firmas.get(clase+"|"+nombre);
        String llamada=(objeto==null?"":objeto+".")+nombre+"(...)";
        if(candidatos==null || candidatos.isEmpty()) {
            error(linea,columna,nombre,"El metodo '"+nombre+"' no esta declarado en la clase '"+clase+"'.");
            return ManejadorTipos.literal("ERROR",llamada);
        }
        List<FirmaMetodo> visibles=new ArrayList<>();
        for(FirmaMetodo f:candidatos) {
            if(objeto==null || claseActual!=null && claseActual.equals(f.clase) || "public".equals(f.acceso))
                visibles.add(f);
        }
        if(visibles.isEmpty()) {
            error(linea,columna,nombre,"El metodo '"+nombre+"' no es visible ("+candidatos.get(0).acceso+").");
            return ManejadorTipos.literal("ERROR",llamada);
        }
        for(FirmaMetodo f:visibles) {
            if(f.parametros.size()!=args.size()) continue;
            boolean compatible=true;
            for(int i=0;i<args.size();i++) {
                if(!ManejadorTipos.asignable(f.parametros.get(i),args.get(i).tipo)) compatible=false;
            }
            
            if (compatible) {
                return ManejadorTipos.llamada(
                        f.retorno,
                        llamada,
                        CodigoIntermedio.Nodo.llamada(
                                f.etiqueta, args, objeto));
            }
        }
        FirmaMetodo f=visibles.get(0);
        if(f.parametros.size()!=args.size()) {
            error(linea,columna,nombre,"El metodo '"+nombre+"' requiere "+f.parametros.size()+
                " argumentos, pero se proporcionaron "+args.size()+".");
        } else {
            for(int i=0;i<args.size();i++) {
                if(!"ERROR".equals(args.get(i).tipo) && !ManejadorTipos.asignable(f.parametros.get(i),args.get(i).tipo)) {
                    error(linea,columna,nombre,"Argumento "+(i+1)+" de '"+nombre+"' incompatible: se esperaba '"+
                        f.parametros.get(i)+"' y se obtuvo '"+args.get(i).tipo+"'.");
                    break;
                }
            }
        }
        return ManejadorTipos.literal("ERROR",llamada);
    }
    private Simbolo buscarObjeto(String nombre,int linea,int columna) {
        if("this".equals(nombre)) {
            if(claseActual!=null) return new Simbolo("this","VARIABLE",claseActual,"N/A",ambitoActual(),linea,columna,"THIS");
            error(linea,columna,nombre,"'this' no esta disponible fuera de una clase.");
            return null;
        }
        Simbolo s=buscarUso(nombre,linea,columna);
        if(s!=null && !simbolos.containsKey("GLOBAL|"+s.tipodato)) {
            error(linea,columna,nombre,"El identificador '"+nombre+"' no es un objeto de clase declarada.");
            return null;
        }
        return s;
    }
    public ManejadorTipos.Expresion accederAtributo(String obj,String miembro,int linea,int columna) {
        Simbolo o=buscarObjeto(obj,linea,columna);
        if(o==null) return ManejadorTipos.literal("ERROR",obj+"."+miembro);
        String clave=o.tipodato+"|"+miembro;
        Simbolo s=simbolos.get(clave);
        if(s==null || "METODO".equals(s.tiposimbolo)) {
            error(linea,columna,miembro,"El atributo '"+miembro+"' no existe en la clase '"+o.tipodato+"'.");
            return ManejadorTipos.literal("ERROR",obj+"."+miembro);
        }
        if(!o.tipodato.equals(claseActual) && !"public".equals(accesos.get(clave))) {
            error(linea,columna,miembro,"No se permite acceder al atributo '"+miembro+"' ("+accesos.get(clave)+").");
            return ManejadorTipos.literal("ERROR",obj+"."+miembro);
        }
        return ManejadorTipos.referencia(s.tipodato,obj+"."+miembro);
    }
    public void asignarAtributo(String obj,String miembro,ManejadorTipos.Expresion valor,int l,int c) {
        ManejadorTipos.Expresion acceso=accederAtributo(obj,miembro,l,c);
        if("ERROR".equals(acceso.tipo) || "ERROR".equals(valor.tipo)) return;
        Simbolo objeto="this".equals(obj) ? null : buscarUso(obj,l,c);
        String clase="this".equals(obj) ? claseActual : objeto.tipodato;
        Simbolo destino=simbolos.get(clase+"|"+miembro);
        if("CONSTANTE".equals(destino.tiposimbolo)) {
            error(l,c,miembro,"No se puede reasignar la constante '"+miembro+"'."); return;
        }
        if(!ManejadorTipos.asignable(destino.tipodato,valor.tipo)) {
            error(l,c,miembro,"Incompatibilidad al asignar a '"+miembro+"': '"+
                valor.tipo+"' no es compatible con '"+destino.tipodato+"'."); return;
        }
        destino.valor=valor.texto;
        codigoIntermedio.asignar(obj + "." + miembro, valor);
    }
    
    public void registrarObjeto(String clase, String nombre,
                                int linea, int columna) {
        if (!simbolos.containsKey("GLOBAL|" + clase)) {
            error(linea, columna, clase,
                    "La clase '" + clase + "' no esta declarada.");
            return;
        }

        registrar(nombre, "VARIABLE", clase,
                "N/A (Objeto)", linea, columna);

        codigoIntermedio.crearObjeto(nombre, clase);
    }


    public void abrirBloque() {
        if(primerBloqueMetodo) { primerBloqueMetodo=false; aperturasBloque.push(false); }
        else { ambitos.push(ambitoActual()+".bloque"+(++bloques)); aperturasBloque.push(true); }
    }
    public void cerrarBloque() { if(aperturasBloque.pop()) ambitos.pop(); }
    public void abrirCiclo() { ciclos++; }
    public void cerrarCiclo() { ciclos--; }
    public void abrirFor() { ambitos.push(ambitoActual()+".for"+(++bloques)); ciclos++; }
    public void cerrarFor() { ciclos--; ambitos.pop(); }
    public void validarBreak(int l,int c) {
        if(ciclos==0 && casosSwitch.isEmpty()) error(l,c,"romper","La instruccion romper solo se permite dentro de ciclo o segun.");
    }
    public void validarContinue(int l,int c) {
        if(ciclos==0) error(l,c,"continue","La instruccion continue solo se permite dentro de un ciclo.");
    }
    public void abrirSwitch(ManejadorTipos.Expresion exp,int l,int c) {
        if(!"ERROR".equals(exp.tipo) && !"int".equals(exp.tipo) && !"char".equals(exp.tipo))
            error(l,c,"segun","La expresion de segun debe ser integral y se obtuvo '"+exp.tipo+"'.");
        casosSwitch.push(new HashSet<>());
        tiposSwitch.push(exp.tipo);
    }
    public void registrarCaso(String valor,int l,int c) {
        if(!tiposSwitch.isEmpty() && "int".equals(tiposSwitch.peek()) && !valor.matches("[0-9]+"))
            error(l,c,"caso","La etiqueta case debe ser una constante entera compatible con segun.");
        if(!casosSwitch.isEmpty() && !casosSwitch.peek().add(valor))
            error(l,c,"caso","Etiqueta de caso duplicada: "+valor+".");
    }
    public void cerrarSwitch() { casosSwitch.pop(); tiposSwitch.pop(); }

    public void registrar(String id,String categoria,String tipo,String valor,int linea,int columna) {
        if("CLASE".equals(categoria)) {
            if(!id.matches("[A-Z][a-zA-Z0-9]*")) error(linea,columna,id,"La clase '"+id+"' debe usar PascalCase.");
        } else if("CONSTANTE".equals(categoria)) {
            if(!id.matches("[A-Z][A-Z0-9]*(?:_[A-Z0-9]+)*") && !id.matches("[a-z][a-zA-Z0-9]*"))
                error(linea,columna,id,"La constante '"+id+"' debe usar SNAKE_CASE o camelCase.");
        } else if(!id.matches("[a-z][a-zA-Z0-9]*"))
            error(linea,columna,id,"El identificador '"+id+"' debe usar camelCase.");
        String clave=ambitoActual()+"|"+id;
        if(simbolos.containsKey(clave)) {
            error(linea,columna,id,"El identificador '"+id+"' ya fue declarado en el ambito '"+ambitoActual()+"'.");
            return;
        }
        simbolos.put(clave,new Simbolo(id,categoria,tipo,valor,ambitoActual(),linea,columna,"IDENTIFICADOR"));
        accesos.put(clave,claseActual!=null && metodoActual==null?accesoActual:"local");
    }
    public Simbolo buscarUso(String id,int l,int c) {
        for(String a:ambitos) {
            Simbolo s=simbolos.get(a+"|"+id);
            if(s!=null) return s;
        }
        error(l,c,id,"El identificador '"+id+"' NO ha sido declarado o esta fuera de ambito.");
        return null;
    }
    public void verificarUso(String id,int l,int c) { buscarUso(id,l,c); }
    public void asignar(String id,ManejadorTipos.Expresion exp,int l,int c) {
        Simbolo s=buscarUso(id,l,c);
        if(s==null) return;
        if("CONSTANTE".equals(s.tiposimbolo)) { error(l,c,id,"No se puede reasignar la constante '"+id+"'."); return; }
        if("ERROR".equals(exp.tipo)) return;
        if(!ManejadorTipos.asignable(s.tipodato,exp.tipo)) {
            error(l,c,id,"Incompatibilidad de tipos: '"+exp.tipo+"' no se puede asignar a '"+s.tipodato+"'."); return;
        }
        s.valor=exp.texto;
        codigoIntermedio.asignar(id, exp);
    }
    public void validarInicializacion(String id,String tipo,ManejadorTipos.Expresion exp,int l,int c) {
        if(!"ERROR".equals(exp.tipo) && !ManejadorTipos.asignable(tipo,exp.tipo))
            error(l,c,id,"Incompatibilidad de tipos al inicializar '"+id+"': '"+exp.tipo+"' a '"+tipo+"'.");
    }
    /** Inicializa un simbolo y genera 3AC solo cuando la declaracion es valida. */
    public void registrarInicializado(String id, String categoria, String tipo,
            ManejadorTipos.Expresion exp, int linea, int columna) {
        int erroresAntes = errores;
        boolean duplicado = simbolos.containsKey(ambitoActual() + "|" + id);
        validarInicializacion(id, tipo, exp, linea, columna);
        registrar(id, categoria, tipo, exp.texto, linea, columna);
        if (!duplicado && errores == erroresAntes && !"ERROR".equals(exp.tipo)) {
            codigoIntermedio.asignar(id, exp);
        }
    }
    public void error(int l,int c,String motivo) { error(l,c,"?",motivo); }
    public void error(int l,int c,String token,String motivo) {
        errores++;
        System.err.println("Error semantico en linea "+l+", columna "+c+", token '"+token+"': "+motivo);
    }
    public int getErrores() { return errores; }
    public void imprimir() {
        System.out.println("\n=================== TABLA DE SIMBOLOS ===================");
        System.out.printf("%-20s %-10s %-10s %-19s %-34s %-8s %5s %7s %s%n",
            "ID","Categoria","Tipo","Valor","Ambito","Acceso","Linea","Columna","Token");
        for(Map.Entry<String,Simbolo> e:simbolos.entrySet()) {
            Simbolo s=e.getValue();
            System.out.printf("%-20s %-10s %-10s %-19s %-34s %-8s %5d %7d %s%n",
                s.id,s.tiposimbolo,s.tipodato,s.valor,s.ambito,
                accesos.getOrDefault(e.getKey(),"-"),s.linea,s.columna,s.token);
        }
        System.out.println("Total de simbolos registrados: "+simbolos.size());
        System.out.println("Total de errores semanticos: "+errores);
        System.out.println("Resultado semantico: "+(errores==0?"SIN ERRORES":"CON ERRORES"));
        System.out.println("=========================================================\n");
    }
}
