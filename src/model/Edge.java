package model;

public class Edge {
    private int x1,y1,x2,y2;


    public Edge (int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y2;
        this.x2 = x2;
        this.y2 = y2;
    }

    public boolean isHorizontal() {
        return y1 == y2;
    }

    public void orientate(){
        //TODO: změna orientace
        int temp = 0;
        y1 = temp;
        y2 = y1;
        y1 = temp;
    }

}
