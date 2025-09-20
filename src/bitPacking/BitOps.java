package bitPacking;

final class BitOps {
	static final int WORD_BITS = 32;

	
    static int mask(int k) {
        return (1 << k) - 1;
    }

    
    static int readBits(int word, int offset, int k) {
        return (word >>> offset) & mask(k);
    }

    
    static void writeBits(int[] words, int indWord, int offset, int k, int val) {
    	words[indWord] |= (val & mask(k)) << offset; 
    }
}
