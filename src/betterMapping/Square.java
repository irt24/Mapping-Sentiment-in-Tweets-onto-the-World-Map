package betterMapping;

public class Square {
    private int horizontalIndex;
    private int verticalIndex;
    private double edgeLength;
    private Point topLeft;
    private Point topRight;
    private Point bottomLeft;
    private Point bottomRight;
    private Point center;
    
    public Square(int horizontalIndex, int verticalIndex, double edgeLength) {
        this.horizontalIndex = horizontalIndex;
        this.verticalIndex = verticalIndex;
        this.edgeLength = edgeLength;
    }
    public int horizontalIndex() {
        return horizontalIndex;
    }
    public int verticalIndex() {
        return verticalIndex;
    }
    public double edgeLength() {
        return edgeLength;
    }
    public Point topLeft() {
        Point p = new Point(Map.startHorizontal+horizontalIndex*edgeLength,
                Map.endVertical-verticalIndex*edgeLength);
        return p;
    }
    public Point topRight() {
        Point p = new Point(Map.startHorizontal+(horizontalIndex+1)*edgeLength,
                Map.endVertical-verticalIndex*edgeLength);
        return p;
    }
    public Point bottomLeft() {
        Point p = new Point(Map.startHorizontal+horizontalIndex*edgeLength,
                Map.endVertical-(verticalIndex+1)*edgeLength);
        return p;
    }
    public Point bottomRight() {
        Point p = new Point(Map.startHorizontal+(horizontalIndex+1)*edgeLength,
                Map.endVertical-(verticalIndex+1)*edgeLength);
        return p;
    }
    public Point center(){
        Point p = new Point(topLeft().getX()+edgeLength/2,topLeft().getY()-edgeLength/2);
        return p;
    }
    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof Square))return false;
        Square other = (Square)o;
        if (this.horizontalIndex != other.horizontalIndex) return false;
        if (this.verticalIndex != other.verticalIndex) return false;
        if (this.edgeLength != other.edgeLength) return false;
        return true;
    }
}
