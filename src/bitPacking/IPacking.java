package bitPacking;

public interface IPacking {
	
	// fonctions du bitPacking
	void compress(int[] tabInput);   		// compresse un Tab d'entiers (tabInput) en un Tab de mots (tabWords)
    int get(int i);               			// retourne la valeur du i-ème entier du tabWords 
    void decompress(int[] tabOutput);       // décompresse tabWords et place le résultat dans tabOutput
    
    // getters
    int getK();           			// k : le nombre de bits pour représenté un entier de tabInput
    int getTabInputLength();        // taille du tabInput
    int[] getTabWords();            // permet la lecture du Tab compressé (tabWords)
}
