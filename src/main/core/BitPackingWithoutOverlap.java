package main.core;

import main.utils.BitOps;

/**
 * Variante BitPacking sans chevauchement (without overlap)
 *
 * Principe :
 *  - Les valeurs sont écrites pour tenir dans un unique mot
 *  - Un slot de k bits commence et se terminer dans le même mot
 *  - On adresse les bits par "position absolue" : bitPos = index * k
 *  - Un mot contient nbSlotPerWord = WORD_SIZE / k slots
 */

public class BitPackingWithoutOverlap extends BitPacking {
	private int nbSlotPerWord;
	
	// -------------------------
    //  Constructeurs
    // -------------------------
	
    /** Chemin AUTO : calcule k à partir de "input" */
    public BitPackingWithoutOverlap(int[] tabInput) {
        computeK(tabInput);
        this.inputLength = tabInput.length;
        this.nbSlotPerWord = WORD_SIZE / k;
    }

    /** Chemin FIXED : k imposés (Overflow). */
    public BitPackingWithoutOverlap(int length, int k) {
        this.k = k; 
        this.inputLength = length;
        this.nbSlotPerWord = WORD_SIZE / k;
    }
	
    
    // -------------------------
    //  Hooks (implémentations)
    // -------------------------
    
    /** Alloue le tableau words à la bonne taille */
	@Override
	protected int[] allocateWords() {
		int wordsLength = (inputLength + nbSlotPerWord - 1 ) / nbSlotPerWord;
		return new int[wordsLength];
	}
	
	
	/** Écrit la valeur "value" dans le tab "words" au slot "index" */
	@Override
	protected void writeSlot(int i, int value) {
		// Position absolue en bits
		SlotPosition pos = new SlotPosition(nbSlotPerWord, k, i);
        
        BitOps.writeBits(words, pos.wordIndex, pos.offset, k, value);
	}
	
	
    /** retourne la valeur lu dans le tab "words" au slot "index" */
	@Override
    protected int readSlot(int i) {
		// Position absolue en bits
		SlotPosition pos = new SlotPosition(nbSlotPerWord, k, i);
        
        return BitOps.readBits(words[pos.wordIndex], pos.offset, k);    
    }
    

	// -------------------------
    //  Classe utilitaire interne
    // -------------------------

    /** Structure représentant la position d’un slot dans le tableau de mots. */
    private static class SlotPosition {
        final int wordIndex;   
        final int bitWordPos;
        final int offset;
        
        SlotPosition(int nbSlotPerWord, int k, int i) {
            this.wordIndex = i / nbSlotPerWord;
            this.bitWordPos = i % nbSlotPerWord;
            this.offset = bitWordPos * k;
        }
    }
	
}
