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
	
	private static void benchRunner(String configFile) {
		// Récupérer les paramètres depuis la config
		BenchConfig cfg = new BenchConfig(configFile);
		
		// pour input
		List<Integer> percents = cfg.getPercents(); 						
		List<Integer> bits = cfg.getBits();
	    
	    // pour boucler
	    List<Mode> modes = cfg.getModes();
	    int[] sizes = cfg.getSizes();
	    
	    // nb iterations
	    int warmupIterations = cfg.getWarmupIterations();
	    int measureIterations = cfg.getMeasureIterations();
	    
		DataGenerator dataGen = new DataGenerator(42);
		
		String configName = new File(configFile).getName();
		String resultPath = "results/" + configName + ".csv";
		
        try (FileWriter fw = new FileWriter(resultPath, false)) {
            fw.write("mode,size,tCompNsPerInt,tDecompNsPerInt,tGetNs,bitsPerInt,ratio\n");
        
			for (int size : sizes) {
				System.out.println("\n=== size: " + size + " ===");
				
				// Construire l'entrée conforme à la distribution
				int[] input = dataGen.buildInput(bits, percents, size);
				
				for (Mode mode : modes) {
					// Instancier l'algo via la Factory
					IPacking bp = BitPackingFactory.create(mode, input);
					
					// Mesurer compress()
	                // Warm-up
	                for (int w = 0; w < warmupIterations; w++) bp.compress(input);
	
	                long tCompSum = 0;
	                for (int it = 0; it < measureIterations; it++) {
	                    long t0 = System.nanoTime();
	                    bp.compress(input);
	                    tCompSum += (System.nanoTime() - t0);
	                }
	                double tCompNsPerInt = (tCompSum / (double) measureIterations) / size;
	                
	                // Mesurer decompress()
	                int[] out = new int[size];
	                bp.decompress(out);
	                
	                for (int w = 0; w < warmupIterations; w++) bp.decompress(out);
	
	                long tDecompSum = 0;
	                for (int it = 0; it < measureIterations; it++) {
	                    long t0 = System.nanoTime();
	                    bp.decompress(out);
	                    tDecompSum += (System.nanoTime() - t0);
	                }
	                double tDecompNsPerInt = (tDecompSum / (double) measureIterations) / size;
	
	                // 4.4) Mesurer get(i) aléatoire
	                final int Q = Math.min(1_000_000, size * 10);
	                long tGetSum = 0;
	                long blackhole = 0;
	                Random rng = new Random();
	                for (int it = 0; it < measureIterations; it++) {
	                    long t0 = System.nanoTime();
	                    for (int q = 0; q < Q; q++) {
	                        blackhole += bp.get(rng.nextInt(size));
	                    }
	                    tGetSum += (System.nanoTime() - t0);
	                }
	                if (blackhole == 42) System.out.print(""); // anti-optimisation morte
	                double tGetNs = tGetSum / (double) (measureIterations * Q);
	
	                // 4.5) Taille compressée → bits par entier & ratio
	                long compressedBits = ((long) bp.getWords().length) * 32L; 
	                double bitsPerInt = compressedBits / (double) size;
	                double ratio = bitsPerInt / 32.0;
	                
	                // Log console court
	                System.out.printf(Locale.US,
	                        "[OK] mode=%-25s | comp=%8.3f ns/int, decomp=%8.3f ns/int, get=%8.3f ns, ratio=%8.3f%n",
	                        mode.name(), tCompNsPerInt, tDecompNsPerInt, tGetNs, ratio);
	            
	                fw.write(String.join(",",
                            mode.name(),
                            String.valueOf(size),
                            fmt6(tCompNsPerInt),
                            fmt6(tDecompNsPerInt),
                            fmt6(tGetNs),
                            fmt6(bitsPerInt),
                            fmt6(ratio)));
                    fw.write("\n");
				}
			}
			
			
		} catch (IOException e) {
			System.err.println("Erreur écriture résultat: " + e.getMessage());
		}
	}
	
	private static String fmt6(double v) {
        return String.format(Locale.US, "%.6f", v);
    }
	
	public static void main(String[] args) {
		File configDir = new File("ressources/configs");
		
		File[] configFiles = configDir.listFiles((dir, name) -> name.endsWith(""));

		for (File configFile : configFiles) {
            System.out.println("\n=== Benchmark: " + configFile.getName() + " ===");
            
            benchRunner(configFile.getPath());
		}
		
		System.out.println("\n=== Tous les benchmarks terminés ===");
	}
	
}
