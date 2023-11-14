package fill;

import model.*;
import model.Point;
import model.Polygon;
import rasterize.LineRasterizer;
import java.util.ArrayList;
import java.util.List;

public class ScanLineFiller implements Filler {
    private LineRasterizer lineRasterizer;
    private Polygon polygon;
    private int outlineColor;


    public ScanLineFiller(LineRasterizer lineRasterizer, Polygon polygon, int outlineColor){
        this.lineRasterizer = lineRasterizer;
        this.polygon = polygon;
        this.outlineColor = outlineColor;
    }

    @Override
    public void fill(int color){
        scanFill(polygon, color);
    }

    public void scanFill(Polygon polygon, int color){

        ArrayList<Edge> edges = new ArrayList<>();

        for(int i = 0; i < polygon.getSize(); i++){
           Point p1 = polygon.getPoint(i);
           int indexB = i + 1;
            if (indexB == polygon.getSize()) {
                indexB = 0;
            }

            Point p2 = polygon.getPoint(indexB);

            Edge edge = new Edge(p1.x,p1.y,p2.x,p2.y);
            if(edge.isHorizontal())
                continue;
            
            // změna orientace
            edge.orientate();
            // zkraceni o 1px
            edge.shortenByOnePixel();
            // přidání hrany do seznamu
            edges.add(edge);
        }

        // TODO: Najít yMin a yMax (projít vrcholy polygonu)
        // yMin, yMax, defaultně naplnit prvním pointem
        int yMin = polygon.getPoint(0).y;
        int yMax = polygon.getPoint(0).y;

        // get yMin yMax
        for (int i = 0; i < polygon.getSize();i++) {
            if(yMin > polygon.getPoint(i).y){
                yMin = polygon.getPoint(i).y;
            }
            if(yMax < polygon.getPoint(i).y){
                yMax = polygon.getPoint(i).y;
            }
        }

        // TODO: for cyklus od yMin po yMax
        // {
        // Pro všechny hrany
        //  1. TODO: Zjistím jestli existuje průsečík
        //  2. TODO: Pokud existuje, tak ho spočítáme. Výsledek uložím.
        // }

        List<Integer> intersections = new ArrayList<>();

        for(int i = yMin; i <= yMax; i++){
            intersections.clear();

            for (Edge edge : edges){
                if (edge.isIntersection(i)) {
                    int intersect = edge.calcIntersection(i);
                    intersections.add(intersect);
                }
            }

            // TODO: Seřadit naležené průsečíky
            intersections = BubbleSort(intersections);

            // TODO: Spojit lichý se sudým
            for (int j = 0; j < intersections.size(); j = j + 2) {
                if (intersections.size() > j + 1) {
                    lineRasterizer.rasterize(new Line(new Point(intersections.get(j), i), new Point(intersections.get(j + 1), i), color));
                }
            }
        }

        // TODO: Obtáhnu polygon
        for(int i = 0; i < polygon.getSize(); i++){
            Point p1 = polygon.getPoint(i);
            int indexB = i + 1;
            if (indexB == polygon.getSize()) {
                indexB = 0;
            }

            Point p2 = polygon.getPoint(indexB);

            lineRasterizer.rasterize(new Line(p1, p2, outlineColor)); // obtáhnutí polygonu
        }

    }

    private List<Integer> BubbleSort(List<Integer> intersections){
        for (int j = 0; j < intersections.size()-1; j++) {
            for (int k = 0; k < intersections.size()-j-1; k++) {
                if (intersections.get(k) > intersections.get(k+1)) {
                    //swap
                    int temp = intersections.get(k);
                    intersections.set(k, intersections.get(k+1));
                    intersections.set(k+1, temp);
                }
            }
        }
        return intersections;
    }

}
