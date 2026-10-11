/* CENTENO DOMINGUEZ EDGAR YAIR, RAMOS GUTIERREZ RODRIGO, RODRIGUEZ ROMERO ALEJANDRO */

package ManejoErrores;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;

public class AnalizadorLexico {

    public static void main(String[] args) {
        System.out.println(
            "CENTENO DOMINGUEZ EDGAR YAIR, "
            + "RAMOS GUTIERREZ RODRIGO, "
            + "RODRIGUEZ ROMERO ALEJANDRO"
        );

        
    //  String rutaPrueba = "prueba_valida.txt";
        String rutaPrueba = "prueba_errores.txt";

        try (
            Reader lector =
                new BufferedReader(new FileReader(rutaPrueba))
        ) {
            System.out.println("Archivo: " + rutaPrueba);

            cppLexer lexer = new cppLexer(lector);
            ParserJava parser = new ParserJava(lexer);

            try {
                parser.parse();

                int sintacticos = parser.getErroresSintacticos();
                int lexicos = lexer.getErroresLexicos();

                if (sintacticos == 0 && lexicos == 0) {
                    System.out.println(
                        "Analisis sintactico finalizado correctamente."
                    );
                } else {
                    System.out.println(
                        "Analisis finalizado con errores. "
                        + "Sintacticos: " + sintacticos
                        + "; lexicos: " + lexicos + "."
                    );
                }

            } catch (Exception ex) {
                if (parser.huboErrorFatal()) {
                    System.out.println(
                        "Resultado: analisis detenido por error fatal."
                    );
                } else {
                    System.err.println(
                        "No se pudo completar el analisis: "
                        + ex.getMessage()
                    );
                }
            }

        } catch (Exception ex) {
            System.err.println(
                "No se pudo procesar el archivo: "
                + ex.getMessage()
            );
        }

        System.out.println(
            "CENTENO DOMINGUEZ EDGAR YAIR, "
            + "RAMOS GUTIERREZ RODRIGO, "
            + "RODRIGUEZ ROMERO ALEJANDRO"
        );
    }
}