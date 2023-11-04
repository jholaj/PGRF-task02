package model;

import java.awt.*;

public class Line {

    private int  x1, x2, y1, y2;
    private final int color;

    public Line(int x1, int y1, int x2, int y2, int color) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.color = color;
    }

    public Line(Point p1, Point p2, int color) {
        this.x1 = p1.x;
        this.x2 = p2.x;
        this.y1 = p1.y;
        this.y2 = p2.y;
        this.color = color;
    }

    public int getX1() {
        return x1;
    }

    public int getX2() {
        return x2;
    }

    public int getY1() {
        return y1;
    }

    public int getY2() {
        return y2;
    }

    public void setX1(int x1) {
        this.x1 = x1;
    }

    public void setX2(int x2) {
        this.x2 = x2;
    }

    public void setY1(int y1) {
        this.y1 = y1;
    }

    public void setY2(int y2) {
        this.y2 = y2;
    }

    public int getColor() {
        return color;
    }

     public boolean FindClosePoint(int mouseX, int mouseY, Line line) {
        int threshold = 5;

        // PYTHAGORAS
        // D = √((x2 - x1)^2 + (y2 - y1)^2)
        double distance1 = Math.sqrt(Math.pow(mouseX - line.getX1(), 2) + Math.pow(mouseY - line.getY1(), 2));
        double distance2 = Math.sqrt(Math.pow(mouseX - line.getX2(), 2) + Math.pow(mouseY - line.getY2(), 2));

        return distance1 < threshold || distance2 < threshold;
    }

}
