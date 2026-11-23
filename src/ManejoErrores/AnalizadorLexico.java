
/* CENTENO DOMINGUEZ EDGAR YAIR, RAMOS GUTIERREZ RODRIGO, RODRIGUEZ ROMERO ALEJANDRO */
package ManejoErrores;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;

public class AnalizadorLexico {
    public static void main(String[] args) {
        System.out.println("CENTENO DOMINGUEZ EDGAR YAIR, RAMOS GUTIERREZ RODRIGO, RODRIGUEZ ROMERO ALEJANDRO");

        String rutaPrueba = "prueba_valida_a13.txt";
        // String rutaPrueba = "prueba_errores_a13.txt";

        try (Reader lector = new BufferedReader(new FileReader(rutaPrueba))) {
            System.out.println("Archivo analizado: " + rutaPrueba);
            System.out.println("-------------------------------------------------");

            cppLexer lexer = new cppLexer(lector);
            ParserJava parser = new ParserJava(lexer);

            parser.parse();
            parser.getTablaSimbolos().imprimir();
            parser.getTablaSimbolos().getCodigoIntermedio().imprimir();

        } catch (Exception ex) {
            System.err.println("El analisis se detuvo por un error irrecuperable: "
                    + ex.getMessage());
        }

        System.out.println("-------------------------------------------------");
        System.out.println("CENTENO DOMINGUEZ EDGAR YAIR, RAMOS GUTIERREZ RODRIGO, RODRIGUEZ ROMERO ALEJANDRO");
    }
}
