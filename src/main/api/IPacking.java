package main.api;

public interface IPacking {
	
	// fonctions du bitPacking
	void compress(int[] tabInput);   		// compresse un Tab d'entiers (tabInput) en un Tab de mots (tabWords)
    int get(int i);               			// retourne la valeur du i-ème entier du tabWords 
    void decompress(int[] tabOutput);       // décompresse tabWords et place le résultat dans tabOutput
	
    int getK();
	int[] getWords();
	int getWordsLength();
	void computeK(int[] tab);
    
}
