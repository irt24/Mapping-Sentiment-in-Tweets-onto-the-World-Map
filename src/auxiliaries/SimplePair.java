package auxiliaries;

public class SimplePair<L extends Comparable<L>,R extends Comparable<R>> implements Comparable<SimplePair<L,R>>{
    private L left;
    private R right;
    public SimplePair(L left, R right) {
        this.left = left;
        this.right = right;
    }
    public L getLeft() {
        return left;
    }
    public R getRight() {
        return right;
    }
    public void setLeft(L left) {
        this.left = left;
    }
    public void setRight(R right) {
        this.right = right;
    }

    @Override
    public int compareTo(SimplePair<L, R> o) {
        int c1 = this.left.compareTo(o.left);
        return c1;
    }
}
