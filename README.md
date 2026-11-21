
# Analizador sintáctico — C++ (Actividad 8)

**Materia:** Traductores de Lenguajes  
**Práctica:** Actividad 8 — Análisis sintáctico de métodos con parámetros y valores de retorno  
**Lenguaje analizado:** subconjunto de C++ con palabras reservadas adaptadas al español  
**Implementación del analizador:** Java, JFlex y JavaCUP

## Integrantes

- Centeno Domínguez Edgar Yair
- Ramos Gutiérrez Rodrigo
- Rodríguez Romero Alejandro

## Objetivo

Extender el analizador sintáctico de las prácticas anteriores para reconocer definiciones y llamadas a métodos con y sin parámetros, con y sin valor de retorno, además de detectar errores en su estructura y continuar el análisis cuando la recuperación sea posible.

**Importante:** el proyecto está escrito en Java, pero analiza archivos de entrada que utilizan un subconjunto de la sintaxis de C++. Algunas construcciones se escriben en español, como `si`, `sino`, `mientras`, `escribir` y `retornar`. El programa reconoce estructuras sintácticas; no ejecuta el código de entrada ni genera un programa C++ compilable.

## Funcionalidades

### Incorporadas en la Actividad 8

- Reconocimiento de métodos `void` y de métodos con tipo de retorno.
- Definición de métodos sin parámetros y con uno o varios parámetros tipados.
- Reconocimiento de las cuatro combinaciones: sin parámetros/sin retorno, con parámetros/sin retorno, sin parámetros/con retorno y con parámetros/con retorno.
- Reconocimiento de llamadas sin argumentos y con argumentos.
- Aceptación de llamadas como sentencias y como parte de expresiones, por ejemplo `int resultado = sumar(x, y);`.
- Reconocimiento de llamadas a otros métodos dentro del cuerpo de un método.
- Reconocimiento sintáctico de `retornar expresion;` y `retornar;`.
- Mensajes de consola al reconocer reglas de métodos, listas de parámetros, llamadas, listas de argumentos, bloques y retornos.
- Detección de diferentes errores sintácticos y mecanismos de recuperación mediante producciones de error.

### Conservadas de la Actividad 7

- Estructuras selectivas: `si`, `si/sino`, `segun`, `caso` y `defecto`.
- Estructuras iterativas: `mientras`, `hacer-mientras` y `para`.
- Expresiones aritméticas, relacionales y lógicas, y anidamiento de estructuras admitido por la gramática.
- Declaraciones, asignaciones y sentencias de lectura y escritura contempladas por el analizador.

## Estructura del proyecto

```text
AnalizadorSintactico-Cplusplus/
├── README.md
├── build.xml
├── manifest.mf
├── nbproject/                     # Configuración del proyecto NetBeans
├── lib/                           # Bibliotecas JFlex y JavaCUP incluidas
│   ├── jflex-1.9.1.jar
│   ├── java-cup-11b.jar
│   ├── java-cup-11b-runtime.jar
│   └── DEPENDENCIAS.txt
├── prueba_valida.txt              # Métodos y llamadas con sintaxis válida
├── prueba_errores.txt             # Casos con errores sintácticos
└── src/ManejoErrores/
    ├── java_cmasmas.jflex          # Definición del analizador léxico
    ├── cmasmas_lexico.cup          # Gramática y recuperación de errores
    ├── cppLexer.java               # Analizador léxico generado
    ├── ParserJava.java             # Analizador sintáctico generado
    ├── sym.java                    # Símbolos/terminales generados
    ├── GeneradorJFlexLexico.java   # Generación del lexer
    ├── GeneradorCupSintantico.java # Generación del parser
    └── AnalizadorLexico.java       # Clase principal y selección de entrada
```

## Requisitos

- Apache NetBeans con soporte para proyectos Java con Ant.
- JDK 26 para utilizar sin cambios la configuración actual (`javac.source=26` y `javac.target=26`).
- Las bibliotecas necesarias ya están en `lib/` y referenciadas en `nbproject/project.properties`.

## Compilación y ejecución en NetBeans

1. Abrir NetBeans y seleccionar **File > Open Project**; elegir la carpeta `AnalizadorSintactico-Cplusplus` (la que contiene `build.xml`).
2. Comprobar que el proyecto utilice un JDK compatible con la configuración indicada. Si el código generado ya está presente, **no es necesario regenerarlo en cada ejecución**.
3. Si se modificó `java_cmasmas.jflex`, ejecutar `GeneradorJFlexLexico.java` con **Shift + F6** para regenerar `cppLexer.java`.
4. Si se modificó `cmasmas_lexico.cup`, ejecutar `GeneradorCupSintantico.java` con **Shift + F6** para regenerar `ParserJava.java` y `sym.java`.
5. Abrir `src/ManejoErrores/AnalizadorLexico.java` y seleccionar el archivo de prueba mediante `rutaPrueba` (líneas 13–14 en la versión entregada):

   Para la prueba válida:

   ```java
   String rutaPrueba = "prueba_valida.txt";
   // String rutaPrueba = "prueba_errores.txt";
   ```

   Para la prueba de errores:

   ```java
   // String rutaPrueba = "prueba_valida.txt";
   String rutaPrueba = "prueba_errores.txt";
   ```

6. Guardar y ejecutar `AnalizadorLexico.java` con **Shift + F6** (o ejecutar el proyecto con la clase principal `ManejoErrores.AnalizadorLexico`). Las rutas relativas de prueba suponen que el directorio de trabajo es la raíz del proyecto.
7. Revisar la salida de consola: aparecen las reglas reconocidas y, en los casos incorrectos, mensajes de error y recuperación.

## Archivos de prueba

| Archivo | Contenido | Comportamiento observable |
|---|---|---|
| `prueba_valida.txt` | Métodos `iniciar`, `mostrar`, `sumar`, `obtenerValor`, `calcular` y `proceso`; llamadas dentro de sentencias y expresiones; estructura `si/sino`. | Se imprimen los mensajes de reconocimiento de reglas y la finalización del análisis sin errores sintácticos. |
| `prueba_errores.txt` | Paréntesis faltantes, comas duplicadas, un parámetro mal formado, llamada sin paréntesis y llamadas con argumentos incorrectamente escritos. | Se reportan tokens inesperados, se emiten algunos mensajes específicos y el analizador intenta recuperar el análisis. |

Ejemplo de entrada aceptada:

```cpp
class Principal {
    int sumar(int a, int b) {
        retornar a + b;
    }

    void proceso() {
        int total = sumar(5, 10);
        escribir(total);
    }
}
```

## Alcance actual y limitaciones

- La gramática verifica la **estructura sintáctica** de las listas de argumentos, pero **no comprueba** que la cantidad de argumentos de una llamada coincida con la cantidad de parámetros declarados en el método.
- No hay validación semántica completa de existencia de métodos, compatibilidad de tipos ni correspondencia entre el tipo de retorno declarado y la sentencia `retornar`. Por ejemplo, `retornar;` se reconoce sintácticamente incluso dentro de un método declarado `int`.
- La recuperación de errores es parcial: la línea y la columna aparecen en los mensajes de tokens inesperados, mientras que algunas reglas de recuperación solo presentan la línea o un mensaje general.
- El mensaje `Analisis sintactico finalizado correctamente` se imprime al reducir la regla inicial, **incluso cuando se recuperaron errores**. Por ello, no debe interpretarse por sí solo como ausencia de errores; es necesario revisar los mensajes anteriores.
- El lenguaje admitido es un subconjunto definido por los archivos `.jflex` y `.cup`, no todo el estándar de C++.

## Archivos principales para revisar la Actividad 8

- **Reglas de métodos, parámetros, argumentos y retorno:** `src/ManejoErrores/cmasmas_lexico.cup`, líneas 87–124.
- **Llamadas como sentencias y recuperación:** mismo archivo, líneas 135–146.
- **Llamadas como expresiones:** mismo archivo, líneas 175–182.
- **Palabra reservada `retornar` y tokens:** `src/ManejoErrores/java_cmasmas.jflex`.
- **Selección del archivo y ejecución:** `src/ManejoErrores/AnalizadorLexico.java`.

La Actividad 8 amplía el trabajo de la Actividad 7, sin eliminar las reglas de estructuras de control implementadas previamente. 