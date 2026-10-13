/* CENTENO DOMINGUEZ EDGAR YAIR, RAMOS GUTIERREZ RODRIGO, RODRIGUEZ ROMERO ALEJANDRO */
package ManejoErrores;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;

public class AnalizadorLexico {

    public static void main(String[] args) {
        System.out.println("CENTENO DOMINGUEZ EDGAR YAIR, RAMOS GUTIERREZ RODRIGO, RODRIGUEZ ROMERO ALEJANDRO");
        
        String rutaPrueba = "prueba_valida.txt";
        // String rutaPrueba = "prueba_errores.txt";

        try {
            Reader lector = new BufferedReader(new FileReader(rutaPrueba));
            System.out.println("Archivo analizado: " + rutaPrueba);
            System.out.println("-------------------------------------------------");
            
            cppLexer lexer = new cppLexer(lector);
            ParserJava parser = new ParserJava(lexer);
            
            parser.parse();
            
        } catch (Exception ex) {
            System.err.println("El analisis se detuvo por un error irrecuperable.");
        }

        System.out.println("-------------------------------------------------");
        System.out.println("CENTENO DOMINGUEZ EDGAR YAIR, RAMOS GUTIERREZ RODRIGO, RODRIGUEZ ROMERO ALEJANDRO");
    }
}