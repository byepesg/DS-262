# Parchapp — Entrega 1

**Asignatura:** Estructuras de Datos · Universidad Nacional de Colombia · 2026-2
**Lenguaje:** Java 17 o superior, sin librerías externas
**Interfaz:** consola. La interfaz gráfica (Android) y la conexión con Google Places quedan para las siguientes entregas.

## Participantes

| Nombre | Correo |
|---|---|
| Juan Camilo Gomez Chacon | juagomezch@unal.edu.co |
| Santiago Cortes Espinosa | scortese@unal.edu.co |
| Pablo Alejandro Villa | pvilla@unal.edu.co |

## ¿Qué hace?

Parchapp sirve para descubrir lugares y armar planes en grupo, llamados **parches**. En esta entrega
funciona el núcleo completo:

**Lugares**
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

**Tu día**
- **Ruta del día**: lugares para visitar en orden, con la distancia de cada tramo.

**Para la sustentación**
- La pantalla **"Ver estructuras de datos"** muestra los nodos reales de cada lista y cola, y si
  cumplen sus invariantes.

> Los lugares, usuarios y parches iniciales son **datos de demostración**. Las coordenadas son
> aproximadas y la app lo indica. Para probar cómo otra persona se une a tu parche, usa
> *Cambiar de usuario*.

## Cómo ejecutar

Solo se necesita un **JDK 17 o superior** (`java -version`).

- **VS Code** (con *Extension Pack for Java*): abrir la carpeta, ir a *Run and Debug* y elegir
  **Parchapp** o **Pruebas**.
- **NetBeans:** *Clean and Build* (Shift+F11) y luego *Run* (F6). La ventana de salida de NetBeans no
  limpia la pantalla ni muestra colores; para que se vea bien, en *Properties → Run → Arguments*
  escribir `--simple`.
- **IntelliJ IDEA:** marcar `src` como *Sources Root* y `test` como *Test Sources Root*, y ejecutar
  `com.parchapp.Parchapp`.
- **Terminal**, desde la carpeta del proyecto:

```bash
javac -encoding UTF-8 -d out --source-path src src/com/parchapp/Parchapp.java
java -cp out com.parchapp.Parchapp
```

Si la terminal muestra símbolos raros (`?`, `[1;36m`), ejecuten con `--simple`: sin colores, sin
caracteres especiales y sin tildes.

## Pruebas

Son 38 pruebas automáticas:
- de las estructuras;
- de las reglas de los parches;
- de los servicios.

Cubren los casos de la sección 11.3 del documento maestro y las pruebas T-02, T-04 a T-09. Usan un
mini framework propio (`MiniTest`) para no depender de JUnit.

```bash
# Linux / macOS
javac -encoding UTF-8 -d out --source-path src:test test/com/parchapp/pruebas/EjecutarPruebas.java
# Windows
javac -encoding UTF-8 -d out --source-path "src;test" test/com/parchapp/pruebas/EjecutarPruebas.java

java -cp out com.parchapp.pruebas.EjecutarPruebas
```

## Organización del código

```
src/com/parchapp/
  Parchapp.java       Punto de entrada
  estructuras/        Nodo, ListaEnlazada, Cola (hechas por el equipo)
  dominio/            Lugar, Parche, Usuario, Ubicacion, Categoria, EstadoParche, ResultadoUnion
  servicio/           CatalogoLugares, GestorParches, HistorialVistas, RutaDelDia, NavegacionMaps
  datos/              DatosDemo (datos de demostración)
  consola/            Pantalla (dibujar y leer), Sesion, App (menú) y las vistas de cada sección
test/com/parchapp/pruebas/
```

**Regla de dependencias:**
- `consola` usa `servicio` y `dominio`;
- `dominio` y `servicio` usan `estructuras`;
- `estructuras` no depende de nada.

Así las estructuras y las reglas se pueden probar sin la consola, y pasarán sin cambios a la app
Android.

## Estructuras de datos

Ninguna usa `ArrayList`, `LinkedList` ni otra colección de `java.util` para guardar datos.

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
| **Ruta del día** | Las paradas se visitan en el orden en que se agregaron. |

| Operación | Costo |
|---|---|
| `encolar`, `desencolar`, `verFrente` | O(1) |
| `contiene`, `posicionDe`, recorrido | O(n) |

Con la cola vacía, `desencolar` y `verFrente` lanzan `NoSuchElementException` y la cola no cambia.

### Invariantes (se verifican con `invariantesValidos()`)

- `primero == null ⇔ ultimo == null ⇔ tamano == 0`. En la cola es lo mismo con `frente` y `fin`.
- El siguiente del último nodo es `null`.
- `tamano` es igual al número de nodos que se alcanzan desde el primero.

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
| Ruta del día (cola) | Implementado |
| Pruebas automáticas | Implementado |
| Datos guardados entre ejecuciones (persistencia) | Pendiente: todo vive en memoria mientras la app está abierta |
| Google Places, interfaz Android, cuentas reales | Pendiente (siguientes entregas) |
