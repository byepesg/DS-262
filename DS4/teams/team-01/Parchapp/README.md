# Parchapp — Entrega 2

**Asignatura:** Estructuras de Datos · Universidad Nacional de Colombia · 2026-2
**Lenguaje:** Java 17 o superior. El núcleo (`core`) no usa librerías externas.
**Interfaces:** app **Android** (versión 0.4.0) y **consola** (la de la Entrega 1). Ambas usan el mismo núcleo.

## Participantes

| Nombre | Correo |
|---|---|
| Juan Camilo Gomez Chacon | juagomezch@unal.edu.co |
| Santiago Cortes Espinosa | scortese@unal.edu.co |
| Pablo Alejandro Villa | pvilla@unal.edu.co |

## ¿Qué hace?

Parchapp sirve para descubrir lugares y armar planes en grupo, llamados **parches**.

**¿Qué hacemos hoy?** (app Android)
- Unas preguntas cortas: con quién vas (solo, en pareja, con amigos, en familia), cuántos son, qué
  tipo de plan, si quieren tragos, si quieren comer y cuánto gastar.
- Al final muestra los 5 lugares que mejor encajan y por qué. Usa un **montículo de máximos**.

**Lugares**
- 68 lugares de Bogotá y alrededores en 10 categorías (comida, cafés, rumba, cultura, miradores,
  parques, deporte, diversión, aventura y estudio), con zona, precio y etiquetas.
- Explorar, buscar por nombre (sin importar tildes ni mayúsculas) y filtrar por categoría.
- Ordenar por nombre, cercanía o categoría.
- Ver el detalle de un lugar y abrir **"Cómo llegar" en Google Maps**.
- Historial de lugares **vistos recientemente**.

**Parches**
- **Crear** un parche: lugar, título, día, hora y capacidad.
- **Unirse**, con reglas para no repetirse ni pasar el cupo.
- **Lista de espera**: si está lleno, quedas en fila. Cuando alguien sale, entra el primero de la fila.
- **Salir** de un parche y **cancelarlo** si lo organizas.
- Estados: abierto, lleno, cancelado y finalizado. Cuando pasa la fecha, el parche queda finalizado solo.

**Solo en la consola**
- **Ruta del día**: lugares para visitar en orden, con la distancia de cada tramo.
- **"Ver estructuras de datos"**: muestra los nodos reales de cada lista y cola, y si cumplen sus
  invariantes. Sirve para la sustentación.

> Los lugares, 40 usuarios y unos 50 parches iniciales son datos de prueba (los parches se generan
> con una semilla fija). Las coordenadas son aproximadas. Para probar cómo otra persona se une a tu
> parche, usa *Cambiar de cuenta* en Perfil.

## Cómo ejecutar

### Consola y pruebas: solo se necesita un JDK 17 o superior (`java -version`)

El proyecto usa **Gradle** a través de su *wrapper* (`gradlew`), que descarga Gradle la primera vez.
Desde la carpeta del proyecto:

```bash
./gradlew :consola:run -q --console=plain          # la app de consola (Windows: gradlew.bat)
./gradlew :consola:run -q --console=plain --args=--simple   # sin colores ni caracteres especiales
./gradlew :core:pruebas                             # las pruebas automáticas
```

Sin Gradle, solo con `javac` (Linux/macOS; en Windows cambiar `:` por `;`):

```bash
javac -encoding UTF-8 -d out --source-path core/src/main/java:consola/src/main/java \
      consola/src/main/java/com/parchapp/Parchapp.java
java -cp out com.parchapp.Parchapp

javac -encoding UTF-8 -d out --source-path core/src/main/java:core/src/test/java \
      core/src/test/java/com/parchapp/pruebas/EjecutarPruebas.java
java -cp out com.parchapp.pruebas.EjecutarPruebas
```

Si la terminal muestra símbolos raros (`?`, `[1;36m`), ejecuten con `--simple`: sin colores, sin
caracteres especiales y sin tildes. **VS Code**, **IntelliJ IDEA** y **NetBeans** abren el proyecto
directamente como proyecto Gradle.

### App Android

- **Con Android Studio** (equipo x86_64): abrir la carpeta `Parchapp/` y ejecutar el módulo `app`.
  Android Studio crea `local.properties` con la ruta del SDK, y con eso Gradle incluye el módulo.
- **Sin Android Studio:** el APK se compila en GitHub Actions
  (`.github/workflows/parchapp-apk.yml` en el fork del equipo) y se descarga desde la pestaña
  *Actions*, en el artefacto `parchapp-apk`.

Requisitos: Android 8.0 (API 26) o superior. Se compila contra la API 36 (Android 16).

## Pruebas

Son 47 pruebas automáticas:
- de las estructuras;
- de las reglas de los parches;
- de los servicios;
- del montículo y del recomendador.

Cubren los casos de la sección 11.3 del documento maestro y las pruebas T-02, T-04 a T-09. Usan un
mini framework propio (`MiniTest`) para no depender de JUnit. `./gradlew :core:check` también las
ejecuta y falla si alguna falla.

## Organización del código

```
core/       Núcleo en Java puro: no depende de Android ni de la consola
  src/main/java/com/parchapp/
    estructuras/      Nodo, ListaEnlazada, Cola, MonticuloMax (hechas por el equipo)
    dominio/          Lugar, Parche, Usuario, Ubicacion, Categoria, Etiqueta, Compania, EstadoParche, ResultadoUnion
    servicio/         CatalogoLugares, GestorParches, HistorialVistas, RutaDelDia, NavegacionMaps,
                      Preferencias, Recomendador, Recomendacion
    datos/            DatosDemo (datos de demostración)
  src/test/java/com/parchapp/pruebas/
consola/    Interfaz de texto: Pantalla, Sesion, App (menú), vistas y Navegador
app/        Aplicación Android (pantallas, adaptadores y recursos)
```

**Regla de dependencias:**
- `consola` y `app` usan `core`; `core` no conoce ninguna interfaz;
- dentro de `core`, `dominio` y `servicio` usan `estructuras`, y `estructuras` no depende de nada.

Así las estructuras y las reglas se escriben y prueban una sola vez y las usan las dos interfaces.
`core` evita las APIs de Java que no existen en Android (`java.awt`, `String.strip()`, etc.): por eso
`NavegacionMaps` solo arma el enlace a Google Maps, y cada interfaz decide cómo abrirlo.

## Estructuras de datos

Ninguna usa `ArrayList`, `LinkedList`, `PriorityQueue` ni otra colección de `java.util` para guardar datos.

### `ListaEnlazada<T>`: lista simple con referencias al primero y al último

| Dónde se usa | Por qué |
|---|---|
| Catálogo de lugares y resultados de búsqueda | Se recorren completos y se ordenan. |
| **Historial de vistos** (máx. 10, sin repetidos) | La operación principal es insertar al inicio: O(1) en la lista, O(n) en un arreglo. |
| Todos los parches | Se agregan al final y se recorren para filtrar. |
| **Participantes** de cada parche | Se agregan al final y se puede eliminar a quien sale. |

| Operación | Costo |
|---|---|
| `agregarAlInicio`, `agregarAlFinal`, `eliminarPrimero`, `obtenerPrimero`, `obtenerUltimo` | O(1) |
| `obtener(i)`, `contiene`, `eliminar`, `eliminarUltimo` | O(n) |
| Recorrido con `for-each` (iterador propio) | O(n) |
| `ordenar` (merge sort, estable) | O(n log n) |

`agregarAlFinal` es O(1) porque se guarda la referencia al último nodo. Se usa merge sort porque solo
necesita recorrer y reenlazar nodos; los métodos que acceden por posición serían lentos en una lista.

### `Cola<T>`: FIFO con referencias al frente y al fin

| Dónde se usa | Por qué |
|---|---|
| **Lista de espera** de cada parche | Es justo que entre primero quien llegó primero. |
| **Ruta del día** (consola) | Las paradas se visitan en el orden en que se agregaron. |

| Operación | Costo |
|---|---|
| `encolar`, `desencolar`, `verFrente` | O(1) |
| `contiene`, `posicionDe`, recorrido | O(n) |

Con la cola vacía, `desencolar` y `verFrente` lanzan `NoSuchElementException` y la cola no cambia.

### `MonticuloMax<T>`: montículo binario de máximos sobre un arreglo

| Dónde se usa | Por qué |
|---|---|
| **¿Qué hacemos hoy?** (`Recomendador`) | Hay que mostrar los 5 mejores lugares sin ordenar todo el catálogo. |

| Operación | Costo |
|---|---|
| `insertar` | O(log n); el arreglo se duplica cuando se llena (O(1) amortizado) |
| `extraerMax` | O(log n) |
| `verMax`, `tamano`, `estaVacio` | O(1) |

Para el nodo en `i`, los hijos están en `2i+1` y `2i+2` y el padre en `(i-1)/2`. Ningún hijo es mayor
que su padre. Recomendar k lugares entre n cuesta O(n log n + k log n).

### Etiquetas como bits

Cada lugar tiene etiquetas (romántico, con vista, con tragos, en familia…). Cada una es un bit de un
`int`, así que cruzar lo que tiene un lugar con lo que se busca es un AND: O(1).

### Invariantes (se verifican con `invariantesValidos()`)

- `primero == null ⇔ ultimo == null ⇔ tamano == 0`. En la cola es lo mismo con `frente` y `fin`.
- El siguiente del último nodo es `null`.
- `tamano` es igual al número de nodos que se alcanzan desde el primero.
- En el montículo: ningún hijo es mayor que su padre y las casillas después de `tamano` son `null`.

## Reglas de los parches

- La capacidad (de 2 a 30) **incluye a quien crea** el parche, que entra como primer participante.
- Nadie puede estar dos veces, ni como participante ni en la lista de espera.
- Al llegar a la capacidad, el parche pasa a **LLENO** y quien quiera entrar va a la lista de espera.
- Si alguien sale, el primero de la lista de espera toma su cupo. Si no hay nadie esperando, el parche
  vuelve a **ABIERTO**.
- Quien crea el parche no puede salirse, pero puede **cancelarlo**.
- Cuando pasa la fecha, el parche queda **FINALIZADO**. Un parche cancelado o finalizado no acepta
  cambios.
- Las reglas están en `Parche` y `GestorParches`, no en la consola. La interfaz también valida, para
  ayudar al usuario, pero el modelo es el que garantiza las reglas.

## Estado de implementación

| Función | Estado |
|---|---|
| Explorar, buscar, filtrar y ordenar lugares | Implementado |
| Detalle de lugar y "Cómo llegar" en Google Maps | Implementado |
| Historial de vistos (lista enlazada) | Implementado |
| Crear, unirse, salir y cancelar parches; estados | Implementado |
| Lista de espera (cola) | Implementado |
| Ruta del día (cola, en la consola) | Implementado |
| ¿Qué hacemos hoy? (montículo y etiquetas) | Implementado |
| App Android | Implementado (0.4.0) |
| Pruebas automáticas | Implementado |
| Datos guardados entre ejecuciones (persistencia) | Pendiente: todo vive en memoria mientras la app está abierta |
| Google Places, cuentas reales, tabla hash por id | Pendiente (siguientes entregas) |

## Créditos

- Tipografía de títulos: **DM Serif Display** (Google Fonts), licencia SIL Open Font License 1.1.
  El texto de la licencia está en `licencias/OFL-DM-Serif-Display.txt`.
- Íconos: **Material Symbols Rounded** de Google, licencia Apache 2.0. Cada archivo
  `app/src/main/res/drawable/ic_*.xml` dice de qué ícono viene.
