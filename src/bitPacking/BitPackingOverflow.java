package bitPacking;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class BitPackingOverflow implements IPacking {

	BitPacking bp; // Overlap or no
	private int[] tabBase;
	private int[] tabOverflow;
	
	private int kPrime; // nb de bits pour les valeurs hors de la zone d'overflow et coder l'indice des valeurs en overflow
	private int over; // nb de valeurs en overflow
	private int w; // largeur d'un slot dans la base (tag + kPrime=
	
    private int tabInputLength;
	
	public BitPackingOverflow(BitPacking bp) {
		this.bp = bp;
	}

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
		
		
	}

	
	@Override
	public int get(int i) {
	    int maskValue = (1 << (w - 1)) - 1;
	    
	    int word = tabBase[i];
	    int flag = word >>> (w - 1);
	    int valueOrIndex = word & maskValue;

	    if (flag == 0) {
	        return valueOrIndex;  
	    } else {
	        return tabOverflow[valueOrIndex];
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

}
