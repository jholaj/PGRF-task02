package rasterize;

import model.Line;
import model.Point;
import model.Polygon;

public class PolygonRasterizer {
    private LineRasterizer lineRasterizer;
    private int color;

    public PolygonRasterizer(LineRasterizer lineRasterizer, int color) {
        this.lineRasterizer = lineRasterizer;
        this.color = color;
    }

    public void rasterize(Polygon polygon) {

        if (polygon.getSize() < 3)
            return;

        for (int i = 0; i < polygon.getSize(); i++) {
            int indexA = i;
            int indexB = i + 1;
            if(i == polygon.getSize() - 1)
                indexB = 0;

            Point pointA = polygon.getPoint(indexA);
            Point pointB = polygon.getPoint(indexB);

            lineRasterizer.rasterize(new Line(pointA, pointB, color));
        }
    }
}

