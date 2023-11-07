package model;

public class Rectangle extends Polygon {
    private Point p1,p3;
    public Rectangle (Point p1, Point p3) {
        this.p1 = p1;
        this.p3 = p3;

        addPoint(p1);
        addPoint(new Point(p1.getX(), p3.getY()));
        addPoint(p3);
        addPoint(new Point(p3.getX(), p1.getY()));
    }
}
