package test.coverage;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import main.api.IPacking;
import main.api.Mode;
import main.factory.BitPackingFactory;

class BitPackingTest {

	private IPacking bp;
	private int[] tab;
	
	@BeforeEach
	void setUp() {
		// Crée une nouvelle instance à chaque test = état neuf
		tab = new int[] {13, 3, 9, 560, 6, 670, 4, 12};
        bp = BitPackingFactory.create(Mode.WITHOUT_OVERLAP, tab);
	}
	
	
	@Test
	void testComputeKEmpty() {
		int[] empty = {};
		IPacking bpEmpty = BitPackingFactory.create(Mode.WITHOUT_OVERLAP, empty);
		
		assertEquals(1, bpEmpty.getK());
	}
	
	
	@Test
	void testCompress() {
		bp.compress(tab);
		int[] words = new int[] {9440269, 702552624, 12292};
		
		assertArrayEquals(words, bp.getWords());
	}
	
	
	@Test
	void testGet() {
		bp.compress(tab);
		
		assertEquals(13, bp.get(0));
	}
	
	
	@Test
	void testDecompress() {
		bp.compress(tab);
		int[] out = new int[tab.length];
		bp.decompress(out);
		
		assertArrayEquals(tab, out);
	}
	
	
	@Test
	void testGetWordsLength() {
		bp.compress(tab);
		
		assertEquals(3, bp.getWordsLength());
	}

}
