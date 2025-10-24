package test.coverage;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import main.core.BitPackingOverflow;
import main.core.BitPackingOverlap;
import main.core.BitPackingWithoutOverlap;
import main.model.Mode;

class BitPackingOverlapTest {

	private BitPackingOverlap bp;
	private int[] tab;
	
	@BeforeEach
	void setUp() {
		// Crée une nouvelle instance à chaque test = état neuf
		tab = new int[] {13, 3, 9, 560, 6, 670, 4, 12};
        bp = new BitPackingOverlap(tab);
	}
	
	@Test
	void testConstructor() {
		assertEquals(10, bp.getK());
	}
	
	
	@Test
	void testConstructorOverflow() {
		BitPackingOverflow bpOver = new BitPackingOverflow(Mode.OVERLAP, tab);
		bpOver.computeOptimumK(tab);
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
		int[] words = new int[] {9440269, 1249379980, 768};
		
		assertArrayEquals(words, bp.getWords());
	}
	
	@Test
	void testReadSlotSimpleCase() {
		bp.compress(tab);
		
		assertEquals(13, bp.get(0));
	}
	
	@Test
	void testReadSlotOverlapCase() {
		bp.compress(tab);
		
		assertEquals(560, bp.get(3));
	}

}
