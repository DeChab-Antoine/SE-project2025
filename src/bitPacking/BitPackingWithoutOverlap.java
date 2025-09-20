package bitPacking;

public class BitPackingWithoutOverlap {
	private int k;
	private int[] tab;
	private int n_tab;
	private int n;
	private int c;
	private int mask;
	private int out[];
	
	public BitPackingWithoutOverlap(int[] tab) {
		this.tab = tab;
		this.n_tab = tab.length;
		this.k = trouverK(tab);
		this.c = 32 / k; // nb d'élem par mots
		this.n = (n_tab + c - 1 ) / c; // nb d'elem dans tab de retour
		// mask pour couper le mot : si max=8 alors mask => 1111
		this.mask = (1 << k) - 1;
	}
	
	
	
	public int getK() {
		return k;
	}

	public int getN_tab() {
		return n_tab;
	}


	public int getC() {
		return c;
	}

	public int[] getOut() {
		return out;
	}

	
	public int trouverK(int[] tab) {
		int max = tab[0];
		for (int n : tab) {
			if (n > max) max = n;
		}
		
		if (max == 0) {
			k = 1;	
		} else {
			k = 32 - Integer.numberOfLeadingZeros(max);
		}
		
		return k;
	}
	
	
	// compresse un tableau A d'entiers (size = 8*32bits) en un tableau d'entiers B (size=1*32bits)
	public void compress() {
		this.out = new int[n];
		
		for (int i = 0; i < n_tab; i++) {
            
            // les Index 
            int w = i / c;                   // du mot de sortie
            int p = i % c;                   // du rang de l’élément dans ce mot
            int offset = p * k;              // décalage en bits 
            
            // Écrire la val à partir d'offset
            out[w] |= (tab[i] & mask) << offset;
        }
	}
	
	// pioche un entier dans le tab B compresser
	public int get(int i) {
		// les Index 
        int w = i / c;                   // du mot de sortie
        int p = i % c;                   // du rang de l’élément dans ce mot
        int offset = p * k;              // décalage en bits 
        
        // Lecture du mot
        int m = out[w];
        // appliquer le mask pour arriver à la bonne pos
        m = (( m >>> offset) & mask);
        
		return m;
	}
		
		
	// prend le tableau B compresser et retourne un tableau C décompresser on doit retrouver A
	public int[] decompress() {
		
		int[] out = new int[n_tab];
		for (int i = 0; i < n_tab; i++) {
			out[i] = get(i);
		}
		
		return out;
	}
	
	
	
}
