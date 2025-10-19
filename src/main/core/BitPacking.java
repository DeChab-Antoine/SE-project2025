package main.core;


/**
 * 
 * Abstraction commune (Template Method) pour les variantes de Bit Packing.
 * 
 * Rôle : 
 *  - Définir le squelette de la compression/décompression
 *  - Calcul commun de bitWidth
 *  - Déléguer la lecture/écriture d'un "slot" aux classes filles (Overlap et WithoutOverlap) via readSlot/writeSlot
 *    
 * Contrats : 
 *  - compress(int[] input)    : Compresse le tableau d'entiers "input", après l'appel, le tableau d'entiers "words" contient la représentation compressée
 *  - get(int index)           : Retourne la valeur du i-ème entier "index" dans le tableau compressé "words" (doit être en O(1))
 *  - decompress(int[] output) : Reconstruit le tableau original "input", et place le résultat dans le tableau d'entiers output    
 * 
 * Variables : 
 * - WORD_SIZE   : Taille d'un mot (taille "Integer" = 32)
 * - bitWidth    : Nombre de bits par valeur (k) 
 * - inputLength : Nombre d'éléments à compresser (taille du tab "input")
 * - words       : Tableau compressée (composé de mots) 
 * 
 * */

public abstract class BitPacking implements IPacking {
	
	protected static final int WORD_SIZE = 32;
	protected int bitWidth;
    protected int inputLength;
    protected int[] words;
    
    
    protected BitPacking(int bitWidth) {
        this.bitWidth = bitWidth;
    }
    
    
    /**
     * Calcule la largeur minimale en bits "bitWidth" nécessaire pour représenter toutes les valeurs d’entrée
     * Si l’entrée est vide, on retourne 1
     */
    protected static int computeBitWidth(int[] tabInput) {
    	if (tabInput.length == 0) return 1;
    	
		int max = tabInput[0];
		for (int val : tabInput) {
			max = Math.max(max, val);
		}
		
		return BitOps.nbBits(max);
	}
    
    
    // -------------------------
    //  Méthodes du contrat API
    // -------------------------

    /** Alloue "words" via createWords() et écrit chaque valeur dans le bon "slot" via writeSlot() */
	@Override
	public void compress(int[] tabInput) {
		
		this.words = createWords();
		
		for (int i = 0; i < inputLength; i++) {
			writeSlot(i, tabInput[i]);
        }
	}

	
	/** retourne la valeur lu via readSlot() */
	@Override
	public int get(int index) {
		return readSlot(index);
	}
	
	
	/** décompresse dans output */
	@Override
	public void decompress(int[] output) {
		for (int i = 0; i < inputLength; i++) {
			output[i] = readSlot(i);
		}
	}
	
	
	
	// -------------------------
    //  Méthodes d'inspection (tests & debug)
    // -------------------------
	
	
	/** Getter de "bitWidth" */
	public int getBitWidth() {
		return bitWidth;
	}

	
	/** Getter de "words" */
	public int[] getWords() {
	    return words;
	}
	
	
	/** Retourne le nombre de mots compressés */
	public int getWordsLength() {
		return words.length;
	}

	
	
	// --------------------------------------------
    //  Méthodes que chaque variante doit fournir
    // --------------------------------------------
	
	/** Alloue le tableau words à la bonne taille */
	protected abstract int[] createWords(); 
	
	/** Écrit la valeur "value" dans le tab "words" au slot "index" */
    protected abstract void writeSlot(int index, int value);
    
    /** retourne la valeur lu dans le tab "words" au slot "index" */
    protected abstract int readSlot(int index);

}
