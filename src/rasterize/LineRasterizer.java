package rasterize;

import model.Line;

public abstract class LineRasterizer {
    Raster raster;
    int color;

    public LineRasterizer(Raster raster, int color){
        this.raster = raster;
        this.color = color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void rasterize(Line line) {
        setColor(line.getColor());
        drawLine(line.getX1(), line.getY1(), line.getX2(), line.getY2());
    }

    public void rasterize(int x1, int y1, int x2, int y2, int color) {
        setColor(color);
        drawLine(x1, y1, x2, y2);
    }

    protected void drawLine(int x1, int y1, int x2, int y2) {

    }
}