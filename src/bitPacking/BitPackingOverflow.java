package bitPacking;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class BitPackingOverflow implements IPacking {

	BitPacking bp; // Overlap or no
	private int[] tabOverflow;
	private int k; // le k de la base à fixer dans computeOptimumK
    private int tabInputLength;
    
	
	public BitPackingOverflow(BitPacking bp) {
		this.bp = bp;
	}

	private int[] getOverflowCount(int n, Map<Integer, Integer> map, int ko) {
		int[] overflowCount = new int[ko+1];
		
		// cumul inverse : partir de la fin et accumuler
	    int cum = 0;
	    for (int i = ko; i >= 0; i--) {
	        // si la map contient des valeurs exactement de taille k, on ajoute leur fréquence au cumul
	        if (map.containsKey(i + 1)) {
	            cum += map.get(i + 1);
	        }
	        overflowCount[i] = cum;
	    }

	    return overflowCount;
	}
	
	
	
	
	
	public void computeOptimumK(int[] tab) {
		Map<Integer, Integer> map = new TreeMap<>();
		
		int ko = 0; // le k de la zone d'overflow (k max)
		for (int val : tab) {
			int nbBits = 32 - Integer.numberOfLeadingZeros(val); // le nb de bits mini pour représenter la val (3 -> nbBits = 2)
			if (nbBits > ko) ko = nbBits;
			map.put(nbBits, map.getOrDefault(nbBits, 0) + 1);
		}
		
		int n = tab.length;
		int nbBitsTotalMini = (ko+1)*n; // pire cas
		
		int[] overflowCount = getOverflowCount(n, map, ko);
		
		
		for (Integer k: map.keySet()) {
		    int over = overflowCount[k];
		    int f = 32 - Integer.numberOfLeadingZeros(over);
		    int w = 1 + ((k > f) ? k : f);
		    int nbBitsTotal = n*w + over * ko;
		    
		    System.out.println("\n---- Candidat k'=" + k + " ----");
		    System.out.println("overflowCount = " + over);
		    System.out.println("f (bits index overflow) = " + f);
		    System.out.println("w (taille slot) = " + w);
		    System.out.println("nbBitsTotal = " + nbBitsTotal);
		    
		    if (nbBitsTotal < nbBitsTotalMini) {
		        nbBitsTotalMini = nbBitsTotal;
		        this.k = k;
		        System.out.println("=> Nouveau k optimum trouvé: " + k);
		    }
		}
	}
	
	
	@Override
	public void compress(int[] tabInput) {
		// TODO Auto-generated method stub

	}

	
	@Override
	public int get(int i) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	@Override
	public void decompress(int[] tabOutput) {
		// TODO Auto-generated method stub

	}

	public int getK() {
		return k;
	}

}
