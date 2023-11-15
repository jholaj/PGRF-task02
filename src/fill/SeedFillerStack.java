package fill;

import model.Point;
import rasterize.Raster;

import java.util.Stack;

public class SeedFillerStack implements Filler {

    private Raster raster;

    private int x, y;
    private int backgroundColor;

    public SeedFillerStack(Raster raster, int backgroundColor, int x, int y) {
        this.raster = raster;
        this.backgroundColor = backgroundColor;
        this.x = x;
        this.y = y;
    }

    @Override
    public void fill(int color){
        Stack<Point> stack = new Stack<>();
        // přidání počatečního bodu do zásobníku
        stack.push(new Point(x, y));

        try {
            while (!stack.isEmpty()) {
                // vyjsmutí bodu ze zásobníku
                Point p = stack.pop();
                if (raster.getPixel(p.x, p.y) == backgroundColor) {
                    raster.setPixel(p.x, p.y, color);

                    // sousední body do zásobníku pro další vyplňování
                    stack.push(new Point(p.x + 1, p.y));
                    stack.push(new Point(p.x - 1, p.y));
                    stack.push(new Point(p.x, p.y + 1));
                    stack.push(new Point(p.x, p.y - 1));
                }
            }
        } catch (OutOfMemoryError e) {
            System.err.println("Vyčerpání paměti: " + e.getMessage());
        }
    }
}