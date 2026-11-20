# Recursos gráficos

## Animaciones de personajes

- `sprites/player/player_frames.tres` contiene los frames actuales del jugador para exploración y combate. Las animaciones de combate pueden añadirse como animaciones adicionales o reemplazarse por un recurso dedicado en `CombatScene.tscn`.
- El código de combate busca `attack_right`, `cast_right`, `hurt_right` y `heal_right`, y vuelve a una animación disponible si todavía no existen.
- `sprites/enemies/placeholder_frames.tres` es un placeholder de un solo frame. Cada entrada de `EnemyTypes.TYPES` indica el recurso `sprite_frames` que usan exploración y combate; reemplaza ese recurso cuando estén listas las animaciones del tipo.
- En batalla, los enemigos conservan `idle` sin movimiento automático. `hurt` y `death` se representan con animaciones procedurales de deformación, retroceso, giro y desvanecimiento, así que funcionan aunque el sprite solo tenga un frame.

Las animaciones de combate pueden usar celdas de 32×32 aunque el personaje dibujado ocupe 16×16. Mantén el centro del personaje alineado entre frames. El tamaño de la imagen no modifica la colisión.

## Efectos de combate

`scripts/combat/battle_effects.gd` genera feedback temporal de daño y curación sin teñir permanentemente los sprites. La tabla `BattleEffects.COLORS` centraliza los colores para impacto físico, fuego, curación, electricidad y parry; cada poder selecciona su efecto en `Combat.POWERS`.

`ParryView` dibuja el aro y la ventana perfecta; al resolver cada golpe, presenta el resultado con una entrada inclinada, estallido angular y líneas de impacto en el color correspondiente. Los combos se representan con flechas geométricas en `ComboArrowDisplay` y conservan una sola barra de tiempo. Al completarse, las flechas y el rótulo del poder toman el color de su efecto, con destello, estallido y aro.

## Organización y licencias

Guarda sprites, texturas, audio y fuentes dentro de `assets/`. Todo recurso externo debe tener una licencia que permita su uso en el proyecto y su procedencia debe documentarse.
