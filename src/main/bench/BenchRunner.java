package main.bench;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import main.api.IPacking;
import main.api.Mode;
import main.factory.BitPackingFactory;

public final class BenchRunner {
	
    public static void main(String[] args) {
        File configDir = new File("ressources");
        File[] configFiles = configDir.listFiles();

        // --- UNIQUE fichier de résultats pour toutes les configs ---
        new File("results").mkdirs();
        String resultPath = "results/all_results.csv";

        try (FileWriter fw = new FileWriter(resultPath, false)) {
            // En-tête avec le nom du fichier d'entrée en première colonne
            fw.write(
                "input,mode,size," +
                "tComp_ms,tDecomp_ms," +
                "nO,nC," +
                "gainT\n"
            );

            for (File configFile : configFiles) {
                System.out.println("\n=== Benchmark: " + configFile.getName() + " ===");

                // Récupérer les paramètres depuis la config
                BenchConfig cfg = new BenchConfig(configFile.getPath());

                // pour input
                List<Integer> percents = cfg.getPercents();
                List<Integer> bits = cfg.getBits();

                // pour boucler
                List<Mode> modes = cfg.getModes();
                int[] sizes = cfg.getSizes();

                // nb iterations
                int warmupIt = cfg.getWarmupIterations();
                int measureIt = cfg.getMeasureIterations();

                // info de mon réseau
                final long LATENCY_NS = 12_000_000;
                final long MTU = 12_000; 

                DataGenerator dataGen = new DataGenerator(42);

                for (int size : sizes) {
                    System.out.println("\n=== size: " + size + " | file=" + configFile.getName() + " ===");

                    // Construire l'entrée conforme à la distribution
                    int[] input = dataGen.buildInput(bits, percents, size);

                    int latTmin = Integer.MAX_VALUE;
                    
                    for (Mode mode : modes) {
                        // Instancier l'algo via la Factory
                        IPacking bp = BitPackingFactory.create(mode, input);

                        // --- Mesurer compress() ---
                        for (int w = 0; w < warmupIt; w++) bp.compress(input);

                        long tCompSum = 0;
                        for (int it = 0; it < measureIt; it++) {
                            long t0 = System.nanoTime();
                            bp.compress(input);
                            tCompSum += (System.nanoTime() - t0);
                        }
                        double tComp = tCompSum / (double) measureIt;

                        // --- Mesurer decompress() ---
                        int[] out = new int[size];

                        for (int w = 0; w < warmupIt; w++) bp.decompress(out);

                        long tDecompSum = 0;
                        for (int it = 0; it < measureIt; it++) {
                            long t0 = System.nanoTime();
                            bp.decompress(out);
                            tDecompSum += (System.nanoTime() - t0);
                        }
                        double tDecomp = tDecompSum / (double) measureIt;

                        // --- Mesurer get(i) aléatoire ---
                        final int Q = Math.min(1_000_000, size * 10);
                        long tGetSum = 0;
                        long blackhole = 0;
                        Random rng = new Random(123);
                        for (int it = 0; it < measureIt; it++) {
                            long t0 = System.nanoTime();
                            for (int q = 0; q < Q; q++) {
                                blackhole += bp.get(rng.nextInt(size));
                            }
                            tGetSum += (System.nanoTime() - t0);
                        }
                        if (blackhole == 42) System.out.print(""); // anti-optimisation morte
                        double tGet = tGetSum / (double) (measureIt * Q);

                        // --- Tailles ---
                        long S0bits = 32L * size; // non compressé
                        long Scbits = ((long) bp.getWords().length) * 32L; // compressé
                        
                        // --- Packets ---
                        double n0 = Math.ceil(S0bits / MTU); 
                        double nC = Math.ceil(Scbits / MTU);

                        // --- Temps bout-en-bout (en s) ---
                        double tTotalComp = toMilliSecond(tComp + tDecomp + nC * LATENCY_NS);
                        double tTotalNonComp = toMilliSecond(n0 * LATENCY_NS);

                        // --- Décisions ---
                        boolean gainT = (tTotalComp < tTotalNonComp);
                        
                        // --- Latence t à partir du quel la transmission devient intéressante ---
                        int latT = (int) Math.round((tComp + tDecomp) / (n0 - nC));

                        // Log console court (inchangé)
                        System.out.printf(Locale.US,
                            "[OK] mode=%-25s | comp=%8.3f ms, decomp=%8.3f ms, get=%8.3f ns | " +
                            		"T0=%8.3f ms Tc=%8.3f ms | gainT=%s | latT=%8d ns%n",
                            mode.name(),
                            toMilliSecond(tComp), toMilliSecond(tDecomp), tGet,
                            tTotalNonComp, tTotalComp,
                            gainT ? "Y" : "N",
                            latT
                        );

                        // --- CSV (ajout "input" en première colonne) ---
                        fw.write(String.join(",",
                            configFile.getName(),
                            mode.name(),
                            String.valueOf(size),
                            fmt6(tComp),
                            fmt6(tDecomp),
                            fmt6(n0),
                            fmt6(nC),
                            String.valueOf(gainT)
                        ));
                        fw.write("\n");
                        
                        if (latT != -1 && latT < latTmin) {
                        	latTmin = latT;
                        }
                    }
                    
                    if (latTmin == Integer.MAX_VALUE) 
                    	System.out.println("\nLa compression n'est jamais intéressante.");
                    else 
                    	System.out.println("\nA partir d'une latence d'environ " + latTmin + ", la compression devient intéressante.");
                   
                }
            }

            System.out.println("\n=== Tous les benchmarks terminés ===");
        } catch (IOException e) {
            System.err.println("Erreur écriture résultat: " + e.getMessage());
        }
    }
            
    private static String fmt6(double v) {
        return String.format(Locale.US, "%.6f", v);
    }
    
    private static double toMilliSecond(double v) {
    	return v/1e6;
    }
    
}
