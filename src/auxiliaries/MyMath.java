package auxiliaries;

/**
 * Various mathematical functions that cannot be found in Java libraries.
 */
public class MyMath {

    public static long factorial(int n) {
        if (n < 0) return -1;
        if ((n == 0)||(n == 1)) return 1;
        long fact = 1;
        for (int i = 2; i <= n; i++) {
            fact *= i;
        }
        return fact;
    }
}
