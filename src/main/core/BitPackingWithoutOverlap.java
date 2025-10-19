package main.core;

public class BitPackingWithoutOverlap extends BitPacking {
	private int c;
	
	// --- Chemin AUTO-K ---
    public BitPackingWithoutOverlap(int[] tabInput) {
        super(computeBitWidth(tabInput)); // calcule k à partir du tableau
        this.inputLength = tabInput.length;
        this.c = 32 / bitWidth;
    }

    // --- Chemin FIXED-K ---
    public BitPackingWithoutOverlap(int length, int k) {
        super(k); // k est déjà fixé par Overflow
        this.inputLength = length;
        this.c = 32 / k;
    }
	
	@Override
	protected int[] createWords() {
		int tabWordsLength = (inputLength + c - 1 ) / c;
		return new int[tabWordsLength];
	}
	
	
	@Override
	protected void writeSlot(int index, int val) {
		int indWord = index / c;             // ind du mot 
        int pos = index % c;                   // rang dans ce mot
        int offset = pos * bitWidth;              // décalage en bits 
        
        BitOps.writeBits(words, indWord, offset, bitWidth, val);
	}
	
	
	@Override
    protected int readSlot(int i) {
		int indWord = i / c;             // ind du mot 
        int pos = i % c;                   // rang dans ce mot
        int offset = pos * bitWidth;              // décalage en bits
        
        return BitOps.readBits(words[indWord], offset, bitWidth);    
    }
    

	
	
}
