package bitPacking;

public class BitPackingWithoutOverlap {
	private static int k;
	
	public BitPackingWithoutOverlap() {
	
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
	public int[] compress(int[] tab) {
		k = trouverK(tab);
		
		int c = 32/k; // nb d'élem par mots		
		int n = (tab.length + c - 1 ) / c; // nb d'elem dans tab de retour
		int[] out = new int[n];
		
		// mask pour couper le mot : si max=8 alors mask => 1111
		final int mask = (1 << k) - 1;
		
		for (int i = 0; i < tab.length; i++) {
            
            // les Index 
            int w = i / c;                   // du mot de sortie
            int p = i % c;                   // du rang de l’élément dans ce mot
            int offset = p * k;              // décalage en bits 
            
            // Écrire la val à partir d'offset
            out[w] |= (tab[i] & mask) << offset;
        }
		
		return out;
	}
	
	
	// prend le tableau B compresser et retourne un tableau C décompresser on doit retrouver A
	public void decompress() {
		
	}
	
	
	// pioche un entier dans le tab B compresser
	public int get(int i) {
		return 0;
	}
}
