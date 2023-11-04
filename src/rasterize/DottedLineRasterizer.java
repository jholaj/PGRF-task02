package rasterize;

public class DottedLineRasterizer extends LineRasterizer {
    private int gap;
    public DottedLineRasterizer(Raster raster, int gap) {
        super(raster);
        this.gap = gap;
    }

    protected void drawLine(int x1, int y1, int x2, int y2) {
        calcMidpointWithGap(x1, y1, x2, y2);
    }

    /**
     *
     * Algoritmus: Midpoint (Využívá půlení úseček)
     * Výhody: Jednoduchá implementace, funkční pro všechny kvadranty
     * Nevýhody: Rekurze (lze vyřešit zásobníkem)
     *
     * */
    private void calcMidpointWithGap(int x1, int y1, int x2, int y2) {
        int sx = (x1 + x2) / 2;
        int sy = (y1 + y2) / 2;

        raster.setPixel(sx, sy, 0xffff00);

        if (Math.abs(x1 - sx) > gap || Math.abs(y1 - sy) > gap) {
            drawLine(x1, y1, sx, sy);
        }

        if (Math.abs(x2 - sx) > gap || Math.abs(y2 - sy) > gap) {
            drawLine(sx, sy, x2, y2);
        }
    }
}
