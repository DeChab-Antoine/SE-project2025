package main.api;

public interface IPacking {
	
	// fonctions du bitPacking
	void compress(int[] tabInput);   		// compresse un Tab d'entiers (tabInput) en un Tab de mots (tabWords)
    int get(int i);               			// retourne la valeur du i-ème entier du tabWords 
    void decompress(int[] tabOutput);       // décompresse tabWords et place le résultat dans tabOutput
	
    // fonction du calcul de k
    void computeK(int[] tab);
    
    // fonctions utiles aux tests
    int getK();
	int[] getWords();
	
}
