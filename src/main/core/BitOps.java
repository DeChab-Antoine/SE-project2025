package main.core;

final class BitOps {
	static final int WORD_BITS = 32;

	
    static int mask(int k) {
        return (1 << k) - 1;
    }


    static void writeBits(int[] words, int indWord, int offset, int k, int val) {
    	words[indWord] |= (val & mask(k)) << offset; 
    }
    
    
    static int readBits(int word, int offset, int k) {
        return (word >>> offset) & mask(k);
    }

    
    static int nbBits(int v) {
    	if(v <= 1) return 1;
    	return 32 - Integer.numberOfLeadingZeros(v);
    		
    }
}
