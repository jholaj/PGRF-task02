package fill;

import model.Line;
import model.Point;
import model.Polygon;
import model.Edge;
import rasterize.LineRasterizer;
import rasterize.PolygonRasterizer;

import java.util.ArrayList;
import java.util.List;

public class ScanLineFiller implements Filler {
    private PolygonRasterizer polygonRasterizer;
    private LineRasterizer lineRasterizer;

    private Polygon polygon;


    public ScanLineFiller(LineRasterizer lineRasterizer, PolygonRasterizer polygonRasterizer, Polygon polygon){
        this.lineRasterizer = lineRasterizer;
        this.polygonRasterizer = polygonRasterizer;
        this.polygon = polygon;
    }

    @Override
    public void fill(){
        scanFill(polygon);

    }

    public void scanFill(Polygon polygon){

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
            // přidání hrany do seznamu
            edges.add(edge);
        }

        // TODO: Najít yMin a yMax (projít vrcholy polygonu)
        // yMin, yMax, defaultně naplnit prvním pointem
        int yMin = polygon.getPoint(0).y;
        int yMax = polygon.getPoint(0).y;

        // get yMin yMax
        for (int i = 0; i < polygon.getSize() - 1;i++) {
            if(yMin > polygon.getPoint(i).y){
                yMin = polygon.getPoint(i).y;
            }
            if(yMax < polygon.getPoint(i).y){
                yMax = polygon.getPoint(i).y;
            }
        }

        System.out.println(yMax);
        System.out.println(yMin);

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
                    //System.out.println(intersect);
                    intersections.add(intersect);
                }
            }

            // TODO: Seřadit naležené průsečíky
            intersections = BubbleSort(intersections);
            System.out.println(intersections);

            // TODO: Spojit lichý se sudým
            for (int j = 0; j < intersections.size(); j = j + 2) {
                if (intersections.size() > j + 1) {
                    lineRasterizer.rasterize(new Line(new Point(intersections.get(j), i), new Point(intersections.get(j + 1), i), 0xff0000));
                }
            }
        }

        // TODO: Obtáhnu polygon
        // na to nemam naladu

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
