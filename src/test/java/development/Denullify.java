package development;

import com.cmdjojo.util.Stopwatch;

import java.util.ArrayList;
import java.util.Random;

public class Denullify {
    public static void main(String[] args) {
        System.out.println("Creating arrays...");
        Stopwatch sw = new Stopwatch();
        Random r = new Random();
        int l = 10000000;
        int sl = 30;
        String[] array1 = new String[l];
        for (int i = 0; i < l; i++) {
            if (r.nextBoolean()) array1[i] = generateString(r, sl);
            else array1[i] = null;
        }
        String[] array2 = array1.clone();

        System.out.println("De-nullifying with method 1");
        sw.set("m1 start");
        String[] dn1 = denullify1(array1);
        sw.set("m1 stop");
        System.out.println("De-nullified with method 1");

        System.out.println("De-nullifying with method 2");
        sw.set("m2 start");
        String[] dn2 = denullify2(array2);
        sw.set("m2 stop");
        System.out.println("De-nullified with method 2");

        System.out.println("Verifying...");
        if (verify(dn1)) System.out.println("Method 1 returned no null");
        else System.out.println("!!! Method 1 contained null(s)");
        if (verify(dn2)) System.out.println("Method 2 returned no null");
        else System.out.println("!!! Method 2 contained null(s)");
        if (dn1.length == dn2.length) System.out.println("Method 1 and method 2 contains the same amound of elements");
        else System.out.println("!!! Method 1 and method 2 differs in size");

        System.out.println("DONE!");
        System.out.println(sw.compareNextAll());
    }

    private static String generateString(Random r) {
        return generateString(r, 20);
    }

    private static String generateString(Random r, int l) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < l; i++) {
            s.append((char) r.nextInt(0xFFFF));
        }
        return s.toString();
    }

    private static String[] denullify1(String[] data) {
        int n = 0;
        for (String s : data) if (s != null) n++;
        String[] res = new String[n];
        int i2 = 0;
        for (int i = 0; i < data.length; i++) {
            if (data[i] != null) res[i2++] = data[i];
        }
        return res;
    }

    private static String[] denullify2(String[] data) {
        ArrayList<String> res = new ArrayList<>();
        for (String s : data) if (s != null) res.add(s);
        return res.toArray(new String[0]);
    }

    private static boolean verify(String[] a) {
        for (String s : a) if (s == null) return false;
        return true;
    }
}
