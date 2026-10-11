# Práctica: Análisis sintáctico y gestión de errores en la estructura de un programa
**Integrantes:** 
- Centeno Domínguez Edgar Yair
- Ramos Gutiérrez Rodrigo
- Rodríguez Romero Alejandro

**Lenguaje asignado:** C++

**Reglas sintácticas implementadas:**
- Estructura general del archivo (paquetes, importaciones y clase principal).
- El archivo puede iniciar con paquete, importación o clase.
- Las importaciones aparecen estrictamente antes de la clase.
- Se exige exactamente una clase principal válida delimitada por llaves `{}`.

**Tipos de errores detectados:**
- Errores recuperables (falta de punto y coma, palabras mal formadas en paquetes e importaciones).
- Errores en la clase principal (llaves incorrectas o nombre faltante).
- Errores estructurales fatales (orden inválido e irrecuperable de las estructuras principales).

**Mecanismos de recuperación utilizados:**
- Uso de la palabra reservada `error` de JavaCUP anclada a los tokens `USING`, `IMPORT` y `CLASS` para omitir estructuras inválidas hasta encontrar el siguiente delimitador (`;` o `}`) y continuar el análisis.

**Instrucciones para compilar y ejecutar:**
1. Ejecutar `GeneradorJFlexLexico.java` (Shift + F6) para generar el escáner.
2. Ejecutar `GeneradorCupSintantico.java` (Shift + F6) para generar el parser.
3. Ejecutar `AnalizadorLexico.java` (Shift + F6) definiendo la variable `rutaPrueba` hacia `prueba_valida.txt` o `prueba_errores.txt`.