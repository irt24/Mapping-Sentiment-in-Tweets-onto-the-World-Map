package betterMapping;

public class Point {
    private double x;
    private double y;
    
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof Point))return false;
        Point otherPoint = (Point)o;
        if ((this.x == otherPoint.x)&&(this.y == otherPoint.y)) return true;
        return false;
    }
}
