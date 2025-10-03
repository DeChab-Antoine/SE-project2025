package test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

import org.junit.jupiter.api.Test;

import bitPacking.BitPackingOverflow;
import bitPacking.BitPackingOverlap;

class BitPackingOverflowTest {

	@Test
	public void testSimple() {
		int[] tab = {13, 9, 12, 3, 6, 4, 560, 670}; 
	    // histogramme attendu: 2→1, 3→2, 4→3, 10→2

	    BitPackingOverflow c = new BitPackingOverflow(new BitPackingOverlap(tab));
	    c.computeOptimumK(tab);

	    // Affichage résultat final
	    System.out.println("\n=== Résultat final ===");
	    System.out.println("k optimum = " + c.getK());
	 
	    assertEquals(4, c.getK()); // optimum = k'=2, avec 1 valeur envoyée en overflow
	}
	
	
	@Test
	public void testHugeArrayDebug() {
	    int n = 10000; // 10 000 éléments
	    int[] tab = new int[n];
	    Random rnd = new Random(42);

	    for (int i = 0; i < n; i++) {
	        if (i % 1000 == 0) {
	            // tous les 1000 éléments, on met un outlier énorme
	            tab[i] = rnd.nextInt(1 << 30); // très grand, ~30 bits
	        } else {
	            // sinon valeurs petites/moyennes
	            tab[i] = rnd.nextInt(1 << 12); // jusqu’à 4096 (12 bits)
	        }
	    }

	    BitPackingOverflow c = new BitPackingOverflow(new BitPackingOverlap(tab));
	    c.computeOptimumK(tab);

	    System.out.println("=== Résultat pour énorme tableau ===");
	    System.out.println("n = " + n);
	    System.out.println("k optimum choisi = " + c.getK());
	}
}
