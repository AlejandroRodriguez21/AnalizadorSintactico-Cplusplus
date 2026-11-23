# Analizador sintáctico y semántico — C++ (Actividad 10)

**Materia:** Traductores de Lenguajes  
**Práctica:** Actividad 10 — Análisis semántico: verificación de expresiones aritméticas y lógicas  
**Implementación:** Java, JFlex y JavaCUP  
**Lenguaje de entrada:** subconjunto didáctico de C++ (con `si`, `escribir`, `retornar`, `verdadero`, `falso`)

## Integrantes
- Centeno Domínguez Edgar Yair
- Ramos Gutiérrez Rodrigo
- Rodríguez Romero Alejandro

## Objetivo

Ampliar el analizador de la actividad 9 para comprobar tipos en expresiones aritméticas, relacionales y lógicas; verificar asignaciones e inicializaciones; consultar la tabla de símbolos; y rechazar la reasignación de constantes. Se conservan los analizadores léxico, sintáctico, de métodos, estructuras de control y la tabla dinámica de símbolos.

C++ es un lenguaje de **tipado estático**: el tipo de un identificador queda determinado por su declaración. A diferencia de un lenguaje dinámicamente tipado, una asignación no cambia el tipo declarado.

## Cambios específicos de la actividad 10

| Archivo | Función |
|---|---|
| `src/ManejoErrores/ManejadorTipos.java` | Representa el tipo y el texto de una expresión; comprueba operadores y compatibilidad. |
| `src/ManejoErrores/TablaSimbolos.java` | Consulta tipos por ámbito, verifica inicializaciones y asignaciones, impide la modificación de constantes. |
| `src/ManejoErrores/Simbolo.java` | Permite actualizar el campo textual `valor` en las asignaciones válidas. |
| `src/ManejoErrores/cmasmas_lexico.cup` | Propaga tipos desde literales e identificadores, aplica comprobaciones en expresiones y condiciones. |
| `src/ManejoErrores/AnalizadorLexico.java` | Selecciona los archivos de prueba de la actividad 10. |
| `src/ManejoErrores/ParserJava.java` y `sym.java` | Se regeneran a partir de la gramática actualizada. |

No fue necesario modificar `java_cmasmas.jflex` ni `cppLexer.java`: el léxico ya reconocía los tokens requeridos y sus posiciones.

## Reglas semánticas

- En `+`, `-`, `*` y `/`, los operandos deben ser numéricos. La combinación `int` y `float` produce `float`; `double` mantiene su mayor precisión.
- El operador `%` exige operandos `int`, conforme al subconjunto C++ analizado.
- `>`, `<`, `>=` y `<=` comparan números y devuelven `bool`. `==` y `!=` también permiten `bool` con `bool` y `string` con `string`.
- `&&`, `||` y `!` exigen `bool` y devuelven `bool`.
- Las inicializaciones y asignaciones comprueban el tipo de destino. Se aceptan asignaciones idénticas y promociones numéricas `int` → `float` / `double`, `float` → `double`; no se permite convertir `string` o `bool` a números.
- Los identificadores deben existir en la tabla de símbolos y ser visibles desde el ámbito actual. Las constantes pueden leerse pero no reasignarse.
- Las condiciones de `si`, `mientras` y `para` deben ser `bool`.
- Los errores indican línea, columna, token y causa. El tipo `ERROR` evita repetir mensajes derivados de una misma expresión incorrecta.

Las expresiones se **verifican y se les infiere tipo**, pero no se evalúa su resultado numérico ni se ejecuta el lenguaje de entrada. La tabla conserva una representación textual del valor o expresión asignada.

## Requisitos y ejecución

- NetBeans con proyecto Java/Ant.
- Un JDK compatible con `nbproject/project.properties` (la configuración original usa Java 26). Las pruebas de esta entrega se compilaron también con JDK 21 desde la línea de comandos.
- Bibliotecas `jflex-1.9.1.jar`, `java-cup-11b.jar` y `java-cup-11b-runtime.jar` incluidas en `lib/`.

1. Abrir `AnalizadorSintactico-Cplusplus` como proyecto en NetBeans.
2. Sustituir los archivos Java y `.cup` de la actividad 9 por los de esta versión.
3. Ejecutar `GeneradorCupSintantico.java` con **Shift + F6** para regenerar `ParserJava.java` y `sym.java`.
4. En `AnalizadorLexico.java`, dejar activa la ruta `prueba_valida_a10.txt` o comentar esa línea y activar `prueba_errores_a10.txt`.
5. Ejecutar **Clean and Build Project** y luego `AnalizadorLexico.java` con **Shift + F6**.
6. Consultar la consola: reglas `Tipos: ...`, diagnósticos semánticos y tabla final.

## Pruebas y resultados comprobados

| Entrada | Finalidad | Resultado |
|---|---|---|
| `prueba_valida_a10.txt` | Declaraciones, asignaciones, promoción, relacionales y lógicas válidas | 10 símbolos, 0 errores semánticos |
| `prueba_errores_a10.txt` | Asignación incompatible, operador aritmético y lógico incompatibles, condición no booleana, constante, identificador no declarado | 7 símbolos, 6 errores semánticos |
| `prueba_reglas_a10.txt` | `int + int`, `int * float`, `>=`, `==`, `!=`, `&&`, `||`, `!` y `%` | 14 símbolos, 0 errores semánticos |
| `prueba_valida_a9.txt` | Regresión de tabla de símbolos | 10 símbolos, 0 errores semánticos |
| `prueba_valida.txt` | Regresión de métodos y estructuras anteriores | 14 símbolos, 0 errores semánticos |

**Nota sobre los archivos de prueba:** la actividad 9 requiere inicializar las variables locales en su declaración. Los archivos de prueba de la actividad 10 mantienen esa regla, aunque el ejemplo conceptual del PDF contiene variables locales sin valor inicial. En C++ la forma booleana utilizada por el analizador es `bool`, no `boolean`.

## Alcance y limitaciones

Se implementó la verificación solicitada, no un compilador completo de C++. La gramática admite un subconjunto didáctico y no comprueba exhaustivamente firmas de métodos, sobrecargas, conversiones arbitrarias, ejecución de expresiones ni otros aspectos fuera de la actividad 10. El mensaje de finalización sintáctica no significa ausencia de errores semánticos; para comprobarlos, consultar el contador final de la tabla.
