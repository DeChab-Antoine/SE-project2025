package main.core;

import main.api.IPacking;
import main.api.Mode;
import main.factory.BitPackingFactory;
import main.utils.BitOps;

/**
 * Variante BitPacking avec overflow (base + zone d’overflow)
 *
 * Principe :
 *  - Les valeurs ≤ k sont stockées dans la zone BASE (flag=0 | value)
 *  - Les valeurs > k sont envoyées dans OVERFLOW (flag=1 | index)
 *  - Compress/Decrompress/Get via une variante de bitPacking choisit via l'enum "mode"
 *
 */

public class BitPackingOverflow implements IPacking {

	// --- Paramètres ---
	private int k;          	 // nb de bits pour coder les valeurs ou l'indice (pour la zone base)
	private int kOver;      	 // nb de bits max (pour la zone overflow)
	private int slotLength;  	// largeur d'un slot dans la base 
	private int overflowCount;	// nb de valeurs en overflow
	private int inputLength; 
    private Mode mode;
	
	
	// --- Bit packers ---
	private IPacking bpBase;
    private IPacking bpOver; 
	
    
    // --- Flux bruts (avant bit-packing) ---
    private int[] base;     // taille inputLength, chaque mot sur w bits significatifs
	private int[] overflow; // taille over, chaque mot sur kOver bits 
    
	
	// -------------------------
	//  Constructeurs
	// -------------------------
	
	
	public BitPackingOverflow(Mode mode, int[] input) {
		this.mode = mode;
		this.inputLength = input.length;
	}
	
	
	// -------------------------
	//  Calcul du k optimum
	// -------------------------

	
	/** Remplit freqBits[k] (si freqBits[2] = 4 -> 4 valeurs sur 2 bits) et retourne kMax */
	private static int buildFreqBitsAndGetKMax(int[] tab, int[] freqBits) {
		int kMax = 1;
		for (int v : tab) {	
	    	int k = BitOps.nbBits(v);
	    	freqBits[k]++;
	    	if (k > kMax) kMax = k;
	    }
		
		return kMax;
	}
	
	
	/**
	 *  - on part d’un tableau de fréquences par taille de bits, puis cumul suffixe
	 *  - overflowCount[k] = nb d'éléments dont nbBits(val) > k
	 */
	private static int[] buildOverflowCountByK(int[] freqBits, int kMax) {
	    int[] overflowCountByK = new int[kMax + 1];
	    int cum = 0;
	    for (int k = kMax; k >= 0; k--) {
	        if (k + 1 <= kMax) cum += freqBits[k + 1];
	        overflowCountByK[k] = cum;
	    }
	    
	    return overflowCountByK;
	}
	
	
	/** Calculer la taille du slot pour un k et un nombre de valeur en overflow "over" */
	private static int computeSlotLength(int k, int over) {
		int f = BitOps.nbBits(over - 1); // nb bits pour coder les indices des valeurs en overflow
		int slotLength = 1 + Math.max(k, f);
		
		return slotLength;
	}
	
	

	/** Applique les paramètres pour le meilleur k trouvés */
	private void applyBest(int kMax, int bestK, int bestOverflowCount) {
		this.kOver = kMax;
		this.k = bestK;
        this.overflowCount = bestOverflowCount;
        this.slotLength = computeSlotLength(bestK, bestOverflowCount);
        
	}
	
	
	/**
	 * 	choisir k qui minimise : nbBitsTotal = inputLength * slotLength + over * kOver
	 *  avec : 
	 *  - slotLength = 1 + max(k, f) 
	 *  - f = nbBits(over-1) 
	 *  
	 *  fixe k, kOver, slotLength, overflowCount
	 */
	public void computeK(int[] tab) {
		// 1) histogramme par taille en bits 
		int[] freqBits = new int[33]; 	
		int kMax = buildFreqBitsAndGetKMax(tab, freqBits);
	    
	    // 2) overflowCountByK[k] = nb de valeurs dont nbBits(val) > k
		int[] overflowCountByK = buildOverflowCountByK(freqBits, kMax);
		
		// 3) recherche du meilleur k (brute-force 1..kMax)
		int bestTotal = Integer.MAX_VALUE; 
		int bestK = 1;
			
		for (int candK = 1; candK <= kMax; candK++) {
			int candOverflowCount = overflowCountByK[candK]; 									
		    int candSlotLength = computeSlotLength(candK, candOverflowCount); 
		    int total = inputLength * candSlotLength + candOverflowCount * kMax;
		    
		    if (total < bestTotal) {
		    	bestTotal = total;
		        bestK = candK;
		    }
		}
		
		// 4) appliquer 
		applyBest(kMax, bestK, overflowCountByK[bestK]);
	}
	
	
	/* Alloue les flux BASE/OVERFLOW */
	private void allocateFlux(int inputLength, int overflowCount) {
		this.base = new int[inputLength];
		this.overflow = new int[overflowCount];
	}
	
	
	/* Crée les variantes de BitPacking via la factory */
	private void createBitPacking() {
		this.bpBase = BitPackingFactory.createForOverflow(mode, base.length, slotLength);
		this.bpOver = BitPackingFactory.createForOverflow(mode, overflow.length, kOver);
	}
	
	
	/* Écrit les flux BASE/OVERFLOW puis délègue la compression aux variantes BitPacking */
	@Override
	public void compress(int[] tabInput) {
		allocateFlux(inputLength, overflowCount);
		
		int indexOverflow = 0;
		for (int i = 0; i < inputLength; i++) {
			int value = tabInput[i];
			
			if (BitOps.nbBits(value) <= k) {
				// FLAG = 0 | value
				base[i] = value;
			} else {
				// FLAG = 1 | index
				base[i] = indexOverflow + (1 << (slotLength - 1));
				
				// écrire value dans le flux overflow
				overflow[indexOverflow] = value;
				indexOverflow++;
			}
		}
		
		createBitPacking();
		
        bpBase.compress(base);
        bpOver.compress(overflow);
	}

	/* get(i) : lit le slot "word" ; si FLAG=0 => valeur ; si FLAG=1 => OVERFLOW[index] */
	@Override
	public int get(int i) {
	    int maskValue = (1 << (slotLength - 1)) - 1;
	    
	    int word = bpBase.get(i);
	    int flag = word >>> (slotLength - 1);
	    int valueOrIndex = word & maskValue;

	    if (flag == 0) {
	        return valueOrIndex;  
	    } else {
	        return bpOver.get(valueOrIndex);
	    }
	    
	}


	/* boucle get pour decompresser tout le tableau */
	@Override
	public void decompress(int[] tabOutput) {
		for (int i = 0; i < inputLength; i++) {
			tabOutput[i] = get(i);
		}

	}
	
	
	@Override
	public int getK() {
		return k;
	}


	@Override
	public int[] getWords() {
		return base;
	}


	@Override
	public int getWordsLength() {
		return base.length;
	}

}
