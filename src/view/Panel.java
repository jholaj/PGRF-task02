package view;

import rasterize.Raster;
import rasterize.RasterBufferedImage;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class Panel extends JPanel {

    private RasterBufferedImage raster;

    public Raster getRaster() {
        return raster;
    }

    private static final int FPS = 1000 / 20;
    public static final int WIDTH = 800, HEIGHT = 600;

    Panel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        raster = new RasterBufferedImage(WIDTH, HEIGHT);
        raster.setClearColor(Color.BLACK.getRGB());
        setLoop();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        raster.repaint(g);
        // pro zájemce - co dělá observer - https://stackoverflow.com/a/1684476
    }

    public void resize(){
        if (this.getWidth()<1 || this.getHeight()<1)
            return;
        if (this.getWidth()<=raster.getWidth() && this.getHeight()<=raster.getHeight()) //no resize if new is smaller
            return;
        RasterBufferedImage newRaster = new RasterBufferedImage(this.getWidth(), this.getHeight());

        newRaster.draw(raster);
        raster = newRaster;
    }

    private void setLoop() {
        // časovač, který 30 krát za vteřinu obnoví obsah plátna aktuálním img
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                repaint();
            }
        }, 0, FPS);
    }

    public void clear() {
        raster.clear();
    }
}
