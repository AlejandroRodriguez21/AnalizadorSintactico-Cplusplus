# Práctica: Análisis sintáctico de atributos, variables y constantes
**Integrantes:** 
- Centeno Domínguez Edgar Yair
- Ramos Gutiérrez Rodrigo
- Rodríguez Romero Alejandro

**Lenguaje asignado:** C++

**Nuevas funcionalidades incorporadas (Actividad 5):**
- Reconocimiento del cuerpo interno de la clase.
- Declaración de atributos y variables (individuales y múltiples separadas por comas).
- Soporte para inicialización de variables en su declaración.
- Declaración de constantes exigiendo la palabra reservada `const` y su inicialización obligatoria.
- Integración de tipos de datos (`int`, `float`, `double`, `string`, `char`, `bool`) y valores literales.
- Mecanismos de recuperación de errores anclados al punto y coma (`;`) para omitir declaraciones mal formadas sin detener el análisis.

**Instrucciones para compilar y ejecutar:**
1. Ejecutar `GeneradorJFlexLexico.java` (Shift + F6) para generar el escáner.
2. Ejecutar `GeneradorCupSintantico.java` (Shift + F6) para generar el parser.
3. Ejecutar `AnalizadorLexico.java` (Shift + F6) alternando la variable `rutaPrueba` entre `prueba_valida.txt` y `prueba_errores.txt`.