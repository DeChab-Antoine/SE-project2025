package main.core;

import main.utils.BitOps;

/**
 * Variante BitPacking avec chevauchement (overlap)
 *
 * Principe :
 *  - Les valeurs sont écrites les unes après les autres dans un flux de bits continu
 *  - Un slot de k bits peut commencer en fin de mot et se terminer dans le mot suivant
 *  - On adresse les bits par "position absolue" : bitPos = index * k
 *
 */

public class BitPackingOverlap extends BitPacking {

	// -------------------------
    //  Constructeurs
    // -------------------------
	
    /** Chemin AUTO : calcule k à partir de "input" */
	public BitPackingOverlap(int[] input) {
		computeK(input);
		this.inputLength = input.length;
	}
	
	
    /** Chemin FIXED : k imposés (Overflow) */
	public BitPackingOverlap(int length, int k) {
		this.k = k;
		this.inputLength = length;
	}

	
	// -------------------------
    //  Hooks (implémentations)
    // -------------------------
	
	/** Alloue le tableau words à la bonne taille */
	@Override
	protected int[] allocateWords() {
		// calcule en 64 bits (long) éviter un overflow si tabInputLength proche de IntegerMaxValue
		long bitsTotal = (long) k * (long) inputLength; 
		int wordsLength = (int) ((bitsTotal + WORD_SIZE - 1) / WORD_SIZE);
		
		return new int[(int) wordsLength];
	}
	
	
	/** Écrit la valeur "value" dans le tab "words" au slot "index" */
	@Override
	protected void writeSlot(int i, int value) {
		// Position absolue en bits
		int bitPos = i * k;      
		int wordIndex = bitPos / WORD_SIZE;
		int bitWordPos = bitPos % WORD_SIZE; 
		
		if (bitWordPos + k <= WORD_SIZE) {
            // Cas simple : tout tient dans le mot courant
			BitOps.writeBits(words, wordIndex, bitWordPos, k, value);
		} else {
			// Cas chevauchant :
			
			// découpage du nombre de bits
			int lowerBits = WORD_SIZE - bitWordPos; 
            int upperBits = k - lowerBits; 
		    
            // découpage de la valeur
            int lowerPartValue = value & BitOps.mask(lowerBits); 
            int upperPartValue = value >>> lowerBits;           

            // écriture
            BitOps.writeBits(words, wordIndex, bitWordPos, lowerBits, lowerPartValue);
            BitOps.writeBits(words, wordIndex + 1, 0, upperBits, upperPartValue);
		}
	}

	
    /** retourne la valeur lu dans le tab "words" au slot "index" */
	@Override
	protected int readSlot(int i) {
		// Position absolue en bits
		int bitPos = i * k;       
		int wordIndex = bitPos / WORD_SIZE; 
		int bitWordPos = bitPos % WORD_SIZE; 
		
	    if (bitWordPos + k <= WORD_SIZE) {
	    	// Cas simple : tout tient dans le mot courant
	        return BitOps.readBits(words[wordIndex], bitWordPos, k);
	    } else {
	    	// Cas chevauchant :

	    	// découpage
			int lowerBits = WORD_SIZE - bitWordPos; 
            int upperBits = k - lowerBits; 
            
            // lecture
	        int lowerPartValue = BitOps.readBits(words[wordIndex], bitWordPos, lowerBits);
	        int upperPartValue = BitOps.readBits(words[wordIndex + 1], 0, upperBits);
	        
	        // regroupage 
	        int value = lowerPartValue | (upperPartValue << lowerBits);
	        
	        return value;
	    }
	}
	
}
