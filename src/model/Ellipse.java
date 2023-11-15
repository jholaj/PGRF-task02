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

    public Polygon convertToPolygon() {
        Polygon polygon = new Polygon();
        for (Point point : getPoints()) {
            polygon.addPoint(point);
        }
        return polygon;
    }

    // func for bounding rectangle
    public Point[] findMinMaxOfEllipse() {
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (int i = 0; i < getSize() - 1; i++) {
            Point p = getPoint(i);
            minX = Math.min(minX, p.x);
            maxX = Math.max(maxX, p.x);
            minY = Math.min(minY, p.y);
            maxY = Math.max(maxY, p.y);
        }

        return new Point[]{new Point(minX, minY), new Point(maxX, maxY)};
    }
}
