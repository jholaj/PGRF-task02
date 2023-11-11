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
	private double finalHeight, finalWidth;

	// DRAWN POLYGONS
	private List<Polygon> polygons = new ArrayList<>();
	// COLORED OBJECTS / SEED FILL / SCAN LINE
	private List<SeedFiller> seedFillObjects = new ArrayList<>();
	private List<ScanLineFiller> scanLinedObjects = new ArrayList<>();
	private Polygon polygon;
	private Polygon cutterGon; // here we go
	private Rectangle rectangle;
	private  Ellipse ellipse;
	private boolean polygonMode, rectangleMode, rectangleCreated, ellipseMode, ellipseCreated = false;


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
						// start
						polygon = new Polygon();
						polygonMode = true;
						// disable other modes
						rectangleMode = false;
						ellipseMode = false;
						System.out.println("Caps pressed... Polygon mode");
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
						System.out.println("R pressed... Rectangle mode");
						// start
						rectangleCreated = false;
						rectangleMode = true;
						// disable other modes
						polygonMode = false;
						ellipseMode = false;
					}
				}

				// ellipse mode
				if(keyEvent.getKeyCode() == KeyEvent.VK_E){
					if (ellipseMode) {
						System.out.println("E pressed again - Ellipse mode turned off");
						ellipseMode = false;
						clear(0x000000);
						processObjects(polygons, seedFillObjects, polygonRasterizer);
						panel.repaint();
					} else {
						System.out.println("E pressed... Ellipse mode");
						// start
						ellipseCreated = false;
						ellipseMode = true;
						// disable other modes
						polygonMode = false;
						rectangleMode = false;
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

				if(ellipseMode  && ellipseCreated){
					double width = Math.abs(e.getX() - startClickX);
					double height = Math.abs(e.getY() - startClickY);
					Ellipse tempEllipse = new Ellipse(new Point(startClickX, startClickY), width, height);
					tempEllipse.createEllipse();
					clear(0x000000);

					processObjects(polygons, seedFillObjects, polygonRasterizer);

					// min / max of ellipse
					Point[] minMax = findMinMaxOfEllipse(tempEllipse);

					rectangle = new Rectangle(minMax[0], minMax[1]);

					// help bounding rectangle
					// all quadrants
					dottedLineRasterizer.rasterize(minMax[0].x, minMax[0].y, minMax[1].x, minMax[0].y, Color.YELLOW); // top
					dottedLineRasterizer.rasterize(minMax[1].x, minMax[0].y, minMax[1].x, minMax[1].y, Color.YELLOW); // right
					dottedLineRasterizer.rasterize(minMax[1].x, minMax[1].y, minMax[0].x, minMax[1].y, Color.YELLOW); // bottom
					dottedLineRasterizer.rasterize(minMax[0].x, minMax[1].y, minMax[0].x, minMax[0].y, Color.YELLOW); // left

					// temporary ellipse
					for (int i = 0; i < tempEllipse.getSize() - 1; i++) {
						Point p1 = tempEllipse.getPoint(i);
						Point p2 = tempEllipse.getPoint(i + 1);
						dottedLineRasterizer.rasterize(p1.x, p1.y, p2.x, p2.y, Color.YELLOW);
					}

					// set final height & width from temp ellipse
					finalHeight = height;
					finalWidth = width;

				}

				panel.repaint();

			}
		});

		panel.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {

				startClickX = e.getX();
				startClickY = e.getY();

				// POLYGON

				if(polygonMode){
					Point p = new Point(e.getX(), e.getY());
					polygon.addPoint(p);
					polygons.add(polygon);
				}

				// RECTANGLE

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

				// ELLIPSE

				if(ellipseMode){
					Point p = new Point(e.getX(), e.getY());
					if(!ellipseCreated){
						ellipse = new Ellipse(p, 0, 0);
						ellipseCreated = true;
					} else {
						ellipse.createEllipse();
						ellipse = new Ellipse(ellipse.getPoint(0), finalWidth, finalHeight);
						polygons.add(ellipse);
						ellipseCreated = false;

						// bounding rectangle
						Rectangle boundingRectangle = new Rectangle(
								new Point(
										ellipse.getCenter().getX() - ellipse.getWidth(),
										ellipse.getCenter().getY() - ellipse.getHeight()
								),
								new Point(
										ellipse.getCenter().getX() + ellipse.getWidth(),
										ellipse.getCenter().getY() + ellipse.getHeight()
								)
						);

						polygons.add(boundingRectangle);
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
		// load polygons && rectangles && ellipses
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

	public Point[] findMinMaxOfEllipse(Ellipse ellipse) {
		int minX = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int minY = Integer.MAX_VALUE;
		int maxY = Integer.MIN_VALUE;

		for (int i = 0; i < ellipse.getSize() - 1; i++) {
			Point p = ellipse.getPoint(i);
			minX = Math.min(minX, p.x);
			maxX = Math.max(maxX, p.x);
			minY = Math.min(minY, p.y);
			maxY = Math.max(maxY, p.y);
		}

		return new Point[]{new Point(minX, minY), new Point(maxX, maxY)};
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new Canvas(800, 600).start());
	}

}