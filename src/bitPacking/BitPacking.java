package bitPacking;

public abstract class BitPacking implements IPacking {
	
	protected final int k;
    protected final int tabInputLength;
    protected int[] tabWords;
    
    
    protected BitPacking(int k, int tabInputLength) {
        this.k = k;
        this.tabInputLength = tabInputLength;
    }
    
    
    protected static int computeK(int[] tab) {
    	if (tab.length == 0) return 1;
		int max = tab[0];
		for (int val : tab) {
			if (val > max) max = val;
		}
		
		return (max == 0) ? 1 : (32 - Integer.numberOfLeadingZeros(max));
	}
    
    // Méthode Partagé
	@Override
	public void compress(int[] tabInput) {
		createTabWords();
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

	
	// Méthodes que chaque variante doit fournir
	protected abstract void createTabWords();          // doit le créer à la bonne taille 
    protected abstract void writeValue(int i, int val);
    protected abstract int  readValue(int i);

}
