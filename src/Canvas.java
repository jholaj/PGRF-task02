import fill.ScanLineFiller;
import fill.SeedFiller;
import fill.SeedFillerBorder;
import fill.SeedFillerStack;
import model.*;
import rasterize.*;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	private PolygonRasterizer borderRasterizer;
	private int startClickX, startClickY;
	private double finalHeight, finalWidth;

	// DRAWN POLYGONS
	private List<Polygon> polygons = new ArrayList<>();
	private List<Polygon> bordergons = new ArrayList<>();

	// COLORED OBJECTS / SEED FILL / SCAN LINE
	private List<SeedFiller> seedFillObjects = new ArrayList<>();
	private List<ScanLineFiller> scanLinedObjects = new ArrayList<>();
	private List<SeedFillerStack> seedFillStackObjects = new ArrayList<>();
	private List<SeedFillerBorder> seedFillBorderObjects = new ArrayList<>();

	private Polygon polygon;
	private Polygon borderGon;
	private Rectangle rectangle;
	private Ellipse ellipse;
	private boolean polygonMode, rectangleMode, rectangleCreated, ellipseMode, ellipseCreated, borderMode = false;
	// fills
	String[] fillModeNames = {"Seed Fill (Blue)", "Scanline Fill (Red)", "Seed Fill Stack (Green)", "Seed Fill Border (?)"};
	Set<String> activeModes = new HashSet<>();
	int fillMode = 0;
	int outlineColor = 0xf0f0f0f0;
	int borderOutlineColor = 0x8B0000;


	public Canvas(int width, int height) {
		frame = new JFrame();

		frame.setLayout(new BorderLayout());
		frame.setTitle("UHK FIM PGRF : " + this.getClass().getName());
		frame.setResizable(false);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		raster = new RasterBufferedImage(width, height);

		lineRasterizer = new LineRasterizerGraphics(raster, outlineColor);
		dottedLineRasterizer = new DottedLineRasterizer(raster, 10, outlineColor);
		polygonRasterizer = new PolygonRasterizer(lineRasterizer, outlineColor);
		borderRasterizer = new PolygonRasterizer(lineRasterizer, borderOutlineColor); // diff color

		polygon = new Polygon();
		borderGon = new Polygon();

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

				// POLYGON

				if(keyEvent.getKeyCode() == KeyEvent.VK_CAPS_LOCK){
					if (polygonMode) {
						System.out.println("Caps pressed again... Polygon mode turned off");
						polygonMode = false;
						clear(0x000000);
						processObjects();
						panel.repaint();
					} else {
						// start
						polygon = new Polygon();
						polygonMode = true;
						// disable other modes
						rectangleMode = false;
						ellipseMode = false;
						borderMode = false;
						clear(0x000000);
						processObjects();
						System.out.println("Caps pressed... Polygon mode");
					}
				}

				// CLEAR CANVAS

				if(keyEvent.getKeyCode() == KeyEvent.VK_C){
					System.out.println("C pressed - CLEAR CANVAS");
					clear(0x000000);
					drawString(raster.getGraphics(), "POLYGON MODE - CAPSLOCK\nRECTANGLE MODE - R\nELLIPSE MODE - E\nCHANGE FILL MODE - Q\nCLEAR CANVAS - C", 590, 500);
					drawString(raster.getGraphics(), "ACTIVE MOD & FILL: " + checkActiveModes(fillModeNames),0,0);
					polygons.clear();
					bordergons.clear();
					seedFillObjects.clear();
					seedFillStackObjects.clear();
					scanLinedObjects.clear();
					polygon = new Polygon();
					panel.repaint();
				}

				// RECTANGLES

				if(keyEvent.getKeyCode() == KeyEvent.VK_R){
					if (rectangleMode) {
						System.out.println("R pressed again... Rectangle mode turned off");
						rectangleMode = false;
						clear(0x000000);
						processObjects();
						panel.repaint();
					} else {
						System.out.println("R pressed... Rectangle mode");
						// start
						rectangleCreated = false;
						rectangleMode = true;
						// disable other modes
						polygonMode = false;
						ellipseMode = false;
						borderMode = false;
						clear(0x000000);
						processObjects();
					}
				}

				// ELLIPSE

				if(keyEvent.getKeyCode() == KeyEvent.VK_E){
					if (ellipseMode) {
						System.out.println("E pressed again - Ellipse mode turned off");
						ellipseMode = false;
						clear(0x000000);
						processObjects();
						panel.repaint();
					} else {
						System.out.println("E pressed... Ellipse mode");
						// start
						ellipseCreated = false;
						ellipseMode = true;
						// disable other modes
						polygonMode = false;
						rectangleMode = false;
						borderMode = false;
						clear(0x000000);
						processObjects();
					}
				}

				// CHANGE FILL (SEED -> SCAN)

				if(keyEvent.getKeyCode() == KeyEvent.VK_Q){
					fillMode = (fillMode + 1) % fillModeNames.length; // platne indexy
					System.out.println(fillModeNames[fillMode]);
					clear(0x000000);
					processObjects();
				}

				// CLIPPER

				if(keyEvent.getKeyCode() == KeyEvent.VK_X){
					if(borderMode){
						System.out.println("Bordergon mode turned off...");
						borderMode = false;
						clear(0x000000);
						processObjects();
						panel.repaint();
					} else {
						System.out.println("Border");
						borderGon = new Polygon();
						borderMode = true;
						polygonMode = false;
						rectangleMode = false;
						ellipseMode = false;
						clear(0x000000);
						processObjects();
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
					lineRasterizer.rasterize(polygon.getPoint(0).x, polygon.getPoint(0).y, startClickX, startClickY, outlineColor);

					// draw drawn objects
					processObjects();

					// draw polygon help lines
					dottedLineRasterizer.rasterize(startClickX, startClickY,e.getX(), e.getY(), outlineColor);
					dottedLineRasterizer.rasterize(polygon.getPoints().get(0).x, polygon.getPoints().get(0).y,e.getX(), e.getY(), outlineColor);

				}

				if(borderGon.getSize() > 1 && borderMode) {
					clear(0x000000);

					// draw base of bordergon
					lineRasterizer.rasterize(borderGon.getPoint(0).x, borderGon.getPoint(0).y, startClickX, startClickY, borderOutlineColor);

					// draw drawn objects
					processObjects();

					// draw polygon help lines
					dottedLineRasterizer.rasterize(startClickX, startClickY,e.getX(), e.getY(), borderOutlineColor);
					dottedLineRasterizer.rasterize(borderGon.getPoints().get(0).x, borderGon.getPoints().get(0).y,e.getX(), e.getY(), borderOutlineColor);

				}

				if(rectangleMode) {
					clear(0x000000);

					// draw drawn objects
					processObjects();

					// draw rectangle help lines
					if(rectangleCreated){
						dottedLineRasterizer.rasterize(rectangle.getPoint(0).x, rectangle.getPoint(0).y, e.getX(), rectangle.getPoint(0).y, outlineColor); // top
						dottedLineRasterizer.rasterize(e.getX(), rectangle.getPoint(0).y, e.getX(), e.getY(), outlineColor); // right
						dottedLineRasterizer.rasterize(e.getX(), e.getY(), rectangle.getPoint(0).x, e.getY(), outlineColor); // bottom
						dottedLineRasterizer.rasterize(rectangle.getPoint(0).x, e.getY(), rectangle.getPoint(0).x, rectangle.getPoint(0).y, outlineColor); // left
					}
				}

				if(ellipseMode  && ellipseCreated){
					double width = Math.abs(e.getX() - startClickX);
					double height = Math.abs(e.getY() - startClickY);
					Ellipse tempEllipse = new Ellipse(new Point(startClickX, startClickY), width, height);
					tempEllipse.createEllipse();
					clear(0x000000);

					processObjects();

					// min / max of ellipse
					Point[] minMax = findMinMaxOfEllipse(tempEllipse);

					rectangle = new Rectangle(minMax[0], minMax[1]);

					// help bounding rectangle
					// all quadrants
					dottedLineRasterizer.rasterize(minMax[0].x, minMax[0].y, minMax[1].x, minMax[0].y, outlineColor); // top
					dottedLineRasterizer.rasterize(minMax[1].x, minMax[0].y, minMax[1].x, minMax[1].y, outlineColor); // right
					dottedLineRasterizer.rasterize(minMax[1].x, minMax[1].y, minMax[0].x, minMax[1].y, outlineColor); // bottom
					dottedLineRasterizer.rasterize(minMax[0].x, minMax[1].y, minMax[0].x, minMax[0].y, outlineColor); // left

					// temporary ellipse
					for (int i = 0; i < tempEllipse.getSize() - 1; i++) {
						Point p1 = tempEllipse.getPoint(i);
						Point p2 = tempEllipse.getPoint(i + 1);
						dottedLineRasterizer.rasterize(p1.x, p1.y, p2.x, p2.y, outlineColor);
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
						polygon = rectangle.convertToPolygon(rectangle); // convert for scanline
						polygons.add(polygon);
						rectangleCreated = false;
						rectangleMode = false;
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
						polygon = ellipse.convertToPolygon(); // convert for scanline
						polygons.add(polygon);
						ellipseCreated = false;
						ellipseMode = false;

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

				// BORDER MODE

				if(borderMode){
					Point p = new Point(e.getX(), e.getY());
					borderGon.addPoint(p);
					bordergons.add(borderGon);
				}

				if(fillMode == 0){
					if(e.getButton() == MouseEvent.BUTTON3){
						SeedFiller seedFiller = new SeedFiller(raster, raster.getPixel(e.getX(), e.getY()), e.getX(), e.getY());
						seedFiller.fill(0x0000FF);
						seedFillObjects.add(seedFiller);
						System.out.println("Seed fill... // BLUE");
					}
				}

				if(fillMode == 1){
					if(e.getButton() == MouseEvent.BUTTON3){
						ScanLineFiller scanLineFiller = new ScanLineFiller(lineRasterizer, polygon, outlineColor);
						scanLineFiller.fill(0xFF0000);
						scanLinedObjects.add(scanLineFiller);
						System.out.println("Scanline fill... // RED");
					}
				}

				if(fillMode == 2){
					if(e.getButton() == MouseEvent.BUTTON3){
						SeedFillerStack seedFillerStack = new SeedFillerStack(raster, raster.getPixel(e.getX(), e.getY()), e.getX(), e.getY());
						seedFillerStack.fill(0x008000);
						seedFillStackObjects.add(seedFillerStack);
						System.out.println("Seed Fill Stack... // GREEN");
					}
				}

				if(fillMode == 3){
					if(e.getButton() == MouseEvent.BUTTON3){
						SeedFillerBorder seedFillerBorder = new SeedFillerBorder(raster, borderOutlineColor, e.getX(), e.getY());
						seedFillerBorder.fill(0x008000);
						seedFillBorderObjects.add(seedFillerBorder);
						System.out.println("Seed Fill Border... // GREEN");
					}
				}

				processObjects();
				panel.repaint();


			}

			@Override
			public void mouseReleased(MouseEvent e) {
				clear(0x000000);

				processObjects();

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
		clear(0x000000);
		drawString(raster.getGraphics(), "POLYGON MODE - CAPSLOCK\nRECTANGLE MODE - R\nELLIPSE MODE - E\nCHANGE FILL MODE - Q\nCLEAR CANVAS - C", 590, 500);
		drawString(raster.getGraphics(), "ACTIVE MOD & FILL: " + checkActiveModes(fillModeNames),0,0);
		panel.repaint();
	}

	public void drawString(Graphics g, String text, int x, int y) {
		for (String line : text.split("\n")) {
			g.drawString(line, x, y += g.getFontMetrics().getHeight());
		}
	}

	public void processObjects() {
		drawString(raster.getGraphics(), "ACTIVE MOD & FILL: " + checkActiveModes(fillModeNames),0,0);


		// load polygons && rectangles && ellipses
		for (Polygon polygon : polygons) {
			polygonRasterizer.rasterize(polygon);
		}

		// diff border polygons (seed fill border)
		for (Polygon polygon : bordergons) {
			borderRasterizer.rasterize(polygon);
		}

		// load seed filled objects
		for (SeedFiller coloredObject : seedFillObjects) {
			coloredObject.fill(0x0000FF); // BLUE
		}

		// load seed filled border objects
		for (SeedFillerBorder coloredObject : seedFillBorderObjects) {
			coloredObject.fill(0xf0f0FF); // idk
		}

		// load seed filled stack objects
		for (SeedFillerStack coloredObject : seedFillStackObjects) {
			coloredObject.fill(0x008000); // GREEN
		}

		// load scanline filled objects
		for (ScanLineFiller coloredObject : scanLinedObjects) {
			coloredObject.fill(0xFF0000); // RED
		}

		drawString(raster.getGraphics(), "POLYGON MODE - CAPSLOCK\nRECTANGLE MODE - R\nELLIPSE MODE - E\nCHANGE FILL MODE - Q\nCLEAR CANVAS - C", 590, 500);
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

	public String checkActiveModes(String[] fillModeNames) {
		activeModes.clear();

		// modes
		boolean[] modes = {polygonMode, rectangleMode, ellipseMode, borderMode};
		String[] modeNames = {"Polygon", "Rectangle", "Ellipse", "Border"};

		for (int i = 0; i < modes.length; i++) {
			if (modes[i]) {
				activeModes.add(modeNames[i]);
			}
		}

		if(fillMode >= 0 && fillMode < fillModeNames.length) {
			activeModes.add(fillModeNames[fillMode]);
		}

		return String.join(", ",activeModes);

	}


	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new Canvas(800, 600).start());
	}

}