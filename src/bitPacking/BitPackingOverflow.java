package bitPacking;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class BitPackingOverflow extends BitPacking{

	// données du format 
	private int kPrime; // nb de bits pour les valeurs hors de la zone d'overflow et coder l'indice des valeurs en overflow
	private int over; // nb de valeurs en overflow
	private int w; // largeur d'un slot dans la base (tag + kPrime)
	private int nbBitsTotal;
	
	// flux 
	protected int[] tabWords; 
	
	// mode 
	private final Mode mode;
    
	
	public BitPackingOverflow(int[] tabInput, Mode mode) {
		this.tabInput = tabInput;
		this.tabInputLength = tabInput.length;
		this.k = computeK(tabInput);
		
		this.mode = mode;
	}
	
	
	public int getK() {
		return kPrime;
	}

	private static int[] getOverflowCount(Map<Integer, Integer> map, int kOver) {
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
	
	
	@Override
	protected int computeK(int[] tab) {
		Map<Integer, Integer> map = new TreeMap<>();
		
		int kOver = 0; // le k de la zone d'overflow (k max)
		for (int val : tab) {
			int nbBits = BitOps.nbBits(val); // le nb de bits mini pour représenter la val (3 -> nbBits = 2)
			kOver = Math.max(kOver, nbBits);
			map.put(nbBits, map.getOrDefault(nbBits, 0) + 1);
		}
		
		int[] overflowCount = getOverflowCount(map, kOver);
		int nbBitsTotalMini = Integer.MAX_VALUE;
		int k = 1;
		
		for (Integer candK: map.keySet()) {
		    int over = overflowCount[candK];
		    int f = BitOps.nbBits(over-1);
		    int w = 1 + Math.max(candK, f);
		    int nbBitsTotal = tabInputLength*w + over * kOver;
		    
		    if (nbBitsTotal < nbBitsTotalMini) {
		        nbBitsTotalMini = nbBitsTotal;
		        k = candK;
		        this.w = w;
		        this.over = over;
		        this.nbBitsTotal = nbBitsTotal;
		    }
		}
		
		return k; 
	}
	
	@Override
	protected void createTabWords() {
		int tabWordsLength = (nbBitsTotal + 31) >>> 5;
		
		this.tabWords = new int[tabWordsLength];
	}
	

	@Override
	protected void writeValue(int i, int val) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected int readValue(int i) {
		// TODO Auto-generated method stub
		return 0;
	}

//	@Override
//	public void compress(int[] tabInput) {
//		this.tabWords = 
//		
//		int iOver = 0;
//		
//		for (int i = 0; i < tabInputLength; i++) {
//			int val = tabInput[i];
//			
//			if (BitOps.nbBits(val) <= kPrime) {
//				tabBase[i] = val;
//			} else {
//				tabBase[i] = iOver + (1 << (w - 1));
//				tabOverflow[iOver] = val;
//				iOver++;
//			}
//			
//		}
//		
//		
//	}

	
//	@Override
//	public int get(int i) {
//	    int maskValue = (1 << (w - 1)) - 1;
//	    
//	    int word = tabBase[i];
//	    int flag = word >>> (w - 1);
//	    int valueOrIndex = word & maskValue;
//
//	    if (flag == 0) {
//	        return valueOrIndex;  
//	    } else {
//	        return tabOverflow[valueOrIndex];
//	    }
//	}
//
//
//	
//	@Override
//	public void decompress(int[] tabOutput) {
//		for (int i = 0; i < tabInputLength; i++) {
//			tabOutput[i] = get(i);
//		}
//
//	}




}
