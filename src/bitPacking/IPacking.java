package bitPacking;

public interface IPacking {
	
	// fonctions du bitPacking
	void compress(int[] tabInput);   		// compresse un Tab d'entiers (tabInput) en un Tab de mots (tabWords)
    int get(int i);               			// retourne la valeur du i-ème entier du tabWords 
    void decompress(int[] tabOutput);       // décompresse tabWords et place le résultat dans tabOutput
    
    // getters
    int bitsPerValue();           			// k : le nombre de bits pour représenté un entier de tabInput
    int capacityPerWord();        			// c : le nombre d'entier dans un mot (que withoutOverlap)
    int tabInputLength();         			// taille du tabInput
    int[] readTabWords();                	// permet la lecture du Tab compressé (tabWords)
}
