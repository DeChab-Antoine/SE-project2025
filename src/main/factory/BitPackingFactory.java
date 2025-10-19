package main.factory;

import main.core.*;
import main.model.Mode;

public final class BitPackingFactory {
	
	public static BitPacking create(Mode mode, int length, int k) {
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
