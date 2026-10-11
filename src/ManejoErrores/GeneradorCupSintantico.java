/*
CENTENO DOMINGUEZ EDGAR YAIR, RAMOS GUTIERREZ RODRIGO, RODRIGUEZ ROMERO ALEJANDRO
*/

package ManejoErrores;

import java.io.IOException;

public class GeneradorCupSintantico {
    public static void main(String[] args) throws IOException, Exception {
        String[] parametros = {
            "-destdir", "src/ManejoErrores",
            "-parser", "ParserJava",
            "-progress", "src/ManejoErrores/cmasmas_lexico.cup"
        };
        java_cup.Main.main(parametros);
    }
}