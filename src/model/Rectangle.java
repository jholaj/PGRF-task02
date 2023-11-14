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

    public Point getP1() {
        return p1;
    }

    public Point getP3() {
        return p3;
    }

    public Polygon convertToPolygon(Rectangle rectangle) {
        Polygon polygon = new Polygon();

        polygon.addPoint(rectangle.getP1());
        polygon.addPoint(new Point(rectangle.getP1().getX(), rectangle.getP3().getY()));
        polygon.addPoint(rectangle.getP3());
        polygon.addPoint(new Point(rectangle.getP3().getX(), rectangle.getP1().getY()));

        return polygon;
    }
}
