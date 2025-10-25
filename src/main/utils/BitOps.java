package main.utils;

/**
 * Opérations binaires de bas niveau pour la manipulation des mots (32 bits)
 *
 * Fournit :
 *  - masquage (mask)
 *  - écriture/lecture de champs de bits
 *  - calcul du nombre minimal de bits nécessaires pour coder une valeur
 */

public class BitOps {
	
	private BitOps() {} // empêche l’instanciation
	
	/** Retourne un masque de "k" bits à 1 (ex: k=3 → 0b111) */
	public static int mask(int k) {
        return (1 << k) - 1;
    }


    /** Écrit la valeur "value" sur "k" bits dans le mot "words[wordIndex]", à partir de "bitWordPos" */
    public static void writeBits(int[] words, int wordIndex, int bitWordPos, int k, int value) {
    	words[wordIndex] |= (value & mask(k)) << bitWordPos; 
    }
    
    
    /** Lit une valeur sur "k" bits dans "word", à partir du décalage "bitWordPos" */
    public static int readBits(int word, int bitWordPos, int k) {
        return (word >>> bitWordPos) & mask(k);
    }

    
    /** Retourne le nombre minimal de bits nécessaires pour coder "value" (Ex: nbBits(0)=1, nbBits(5)=3) */
    public static int nbBits(int value) {
    	if (value <= 1) return 1;
    	return 32 - Integer.numberOfLeadingZeros(value);
    		
    }
}
