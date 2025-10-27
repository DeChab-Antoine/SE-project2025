package main.bench;

import java.util.List;
import java.util.Random;


public class DataGenerator {
	
	private final Random random;

	
    public DataGenerator(long seed) {
        this.random = new Random(seed);
    }
    
    
	public int[] buildInput(List<Integer> bits, List<Integer> percents, int size) {
        int[] input = new int[size];
        int pos = 0;
        
        // Remplissage selon les pourcentages
        for (int i = 0; i < bits.size(); i++) {
            int nbVals = (int) Math.round(size * (percents.get(i) / 100.0));
            int maxVal = (bits.get(i) == 31) ? Integer.MAX_VALUE : (1 << bits.get(i)) - 1;
            int minVal = (1 << bits.get(i) - 1) - 1;

            for (int j = 0; j < nbVals && pos < size; j++) {
                input[pos++] = random.nextInt(minVal, maxVal + 1);
            }
        }
        
        // Si il manque des valeurs
        while (pos < size) {
            input[pos++] = random.nextInt(1 << bits.get(0));
        }
        
        // Mélange de Fisher-Yates 
        for (int i = size - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int tmp = input[i];
            input[i] = input[j];
            input[j] = tmp;
        }

        return input;
    }
}
