package bitPacking;

public class BitPackingOverlap extends BitPacking {

	protected BitPackingOverlap(int[] tabInput) {
		super(computeK(tabInput), tabInput.length);
	}

	@Override
	protected void createTabWords() {
		long totalBits = (long) k * (long) tabInputLength; // calcule en 64 bits (long) pour éviter un overflow si tabInputLength proche de IntegerMaxValue
		long tabWordsLength = (totalBits + 31L) >>> 5;
		
		this.tabWords = new int[(int) tabWordsLength];
	}

	@Override
	protected void writeValue(int i, int val) {
		
	}

	@Override
	protected int readValue(int i) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
