package fill;

import rasterize.Raster;

public class SeedFiller implements Filler {

    private Raster raster;

    private int x, y;
    private int backgroundColor;

    public SeedFiller(Raster raster, int backgroundColor, int x, int y) {
        this.raster = raster;
        this.backgroundColor = backgroundColor;
        this.x = x;
        this.y = y;
    }

    @Override
    public void fill(){
        seedFill(x, y);

    }

    private void seedFill(int x, int y) {
        //alg
        // 1. načtu barvu pixelu na souřadnici x, y
        int pixelColor = raster.getPixel(x, y);

        // 2. podmínka: pokud se barva pozadí rovná načtené => obarvím
        if(backgroundColor != pixelColor){
            return;
        } else {
            // 3. obarvím
            raster.setPixel(x, y, 0xff0000);
        }

        seedFill(x + 1, y);
        seedFill(x - 1,y);
        seedFill(x, y + 1);
        seedFill(x, y - 1);
        // 4. 4x zavolám seedFill (pro 4 sousedy)

    }

}
