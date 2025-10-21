package main.core;

/**
 * Variante BitPacking sans chevauchement (without overlap)
 *
 * Principe :
 *  - Les valeurs sont écrites pour tenir dans un unique mot
 *  - Un slot de bitWidth bits commence et se terminer dans le même mot
 *  - On adresse les bits par "position absolue" : bitPos = index * bitWidth
 *  - Un mot contient nbSlotPerWord = WORD_SIZE / bitWidth slots
 */

public class BitPackingWithoutOverlap extends BitPacking {
	private int nbSlotPerWord;
	
	// -------------------------
    //  Constructeurs
    // -------------------------
	
    /** Chemin AUTO : calcule bitWidth à partir de "input" */
    public BitPackingWithoutOverlap(int[] tabInput) {
        super(computeBitWidth(tabInput));
        this.inputLength = tabInput.length;
        this.nbSlotPerWord = WORD_SIZE / bitWidth;
    }

    /** Chemin FIXED : bitWidth imposés (Overflow). */
    public BitPackingWithoutOverlap(int length, int bitWidth) {
        super(bitWidth); 
        this.inputLength = length;
        this.nbSlotPerWord = WORD_SIZE / bitWidth;
    }
	
    
    // -------------------------
    //  Hooks (implémentations)
    // -------------------------
    
    /** Alloue le tableau words à la bonne taille */
	@Override
	protected int[] createWords() {
		int wordsLength = (inputLength + nbSlotPerWord - 1 ) / nbSlotPerWord;
		return new int[wordsLength];
	}
	
	
	/** Écrit la valeur "value" dans le tab "words" au slot "index" */
	@Override
	protected void writeSlot(int i, int value) {
		// Position absolue en bits
		SlotPosition pos = new SlotPosition(nbSlotPerWord, bitWidth, i);
        
        BitOps.writeBits(words, pos.wordIndex, pos.offset, bitWidth, value);
	}
	
	
    /** retourne la valeur lu dans le tab "words" au slot "index" */
	@Override
    protected int readSlot(int i) {
		// Position absolue en bits
		SlotPosition pos = new SlotPosition(nbSlotPerWord, bitWidth, i);
        
        return BitOps.readBits(words[pos.wordIndex], pos.offset, bitWidth);    
    }
    

	// -------------------------
    //  Classe utilitaire interne
    // -------------------------

    /** Structure représentant la position d’un slot dans le tableau de mots. */
    private static class SlotPosition {
        final int wordIndex;   
        final int bitWordPos;
        final int offset;
        
        SlotPosition(int nbSlotPerWord, int bitWidth, int i) {
            this.wordIndex  = i / nbSlotPerWord;
            this.bitWordPos = i % nbSlotPerWord;
            this.offset     = bitWordPos * bitWidth;
        }
    }
	
}
