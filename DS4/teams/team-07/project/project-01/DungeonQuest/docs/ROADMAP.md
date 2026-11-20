# Roadmap de desarrollo — Dungeon Quest

Plan incremental del proyecto universitario de Estructuras de Datos. El objetivo inmediato es preparar la Entrega 2; las ideas de alcance final se mantienen separadas para no distraer de esa entrega.

Última actualización: 20 de noviembre de 2026.

Leyenda: ✅ implementado/documentado · 🟡 parcial o por verificar · ⬜ pendiente · ⏸️ en espera · ❌ descartado

## Prioridad actual — preparación de la Entrega 2

La Entrega 2 es una evolución del documento técnico presentado en la Entrega 1 y debe corresponder al código y a la demostración del repositorio. Según el PDF de instrucciones, los productos son PDF, código fuente en GitHub y sustentación.

### Estado observado

- ✅ El juego incluye exploración procedural, grafo y BFS, enemigos, combate por turnos, combos, árbol de habilidades y guardado.
- ✅ Las estructuras y scripts están organizados en `scripts/`.
- ✅ `tests/run_tests.gd` contiene pruebas de estructuras de datos, algoritmos, persistencia y sus interacciones. `tests/results.md` registra 100/100 verificaciones, mediciones de rendimiento y 2400 generaciones de mapas.
- 🟡 Las pruebas automatizadas se enfocan deliberadamente en las estructuras de datos y algoritmos del curso; ese es el alcance previsto para esta etapa, no una tarea pendiente de convertir en una suite de pruebas de escenas.
- 🟡 El README y el documento técnico deben coincidir con la implementación exacta, incluidas las reglas actuales de combos y la versión de Godot.
- ⬜ Completar y revisar el PDF técnico partiendo del documento de Entrega 1, no creando uno independiente.
- ⬜ Preparar la demostración y la sustentación; todos los integrantes deben poder explicar las estructuras, las operaciones, sus complejidades, pruebas y resultados.
- ⬜ Verificar que la rama y el repositorio oficial contengan la versión que se presenta.

### Lista de cierre

1. ✅ Corregido el doble otorgamiento de puntos al ganar un combate; se conserva una llamada a `skills.add_points(group.size())`.
2. ✅ Alineado el README con el código: indica Godot 4.7 y describe que una flecha incorrecta rompe el combo y hace perder el turno.
3. ⬜ Actualizar el PDF técnico desde la Entrega 1: problema, objetivos, alcance, diseño actual, estructuras y algoritmos, operaciones, decisiones, análisis de complejidad, pruebas y resultados.
4. ⬜ Explicar los resultados de `tests/results.md` en el PDF y conservar la evidencia en el repositorio.
5. ⬜ Registrar el estado actual y el trabajo que se propone para la entrega final, incluyendo la parte del proyecto que el equipo considera completada.
6. ⬜ Hacer una ejecución de prueba y una demostración de las funciones principales; confirmar instrucciones de arranque y versión de Godot.
7. ⬜ Revisar que PDF, código, README y demostración representen la misma versión.
8. ✅ Fecha vigente confirmada por el usuario: 22 de noviembre de 2026. El PDF de instrucciones conserva la fecha original del 15 de noviembre.

### Alcance de las pruebas de esta etapa

Las pruebas automatizadas están dirigidas a verificar las estructuras de datos y algoritmos implementados: min-heap, cola circular, trie, grafo, BFS, Union-Find, generación procedural, árbol de habilidades, persistencia e integración entre estructuras. Los resultados y benchmarks se mantienen en `tests/results.md`; no se deben presentar como pruebas automatizadas de todo el flujo visual del juego.

La sustentación y la demostración complementan esa evidencia mostrando el funcionamiento principal del juego. Ampliar la suite a pruebas de escenas queda fuera de las prioridades actuales, salvo que el equipo o el docente lo soliciten.

### Mejoras de juego en curso

- 🟡 Las salas intermedias usan marcadores de aparición; el spawner limita el grupo a puntos dentro de la sala y libres de colisiones conocidas. Falta validar visualmente varias plantillas y mapas.
- 🟡 Los enemigos tienen cono direccional y comprobación de visión con raycast. Falta inspeccionar visualmente su orientación, visibilidad y oclusión en ejecución.
- 🟡 El jugador comparte sus `SpriteFrames` de exploración y combate. Los enemigos de combate ya usan `AnimatedSprite2D`, pero por ahora solo hay un frame placeholder; ataque, daño y muerte necesitan arte y animaciones reales.
- 🟡 El combate ya incluye transiciones de franjas al entrar y salir, ataques con squash/stretch, sacudida de pantalla, números flotantes y efectos diferenciados; los enemigos conservan el idle sin rebote y usan hurt/death procedurales. Falta ajustar su apariencia en ejecución.
- 🟡 Las habilidades se definen en recursos editables y la interfaz sigue generándose desde el grafo; cada recurso puede optar por una posición personalizada. El árbol ahora anima su aparición por niveles y el desbloqueo resalta la ruta y el nodo.
- 🟡 Parry con ventana perfecta destacada y resultado animado como impacto; combos con flechas vectoriales grandes, una barra de tiempo y un rótulo de poder en impacto que toma el color de su efecto. Falta revisar estos efectos visualmente en ejecución.
- 🟡 Las transiciones principales usan bandas alternadas al cambiar de sala, entrar/salir de combate o abrir/cerrar el árbol; las pantallas de menú usan el mismo reveal al cargarse.

## Estado por fases

### Fase 0 — base del juego ✅

- Movimiento, animaciones, colisiones y cámara con límites por sala.
- Puertas con distintas orientaciones, conexiones gemelas y transición entre salas.
- Grafo de salas, validación de conexiones y pista por BFS.
- Menú de inicio y pantallas de victoria/derrota.

### Fase 1 — prototipo y organización 🟡

- ✅ Lógica principal separada en configuración, construcción de mapa, generación de enemigos y efectos de pantalla.
- ✅ Scripts organizados por dominio.
- ✅ README con instalación, ejecución y controles.
- 🟡 Revisar consistencia final del README, PDF y código.
- ⬜ Confirmar que el flujo de juego completo está listo para demostrarse en la versión que se entregará.

### Fase 2 — exploración ✅

- ✅ Mazmorra procedural de ocho salas con grafo, Union-Find/Kruskal y semilla.
- ✅ Entrada y salida como hojas; salida ubicada según distancia BFS.
- ✅ Minimapa con niebla y cuatro plantillas de sala.
- ✅ Validación automatizada de conectividad, adyacencia, celdas, hojas y direcciones.

### Fase 3 — enemigos 🟡

- ✅ Enemigos por sala, detección de jugador, alerta de grupo y reinicio al salir.
- ✅ Cuatro tipos con estadísticas distintas; la variedad depende de la distancia a la entrada.
- 🟡 Apariciones con marcadores y validación de espacio; falta revisar el resultado visual en ejecución.
- 🟡 Cono de visión direccional con bloqueo por paredes; falta validación visual.
- ⏸️ Sprites propios para cada enemigo.
- ⏸️ Navegación que esquive obstáculos.

### Fase 4 — combate 🟡

- ✅ Combate por turnos sobre la escena principal; min-heap para el orden de turnos.
- ✅ Ataque, defensa, puntos de acción y parry con cola circular.
- ✅ Combos de flechas procesados por trie y con límite de tiempo.
- ✅ Feedback de impacto para el resultado del parry y para el nombre del poder al completar un combo.
- 🟡 Jugador animado con los frames compartidos de exploración; enemigos preparados para animación, aún con placeholder.
- ✅ Corregido el otorgamiento duplicado de puntos; verificar el flujo de victoria/derrota antes de presentar.
- ⬜ Decidir más adelante si se recupera vida entre combates.
- ⏸️ Efectos de estado con lista enlazada.

### Fase 5 — poderes y progresión ✅/🟡

- ✅ Árbol general de habilidades con desbloqueo, reinicio de ramas, recorrido BFS y guardado.
- 🟡 Definiciones en recursos editables y posiciones personalizables; interfaz generada desde el árbol.
- ✅ Habilidades conectadas con poderes del combate.
- ✅ Puntos de habilidad por progreso y victoria final.
- 🟡 Verificar el balance durante la demostración.
- ⏸️ Iconos propios, más ramas y más poderes.

### Fase 6 — alcance posterior a la Entrega 2 ⏸️

- Pulido de pantallas, sprites, música y efectos de sonido.
- Recuperación de vida y salas especiales.
- Inventario con árbol binario de búsqueda.
- Jefe con heap de amenaza.
- Efectos de estado con lista enlazada, si se confirma su inclusión en el alcance final.
- Récords, semilla visible y modo reto.

## Decisiones para mantener el alcance

- No agregar estructuras solo para aumentar su cantidad; cada una debe resolver una necesidad del juego y explicarse técnicamente.
- Priorizar el cierre de la Entrega 2 antes de empezar nuevas mecánicas.
- Mantener las pruebas automatizadas enfocadas en las estructuras y algoritmos que se deben explicar en la asignatura.
- No asignar una fecha de entrega ni un porcentaje completado sin confirmación del equipo.
