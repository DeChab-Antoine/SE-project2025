package test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import bitPacking.BitPackingOverflow;
import bitPacking.Mode;
import bitPacking.BitPacking;

class BitPackingOverflowTest {

	@Test
	@DisplayName("BitPackingOverflow – Cas simple avec affichage générique")
	void testSimple_debugView() {
	    int[] tab = {13, 3, 9, 560, 6, 670, 4, 12};

	    BitPackingOverflow c = new BitPackingOverflow(Mode.AUTO, tab);
	    c.computeOptimumK(tab);
	    c.compress(tab);

	    int[] out = new int[tab.length];
	    c.decompress(out);

	    // Vérifie round-trip
	    assertArrayEquals(tab, out);

	    // Affiche l’état interne
	    c.printDebugView(tab, out);
	}


    @Test
    @DisplayName("BitPackingOverflow – Gros tableau silencieux (round-trip)")
    void testHugeArray_silent() {
        int n = 10_000;
        int[] tab = new int[n];
        Random rnd = new Random(42);

        for (int i = 0; i < n; i++) {
            if (i % 1000 == 0) tab[i] = rnd.nextInt(1 << 30);
            else tab[i] = rnd.nextInt(1 << 12);
        }

        BitPackingOverflow c = new BitPackingOverflow(Mode.AUTO, tab);
        c.computeOptimumK(tab);
        c.compress(tab);
        int[] out = new int[tab.length];
        c.decompress(out);

        assertArrayEquals(tab, out);
        
     // Affiche l’état interne
	    c.printDebugView(tab, out);
    }

}
