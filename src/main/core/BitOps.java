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
	
	/** Retourne un masque de "bitWidth" bits à 1 (ex: bitWidth=3 → 0b111) */
    static int mask(int bitWidth) {
        return (1 << bitWidth) - 1;
    }


    /** Écrit la valeur "value" sur "bitWidth" bits dans le mot "words[wordIndex]", à partir de "bitWordPos" */
    static void writeBits(int[] words, int wordIndex, int bitWordPos, int bitWidth, int value) {
    	words[wordIndex] |= (value & mask(bitWidth)) << bitWordPos; 
    }
    
    
    /** Lit une valeur sur "bitWidth" bits dans "word", à partir du décalage "bitWordPos" */
    static int readBits(int word, int bitWordPos, int bitWidth) {
        return (word >>> bitWordPos) & mask(bitWidth);
    }

    
    /** Retourne le nombre minimal de bits nécessaires pour coder "value" (Ex: nbBits(0)=1, nbBits(5)=3) */
    static int nbBits(int value) {
    	if (value <= 1) return 1;
    	return 32 - Integer.numberOfLeadingZeros(value);
    		
    }
}
