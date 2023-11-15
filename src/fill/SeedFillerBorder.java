package fill;

import rasterize.Raster;

public class SeedFillerBorder implements Filler {

    private Raster raster;
    private int x, y;
    private int barvaHranice;

    public SeedFillerBorder(Raster raster, int barvaHranice, int x, int y) {
        this.raster = raster;
        this.barvaHranice = barvaHranice;
        this.x = x;
        this.y = y;
    }

    @Override
    public void fill(int color) {
        if (barvaHranice != color) {
            seedFillBorder(x, y, color);
        }
    }

    private void seedFillBorder(int x, int y, int color) {
        //alg
        // 1. načtu barvu pixelu na souřadnici x, y
        if (x < 0 || x >= raster.getWidth() || y < 0 || y >= raster.getHeight()) {
            return;
        }
        int pixelColor = raster.getPixel(x, y) & 0xFFFFFF;

        // 2. podmínka: pokud se barva hranice a barva výplně rovná načtené => neobarvuji
        if (pixelColor == color || pixelColor == barvaHranice) {
            return;
        } else {
            // 3. obarvím
            raster.setPixel(x, y, color);
        }

        seedFillBorder(x + 1, y, color);
        seedFillBorder(x - 1, y, color);
        seedFillBorder(x, y + 1, color);
        seedFillBorder(x, y - 1, color);
        // 4. 4x zavolám seedFill (pro 4 sousedy)
        }
    }