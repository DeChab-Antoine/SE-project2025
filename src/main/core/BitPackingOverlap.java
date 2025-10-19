package main.core;

public class BitPackingOverlap extends BitPacking {

	// --- Chemin AUTO-K ---
	public BitPackingOverlap(int[] input) {
		super(computeBitWidth(input)); // calcule k à partir du tableau
		this.inputLength = input.length;
	}
	
	
	// --- Chemin FIXED-K ---
	public BitPackingOverlap(int length, int k) {
		super(k); // k est déjà fixé par Overflow
		this.inputLength = length;
	}

	@Override
	protected int[] createWords() {
		long totalBits = (long) bitWidth * (long) inputLength; // calcule en 64 bits (long) éviter un overflow si tabInputLength proche de IntegerMaxValue
		long tabWordsLength = (totalBits + 31L) >>> 5;
		
		return new int[(int) tabWordsLength];
	}

	@Override
	protected void writeSlot(int index, int value) {
		long start = (long) index * (long) bitWidth; // numéro du bit où débuter à écrire
		int indWord = (int) start >>> 5; // indice du mot (/32)
		int borneInf = (int) start & 31; // nb bits déjà écris dans le mot courant (% 32)
		int borneSup = borneInf + bitWidth;
		if (borneSup <= 32) {
			BitOps.writeBits(words, indWord, borneInf, bitWidth, value);
		} else {
			int space = 32-borneInf;
	        int lowVal = value & ((1 << space) - 1);
	        int highVal = value >>> space;
	        BitOps.writeBits(words, indWord, borneInf, space, lowVal);
	        BitOps.writeBits(words, indWord+1, 0, bitWidth - space, highVal);
		}
	}

	@Override
	protected int readSlot(int index) {
		long start = (long) index * (long) bitWidth; // numéro du bit où débuter à écrire
		int indWord = (int) start >>> 5; // indice du mot (/32)
		int borneInf = (int) start & 31; // nb bits déjà écris dans le mot courant (% 32)
		int borneSup = borneInf + bitWidth;
		
	    if (borneSup <= 32) {
	        return BitOps.readBits(words[indWord], borneInf, bitWidth);
	    } else {
	    	int space = 32-borneInf;
	        int low  = BitOps.readBits(words[indWord], borneInf, space);
	        int high = BitOps.readBits(words[indWord + 1], 0, bitWidth - space);
	        return low | (high << space);
	    }
	}
	
}
