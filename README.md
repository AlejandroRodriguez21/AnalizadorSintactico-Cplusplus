# Práctica: Análisis sintáctico de estructuras de control
**Integrantes:** 
- Centeno Domínguez Edgar Yair
- Ramos Gutiérrez Rodrigo
- Rodríguez Romero Alejandro

**Lenguaje asignado:** C++

**Nuevas funcionalidades incorporadas (Actividad 7):**
- Implementación de estructuras selectivas simples (`si`), dobles (`si/sino`) y múltiples (`segun`, `caso`, `defecto`, `romper`).
- Incorporación de estructuras iterativas (`mientras`, `hacer-mientras`, `para`).
- Soporte para condiciones simples y compuestas mediante operadores relacionales y lógicos (`&&`, `||`, `!`).
- Permisos de anidamiento profundo (por ejemplo, un `mientras` dentro de un `si`, a su vez dentro de un `para`).
- Mecanismo de recuperación de errores avanzados y precisos (detectando falta de llaves, paréntesis, condiciones incompletas o casos sin valor).

**Instrucciones para compilar y ejecutar:**
1. Ejecutar el archivo `GeneradorJFlexLexico.java` (Shift + F6) para generar la clase `cppLexer`.
2. Ejecutar el archivo `GeneradorCupSintantico.java` (Shift + F6) para generar `ParserJava` y `sym`.
3. Ejecutar la clase principal `AnalizadorLexico.java` (Shift + F6) asegurándose de alternar la variable `rutaPrueba` entre los archivos `prueba_valida.txt` y `prueba_errores.txt` que se encuentran en el directorio raíz.