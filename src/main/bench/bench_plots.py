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
    return p.parse_args()

args = parse_args()

CSV_PATH = "results/all_results.csv"
SELECT_MODE = args.mode.upper()
SELECT_INPUT = args.input.lower()

# =============================
# Lecture et filtrage du CSV
# =============================
df = pd.read_csv(CSV_PATH)

# Filtrage sur l’entrée et le mode choisis
row = df[(df["input"] == SELECT_INPUT) & (df["mode"] == SELECT_MODE)]

if row.empty:
    raise ValueError(f"Aucune ligne trouvée pour input='{SELECT_INPUT}' et mode='{SELECT_MODE}'")

# Extraction des valeurs scalaires
tComp_ms    = row["tComp_ms"].iloc[0]
tDecomp_ms  = row["tDecomp_ms"].iloc[0]
nO          = row["nO"].iloc[0]
nC          = row["nC"].iloc[0]

# =============================
# Calcul des temps totaux
# =============================

# Calcul du seuil de latence (en ms)
lat_threshold = (tComp_ms + tDecomp_ms) / (nO - nC)

# Plage de latences à tester (en ms)
latences_ms = np.linspace(0, 4*lat_threshold, 200)

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
plt.title(f"Seuil de rentabilité - Input: {SELECT_INPUT}, Mode: {SELECT_MODE}")
plt.legend()
plt.grid(True)
plt.tight_layout()
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
