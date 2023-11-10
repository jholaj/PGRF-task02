package model;

public class Edge {
    private int x1,y1,x2,y2;


    public Edge (int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    // pridat zkraceni o 1

    public boolean isHorizontal() {
        return y1 == y2;
    }


    public boolean isIntersection(int y) {
        return (y >= y1 && y <= y2);
    }

    public int calcIntersection(int y) {
        double k = ((double) (x2 - x1)) / ((y2 - y1));
        double q = x1 - k * y1;
        return (int) Math.round(k * y + q);
    }

    public void orientate(){
        // změna orientace
        if (y1 > y2) {
            int tempY = y1;
            y1 = y2;
            y2 = tempY;

            int tempX = x1;
            x1 = x2;
            x2 = tempX;
        }
    }

    public void shortenByOnePixel() {
        // minux 1px
        y2 = y2 - 1;
    }
}
