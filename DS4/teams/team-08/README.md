# Residencias UNAL - Entrega 1

Proyecto semestral de Estructuras de Datos, Universidad Nacional de Colombia, 2026-2.

## Integrantes

| Nombre completo | Correo institucional |
|---|---|
| Daniel Felipe Morales Rojas | danmoralesro@unal.edu.co |
| John Alejandro Diaz Duran | jodiazdu@unal.edu.co |
| Daniel Poveda Ruiz | dapovedar@unal.edu.co |

## Descripcion

La universidad tiene pocos cupos de residencia y muchos estudiantes que los solicitan.
Este programa registra estudiantes (ID, nombre y puntaje socioeconomico) y asigna los
cupos a quienes tienen **menor puntaje**, porque a menor puntaje hay mayor necesidad.

Funciones del programa:

1. Registrar estudiantes (el ID no se puede repetir).
2. Definir cuantos cupos hay.
3. Buscar un estudiante por su ID.
4. Listar los estudiantes de menor a mayor puntaje.
5. Asignar cupos y mostrar quienes obtuvieron residencia y quienes quedan en espera.
6. Modificar el puntaje de un estudiante (su posicion se actualiza).
7. Eliminar un estudiante.

## Estructuras de datos

| Estructura | Para que se usa | Relacion con el curso |
|---|---|---|
| Arbol binario de busqueda (implementado por nosotros), clave = (puntaje, ID) | Mantener a los estudiantes ordenados por puntaje | Terminologia de arboles y recorrido inorden |
| HashMap (tabla hash de Java, clave = ID) | Buscar por ID en O(1) promedio y evitar IDs repetidos | Se usa como biblioteca; aun no se ha visto en clase |
| ArrayList (arreglo dinamico) | Devolver las listas de estudiantes | Arreglos dinamicos |

**Por que un arbol binario de busqueda?** Como se pueden modificar y eliminar estudiantes en
cualquier momento, necesitamos una estructura que se mantenga ordenada sin reordenar todo.
Una lista ordenada cuesta O(n) por cada insercion o eliminacion. En el arbol, el recorrido
inorden (izquierda, nodo, derecha) entrega los estudiantes de menor a mayor puntaje.

**Por que la clave es (puntaje, ID)?** Dos estudiantes pueden tener el mismo puntaje.
Con el ID como desempate cada estudiante tiene una posicion unica.

**Limitacion conocida.** El arbol no se balancea: si los puntajes llegan ya ordenados, se
convierte en una lista y las operaciones cuestan O(n). El paso 9 de la demostracion lo muestra
(20 estudiantes ordenados dan altura 20 en vez de unos 5). Mejora prevista para una
entrega posterior: usar un arbol balanceado (AVL).

### Costo de cada operacion (n = estudiantes, h = altura del arbol)

| Operacion | Costo | Promedio / peor caso |
|---|---|---|
| Registrar | O(h) | O(log n) / O(n) |
| Buscar por ID | O(1) promedio | (tabla hash) |
| Modificar puntaje | O(h) | O(log n) / O(n) (se saca del arbol, se cambia y se vuelve a insertar) |
| Eliminar | O(h) | O(log n) / O(n) |
| Listar ordenado | O(n) | recorrido inorden |
| Asignar cupos | O(n) | se recorre el arbol y se parte la lista en dos |

## Organizacion del codigo

Todo esta en `Main.java`, dividido en clases:

| Clase | Responsabilidad |
|---|---|
| `Estudiante` | Datos de cada estudiante y como se comparan (puntaje, luego ID) |
| `Nodo` | Casilla del arbol |
| `ArbolBinarioBusqueda` | Arbol ordenado por puntaje |
| `SistemaResidencias` | Logica: usa el HashMap y el arbol juntos |
| `Main` | Menu de consola y demostracion |

## Como ejecutarlo

Profe es necesario tener JDK version 17 o superior

## PROXIMAS ENTREGAS (WILL RETURN IN AVENGERS DOOMSDAY)