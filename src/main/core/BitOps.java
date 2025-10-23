package main.core;

/**
 * Opérations binaires de bas niveau pour la manipulation des mots (32 bits)
 *
 * Fournit :
 *  - masquage (mask)
 *  - écriture/lecture de champs de bits
 *  - calcul du nombre minimal de bits nécessaires pour coder une valeur
 */

final class BitOps {
	
	/** Retourne un masque de "k" bits à 1 (ex: k=3 → 0b111) */
    static int mask(int k) {
        return (1 << k) - 1;
    }


    /** Écrit la valeur "value" sur "k" bits dans le mot "words[wordIndex]", à partir de "bitWordPos" */
    static void writeBits(int[] words, int wordIndex, int bitWordPos, int k, int value) {
    	words[wordIndex] |= (value & mask(k)) << bitWordPos; 
    }
    
    
    /** Lit une valeur sur "k" bits dans "word", à partir du décalage "bitWordPos" */
    static int readBits(int word, int bitWordPos, int k) {
        return (word >>> bitWordPos) & mask(k);
    }

    
    /** Retourne le nombre minimal de bits nécessaires pour coder "value" (Ex: nbBits(0)=1, nbBits(5)=3) */
    static int nbBits(int value) {
    	if (value <= 1) return 1;
    	return 32 - Integer.numberOfLeadingZeros(value);
    		
    }
}
