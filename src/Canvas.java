import fill.ScanLineFiller;
import fill.SeedFiller;
import model.*;
import rasterize.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;


/**
 * trida pro kresleni na platno: zobrazeni pixelu
 * 
 * @author PGRF FIM UHK
 * @version 2023
 */

public class Canvas {

	private JFrame frame;
	private JPanel panel;
	private RasterBufferedImage raster;
	// RASTERIZERS
	private LineRasterizer lineRasterizer;
	private DottedLineRasterizer dottedLineRasterizer;
	private PolygonRasterizer polygonRasterizer;
	private int startClickX, startClickY, endClickX, endClickY;

	// DRAWN POLYGONS
	private List<Polygon> polygons = new ArrayList<>();
	// COLORED OBJECTS / SEED FILL
	private List<SeedFiller> coloredObjects = new ArrayList<>();
	private Polygon polygon;
	private boolean polygonMode = false;


	public Canvas(int width, int height) {
		frame = new JFrame();

		frame.setLayout(new BorderLayout());
		frame.setTitle("UHK FIM PGRF : " + this.getClass().getName());
		frame.setResizable(false);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		raster = new RasterBufferedImage(width, height);

		lineRasterizer = new LineRasterizerGraphics(raster);
		dottedLineRasterizer = new DottedLineRasterizer(raster, 10);
		polygonRasterizer = new PolygonRasterizer(lineRasterizer);

		polygon = new Polygon();

		panel = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				present(g);
			}
		};

		panel.setPreferredSize(new Dimension(width, height));
		panel.requestFocus();
		panel.requestFocusInWindow();

		frame.add(panel, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);
		frame.requestFocusInWindow();

		frame.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent keyEvent) {

			}

			@Override
			public void keyPressed(KeyEvent keyEvent) {

				if(keyEvent.getKeyCode() == KeyEvent.VK_CAPS_LOCK){
					if (polygonMode) {
						System.out.println("Caps pressed again... Polygon mode turned off");
						polygonMode = false;
						clear(0x000000);
						for (Polygon polygon : polygons) {
							polygonRasterizer.rasterize(polygon);
						}
						for (SeedFiller coloredObject : coloredObjects) {
							coloredObject.fill();
						}
						panel.repaint();
					} else {
						System.out.println("Caps pressed... Polygon mode");
						//clear polygon object
						polygon = new Polygon();
						polygonMode = true;
					}
				}

				// CLEAR CANVAS
				if(keyEvent.getKeyCode() == KeyEvent.VK_C){
					System.out.println("C pressed - CLEAR CANVAS");
					clear(0x000000);
					polygons.clear();
					coloredObjects.clear();
					polygon = new Polygon();
					panel.repaint();
				}

			}

			@Override
			public void keyReleased(KeyEvent keyEvent) {

			}
		});


		panel.addMouseMotionListener(new MouseMotionAdapter() {

			@Override
			public void mouseMoved(MouseEvent e) {
				if(polygon.getSize() > 1 && polygonMode) {
					clear(0x000000);

					//draw base of polygon
					lineRasterizer.rasterize(polygon.getPoint(0).x, polygon.getPoint(0).y, startClickX, startClickY, Color.YELLOW);

					//draw drawn polygons
					for (Polygon polygon : polygons) {
						polygonRasterizer.rasterize(polygon);
					}

					//draw help lines
					dottedLineRasterizer.rasterize(startClickX, startClickY,e.getX(), e.getY(), Color.YELLOW);
					dottedLineRasterizer.rasterize(polygon.getPoints().get(0).x, polygon.getPoints().get(0).y,e.getX(), e.getY(), Color.YELLOW);

					//draw colored objects
					for (SeedFiller coloredObject : coloredObjects) {
						coloredObject.fill();
					}
				}
				panel.repaint();

			}
		});

		panel.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {

				startClickX = e.getX();
				startClickY = e.getY();

				if(polygonMode){
					Point p = new Point(e.getX(), e.getY());
					polygon.addPoint(p);
					polygons.add(polygon);
				}

				polygonRasterizer.rasterize(polygon);

				if(e.getButton() == MouseEvent.BUTTON3){
					SeedFiller seedFiller = new SeedFiller(raster, raster.getPixel(e.getX(), e.getY()), e.getX(), e.getY());
					seedFiller.fill();
					System.out.println("Seed fill...");
					coloredObjects.add(seedFiller);
				}


				/*
				if(e.getButton() == MouseEvent.BUTTON3){
					ScanLineFiller scanLineFiller = new ScanLineFiller(lineRasterizer, polygonRasterizer, polygon);
					scanLineFiller.fill();
					System.out.println("scanline fill");
				}
				 */


				panel.repaint();


			}

			@Override
			public void mouseReleased(MouseEvent e) {
				endClickX = e.getX();
				endClickY = e.getY();
				clear(0x000000);

				for (Polygon polygon : polygons) {
					polygonRasterizer.rasterize(polygon);
				}

				for (SeedFiller coloredObject : coloredObjects) {
					coloredObject.fill();
				}

				panel.repaint();

			}
		});

	}

	public void clear(int color) {
		raster.setClearColor(color);
		raster.clear();
	}

	public void present(Graphics graphics) {
		raster.repaint(graphics);
	}

	public void start() {
		drawString(raster.getGraphics(), "", 575, 525);
		panel.repaint();
		clear(0x000000);
	}

	public void drawString(Graphics g, String text, int x, int y) {
		for (String line : text.split("\n")) {
			g.drawString(line, x, y += g.getFontMetrics().getHeight());
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new Canvas(800, 600).start());
	}

}