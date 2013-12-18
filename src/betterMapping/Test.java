package betterMapping;

/**
 * Tests for this package
 */
public class Test {
    public static void main(String[] args) {
        Square square1 = new Square(1,2,3);
        Square square2 = new Square(1,2,3);
        if (square1.equals(square2)) System.out.println("They are equal!");
        else System.out.println("They are not equal!");
    }
}
