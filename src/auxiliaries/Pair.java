package auxiliaries;

public class Pair<L extends Comparable<L>,R extends Comparable<R>> implements Comparable<Pair<L,R>>{
    private L left;
    private R right;
    public Pair(L left, R right) {
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
    public int compareTo(Pair<L, R> o) {
        int c1 = this.left.compareTo(o.left);
        if (c1 != 0) return c1;
        int c2 = this.right.compareTo(o.right);
        return c2;
    }
}
