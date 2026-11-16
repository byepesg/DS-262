# Residencias UNAL - Entrega 2

Evolución de **ResidenciasUNAL** de la Entrega 1, preparada para **NetBeans 31**.
La base recibida se llamaba `ResidenciasUNAL-NetBeans30.zip`.
Se mantienen las clases originales, sus operaciones públicas y la regla de prioridad:
**menor puntaje socioeconómico primero; en empate, menor ID**. Se conserva el rango
0-100, los IDs enteros positivos y la asignación sin eliminar solicitudes.

## Abrir y ejecutar en NetBeans 31

1. Descomprima el ZIP.
2. Seleccione **File > Open Project** y abra esta carpeta, la que contiene `pom.xml`.
3. Configure un **JDK 21 completo** como plataforma Java del proyecto.
4. Espere a que el Maven integrado descargue las dependencias la primera vez.
5. Use **Run Project (F6)** para abrir el menú.
6. Use **Test Project (Alt+F6)** para ejecutar las pruebas.
7. Use **Clean and Build** para generar el JAR.

Si NetBeans usa otra plataforma, revise las propiedades del proyecto y la plataforma
Java con la que está ejecutando Maven. El proyecto mantiene `maven.compiler.release=21`.
La [página oficial de NetBeans 31](https://netbeans.apache.org/front/main/download/nb31/)
confirma soporte para ejecutar el IDE con JDK 21. No se necesita instalar una base de datos.

## Compilar desde terminal

Requisitos: JDK 21 y Maven. Validación realizada con Maven 3.9.9.

```bash
mvn clean verify
mvn exec:java
mvn clean package
java -jar target/residencias-unal-2.0-SNAPSHOT.jar
```

La validación ejecutó **47 pruebas: todas aprobadas**, con 0 fallos y 0 errores.

La aplicación no tiene dependencias externas en ejecución. Para pruebas se conserva
JUnit Jupiter 5.11.4. Los plugins Maven están declarados en `pom.xml`.
Se necesita internet en la primera descarga; después, el menú y los archivos funcionan localmente.

## Qué cambió respecto a la Entrega 1

| Antes | Ahora |
| --- | --- |
| Motor ligado a `Estudiante` | `SistemaAsignacion<T>` reutilizable y `SistemaResidencias` como subclase |
| Siete operaciones de consola | Se mantienen 1-7 y se agregan estado, espera, resumen, historial y archivos |
| Datos solo en memoria | Guardar y cargar estudiantes y cupos en UTF-8 (`.properties`) |
| Resultado mostrado en consola | También reporte CSV para Excel |
| Una actualización inválida podía desincronizar el heap | Validación sobre copia antes de modificar las estructuras |
| Consultas exponían objetos internos mutables | Consultas y listados devuelven copias independientes |
| Seis pruebas iniciales | Se conservan y se amplían con regresión, integración y casos límite |

La lógica común original de `HashMap`, `PriorityQueue`, listado y distribución por
cupos se trasladó a la superclase. No se creó otro problema ni se reemplazó la regla
de asignación. Una especialización distinta, solo en las pruebas, demuestra que el
motor puede reutilizarse con otro tipo de solicitud y otra prioridad.

## Menú

| Opción | Operación |
| --- | --- |
| 1-7 | Registrar, consultar ID, listar, modificar, eliminar, definir cupos, asignar |
| 8 | Consultar estado y posición actual por ID |
| 9 | Mostrar la lista de espera por prioridad |
| 10 | Resumen: solicitudes, cupos, beneficiarios, espera, vacantes y promedio |
| 11 | Últimas 100 operaciones exitosas de la sesión, en orden cronológico |
| 12 | Guardar estudiantes y cupos en un archivo `.properties` |
| 13 | Cargar un archivo validado y reemplazar estudiantes y cupos |
| 14 | Exportar la asignación actual a CSV |
| 0 | Salir |

Para una demostración rápida: opción **13**, escriba
`ejemplos/datos-demo.properties` y confirme con **CARGAR**. Luego use **3**, **7**,
**8** con ID **40**, **9** y **10**. Los resultados esperados y el guion completo
están en `docs/SUSTENTACION.md`.

Las rutas relativas se resuelven desde el directorio de ejecución (normalmente esta
carpeta en NetBeans). También se aceptan rutas absolutas y rutas con espacios,
sin comillas alrededor. La carpeta de destino debe existir. Guardar o exportar
sobre un archivo existente lo reemplaza. Cargar requiere escribir `CARGAR` en la
consola porque sustituye los estudiantes y cupos actuales. Si el archivo es inválido,
el estado y el historial anteriores se conservan.

## Estructura y documentación

- `src/main/java/co/edu/unal/residencias/`: clases originales ampliadas y motor general.
- `src/test/java/co/edu/unal/residencias/`: pruebas JUnit y evaluación de tamaños de entrada.
- `ejemplos/`: datos ficticios y entradas reproducibles para el menú.
- `docs/ACTUALIZACION_INFORME.md`: texto editable del informe completo de la segunda entrega.
- `docs/DS3_G5_Proyecto_Entrega2.pdf`: informe actualizado a partir del documento de la Entrega 1.
- `docs/EVOLUCION.md`: qué se conservó, qué se extrajo y qué se añadió.
- `docs/MATRIZ_REQUISITOS.md`: correspondencia entre guía, código y evidencias.
- `docs/PRUEBAS.md` y `docs/evidencias/`: casos, resultados y mediciones reales.
- `docs/SUSTENTACION.md`: explicación sencilla y demostración.
- `docs/ENTREGA_GITHUB.md`: incorporación al repositorio existente.
- `cambios/entrega1-a-entrega2.patch`: diferencia de código/configuración respecto del ZIP recibido.

## Alcance y límites actuales

La asignación se recalcula con los puntajes y cupos vigentes; no reserva habitaciones
ni conserva adjudicaciones históricas definitivas. La lista de espera mantiene el
mismo orden de prioridad, no el orden de llegada. El historial registra hasta 100
operaciones exitosas y no se guarda al cerrar. Los cambios se conservan entre
sesiones cuando se usa la opción 12 y se restauran con la 13.
No hay base de datos SQL, interfaz gráfica, usuarios concurrentes ni autenticación.
Estas ampliaciones pueden evaluarse para la entrega final.

## Entrega académica

El informe `docs/DS3_G5_Proyecto_Entrega2.pdf` actualiza el documento de la primera
entrega aportado por el equipo, conservando sus autores, correos, problema y
estructura académica. Incorpora las funcionalidades de la versión 2.0-SNAPSHOT,
sus decisiones de diseño, las 47 pruebas y los resultados registrados.

El código aún debe incorporarse al repositorio real del equipo siguiendo la guía
del curso. No se ha publicado un pull request desde este entorno; se incluyen
instrucciones para continuar el historial existente.

## Integrantes - DS3, G5

| Nombre | Correo |
| --- | --- |
| Laura Victoria Flórez Forero | lflorezf@unal.edu.co |
| Juan Sebastian Manrique Mesa | jumanriqueme@unal.edu.co |
| Jhon Alexander Berdugo Pasachoa | jberdugop@unal.edu.co |
