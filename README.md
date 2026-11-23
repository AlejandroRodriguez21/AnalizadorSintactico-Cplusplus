
# Analizador de C++ — Actividad 13: Código intermedio para control de flujo

**Materia:** Traductores de Lenguajes  
**Lenguaje de implementación:** Java con JFlex y JavaCUP  
**Lenguaje analizado:** Subconjunto didáctico de C++ con algunas palabras reservadas en español

## Integrantes

- Centeno Domínguez Edgar Yair
- Ramos Gutiérrez Rodrigo
- Rodríguez Romero Alejandro

## Objetivo

Extender el código intermedio de tres direcciones (3AC) de la actividad 12 para representar estructuras `si/sino`, `segun`, `mientras`, `para` y `hacer-mientras`, así como definiciones, llamadas y retornos de métodos, clases y creación de objetos.

Se mantienen las comprobaciones sintácticas y semánticas anteriores.

## Componentes principales

| Archivo | Función |
|---|---|
| `src/ManejoErrores/CodigoIntermedio.java` | Generación de temporales, etiquetas, saltos, capturas de bloques, llamadas y objetos. |
| `src/ManejoErrores/cmasmas_lexico.cup` | Gramática y acciones para construir el flujo de control. |
| `src/ManejoErrores/TablaSimbolos.java` | Tipos, ámbitos, firmas, visibilidad y conexión con 3AC. |
| `src/ManejoErrores/ManejadorTipos.java` | Comprobación de tipos y nodos para llamadas a métodos. |
| `src/ManejoErrores/AnalizadorLexico.java` | Ejecución e impresión de tabla y código intermedio. |
| `src/ManejoErrores/java_cmasmas.jflex` | Analizador léxico original, sin cambios en esta actividad. |
| `src/ManejoErrores/ParserJava.java`, `sym.java` | Archivos generados desde JavaCUP. |

## Generación de código intermedio

### Expresiones y temporales

Las expresiones conservan sus temporales `t1`, `t2`, etc., junto con sus comprobaciones de tipos.

### Estructuras condicionales

Las condiciones generan saltos como `if not t1 goto L2`, junto con etiquetas `L1:`, `L2:` e instrucciones `goto`.

Para las estructuras `si/sino`, se generan dos ramas y una etiqueta de salida.

### Selección múltiple

La estructura `segun` realiza comparaciones para cada `caso`. La instrucción `romper` se traduce a un salto hacia la etiqueta de salida.

Cuando un caso no contiene `romper`, se conserva la posibilidad de continuar hacia el siguiente caso.

### Ciclos

- En `mientras`, la condición se evalúa antes del cuerpo.
- En `para`, se respeta el orden: inicialización, condición, cuerpo y actualización.
- En `hacer-mientras`, la condición se evalúa después del cuerpo.
- Se conservan los destinos correctos de `romper` y `continue`, incluidos ciclos anidados.

### Métodos

Los métodos incluyen una etiqueta de entrada, parámetros formales, instrucciones y retornos.

Las llamadas válidas generan `param`, `call` y, cuando corresponde, `return_value`.

### Clases y objetos

La declaración de objeto:

```cpp
Calculadora c;
```

Se representa mediante:

```text
c = create Calculadora
```

Y una llamada:

```cpp
c.sumar(1, 2);
```

Utiliza `param_this` para identificar el objeto receptor.

## Archivos de prueba

| Archivo | Propósito | Resultado comprobado |
|---|---|---|
| `prueba_valida_a13.txt` | Estructuras principales, métodos, llamadas y objetos | 14 símbolos, 0 errores, 109 instrucciones 3AC, 40 temporales y 16 etiquetas |
| `prueba_reglas_a13.txt` | Control anidado, continue dentro de segun, para sin incremento y llamada void | 6 símbolos, 0 errores, 44 instrucciones 3AC, 12 temporales y 8 etiquetas |
| `prueba_errores_a13.txt` | Condiciones inválidas, llamadas incorrectas, errores de contexto y caso duplicado | 6 símbolos, 7 errores, 43 instrucciones 3AC |

Los archivos de prueba de las actividades anteriores permanecen disponibles para comprobar su funcionamiento.

## Requisitos

- Apache NetBeans con soporte para proyectos Java con Ant.
- JDK compatible con la configuración de `nbproject/project.properties`.
- Bibliotecas JFlex y JavaCUP incluidas en `lib/`.

El proyecto anterior especificaba Java 26. Las pruebas de esta versión se realizaron con OpenJDK 21 ajustando `javac.source` y `javac.target` a 21.

## Compilación y ejecución en NetBeans

1. Abrir la carpeta del proyecto, donde se encuentra `build.xml`.
2. Ejecutar `GeneradorCupSintantico.java` mediante **Shift + F6** para regenerar `ParserJava.java` y `sym.java`.
3. No es necesario regenerar `cppLexer.java`, porque la especificación `.jflex` no cambió.
4. Ejecutar **Clean and Build Project**.
5. Abrir `AnalizadorLexico.java`.
6. Seleccionar el archivo de prueba mediante la variable `rutaPrueba`.
7. Ejecutar la clase con **Shift + F6**.
8. Revisar la salida: reglas reconocidas, Tabla de Símbolos, Código Intermedio (3AC) y contadores.

## Alcance y limitaciones

El proyecto genera una representación textual de código intermedio, no un ejecutable C++.

La instrucción `create` representa conceptualmente la creación de un objeto, sin implementar administración de memoria.

La sintaxis admitida sigue siendo el subconjunto educativo de C++ del proyecto. Por ejemplo, los objetos se declaran mediante `Calculadora c;`, sin añadir una regla nueva para `new`.

No se realiza optimización del código intermedio, pues corresponde a la siguiente actividad.

Los errores semánticos reportan línea, columna, token y causa. Cuando una construcción no supera la validación, puede omitirse su traducción válida y el análisis continúa cuando la gramática lo permite.
