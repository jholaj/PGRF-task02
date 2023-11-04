package rasterize;

import model.Line;
import model.Point;
import model.Polygon;

public class PolygonRasterizer {
    private LineRasterizer lineRasterizer;

    public PolygonRasterizer(LineRasterizer lineRasterizer) {
        this.lineRasterizer = lineRasterizer;
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

            lineRasterizer.rasterize(new Line(pointA, pointB, 0xffff00));
        }
    }
}

