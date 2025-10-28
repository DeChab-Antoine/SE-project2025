"""
CLI pour éxécuter le projet BitPacking.

Comportement :
  - si --bench lance BenchRunner (Java) pour générer/mettre à jour results/results.csv
  - si --plot est présent : affiche le graphique en forçant --mode et --input

Exemples :
  python main.py --bench
  python main.py --plot --mode OVERLAP --input large
"""

import argparse
import subprocess
import sys


# --- Réglages très simples : adapte si besoin ---
MAIN_CLASS   = "main.bench.BenchRunner"   # classe Java contenant le main
CP_CLASSES   = "target/classes"    # classpath par défaut après mvn package
PLOT_FILE    = "src/main/bench/bench_plots.py"     # script fourni pour les graphes
# ------------------------------------------------

def run_benchrunner():
    """Exécute la classe principale BenchRunner."""
    cmd = ["java", "-cp", CP_CLASSES, MAIN_CLASS]
    print("[INFO] Exécution de BenchRunner...")
    subprocess.run(cmd)
    print("[OK] Bench terminé.")

def run_plot(mode, input_name):
    """Affiche le graphique"""
    cmd = [sys.executable, str(PLOT_FILE),
        "--mode", mode,
        "--input", input_name]
    print("[INFO] Affichage du graphique...")
    subprocess.run(cmd)

def main():
    parser = argparse.ArgumentParser(description="CLI BitPacking")
    parser.add_argument("--bench", action="store_true", help="lance le benchmark et génère le CSV")
    parser.add_argument("--plot", action="store_true", help="Afficher un graphique obligatoirement avec --mode et --input")
    parser.add_argument("--mode", type=str, help="Mode de compression (OVERLAP, WITHOUT_OVERLAP, OVERFLOW_OVERLAP, OVERFLOW_WITHOUT_OVERLAP)")
    parser.add_argument("--input", type=str, help="Nom du fichier d'entrée (small, large, overlap, without_overlap, overflow)")
    args = parser.parse_args()

    if args.bench:
        run_benchrunner()

    if args.plot:
        run_plot(args.mode, args.input)

main()
