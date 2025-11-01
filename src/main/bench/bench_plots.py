"""
Visualisation des seuils de rentabilité de la compression
- X: Temps total (ms)
- Y: Latence (ms)
- Deux courbes : sans compression vs avec compression
- un point d'intersection = seuil de rentabilité
"""

import argparse
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from math import ceil

# =============================
# CLI
# =============================

def parse_args():
    p = argparse.ArgumentParser(description="Affiche le graphique seuil de rentabilité")
    p.add_argument("--mode",  type=str, required=False,     help="Mode: OVERLAP | WITHOUT_OVERLAP | OVERFLOW_OVERLAP | OVERFLOW_WITHOUT_OVERLAP")
    p.add_argument("--input", type=str, required=False,     help="Jeu d'entrée: small | large | ...")
    p.add_argument("--save", action="store_true", help="Génére tous les graphiques")
    return p.parse_args()

args = parse_args()

CSV_PATH = "results/results.csv"
SELECT_MODE = args.mode.upper()
SELECT_INPUT = args.input.lower()
save = False
if args.save:
    save = True

# =============================
# Lecture et filtrage du CSV
# =============================

df = pd.read_csv(CSV_PATH)

# Filtrage sur l’entrée et le mode choisis
row = df[(df["input"] == SELECT_INPUT) & (df["mode"] == SELECT_MODE)]

if row.empty:
    raise ValueError(f"Aucune ligne trouvée pour input='{SELECT_INPUT}' et mode='{SELECT_MODE}'")

# Extraction des valeurs
tComp_ms    = row["tComp_ms"].iloc[0]
tDecomp_ms  = row["tDecomp_ms"].iloc[0]
nO          = row["nO"].iloc[0]
nC          = row["nC"].iloc[0]
taille      = row["size"].iloc[0]

# =============================
# Calcul des temps totaux
# =============================

# Calcul du seuil de latence (en ms)
den = float(nO - nC)

if den <= 0:
    # compression non rentable structurellement (aucun gain d'unités transmises)
    lat_threshold = float("inf")
else:
    lat_threshold = (tComp_ms + tDecomp_ms) / den

if (lat_threshold < 0 or lat_threshold > 50000):
    echelle = 200000
if (lat_threshold <= 50000):
    echelle = 100000
if (lat_threshold <= 20000):
    echelle = 30000

# Plage de latences à tester (en ms)
latences_ms = np.linspace(0, echelle, echelle//1000)

# Calcul des temps totaux pour chaque latence
tTotalComp = tComp_ms + tDecomp_ms + nC * latences_ms
tTotalNoComp = nO * latences_ms

# =============================
# Affichage graphique
# =============================

plt.figure(figsize=(8, 6))
plt.plot(latences_ms, tTotalNoComp, label="Sans compression", lw=2, color="red")
plt.plot(latences_ms, tTotalComp, label="Avec compression", lw=2, color="blue")

if lat_threshold:
    plt.axvline(lat_threshold, color="green", ls="--", label=f"Seuil ≈ {lat_threshold:.0f} ms")

plt.xlabel("Latence réseau (ms)")
plt.ylabel("Temps total de transmission (ms)")
plt.title(f"Input: {SELECT_INPUT}, Mode: {SELECT_MODE}, Taille: {taille}")
plt.legend()
plt.grid(True)
plt.tight_layout()

if(save):
    filename = f"results/bench_plots_{SELECT_INPUT}_{SELECT_MODE}.png"
    plt.savefig(filename)
    print(f"[OK] Graphique sauvegardé sous '{filename}'")
else:
    plt.show()

# =============================
# Résumé console
# =============================

print(f"\n=== Résumé pour input={SELECT_INPUT}, mode={SELECT_MODE} ===")
print(f"Temps compression : {tComp_ms:.2f} ms")
print(f"Temps décompression : {tDecomp_ms:.2f} ms")
print(f"nO (non compressé) : {nO}")
print(f"nC (compressé)     : {nC}")

if lat_threshold:
    print(f"Seuil de rentabilité = {lat_threshold:.2f} ms")
else:
    print("Aucune intersection détectée (compression jamais rentable ou toujours rentable).")
