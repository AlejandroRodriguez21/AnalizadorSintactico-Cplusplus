# Analizador de C++ — Actividad 11

**Materia:** Traductores de Lenguajes  
**Actividad:** 11 — Análisis semántico y verificación de estructuras de control, métodos y clases  
**Implementación:** Java, JFlex y JavaCUP  
**Lenguaje analizado:** subconjunto educativo de C++ con instrucciones adaptadas al español.

## Integrantes

- Centeno Domínguez Edgar Yair
- Ramos Gutiérrez Rodrigo
- Rodríguez Romero Alejandro

## Objetivo

Conservar el análisis léxico, sintáctico y semántico de las actividades anteriores y extender las verificaciones a los contextos de control, las firmas y retornos de métodos, los miembros de clases y la resolución de identificadores por ámbito.

## Funcionalidades incorporadas en la actividad 11

- Condiciones de tipo `bool` en `si`, `mientras`, `hacer-mientras` y `para`, conforme a las reglas del subconjunto utilizado desde la actividad 10.
- Control del contexto de `romper` (`break`) en ciclos y `segun`, y de `continue` en ciclos.
- Validación de `segun` con expresiones integrales y detección de etiquetas `caso` repetidas.
- Registro de firmas de métodos con tipos de parámetros y retorno; validación de argumentos suficientes, adicionales o incompatibles.
- Reconocimiento de sobrecargas mediante listas de tipos de parámetros distintas; detección de firmas duplicadas.
- Comprobación de compatibilidad de `retornar` con métodos `void` y con retorno, y verificación conservadora de caminos sin retorno obligatorio.
- Registro de varias clases y detección de duplicados; miembros con `public:`, `private:` y `protected:`. El acceso por objeto se comprueba mediante la sintaxis `objeto.atributo` y `objeto.metodo(...)`.
- Comprobación de miembro inexistente, miembro no accesible, clase no declarada y variable fuera de ámbito.
- Ámbitos para bloques anidados y variables de inicialización en `para`.
- Tabla de símbolos con columna de acceso y resumen final de errores semánticos con línea, columna, token y causa.

Las reglas y validaciones de los archivos de las actividades 8, 9 y 10 se conservan.

## Archivos principales

| Archivo | Uso |
|---|---|
| `src/ManejoErrores/java_cmasmas.jflex` | Analizador léxico (sin cambios en la actividad 11). |
| `src/ManejoErrores/cmasmas_lexico.cup` | Gramática y acciones semánticas para control, métodos y clases. |
| `src/ManejoErrores/TablaSimbolos.java` | Registro de clases, firmas, miembros, ámbitos, búsquedas y diagnósticos. |
| `src/ManejoErrores/ManejadorTipos.java` | Tipos de expresiones y comprobación de uso de métodos como valores. |
| `src/ManejoErrores/Simbolo.java` | Entrada individual de la tabla (sin cambios). |
| `src/ManejoErrores/AnalizadorLexico.java` | Clase principal y selección de pruebas. |
| `src/ManejoErrores/ParserJava.java` y `sym.java` | Archivos generados por JavaCUP. |

## Pruebas de la actividad 11

| Archivo | Tipo | Resultado verificado |
|---|---|---|
| `prueba_valida_a11.txt` | Válida | 20 símbolos, 0 errores semánticos. |
| `prueba_errores_a11.txt` | Inválida | 20 símbolos, 16 errores semánticos. |
| `prueba_reglas_a11.txt` | Válida complementaria | 10 símbolos, 0 errores semánticos. |
| `prueba_adicional_invalida_a11.txt` | Inválida complementaria | 11 símbolos, 9 errores semánticos. |

Los archivos válidos de las actividades 8, 9 y 10 siguen reconociéndose sin errores semánticos. Los archivos inválidos de las actividades 9 y 10 continúan reportando sus seis errores respectivos.

## Cómo ejecutar en NetBeans

1. Abrir la carpeta `AnalizadorSintactico-Cplusplus` como proyecto Java con Ant (`build.xml`).
2. Utilizar un JDK compatible con `nbproject/project.properties` (la configuración original define Java 26; las pruebas externas se ejecutaron compilando con Java 21).
3. Abrir `src/ManejoErrores/GeneradorCupSintantico.java` y ejecutar **Shift + F6** para regenerar `ParserJava.java` y `sym.java` a partir del `.cup` actualizado. JavaCUP debe finalizar con 0 errores y 0 conflictos.
4. No es necesario regenerar el lexer: `java_cmasmas.jflex` y `cppLexer.java` no cambiaron.
5. Usar **Clean and Build Project** y ejecutar `AnalizadorLexico.java` con **Shift + F6**.
6. En esa clase, cambiar la línea `String rutaPrueba = "prueba_valida_a11.txt";` por otro archivo de prueba si es necesario. Las rutas relativas se resuelven desde la raíz del proyecto.
7. Revisar en la salida el detalle de errores y la tabla de símbolos con el resumen final.

## Alcance del subconjunto

El analizador verifica un subconjunto didáctico de C++, **no compila ni ejecuta** el código de entrada. Se preserva la exigencia de expresiones `bool` en condiciones y la inicialización obligatoria de variables locales establecidas en actividades previas, aunque C++ estándar admite otros comportamientos.

La comprobación de retorno por caminos es conservadora: reconoce secuencias con `retornar` y decisiones `si/sino` cuyos dos bloques garantizan un retorno; no realiza un análisis completo de flujo en todos los ciclos ni evalúa el resultado numérico de las expresiones. La gramática no incluye constructores, herencia, declaraciones adelantadas de métodos ni acceso con puntero `->`; por lo tanto, estas características no se presentan como implementadas. Las comprobaciones de acceso a objetos utilizan miembros ya declarados y pueden distinguir entre visibilidad pública y no pública según el contexto de clase.