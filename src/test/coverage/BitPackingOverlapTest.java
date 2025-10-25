package test.coverage;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import main.api.IPacking;
import main.api.Mode;
import main.core.BitPackingOverflow;
import main.factory.BitPackingFactory;

class BitPackingOverlapTest {

	private IPacking bp;
	private int[] tab;
	
	@BeforeEach
	void setUp() {
		// Crée une nouvelle instance à chaque test = état neuf
		tab = new int[] {13, 3, 9, 560, 6, 670, 4, 12};
		bp = BitPackingFactory.create(Mode.OVERLAP, tab);
		
		bp.compress(tab);
	}
	
	@Test
	void testConstructor() {
		assertEquals(10, bp.getK());
	}
	
	
	@Test
	void testConstructorOverflow() {
		BitPackingOverflow bpOver = new BitPackingOverflow(Mode.OVERLAP, tab);
		bpOver.computeK(tab);
		bpOver.compress(tab);
		
		assertEquals(4, bpOver.getK());
	}
	
	
	@Test
	void testAllocateWords() {
		int len = bp.getWords().length;
		
		assertEquals(3, len);
	}
	
	@Test
	void testWriteSlot() {
		int[] words = new int[] {9440269, 1249379980, 768};
		
		assertArrayEquals(words, bp.getWords());
	}
	
	@Test
	void testReadSlotSimpleCase() {
		assertEquals(13, bp.get(0));
	}
	
	@Test
	void testReadSlotOverlapCase() {
		assertEquals(560, bp.get(3));
	}

}
