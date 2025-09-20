package bitPacking;

import java.util.Arrays;
import java.util.Random;

public class testBitPacking {

    private static String toBin32(int x) {
        String s = Integer.toBinaryString(x);
        if (s.length() < 32) s = "0".repeat(32 - s.length()) + s;
        return s;
    }

    private static void runCase(String name, int[] input) {
        System.out.println("\n================ " + name + " ================");
        System.out.println("Input (" + input.length + "): " + Arrays.toString(input));

        BitPackingWithoutOverlap bp = new BitPackingWithoutOverlap(input);
        bp.compress();

        // Hypothèse: getters présents (à ajouter si absents)
        int k = bp.getK();
        int c = bp.getC();
        int n = bp.getN_tab();
        int[] words = bp.getOut();

        System.out.println("k (bits/valeur) = " + k);
        System.out.println("c (valeurs par mot 32b) = " + c);
        System.out.println("originalLength = " + n);
        System.out.println("wordsCount = " + words.length);

        // Affichage des mots compressés en binaire
        for (int w = 0; w < words.length; w++) {
            System.out.println("words[" + w + "] = " + toBin32(words[w]));
        }

        // Vérif GET sur quelques indices
        if (n > 0) {
            int[] probes = (n <= 5) ? new int[]{0, n - 1, Math.max(0, n / 2)} : new int[]{0, 1, n / 2, n - 2, n - 1};
            System.out.print("Probes get(i): ");
            for (int i : probes) {
                int v = bp.get(i);
                System.out.print(" get(" + i + ")=" + v + ";");
            }
            System.out.println();
        }

        // Décompression & comparaison
        int[] recon = bp.decompress();
        boolean ok = Arrays.equals(input, recon);
        System.out.println("Decompress equals input? " + ok);
        if (!ok) {
            System.out.println("Reconstructed: " + Arrays.toString(recon));
        }
    }

    public static void main(String[] args) {
        // Cas 1: tout zéros → k=1, c=32, un mot suffit si n<=32
        int[] tab1 = {0, 0, 0, 0};
        runCase("CASE 1: all zeros", tab1);

        // Cas 2: motif (k=4 attendu) – tes anciennes données
        int[] tab2 = {1,8,3,4, 1,8,3,4, 1,8,3,4, 1,8,3,4};
        runCase("CASE 2: pattern k=4", tab2);

        // Cas 3: grandes valeurs (k=31 attendu avec Integer.MAX_VALUE)
        int[] tab3 = {1, 2, Integer.MAX_VALUE, 4};
        runCase("CASE 3: includes MAX_INT (k≈31)", tab3);

        // Cas 4: aléatoire borné (k<=12) – vérifie robustesse et placement
        Random rnd = new Random(42);
        int[] tab4 = new int[34];  // exprès > 1 mot
        for (int i = 0; i < tab4.length; i++) tab4[i] = rnd.nextInt(1 << 12); // [0..4095]
        runCase("CASE 4: random values <= 4095", tab4);

        // Cas 5 (option): erreur attendue si négatifs (selon contrat du prototype)
        int[] tab5 = {0, -1, 3};
        try {
            runCase("CASE 5: negatives (should throw)", tab5);
        } catch (IllegalArgumentException ex) {
            System.out.println("\nCASE 5: OK caught expected IllegalArgumentException: " + ex.getMessage());
        }
    }
}
