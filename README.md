# BitPacking – Système de compression d’entiers
## Génie Logiciel (Master 1 Informatique – UCA 2025)
Auteur : Antoine de Chabannes

Date de fin : 02/11/2025

### Description

Ce projet implémente plusieurs variantes de Bit Packing (Sans/ Avec Chevauchement + Overflow).
L’objectif est d’évaluer l’intérêt de la compression selon la latence réseau :
  - la compression réduit la taille à transmettre,
  - mais ajoute un coût de traitement (compression/décompression).
Le projet mesure donc le seuil de rentabilité à partir duquel la compression devient avantageuse en temps.

### Comment lancer le programme

1. Cloner le dépôt : git clone https://github.com/DeChab-Antoine/SE-project2025.git
2. Se mettre à la racine du projet : cd SE-project2025
3. Compiler le projet (partie java) : javac -d target/classes (Get-ChildItem -Recurse -Filter *.java src/main | ForEach-Object { $_.FullName })
4. Installer les biblio (si non installé) : pip install pandas / pip install matplotlib
5. Lancer : python src/main/main.py --bench --all
6. Observer les graphiques dans le dossier results
7. Lancer : python src/main/main.py --plot --mode OVERLAP --input small
8. Observer le graphique spécifique (OVERLAP, small)
   
### Explication des options:
```
  -h, --help     show this help message and exit
  --bench        lance le benchmark et génère le CSV
  --plot         Afficher un graphique obligatoirement avec --mode et --input
  --all          Génère les graphiques pour toutes les configurations
  --mode MODE    Mode de compression (OVERLAP, WITHOUT_OVERLAP, OVERFLOW_OVERLAP, OVERFLOW_WITHOUT_OVERLAP)
  --input INPUT  Nom du fichier d'entrée (small, large, overlap, without_overlap, overflow)
```
### Analyse du graphique

  - les courbes reflètent le temps total de la transmission sans (bleu) / avec (rouge) compression en fonction de la latence
  - la ligne à pointillé verte : latence où la compression devient rentable

### Structure du projet
```
SE-project2025
 ├── src/
 │    ├── main/
 │    │   ├── api/         => Contient les interfaces publiques et les énumérations des modes de compression.
 │    │   ├── core/        => Regroupe les implémentations principales des algorithmes BitPacking.
 │    │   ├── factory/     => Implémente la fabrique permettant de créer la bonne variante de BitPacking.
 │    │   ├── utils/       => Fournit les fonctions utilitaires bas-niveau (opérations sur les bits).
 │    │   └── bench/       => Contient les classes de configuration, génération de données et benchmarks.
 │    │
 │    ├── test/
 │        ├── coverage/    => Regroupe les tests de couverture du noyau (core).
 │        └── unitaire/    => Espace réservé pour les tests unitaires tout au long du projet.
 │
 ├── results/              => Dossier où sont enregistrés les fichiers de résultats (CSV, graphiques).
 ├── ressources/           => Fichiers d’entrée
 ├── docs/                 => Documentation technique et rapport du projet.
 └── pom.xml               => Fichier de configuration Maven du projet.
```
### Prérequis
- Python 3.10+  
- Java 17+  
- Bibliothèques Python : pandas, numpy, matplotlib (pip install pandas numpy matplotlib)


