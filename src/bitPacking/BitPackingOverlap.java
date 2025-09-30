package bitPacking;

public class BitPackingOverlap extends BitPacking {

	public BitPackingOverlap(int[] tabInput) {
		super(computeK(tabInput), tabInput.length);
	}

	@Override
	protected void createTabWords() {
		long totalBits = (long) k * (long) tabInputLength; // calcule en 64 bits (long) éviter un overflow si tabInputLength proche de IntegerMaxValue
		long tabWordsLength = (totalBits + 31L) >>> 5;
		
		this.tabWords = new int[(int) tabWordsLength];
	}

	@Override
	protected void writeValue(int i, int val) {
		long start = (long) i * (long) k; // numéro du bit où débuter à écrire
		int indWord = (int) start >>> 5; // indice du mot (/32)
		int borneInf = (int) start & 31; // nb bits déjà écris dans le mot courant (% 32)
		int borneSup = borneInf + k;
				
		if (borneSup <= 32) {
			BitOps.writeBits(tabWords, indWord, borneInf, k, val);
		} else {
			int space = 32-borneInf;
	        int lowVal = val & ((1 << space) - 1);
	        int highVal = val >>> space;
	        BitOps.writeBits(tabWords, indWord, borneInf, space, lowVal);
	        BitOps.writeBits(tabWords, indWord+1, 0, k-space, highVal);
		}
	}

	@Override
	protected int readValue(int i) {
		long start = (long) i * (long) k; // numéro du bit où débuter à écrire
		int indWord = (int) start >>> 5; // indice du mot (/32)
		int borneInf = (int) start & 31; // nb bits déjà écris dans le mot courant (% 32)
		int borneSup = borneInf + k;
		
	    if (borneSup <= 32) {
	        return BitOps.readBits(tabWords[indWord], borneInf, k);
	    } else {
	    	int space = 32-borneInf;
	        int low  = BitOps.readBits(tabWords[indWord], borneInf, space);
	        int high = BitOps.readBits(tabWords[indWord + 1], 0, k-space);
	        return low | (high << space);
	    }
	}
	
}
