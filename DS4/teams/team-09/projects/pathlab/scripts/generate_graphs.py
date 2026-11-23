"""Genera las graficas de resultados a partir de resultados/benchmark_consolidado.csv.

Uso (desde la carpeta del proyecto):
    python scripts/generate_graphs.py
Requiere matplotlib.
"""
import csv
from pathlib import Path

import matplotlib

matplotlib.use("Agg")
import matplotlib.pyplot as plt

BASE = Path(__file__).resolve().parent.parent
CSV = BASE / "resultados" / "benchmark_consolidado.csv"

data = {}
with open(CSV, encoding="utf-8-sig") as f:
    for row in csv.DictReader(f):
        algo = row["algoritmo"]
        data.setdefault(algo, {"lado": [], "tiempo": [], "explorados": []})
        data[algo]["lado"].append(int(row["lado"]))
        data[algo]["tiempo"].append(float(row["tiempo_medio_ms"]))
        data[algo]["explorados"].append(int(row["explorados"]))


def plot(metric, ylabel, title, outfile):
    fig, ax = plt.subplots(figsize=(8, 5))
    for algo, serie in data.items():
        ax.plot(serie["lado"], serie[metric], marker="o", label=algo)
    ax.set_xlabel("Lado del mapa (celdas)")
    ax.set_ylabel(ylabel)
    ax.set_title(title)
    ax.grid(True, linestyle="--", alpha=0.5)
    ax.legend()
    fig.tight_layout()
    fig.savefig(BASE / "resultados" / outfile, dpi=150)
    plt.close(fig)
    print(f"Generado resultados/{outfile}")


plot("tiempo", "Tiempo medio (ms)", "Tiempo medio por algoritmo y tamano de mapa", "grafica_tiempos.png")
plot("explorados", "Celdas exploradas", "Celdas exploradas por algoritmo y tamano de mapa", "grafica_explorados.png")
