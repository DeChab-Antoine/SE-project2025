package main.core;

import main.api.IPacking;
import main.utils.BitOps;

/**
 * 
 * Abstraction commune (Template Method) pour les variantes de Bit Packing.
 * 
 * Rôle : 
 *  - Définir le squelette de la compression/décompression
 *  - Calcul commun de k
 *  - Déléguer la lecture/écriture d'un "slot" aux classes filles (Overlap et WithoutOverlap) via readSlot/writeSlot
 *    
 * Contrats : 
 *  - compress(int[] input)    : Compresse le tableau d'entiers "input", après l'appel, le tableau d'entiers "words" contient la représentation compressée
 *  - get(int i)               : Retourne la valeur du i-ème entier dans le tableau compressé "words" (doit être en O(1))
 *  - decompress(int[] output) : Reconstruit le tableau original "input", et place le résultat dans le tableau d'entiers output    
 * 
 * */

public abstract class BitPacking implements IPacking {
	
	protected static final int WORD_SIZE = 32; // Taille d'un mot (taille "Integer" = 32)
	protected int k;					       // Nombre de bits par valeur (k) (1 <= k <= WORD_SIZE)
    protected int inputLength;				   // Nombre d'éléments à compresser (taille du tab "input") 
    protected int[] words;					   // Tableau compressée (composé de mots) (null avant compress, !null après) 
    
    
    protected BitPacking() {
    	
    } 
    
    
    /**
     * Calcule la largeur minimale en bits "k" nécessaire pour représenter toutes les valeurs d’entrée
     * Si l’entrée est vide, on retourne 1
     */
    public void computeK(int[] input) {
    	if (input.length == 0) this.k = 1;
    	
		int kMax = BitOps.nbBits(input[0]);
		for (int val : input) {
			int k = BitOps.nbBits(val);
			if (k > kMax) kMax = k;
		}
		
		this.k = kMax;
	}
    
    
    // -------------------------
    //  Méthodes du contrat API
    // -------------------------

	@Override
	public void compress(int[] input) {
		this.words = allocateWords();
		
		for (int i = 0; i < inputLength; i++) {
			writeSlot(i, input[i]);
        }
	}

	
	@Override
	public int get(int i) {
		return readSlot(i);
	}
	
	
	@Override
	public void decompress(int[] output) {
		for (int i = 0; i < inputLength; i++) {
			output[i] = readSlot(i);
		}
	}
	
	
	// -------------------------
    //  Méthodes d'inspection (tests & debug)
    // -------------------------
	
	/** Getter de "k" */
	public int getK() {
		return k;
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
	protected abstract int[] allocateWords(); 
	
	/** Écrit la valeur "value" dans le tab "words" au slot "index" */
    protected abstract void writeSlot(int i, int value);
    
    /** retourne la valeur lu dans le tab "words" au slot "index" */
    protected abstract int readSlot(int i);

}
