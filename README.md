# Práctica: Análisis sintáctico de expresiones aritméticas y lógicas
**Integrantes:** 
- Centeno Domínguez Edgar Yair 
- Ramos Gutiérrez Rodrigo
- Rodríguez Romero Alejandro

**Lenguaje asignado:** C++

**Nuevas funcionalidades incorporadas (Actividad 6):**
- Reconocimiento de un método temporal como contenedor (`void proceso() {}`).
- Soporte para sentencias de lectura (`leer()`) y escritura (`escribir()`) con argumentos simples y múltiples.
- Implementación de un árbol recursivo para asignaciones con expresiones aritméticas (`+`, `-`, `*`, `/`, `%`) y lógicas (`&&`, `||`, `!`).
- Integración de jerarquía y precedencia de operadores usando las directivas `precedence left / right` de JavaCUP.
- Recuperación de errores en sentencias matemáticas y de E/S mal formadas ancladas al delimitador `;`.

**Instrucciones para compilar y ejecutar:**
1. Ejecutar `GeneradorJFlexLexico.java` (Shift + F6).
2. Ejecutar `GeneradorCupSintantico.java` (Shift + F6).
3. Ejecutar `AnalizadorLexico.java` (Shift + F6) apuntando a los archivos `.txt` de prueba.