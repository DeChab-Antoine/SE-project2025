package main.factory;

import main.api.IPacking;
import main.api.Mode;
import main.core.*;

public final class BitPackingFactory {
	
	private BitPackingFactory() {} // empêche l’instanciation
	
	public static IPacking create(Mode mode, int[] input) {
		switch(mode) {
			case OVERLAP:
				return new BitPackingOverlap(input);
			case WITHOUT_OVERLAP:
				return new BitPackingWithoutOverlap(input);
			case OVERFLOW_OVERLAP:
				return new BitPackingOverflow(Mode.OVERLAP, input);
			case OVERFLOW_WITHOUT_OVERLAP:
				return new BitPackingOverflow(Mode.WITHOUT_OVERLAP, input);
			case AUTO:
				// version naïve pour l'instant
				return new BitPackingOverflow(Mode.WITHOUT_OVERLAP, input);
			default:
				return null;
				
		}
		
	}
	
	
	public static IPacking createForOverflow(Mode mode, int length, int k) {
		switch(mode) {
			case OVERLAP:
				return new BitPackingOverlap(length, k);
			case WITHOUT_OVERLAP:
				return new BitPackingWithoutOverlap(length, k);
			default:
				// version naïve pour l'instant
				if (length < 1000) return new BitPackingWithoutOverlap(length, k);
				else return new BitPackingOverlap(length, k);
				
		}
		
	}
}
