"""Genera mapas reproducibles de obstáculos (semilla fija) y conserva una ruta válida."""
from pathlib import Path
from random import Random

ROOT = Path(__file__).resolve().parent.parent / 'maps'
for n in (20, 50, 100, 200):
    rng = Random(2026 + n)
    grid = [['#' if rng.random() < 0.22 else '.' for _ in range(n)] for _ in range(n)]
    # Un pasillo superior y otro derecho aseguran conectividad entre S y E.
    for j in range(n):
        grid[0][j] = '.'
    for i in range(n):
        grid[i][n - 1] = '.'
    grid[0][0] = 'S'
    grid[n - 1][n - 1] = 'E'
    (ROOT / f'aleatorio_{n}.txt').write_text('\n'.join(''.join(row) for row in grid) + '\n', encoding='utf-8')
    print(f'Creado aleatorio_{n}.txt')
