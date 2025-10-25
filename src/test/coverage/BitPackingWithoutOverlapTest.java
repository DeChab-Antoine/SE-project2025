package test.coverage;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import main.api.IPacking;
import main.api.Mode;
import main.factory.BitPackingFactory;

class BitPackingWithoutOverlapTest {
	
	private IPacking bp;
	private int[] tab;
	
	@BeforeEach
	void setUp() {
		// Crée une nouvelle instance à chaque test = état neuf
		tab = new int[] {13, 3, 9, 560, 6, 670, 4, 12};
		bp = BitPackingFactory.create(Mode.WITHOUT_OVERLAP, tab);
	}
	
	@Test
	void testConstructor() {
		assertEquals(10, bp.getK());
	}
	
	
	@Test
	void testConstructorOverflow() {
		IPacking bpOver = BitPackingFactory.create(Mode.OVERFLOW_WITHOUT_OVERLAP, tab);
		bpOver.computeK(tab);
		bpOver.compress(tab);
		
		assertEquals(4, bpOver.getK());
	}
	
	
	@Test
	void testAllocateWords() {
		bp.compress(tab);
		
		assertEquals(3, bp.getWordsLength());
	}
	
	@Test
	void testWriteSlot() {
		bp.compress(tab);
		int[] words = new int[] {9440269, 702552624, 12292};
		
		assertArrayEquals(words, bp.getWords());
	}
	
	@Test
	void testReadSlot() {
		bp.compress(tab);
		
		assertEquals(13, bp.get(0));
	}

}
