package test.coverage;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import main.api.IPacking;
import main.api.Mode;
import main.factory.BitPackingFactory;


class BitPackingOverflowTest {

	private IPacking bp;
	private int[] tab;
	
	@BeforeEach
	void setUp() {
		// Crée une nouvelle instance à chaque test = état neuf
		tab = new int[] {13, 3, 9, 560, 6, 670, 4, 12};
        bp = BitPackingFactory.create(Mode.AUTO, tab);
        
        bp.computeK(tab);
		bp.compress(tab);
	}
	
	
	@Test
	void testComputeK() {
		assertEquals(4, bp.getK());
	}
	
	
	@Test
	void testCompress() {
		int[] words = new int[] {577250413, 388};
		
		assertArrayEquals(words, bp.getWords());
	}
	
	
	@Test
	void testGet() {
		assertEquals(13, bp.get(0));
	}
	
	
	@Test
	void testDecompress() {
		int[] out = new int[tab.length];
		bp.decompress(out);
		
		assertArrayEquals(tab, out);
	}
	
}
