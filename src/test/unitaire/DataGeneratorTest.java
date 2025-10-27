package test.unitaire;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import main.bench.DataGenerator;

class DataGeneratorTest {

	@Test
	void testBuildInput() {
		DataGenerator dataGen = new DataGenerator(42);
		List<Integer> bits = List.of(4, 10);
		List<Integer> percents = List.of(90, 10);
		int[] input = dataGen.buildInput(bits, percents, 100);
		
		int cptP = 0;
		int cptG = 0;
		for (int i = 0; i < input.length ; i ++) {
			int val = input[i];
			if (val < 16) cptP++;
			else cptG++;
		}
		
		assertEquals(10, cptG);
		assertEquals(90, cptP);
	}

}
