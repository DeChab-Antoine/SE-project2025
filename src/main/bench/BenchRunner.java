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

        Profile[] profiles = { Profile.CAMPAGNE_4G, Profile.FIBRE_BUREAU, Profile.FIBRE_DOMESTIQUE, Profile.SATELLITE, Profile.SATELLITE };

        for (File configFile : configFiles) {
            System.out.println("\n=== Benchmark: " + configFile.getName() + " ===");
            String resultPath = "results/" + configFile.getName() + ".csv";
            try (FileWriter fw = new FileWriter(resultPath, false)) {
            	fw.write(
                    "profile,mode,size," +
                    "tCompNsPerInt,tDecompNsPerInt,tGetNs," +
                    "bitsPerInt,ratio," +
                    "S0bits,Scbits,n0,n1," +
                    "tStar_ms,T0_ms,Tc_ms," +
                    "gainT\n"
            			);
            	for (Profile profile : profiles) {
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

                    // Réseau (selon le profil)
                    final long BPS = profile.bps;
                    final long LATENCY_NS = profile.latencyNs;
                    final int  MTU_BITS = 12_000;

                    DataGenerator dataGen = new DataGenerator(42);

                    for (int size : sizes) {
                        System.out.println("\n=== size: " + size + " | file=" + configFile.getName() + " ===");

                        // Construire l'entrée conforme à la distribution
                        int[] input = dataGen.buildInput(bits, percents, size);

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
                            double tComp = (tCompSum / (double) measureIt) / size;

                            // --- Mesurer decompress() ---
                            int[] out = new int[size];
                            bp.decompress(out);

                            for (int w = 0; w < warmupIt; w++) bp.decompress(out);

                            long tDecompSum = 0;
                            for (int it = 0; it < measureIt; it++) {
                                long t0 = System.nanoTime();
                                bp.decompress(out);
                                tDecompSum += (System.nanoTime() - t0);
                            }
                            double tDecomp = (tDecompSum / (double) measureIt) / size;

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

                            // --- Tailles & ratio ---
                            long S0bits = 32L * size; // non compressé
                            long Scbits = ((long) bp.getWords().length) * 32L; // compressé
                            double bitsPerInt = Scbits / (double) size;
                            double ratio = bitsPerInt / 32.0;

                            // --- Packetisation ---
                            int n0 = (int) Math.ceil(S0bits / (double) MTU_BITS);
                            int n1 = (int) Math.ceil(Scbits / (double) MTU_BITS);

                            // --- Coûts CPU totaux (ns) ---
                            double Tcomp_ns   = tComp * size;
                            double Tdecomp_ns = tDecomp * size;

                            // --- Seuil t* ---
                            // t* = [Tcomp + Tdecomp + (Sc - S0)/B] / (n0 - n1)
                            double numerator = Tcomp_ns + Tdecomp_ns + (Scbits - S0bits) * (1e9 / BPS);
                            int dn = n0 - n1;
                            double tStar_ns = (dn > 0) ? (numerator / dn) : Double.POSITIVE_INFINITY;

                            // --- Temps bout-en-bout pour le profil courant ---
                            double T0_ns = n0 * (double) LATENCY_NS + (S0bits * 1e9) / BPS;
                            double Tc_ns = Tcomp_ns + n1 * (double) LATENCY_NS + (Scbits * 1e9) / BPS + Tdecomp_ns;

                            // --- Décisions ---
                            boolean gainT = (Tc_ns < T0_ns);

                            // Log console court
                            System.out.printf(Locale.US,
                                "[OK] prof=%-8s mode=%-25s | comp=%8.3f ns/int, decomp=%8.3f ns/int, get=%8.3f ns, ratio=%6.3f | " +
                                "n0=%d n1=%d | t*=%8.3f ms | T0=%8.3f ms Tc=%8.3f ms | gainT=%s%n",
                                profile.label, mode.name(),
                                tComp, tDecomp, tGet, ratio,
                                n0, n1,
                                tStar_ns / 1e6, T0_ns / 1e6, Tc_ns / 1e6,
                                gainT ? "Y" : "N"
                            );

                            // CSV
                            fw.write(String.join(",",
                                profile.label,
                                mode.name(),
                                String.valueOf(size),
                                fmt6(tComp),
                                fmt6(tDecomp),
                                fmt6(tGet),
                                fmt6(bitsPerInt),
                                fmt6(ratio),
                                String.valueOf(S0bits),
                                String.valueOf(Scbits),
                                String.valueOf(n0),
                                String.valueOf(n1),
                                fmt6(tStar_ns / 1e6),      // ms
                                fmt6(T0_ns / 1e6),         // ms
                                fmt6(Tc_ns / 1e6),         // ms
                                String.valueOf(gainT)
                            ));
                            fw.write("\n");
                        } 
            	    } 
                } 
            } catch (IOException e) {
            	System.err.println("Erreur écriture résultat: " + e.getMessage());
			}
        } 
        System.out.println("\n=== Tous les benchmarks terminés ===");
    }
            
    private static String fmt6(double v) {
        return String.format(Locale.US, "%.6f", v);
    }
}
