import fill.ScanLineFiller;
import fill.SeedFiller;
import fill.SeedFillerBorder;
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
	private int startClickX, startClickY;

	// DRAWN POLYGONS
	private List<Polygon> polygons = new ArrayList<>();
	// COLORED OBJECTS / SEED FILL
	private List<SeedFiller> seedFillObjects = new ArrayList<>();
	private List<ScanLineFiller> scanLinedObjects = new ArrayList<>();
	private Polygon polygon;
	private Rectangle rectangle;
	private boolean polygonMode, rectangleMode, rectangleCreated = false;


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
						processObjects(polygons, seedFillObjects, polygonRasterizer);
						panel.repaint();
					} else {
						rectangleMode = false;
						System.out.println("Caps pressed... Polygon mode");
						// reset
						polygon = new Polygon();
						polygonMode = true;
					}
				}

				// CLEAR CANVAS
				if(keyEvent.getKeyCode() == KeyEvent.VK_C){
					System.out.println("C pressed - CLEAR CANVAS");
					clear(0x000000);
					polygons.clear();
					seedFillObjects.clear();
					scanLinedObjects.clear();
					polygon = new Polygon();
					panel.repaint();
				}

				if(keyEvent.getKeyCode() == KeyEvent.VK_R){
					if (rectangleMode) {
						System.out.println("R pressed again... Rectangle mode turned off");
						rectangleMode = false;
						clear(0x000000);
						processObjects(polygons, seedFillObjects, polygonRasterizer);
						panel.repaint();
					} else {
						polygonMode = false;
						System.out.println("R pressed... Rectangle mode");
						rectangleCreated = false;
						rectangleMode = true;
					}
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

					// draw base of polygon
					lineRasterizer.rasterize(polygon.getPoint(0).x, polygon.getPoint(0).y, startClickX, startClickY, Color.YELLOW);

					// draw drawn objects
					processObjects(polygons, seedFillObjects, polygonRasterizer);

					// draw polygon help lines
					dottedLineRasterizer.rasterize(startClickX, startClickY,e.getX(), e.getY(), Color.YELLOW);
					dottedLineRasterizer.rasterize(polygon.getPoints().get(0).x, polygon.getPoints().get(0).y,e.getX(), e.getY(), Color.YELLOW);

				}

				if(rectangleMode) {
					clear(0x000000);

					// draw drawn objects
					processObjects(polygons, seedFillObjects, polygonRasterizer);


					// draw rectangle help lines
					if(rectangleCreated){
						dottedLineRasterizer.rasterize(rectangle.getPoint(0).x, rectangle.getPoint(0).y, e.getX(), rectangle.getPoint(0).y, Color.YELLOW); // top
						dottedLineRasterizer.rasterize(e.getX(), rectangle.getPoint(0).y, e.getX(), e.getY(), Color.YELLOW); // right
						dottedLineRasterizer.rasterize(e.getX(), e.getY(), rectangle.getPoint(0).x, e.getY(), Color.YELLOW); // bottom
						dottedLineRasterizer.rasterize(rectangle.getPoint(0).x, e.getY(), rectangle.getPoint(0).x, rectangle.getPoint(0).y, Color.YELLOW); // left
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

				if(rectangleMode){
					Point p = new Point(e.getX(), e.getY());
					if (!rectangleCreated) {
						// start a new rectangle with the first point
						rectangle = new Rectangle(p, p);
						rectangleCreated = true;
					} else {
						// finish the current rectangle
						rectangle = new Rectangle(rectangle.getPoint(0), p);
						polygons.add(rectangle);
						rectangleCreated = false;
					}

				}

				processObjects(polygons, seedFillObjects, polygonRasterizer);

				/*
				if(e.getButton() == MouseEvent.BUTTON3){
					SeedFiller seedFiller = new SeedFiller(raster, raster.getPixel(e.getX(), e.getY()), e.getX(), e.getY());
					seedFiller.fill();
					System.out.println("Seed fill...");
					coloredObjects.add(seedFiller);
				}

				 */


				if(e.getButton() == MouseEvent.BUTTON3){
					ScanLineFiller scanLineFiller = new ScanLineFiller(lineRasterizer, polygon);
					scanLineFiller.fill();
					scanLinedObjects.add(scanLineFiller);
					System.out.println("Scanline fill...");
				}


				/*
				if(e.getButton() == MouseEvent.BUTTON3){
					//SeedFillerBorder seedFillerBorder = new SeedFillerBorder(raster, );
					//seedFillerBorder.fill();
					System.out.println("Seed fill border...");
					//coloredObjects.add(seedFillerBorder);
				}

				 */

				panel.repaint();


			}

			@Override
			public void mouseReleased(MouseEvent e) {
				clear(0x000000);

				processObjects(polygons, seedFillObjects, polygonRasterizer);

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

	public void processObjects(List<Polygon> polygons, List<SeedFiller> seedFillObjects, PolygonRasterizer polygonRasterizer) {
		// load polygons && rectangles
		for (Polygon polygon : polygons) {
			polygonRasterizer.rasterize(polygon);
		}

		// load seed filled objects
		for (SeedFiller coloredObject : seedFillObjects) {
			coloredObject.fill();
		}

		// load scanline filled objects
		for (ScanLineFiller coloredObject : scanLinedObjects) {
			coloredObject.fill();
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new Canvas(800, 600).start());
	}

}