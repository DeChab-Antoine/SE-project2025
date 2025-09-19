package bitPacking;

public class testBitPacking {

	public static void main(String[] args) {
		
		int[] tab1 = {0,0,0,0};
		int[] tab2 = {1,8,3,4,1,8,3,4,1,8,3,4,1,8,3,4};
		int[] tab3 = {1,2,Integer.MAX_VALUE,4};
		BitPackingWithoutOverlap e = new BitPackingWithoutOverlap();
		System.out.println("tab 1 : 1 / " + e.trouverK(tab1));
		System.out.println("tab 2 : 4 / " + e.trouverK(tab2));
		System.out.println("tab 3 : 31 / " + e.trouverK(tab3));
		
		
		for (int elem :e.compress(tab2)) {
			System.out.println(Integer.toBinaryString(elem));
		}
	}

}
