package bitPacking;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class BitPackingOverflow implements IPacking {

	// --- Bit packers ---
	private BitPacking bpBase;    // base area (w bits)
    private BitPacking bpOver;    // overflow area (kOver bits)
	
    // --- Paramétrage ---
	private int kPrime; // nb de bits pour les valeurs hors de la zone d'overflow et coder l'indice des valeurs en overflow
	private int over; // nb de valeurs en overflow
	private int w; // largeur d'un slot dans la base (tag + kPrime)
	private int kOver; 
	
    private int tabInputLength;
    
    private Mode mode;
    
    // --- Flux bruts (avant bit-packing) ---
    private int[] tabBase; // taille n, chaque mot sur w bits significatifs
	private int[] tabOverflow; // taille over, chaque mot sur kOver bits 
    
	
	public BitPackingOverflow(Mode mode, int[] tabInput) {
		this.mode = mode;
	}
	
	
	public BitPacking getBpBase() {
	    return bpBase;
	}

	
	public BitPacking getBpOver() {
	    return bpOver;
	}

	
	public int getOverCount() {
	    return over;
	}
	
	
	public int getKOver() { return kOver; }
	public int getW() { return w; }


	private int[] getOverflowCount(Map<Integer, Integer> map, int kOver) {
		int[] overflowCount = new int[kOver + 1];
		
		// cumul inverse : partir de la fin et accumuler
	    int cum = 0;
	    for (int i = kOver; i >= 0; i--) {
	        // si la map contient des valeurs exactement de taille k, on ajoute leur fréquence au cumul
	        if (map.containsKey(i + 1)) cum += map.get(i + 1);
	        overflowCount[i] = cum;
	    }

	    return overflowCount;
	}
	
	
	
	
	public void computeOptimumK(int[] tab) {
		Map<Integer, Integer> map = new TreeMap<>();
		
		int kOver = 0; // le k de la zone d'overflow (k max)
		for (int val : tab) {
			int nbBits = BitOps.nbBits(val); // le nb de bits mini pour représenter la val (3 -> nbBits = 2)
			kOver = Math.max(kOver, nbBits);
			map.put(nbBits, map.getOrDefault(nbBits, 0) + 1);
		}
		
		this.kOver = kOver;
		this.tabInputLength = tab.length;
		int[] overflowCount = getOverflowCount(map, kOver);
		int nbBitsTotalMini = Integer.MAX_VALUE;
		int bestK = 1;
		
		for (Integer candK: map.keySet()) {
		    int over = overflowCount[candK];
		    int f = BitOps.nbBits(over-1);
		    int w = 1 + Math.max(candK, f);
		    int nbBitsTotal = tabInputLength*w + over * kOver;
		    
		    if (nbBitsTotal < nbBitsTotalMini) {
		        nbBitsTotalMini = nbBitsTotal;
		        bestK = candK;
		        this.w = w;
		        this.over = over;
		    }
		}
		
		this.kPrime = bestK;
	}
	
	
	@Override
	public void compress(int[] tabInput) {
		this.tabBase = new int[tabInputLength];
		this.tabOverflow = new int[over];
		
		int iOver = 0;
		for (int i = 0; i < tabInputLength; i++) {
			int val = tabInput[i];
			
			if (BitOps.nbBits(val) <= kPrime) {
				tabBase[i] = val;
			} else {
				tabBase[i] = iOver + (1 << (w - 1));
				tabOverflow[iOver] = val;
				iOver++;
			}
			
		}
		
		this.bpBase = BitPackingFactory.create(mode, tabBase.length, w);
        bpBase.compress(tabBase);
        
        this.bpOver = BitPackingFactory.create(mode, tabOverflow.length, kOver);
        bpOver.compress(tabOverflow);
		
	}

	
	@Override
	public int get(int i) {
	    int maskValue = (1 << (w - 1)) - 1;
	    
	    int word = bpBase.get(i);
	    int flag = word >>> (w - 1);
	    int valueOrIndex = word & maskValue;

	    if (flag == 0) {
	        return valueOrIndex;  
	    } else {
	        return bpOver.get(valueOrIndex);
	    }
	    
	}


	
	@Override
	public void decompress(int[] tabOutput) {
		for (int i = 0; i < tabInputLength; i++) {
			tabOutput[i] = get(i);
		}

	}

	public int getK() {
		return kPrime;
	}
	
	
	/**
	 * Affiche le contenu interne du BitPackingOverflow de façon lisible :
	 * - paramètres (k', w, kOver, over)
	 * - mots 32 bits pour la base et l’overflow
	 * - valeurs logiques (flag|payload) dans la base
	 * - valeurs brutes dans l’overflow
	 */
	public void printDebugView(int[] tabInput, int[] tabOutput) {
	    System.out.println("\n===== BitPackingOverflow – Debug =====");
	    System.out.println("Entrée originale      : " + arr(tabInput));
	    System.out.println("Sortie décompressée   : " + arr(tabOutput));
	    System.out.println("-------------------------------------------");
	    System.out.println("k' (base)             : " + kPrime);
	    System.out.println("kOver (overflow)      : " + kOver);
	    System.out.println("w (slot base)         : " + w);
	    System.out.println("#valeurs overflow     : " + over);
	    System.out.println("-------------------------------------------");

	    // --- Zone BASE ---
	    if (bpBase != null) {
	        System.out.println("\n--- Zone BASE ---");
	        int[] baseWords = bpBase.getTabWords();
	        System.out.println("Mots 32 bits (" + baseWords.length + "):");
	        for (int i = 0; i < baseWords.length; i++) {
	            String bin = String.format("%32s", Integer.toBinaryString(baseWords[i])).replace(' ', '0');
	            System.out.printf("Word[%02d] = %s%n", i, bin);
	        }

	        System.out.println("Valeurs logiques (flag|payload):");
	        for (int i = 0; i < tabInputLength; i++) {
	            int word = bpBase.get(i);
	            int flag = word >>> (w - 1);
	            int payload = word & ((1 << (w - 1)) - 1);
	            System.out.printf("Base[%02d] = flag:%d | payload:%d%n", i, flag, payload);
	        }
	    }

	    // --- Zone OVERFLOW ---
	    if (bpOver != null && over > 0) {
	        System.out.println("\n--- Zone OVERFLOW ---");
	        int[] overWords = bpOver.getTabWords();
	        System.out.println("Mots 32 bits (" + overWords.length + "):");
	        for (int i = 0; i < overWords.length; i++) {
	            String bin = String.format("%32s", Integer.toBinaryString(overWords[i])).replace(' ', '0');
	            System.out.printf("Word[%02d] = %s%n", i, bin);
	        }

	        System.out.println("Valeurs brutes (overflow):");
	        for (int j = 0; j < over; j++) {
	            System.out.printf("Over[%02d] = %d%n", j, bpOver.get(j));
	        }
	    }

	    System.out.println("===========================================\n");
	}

	// -------- utilitaire privé pour afficher un tableau --------
	private static String arr(int[] a) {
	    StringBuilder sb = new StringBuilder("[");
	    for (int i = 0; i < a.length; i++) {
	        if (i > 0) sb.append(", ");
	        sb.append(a[i]);
	    }
	    return sb.append("]").toString();
	}


}
