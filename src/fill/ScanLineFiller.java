package fill;

import model.Point;
import model.Polygon;
import model.Edge;
import rasterize.LineRasterizer;
import rasterize.PolygonRasterizer;

import java.util.ArrayList;

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

            edges.add(edge);
        }

        // TODO: Najít yMin a yMax (projít vrcholy polygonu)
        // yMin, yMax, defaultně naplnit prvním pointem
        // TODO: for cyklus od yMin po yMax
        // {
        // Pro všechny hrany
        //  1. TODO: Zjistím jestli existuje průsečík
        //  2. TODO: Pokud existuje, tak ho spočítáme. Výsledek uložím.
        // }

        // TODO: Seřadit naležené průsečíky
        // TODO: Spojit lichý se sudým
        // TODO: Obtáhnu polygon


    }



}
