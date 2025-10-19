package main.core;

public class BitPackingWithoutOverlap extends BitPacking {
	private int c;
	
	// --- Chemin AUTO-K ---
    public BitPackingWithoutOverlap(int[] tabInput) {
        super(computeK(tabInput)); // calcule k à partir du tableau
        this.tabInputLength = tabInput.length;
        this.c = 32 / k;
    }

    // --- Chemin FIXED-K ---
    public BitPackingWithoutOverlap(int length, int k) {
        super(k); // k est déjà fixé par Overflow
        this.tabInputLength = length;
        this.c = 32 / k;
    }
	
	@Override
	protected int[] createTabWords() {
		int tabWordsLength = (tabInputLength + c - 1 ) / c;
		return new int[tabWordsLength];
	}
	
	
	@Override
	protected void writeValue(int i, int val) {
		int indWord = i / c;             // ind du mot 
        int pos = i % c;                   // rang dans ce mot
        int offset = pos * k;              // décalage en bits 
        
        BitOps.writeBits(tabWords, indWord, offset, k, val);
	}
	
	
	@Override
    protected int readValue(int i) {
		int indWord = i / c;             // ind du mot 
        int pos = i % c;                   // rang dans ce mot
        int offset = pos * k;              // décalage en bits
        
        return BitOps.readBits(tabWords[indWord], offset, k);    
    }
    

	
	
}
