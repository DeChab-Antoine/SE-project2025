package bitPacking;

public class BitPackingWithoutOverlap extends BitPacking {
	private int c;
	
	public BitPackingWithoutOverlap(int[] tabInput) {
		super(fixK(tabInput), tabInput.length);
		this.c = 32 / k; // nb d'élem par mots
		// mask pour couper le mot : si max=8,k=4 alors mask => 1111
	}
	
	
	private static int fixK(int[] tab) {
		int max = tab[0];
		for (int val : tab) {
			if (val > max) max = val;
		}
		
		return (max == 0) ? 1 : (32 - Integer.numberOfLeadingZeros(max));
	}
	
	
	@Override
	protected void createTabWords() {
		int tabWordsLength = (tabInputLength + c - 1 ) / c;
		this.tabWords = new int[tabWordsLength];
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
