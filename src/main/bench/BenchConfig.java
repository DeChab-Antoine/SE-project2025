package main.bench;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import main.api.Mode;

public final class BenchConfig {
    private int warmupIterations; 					// Nombre d’exécutions préliminaires pour “chauffer” la JVM avant de mesurer
    private int measureIterations; 					// Nombre de répétitions utilisées pour calculer la moyenne des temps mesurés

    private int[] sizes;							// Différentes tailles de tableaux d’entrée testées
    private List<Mode> modes;						// Ensemble des variantes de BitPacking testées
    
    private List<Integer> percents; 						// pourcentage du nombre de valeurs 
    private List<Integer> bits; 							// par bits pour créer Input
    
    
    public int getWarmupIterations() {
		return warmupIterations;
	}


	public int getMeasureIterations() {
		return measureIterations;
	}


	public int[] getSizes() {
		return sizes;
	}


	public List<Mode> getModes() {
		return modes;
	}


	public List<Integer> getPercents() {
		return percents;
	}


	public List<Integer> getBits() {
		return bits;
	}


	public BenchConfig(String file) {
    	this.warmupIterations = 5;
    	this.measureIterations = 20;
    	
    	this.sizes = new int[] {100_000};
    	this.modes = List.of(Mode.WITHOUT_OVERLAP, Mode.OVERLAP, Mode.OVERFLOW_WITHOUT_OVERLAP, Mode.OVERFLOW_OVERLAP);
    	
    	this.bits = new ArrayList<Integer>();
    	this.percents = new ArrayList<Integer>();
    	
    	readConfig(file);
    }
    
    
    private void readConfig(String filePath) {
    	try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            
            while ((line = br.readLine()) != null) {
                line = line.trim();
                
                String[] parts = line.split("\\s+");
                
                int bit = Integer.parseInt(parts[0]);
                int percent = Integer.parseInt(parts[1]);
                
                bits.add(bit);
                percents.add(percent);
            }
        } catch (IOException e) {
            System.err.println("Erreur de lecture du fichier config : " + e.getMessage());
        }
    }
    
}
