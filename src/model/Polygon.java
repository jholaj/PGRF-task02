package model;

import java.util.ArrayList;

public class Polygon {
    private ArrayList<Point> points;

    public Polygon() {
        points = new ArrayList<>();
    }

    public ArrayList<Point> getPoints() {
        return points;
    }

    public void addPoint(Point point) {
        points.add(point);
    }

    public int getSize() {
        return points.size();
    }

    public Point getPoint(int index) {
        return points.get(index);
    }
}
