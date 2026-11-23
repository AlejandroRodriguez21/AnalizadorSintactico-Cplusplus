# Analizador de C++ — Actividad 12: Código intermedio de operaciones

**Materia:** Traductores de Lenguajes  
**Implementación:** Java, JFlex y JavaCUP  
**Lenguaje de entrada:** subconjunto didáctico de C++ (con algunas palabras reservadas en español)

## Integrantes
- Centeno Domínguez Edgar Yair
- Ramos Gutiérrez Rodrigo
- Rodríguez Romero Alejandro

## Objetivo
Generar instrucciones de código intermedio de tres direcciones (3AC) para declaraciones inicializadas y asignaciones con constantes, variables, operaciones aritméticas, relacionales y lógicas; utilizar temporales `t1`, `t2`, ... y conservar la verificación semántica de actividades previas.

## Archivos nuevos o modificados
- `src/ManejoErrores/CodigoIntermedio.java` (nuevo): árbol de operaciones y emisión ordenada de instrucciones, temporales y guardas para división por cero cuando el divisor no es un literal conocido.
- `src/ManejoErrores/ManejadorTipos.java` (modificado): mantiene la compatibilidad de tipos y agrega la representación estructurada de expresiones para generar temporales.
- `src/ManejoErrores/TablaSimbolos.java` (modificado): publica instrucciones 3AC solamente después de validar una declaración inicializada o asignación.
- `src/ManejoErrores/cmasmas_lexico.cup` (modificado): vincula las acciones semánticas de inicialización y preserva las expresiones agrupadas.
- `src/ManejoErrores/AnalizadorLexico.java` (modificado): imprime la tabla de símbolos y la sección final de código intermedio.
- `src/ManejoErrores/Simbolo.java`: únicamente un comentario inicial con los integrantes, conforme al formato de entrega.
- `src/ManejoErrores/ParserJava.java` y `sym.java` se regeneran desde JavaCUP.

Los archivos `java_cmasmas.jflex` y `cppLexer.java` no requieren cambios. Se conservan las validaciones léxicas, sintácticas y semánticas de las actividades 8–11.

## Requisitos y ejecución en NetBeans
1. Abrir la carpeta raíz como proyecto Java Ant (contiene `build.xml`).
2. Usar el JDK compatible con el proyecto: `nbproject/project.properties` conserva `javac.source=26` y `javac.target=26`. Si se usa Java 21, ajustar ambas propiedades a `21`.
3. Ejecutar `GeneradorCupSintantico.java` con **Shift + F6** para regenerar `ParserJava.java` y `sym.java` al modificar la gramática.
4. Seleccionar **Run > Clean and Build Project**.
5. Abrir `AnalizadorLexico.java`. La variable `rutaPrueba` apunta inicialmente a `prueba_valida_a12.txt`. Para otras pruebas, comentar esa línea y activar/cambiar la ruta apropiada.
6. Ejecutar `AnalizadorLexico.java` con **Shift + F6** y revisar los bloques `TABLA DE SIMBOLOS` y `CODIGO INTERMEDIO (3AC)`.

## Archivos de prueba
| Archivo | Propósito | Resultado de referencia |
|---|---|---|
| `prueba_valida_a12.txt` | Constantes, asignaciones, variables, anidamiento, lógica y guarda de divisor variable | 13 símbolos, 0 errores semánticos, 48 instrucciones 3AC y 28 temporales |
| `prueba_reglas_a12.txt` | Resta, módulo, promoción `int + float`, comparadores y OR | Analiza sin errores semánticos |
| `prueba_errores_a12.txt` | División y módulo entre cero literal, `bool + int`, `int + string` e identificador no declarado | 5 errores semánticos, sin emitir 3AC de esas asignaciones inválidas |

## Reglas de generación
- Literal asignado: `t1 = 5`, luego `x = t1`.
- Operación simple: `t2 = x + y`, luego `z = t2`.
- Expresión anidada: primero se generan los temporales de subexpresiones, luego la operación que los combina.
- Lógica y relacionales: `t = a > b`, `t = x && y`, `t = !x`, sin introducir todavía saltos de control.
- División con denominador no literal: `if divisor == 0 then error "Division por cero"` antes de la operación. Para divisor literal `0` se reporta error semántico y se bloquea la asignación.
- La generación usa el árbol de expresiones ya reconocido por JavaCUP; **no ejecuta** las operaciones ni evalúa el valor de las variables.
- Una asignación incompatible se reporta y no se incorpora al bloque 3AC.

## Alcance
Esta práctica genera operaciones y asignaciones de tres direcciones. La traducción de `si`, `mientras`, `para`, métodos y saltos con etiquetas se reserva para actividades posteriores. Las llamadas a métodos siguen verificándose semánticamente, pero no se convierten aún a instrucciones de invocación 3AC.
