
# Analizador sintáctico y tabla de símbolos — C++ (Actividad 9)

**Materia:** Traductores de Lenguajes  
**Actividad:** 9 — Análisis semántico: gestión de la tabla de símbolos y tipos de datos  
**Implementación:** Java, JFlex y JavaCUP  
**Lenguaje analizado:** subconjunto didáctico de C++ con algunas instrucciones adaptadas al español

## Integrantes

- Centeno Domínguez Edgar Yair
- Ramos Gutiérrez Rodrigo
- Rodríguez Romero Alejandro

## Objetivo

Incorporar al analizador de las actividades anteriores una tabla de símbolos dinámica que registre clases, métodos, atributos, variables y constantes, sus ámbitos, tipos, valores y ubicación original, y compruebe las reglas de nomenclatura, inicialización y declaración.

## Funciones implementadas en la Actividad 9

- Clase `Simbolo` con `id`, `tiposimbolo`, `tipodato`, `valor`, `ambito`, `linea`, `columna` y `token`.
- Tabla de símbolos dinámica mediante `LinkedHashMap`, con inserción, consulta y ámbitos anidados.
- Registro de declaraciones de clases, métodos, parámetros, atributos, constantes y variables locales.
- Verificación de `PascalCase` para clases; `camelCase` para métodos, variables y atributos; `SNAKE_CASE` o `camelCase` para constantes.
- Inicialización obligatoria de variables locales y constantes; opcional para atributos.
- Notificación de declaraciones duplicadas en el mismo ámbito y de identificadores utilizados sin declarar.
- Ubicación del token `IDENTIFICADOR` mediante línea y columna suministradas por JFlex/JavaCUP.
- Impresión estructurada de la tabla completa al terminar el análisis, con conteo de símbolos y errores.

Se conservaron las reglas de la Actividad 8: definiciones y llamadas de métodos, parámetros, retornos, expresiones y estructuras de control. No se añadieron verificaciones de compatibilidad de tipos, resultados de operaciones ni número de argumentos.

## Archivos principales

| Archivo | Función |
|---|---|
| `src/ManejoErrores/Simbolo.java` | Modelo de una entrada de la tabla. |
| `src/ManejoErrores/TablaSimbolos.java` | Estructura dinámica, ámbitos y validaciones. |
| `src/ManejoErrores/cmasmas_lexico.cup` | Gramática y acciones semánticas de registro. |
| `src/ManejoErrores/java_cmasmas.jflex` | Analizador léxico original; no requiere cambios en esta actividad. |
| `src/ManejoErrores/AnalizadorLexico.java` | Selección de prueba, análisis y visualización. |
| `src/ManejoErrores/ParserJava.java`, `sym.java` | Archivos generados desde JavaCUP. |
| `prueba_valida_a9.txt` | Ejemplo con 10 símbolos válidos. |
| `prueba_errores_a9.txt` | Ejemplo con 6 errores semánticos. |

## Ejecución en NetBeans

1. Abrir la carpeta del proyecto como un proyecto Java con Ant. Emplear un JDK compatible con `nbproject/project.properties` (el proyecto original establece `javac.source=26` y `javac.target=26`).
2. Ejecutar `GeneradorCupSintantico.java` con **Shift + F6** para regenerar `ParserJava.java` y `sym.java` después de sustituir el `.cup`.
3. No es necesario regenerar `cppLexer.java`, porque `java_cmasmas.jflex` no cambió.
4. Abrir `AnalizadorLexico.java`. La ruta `prueba_valida_a9.txt` está activa. Para probar errores, comentar esa línea y activar `prueba_errores_a9.txt`.
5. Ejecutar `AnalizadorLexico.java` con **Shift + F6**, o ejecutar el proyecto usando esta clase como principal.
6. Revisar la consola: se imprimen las reglas sintácticas y, al final, la tabla de símbolos con la ubicación de cada entrada y el número de errores semánticos.

## Resultados de referencia

- `prueba_valida_a9.txt`: 10 símbolos y 0 errores semánticos.
- `prueba_errores_a9.txt`: 5 símbolos registrados y 6 errores semánticos (clase y atributo con nombre incorrecto, constante y variable sin inicializar, uso de identificador no declarado y duplicado).
- `prueba_valida.txt` de la Actividad 8: se mantiene el reconocimiento sintáctico y se registran 14 símbolos sin errores semánticos.

**Nota:** el mensaje `Analisis sintactico finalizado correctamente` significa que la gramática logró completar la entrada. Para comprobar la validez semántica debe revisarse `Total de errores semanticos` al final de la tabla. No se evalúan los resultados de las expresiones: el campo `valor` registra su representación textual.
