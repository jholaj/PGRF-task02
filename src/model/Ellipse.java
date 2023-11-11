package model;

public class Ellipse extends Polygon {
    private Point center;
    private double width, height;
    private final int points = 50;

    public Ellipse(Point center, double width, double height) {
        this.center = center;
        this.width = width;
        this.height = height;

        createEllipse();
    }

    public Point getCenter() {
        return center;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public void createEllipse() {
        for (int i = 0; i < points; i++) {
            double angle = 2.0 * Math.PI * i / points;
            double dx = center.getX() + width * Math.cos(angle);
            double dy = center.getY() + height * Math.sin(angle);
            addPoint(new Point(dx, dy));
        }
    }
}
