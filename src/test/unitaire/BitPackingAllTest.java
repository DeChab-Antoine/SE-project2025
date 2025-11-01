package test.unitaire;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import main.api.IPacking;
import main.api.Mode;
import main.factory.BitPackingFactory;

class BitPackingAllTest {

	int[] input = new int[1000];
	
	@BeforeEach
	void setUp() {
		Random rng = new Random();
		int k = rng.nextInt(4, 16); 
		for (int i = 0; i < 1000; i++) {
			input[i] = rng.nextInt(0, (int) Math.pow(2, k));
		}
	}
	
	
	@Test
	void testBitPackingWithoutOverlap() {
		IPacking bpWO = BitPackingFactory.create(Mode.WITHOUT_OVERLAP, input);
		bpWO.compress(input);
		int[] output = new int[1000];
		bpWO.decompress(output);
		assertArrayEquals(output, input);
	}
	
	
	@Test
	void testBitPackingOverlap() {
		IPacking bpO = BitPackingFactory.create(Mode.OVERLAP, input);
		bpO.compress(input);
		int[] output = new int[1000];
		bpO.decompress(output);
		assertArrayEquals(output, input);
	}
	
	
	@Test
	void testBitPackingOverflowWithoutOverlap() {
		IPacking bpOWO = BitPackingFactory.create(Mode.OVERFLOW_WITHOUT_OVERLAP, input);
		bpOWO.compress(input);
		int[] output = new int[1000];
		bpOWO.decompress(output);
		assertArrayEquals(output, input);
	}
	
	
	@Test
	void testBitPackingOverflowOverlap() {
		IPacking bpOO = BitPackingFactory.create(Mode.OVERFLOW_OVERLAP, input);
		bpOO.compress(input);
		int[] output = new int[1000];
		bpOO.decompress(output);
		assertArrayEquals(output, input);
	}

}
