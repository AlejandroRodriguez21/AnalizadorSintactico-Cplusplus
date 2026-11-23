
package ManejoErrores;

/** Entrada individual de la tabla de simbolos. */
public class Simbolo {
    public final String id;
    public final String tiposimbolo;
    public final String tipodato;
    public String valor;
    public final String ambito;
    public final int linea;
    public final int columna;
    public final String token;

    public Simbolo(String id, String tiposimbolo, String tipodato,
                   String valor, String ambito, int linea, int columna, String token) {
        this.id = id;
        this.tiposimbolo = tiposimbolo;
        this.tipodato = tipodato;
        this.valor = valor;
        this.ambito = ambito;
        this.linea = linea;
        this.columna = columna;
        this.token = token;
    }
}
