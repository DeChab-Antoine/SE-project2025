package bitPacking;

public final class BitPackingFactory {
	
	public static BitPacking create(Mode mode, int[] inputData) {
		switch(mode) {
			case OVERLAP:
				return new BitPackingOverlap(inputData);
			case WITHOUT_OVERLAP:
				return new BitPackingWithoutOverlap(inputData);
			default:
				// version naïve pour l'instant
				if (inputData.length < 1000) return new BitPackingWithoutOverlap(inputData);
				else return new BitPackingOverlap(inputData);
				
		}
		
	}
}
