package bitPacking;

public abstract class BitPacking implements IPacking {
	
	protected int k;
    protected int tabInputLength;
    protected int[] tabWords;
    
    
    protected BitPacking(int k) {
        this.k = k;
    }
    
    
    protected static int computeK(int[] tabInput) {
    	if (tabInput.length == 0) return 1;
		int max = tabInput[0];
		for (int val : tabInput) {
			max = Math.max(max, val);
		}
		
		return (max == 0) ? 1 : (32 - Integer.numberOfLeadingZeros(max));
	}
    
    // Méthode Partagé
	@Override
	public void compress(int[] tabInput) {
		this.tabInputLength = tabInput.length;
		this.tabWords = createTabWords();
		
		for (int i = 0; i < tabInputLength; i++) {
			writeValue(i, tabInput[i]);
        }
	}

	
	@Override
	public int get(int i) {
		return readValue(i);
	}

	
	@Override
	public void decompress(int[] tabOutput) {
		for (int i = 0; i < tabInputLength; i++) {
			tabOutput[i] = readValue(i);
		}
	}
	
	// Méthode pour les tests
	public int getK() {
		return k;
	}
	
	
	public int[] getTabWords() {
	    return tabWords;
	}

	
	public int getTabWordsLength() {
		return tabWords.length;
	}
	
	// Méthodes que chaque variante doit fournir
	protected abstract int[] createTabWords();          // doit le créer à la bonne taille 
    protected abstract void writeValue(int i, int val);
    protected abstract int  readValue(int i);

}
