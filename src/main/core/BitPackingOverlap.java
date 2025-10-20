package main.core;

/**
 * Variante BitPacking avec chevauchement (overlap).
 *
 * Principe :
 *  - Les valeurs sont écrites les unes après les autres dans un flux de bits continu.
 *  - Un slot de k bits peut commencer en fin de mot et se terminer dans le mot suivant.
 *  - On adresse les bits par "position absolue" : bitPos = index * bitWidth.
 *
 */

public class BitPackingOverlap extends BitPacking {

	// -------------------------
    //  Constructeurs
    // -------------------------
	
    /** Chemin AUTO : calcule bitWidth à partir du contenu et fige inputLength. */
	public BitPackingOverlap(int[] input) {
		super(computeBitWidth(input)); // calcule k à partir du tableau
		this.inputLength = input.length;
	}
	
	
    /** Chemin FIXED : longueur + bitWidth imposés (utile avec Overflow). */
	public BitPackingOverlap(int length, int k) {
		super(k); // k est déjà fixé par Overflow
		this.inputLength = length;
	}

	
	// -------------------------
    //  Hooks (implémentations)
    // -------------------------
	
	/** Alloue le tableau words à la bonne taille */
	@Override
	protected int[] createWords() {
		// calcule en 64 bits (long) éviter un overflow si tabInputLength proche de IntegerMaxValue
		long bitsTotal  = (long) bitWidth * (long) inputLength; 
		int wordsLength = (int) ((bitsTotal + WORD_SIZE - 1) / WORD_SIZE);
		
		return new int[(int) wordsLength];
	}

	/** Écrit la valeur "value" dans le tab "words" au slot "index" */
	@Override
	protected void writeSlot(int i, int value) {
		// Calcul des paramètres pour l'écriture en bits 
		int bitPos    = i * bitWidth;                // numéro du bit où débuter à écrire
		int wordIndex = bitPos / WORD_SIZE;          // indice du mot 
		int bitOffset = bitPos % WORD_SIZE;          // nb bits déjà écris dans le mot courant 
		
        int remainingInWord = WORD_SIZE - bitOffset; // nb bits restants dans le mot
		
		if (remainingInWord >= 32) {
            // Cas simple : tout tient dans le mot courant
			BitOps.writeBits(words, wordIndex, bitOffset, bitWidth, value);
		} else {
			// Cas chevauchant :
            int lowerPartValue = value & BitOps.mask(remainingInWord); // partie basse dans le mot courant
            int upperPartValue = value >>> remainingInWord;            // partie haute pour le mot suivant

            // On écrit une partie en fin de mot courant
            BitOps.writeBits(words, wordIndex, bitOffset, remainingInWord, lowerPartValue);
            
            // puis le reste au début du mot suivant
            BitOps.writeBits(words, wordIndex + 1, 0, bitWidth - remainingInWord, upperPartValue);
		}
	}

    /** retourne la valeur lu dans le tab "words" au slot "index" */
	@Override
	protected int readSlot(int i) {
		// Calcul des paramètres pour la lecture en bits 
		int bitPos    = i * bitWidth;                // numéro du bit où débuter à lire
		int wordIndex = bitPos / WORD_SIZE;          // indice du mot 
		int bitOffset = bitPos % WORD_SIZE;          // nb bits déjà écris dans le mot courant 
		
		int remainingInWord = WORD_SIZE - bitOffset;
		
	    if (remainingInWord >= 32) {
	    	// Cas simple : tout tient dans le mot courant
	        return BitOps.readBits(words[wordIndex], bitOffset, bitWidth);
	    } else {
	    	// Cas chevauchant :
	    	// On lit une partie en fin de mot courant
	        int lowerPartValue = BitOps.readBits(words[wordIndex], bitOffset, remainingInWord);
	        
            // puis le reste au début du mot suivant
	        int upperPartValue = BitOps.readBits(words[wordIndex + 1], 0, bitWidth - remainingInWord);
	        
	        // On compacte la partie basse et haute, et on retourne la valeur 
	        int value = lowerPartValue | (upperPartValue << remainingInWord);
	        return value;
	    }
	}
	
}
