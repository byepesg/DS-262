# Ideas y pendientes — Dungeon Quest

Documento de seguimiento de decisiones y trabajo futuro. Para el estado de entregas y prioridades, consultar [ROADMAP.md](ROADMAP.md).

Última actualización: 19 de noviembre de 2026.

Leyenda: ✅ implementado · 🟡 parcial o por verificar · ⬜ pendiente · ⏸️ en espera · ❌ descartado

## 1. Estado implementado

### Exploración

- Mazmorra procedural de ocho salas en cuadrícula, representada por un grafo no dirigido.
- Generación con Union-Find/Kruskal, semilla y conexiones adicionales.
- BFS para rutas cortas, distancias y pistas visuales hacia la salida.
- Entrada y salida como salas hoja; minimapa con niebla y plantillas `RoomA`–`RoomD`.

### Enemigos

- Enemigos por sala con alerta grupal y reinicio al abandonar la sala.
- Cuatro tipos con estadísticas almacenadas en `EnemyTypes`; las opciones dependen de la distancia a la entrada.
- 🟡 Marcadores y validación de espacios de aparición contra límites y colisiones; falta comprobar visualmente todas las plantillas.
- 🟡 Cono direccional y raycast para bloquear la visión a través de paredes; el resultado visual aún requiere revisión en ejecución.

### Combate

- Combate por turnos como capa sobre la partida; min-heap para programar turnos según velocidad.
- Ataque normal con selección de objetivo, defensa y puntos de acción.
- Cola circular de eventos para resolver la ventana de parry.
- Trie para reconocer combos ocultos y cola circular como búfer de teclas.
- Los poderes cuestan puntos de acción; no usan enfriamientos con heap.
- 🟡 El jugador reutiliza sus animaciones de exploración; los enemigos usan `AnimatedSprite2D` con un placeholder de un solo frame hasta que existan sprites propios.

### Progresión e interfaz

- Árbol general de habilidades con desbloqueo por requisitos, reinicio de ramas, BFS por niveles y guardado.
- 🟡 Definiciones editables como recursos; la interfaz se genera desde el árbol y admite posiciones personalizadas.
- Poderes del árbol conectados a los combos del combate.
- Menú, HUD, minimapa, árbol de habilidades y pantallas de victoria y derrota.

## 2. Pruebas de estructuras de datos

El alcance automatizado actual corresponde a las estructuras de datos y algoritmos del curso. No se considera pendiente convertir esta suite en una batería de pruebas de interfaz o del juego completo.

El archivo `tests/run_tests.gd` cubre:

- `MinHeap`: orden, estabilidad, operaciones y simulación del orden de turnos.
- `CircularQueue`: FIFO, crecimiento, vuelta circular, limpieza y operaciones mezcladas.
- `ComboTrie`: reconocimiento de secuencias, prefijos y caminos inválidos.
- `DungeonGraph`: grafo no dirigido, BFS, Union-Find y generación procedural.
- `SkillTree`: inserción, desbloqueo, reinicio de ramas, recorrido, guardado y carga.
- Integración de los identificadores de poderes y del orden de turnos entre estructuras.

`tests/results.md` registra 100 verificaciones aprobadas, pruebas sobre 2400 generaciones de mapas y mediciones de tiempo. El equipo debe volver a ejecutar el comando documentado en el README antes de entregar y explicar qué valida cada grupo de pruebas y cómo interpretar los benchmarks.

## 3. Trabajo inmediato para la Entrega 2

- ✅ Corregir la llamada duplicada a `skills.add_points(group.size())` en `scripts/core/main.gd`; la victoria acredita una vez al grupo.
- ✅ Corregir en el README la versión de Godot de la descripción (4.7).
- ✅ Alinear la descripción del combo roto con `BREAK_LOSES_TURN`: una flecha incorrecta hace perder el turno.
- 🟡 Revisar visualmente las apariciones, la oclusión y los conos de visión en varias salas.
- 🟡 Probar la pantalla del árbol con el catálogo de recursos y posiciones editables.
- ⏸️ Integrar sprites reales y animaciones de enemigos cuando el equipo disponga de arte adecuado.
- ⬜ Completar el PDF técnico como evolución del entregado en la primera etapa.
- ⬜ Incluir en el PDF evidencia e interpretación de las pruebas de estructuras, generación procedural y rendimiento.
- ⬜ Practicar una demostración del juego y preparar explicaciones de operaciones y complejidades.
- ✅ Fecha vigente confirmada por el usuario: 22 de noviembre de 2026; el PDF de instrucciones conserva la fecha original del 15 de noviembre.

## 4. Ideas para el alcance final

### Combate

| Idea | Estructura | Estado |
|---|---|---|
| Efectos de estado (quemadura, veneno, debilidad) | Lista enlazada | ⏸️ Posible trabajo futuro |
| Selección del enemigo para poderes de objetivo único | Arreglo/UI | ⬜ Mejora posible; hoy se usa el primer enemigo vivo |
| Enfriamientos de poderes | Min-heap | ❌ No se usan; los poderes cuestan puntos de acción |
| Historial de acciones o deshacer | Pila | ❌ Descartada |

### Enemigos

| Idea | Estructura/algoritmo | Estado |
|---|---|---|
| Persecución entre habitaciones | Grafo + BFS | ⬜ Hoy la persecución es dentro de la sala |
| Rutas con costos distintos por puerta | Dijkstra + cola de prioridad | ⬜ Idea futura |
| Patrullas por varias habitaciones | Cola circular | ⬜ Idea futura |
| Decisiones de IA (huir, atacar, patrullar) | Árbol de decisión | ⬜ Idea futura |
| Estadísticas por tipo y grupos por sala | Diccionarios | ✅ Implementado |

### Progresión e inventario

| Idea | Estructura | Estado |
|---|---|---|
| Inventario ordenado y botín | Árbol binario de búsqueda | ⬜ Opcional |
| Elegir objetivo con más amenaza | Heap de amenaza | ⬜ Posible jefe |
| Diálogos o eventos ramificados | Árbol | ⬜ Idea futura |
| Más poderes y ramas | Árbol de habilidades + trie | ⏸️ Después de la Entrega 2 |
| Iconos propios por habilidad | Recursos gráficos | ⬜ Pendiente |

### Presentación y contenido

- ⏸️ Recuperación de vida entre combates o en salas de descanso.
- ⏸️ Salas de tesoro, trampa y descanso.
- ⏸️ Sprites propios para enemigos y música/efectos de sonido.
- ⬜ Documentar origen y licencia de fuentes y sprites de terceros antes de la publicación final.
- ⬜ Semilla visible, modo reto y récords.

## 5. Decisiones confirmadas

- El docente aprobó el conjunto actual de estructuras: min-heap, cola circular, trie, árbol general y Union-Find.
- El combate es por turnos, con parry y entrada de combos como momentos de interacción en tiempo real.
- Los poderes actuales son Corte, Llamarada, Sanación y Tormenta; sus secuencias están en `Combat.POWERS`.
- El árbol de habilidades tiene ramas Espada, Luz y Fuego.
- El jugador empieza con tres puntos; gana puntos una vez por cada enemigo derrotado y al llegar a la salida.
- El combo no revela la secuencia mientras se escribe. El código actual hace perder el turno al equivocarse de flecha, y el README ya refleja esa regla.
- Las pruebas automatizadas de esta entrega se concentran en las estructuras de datos y algoritmos. La demostración del juego sirve para mostrar el flujo funcional, sin ampliar por ahora el alcance de la suite.

## 6. Por decidir

- Definir qué ideas entran efectivamente en la entrega final, según tiempo y retroalimentación docente.
- Decidir si se recupera vida entre combates y cómo se balancean enemigos, poderes y puntos.
- Acordar el porcentaje/avance conceptual que se reportará en el PDF técnico.
