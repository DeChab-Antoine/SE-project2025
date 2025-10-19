package test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

import org.junit.jupiter.api.Test;

import main.core.*;

class BitPackingTest {

	
	private void structTest(BitPacking bp, int[] in) {
		int tabLength = in.length;
		
		bp.compress(in);
		int[] out = new int[tabLength];
		bp.decompress(out);
		
		assertArrayEquals(in, out);
		
		for (int i = 0; i < tabLength; i++) {
			assertEquals(in[i], bp.get(i));
		}
	}
	
	@Test
	void testEmptyArray() {
		int[] tab = {};
		BitPackingOverlap bpO = new BitPackingOverlap(tab);
		structTest(bpO, tab);
		
		BitPackingWithoutOverlap bpWO = new BitPackingWithoutOverlap(tab);
		structTest(bpWO, tab);
	}
	
	
	@Test
	void testAllZeros() {
		int[] tab = {0, 0, 0, 0};
		BitPackingOverlap bpO = new BitPackingOverlap(tab);
		structTest(bpO, tab);
		
		BitPackingWithoutOverlap bpWO = new BitPackingWithoutOverlap(tab);
		structTest(bpWO, tab);
	}
	
	
	@Test
	void testOneElement() {
		int[] tab = {42};
		BitPackingOverlap bpO = new BitPackingOverlap(tab);
		structTest(bpO, tab);
		
		BitPackingWithoutOverlap bpWO = new BitPackingWithoutOverlap(tab);
		structTest(bpWO, tab);
	}
	
	
	@Test
	void testManyElementsEveryK() {
		Random rnd = new Random(42);
		for (int k0 = 1; k0 < 32; k0 ++) {
			int n = 1000;
	        int[] tab = new int[n];

	        int mask = (1 << k0) - 1;
	        tab[n - 1] = mask; // force k = k0
	        for (int i = 0; i < n - 1; i++) {
	            tab[i] = rnd.nextInt() & mask;
	        }
		    
		    BitPackingOverlap bpO = new BitPackingOverlap(tab);
			structTest(bpO, tab);
				
			BitPackingWithoutOverlap bpWO = new BitPackingWithoutOverlap(tab);
			structTest(bpWO, tab);
			
			int kO  = bpO.getK();;
			int tabWordsLengthO = (n * kO + 31) / 32;
			assertEquals(tabWordsLengthO,  bpO.getTabWordsLength());

			int kWO  = bpWO.getK();
			int c = 32 / kWO;
			int tabWordsLengthWO = (n + c - 1) / c;
			assertEquals(tabWordsLengthWO, bpWO.getTabWordsLength());
			
			// overlap ne stocke jamais plus que sans overlap
			assertTrue(bpO.getTabWordsLength() <= bpWO.getTabWordsLength());
		}
		
	}
	
	
	@Test
	void testMaxValue() {
		int[] tab = new int[50];
		for (int i = 0; i < 50; i++) tab[i] = Integer.MAX_VALUE;
		
		BitPackingOverlap bpO = new BitPackingOverlap(tab);
		structTest(bpO, tab);
		
		BitPackingWithoutOverlap bpWO = new BitPackingWithoutOverlap(tab);
		structTest(bpWO, tab);
	}
	

	
	
	
	
	
	
	
	
}
