package Prototype;
public class Main {
    private static final int[] tab = {1, 3, 2, 6, 9, 4, 1, 8};
    private static final int k = 4; // bits par valeur

    static String bin32(int x) {
    	
        return String.format("%32s", Integer.toBinaryString(x)).replace(' ', '0').replaceAll("(.{4})", "$1 ");
    }

    public static void main(String[] args) {
        // Affiche chaque nb sur 32 bits
        for (int nb : tab) {
            System.out.println(nb + ": " + bin32(nb));
        }

        // PACK sur 32 bits (ici n*k = 8*4 = 32 => ça tient dans un seul int)
        int buffer = 0;
        final int mask = (1 << k) - 1;         // suppose 1 <= k <= 31
        for (int i = 0; i < tab.length; i++) {
            int v = tab[i] & mask;
            buffer |= v << (k * i);
        }

        System.out.println("buffer: " + bin32(buffer));

        // UNPACK 
        int tmp = buffer;
        while (tmp != 0) {
            int val = tmp & mask;
            System.out.println(val);
            tmp >>>= k; 
        }
    }
}
