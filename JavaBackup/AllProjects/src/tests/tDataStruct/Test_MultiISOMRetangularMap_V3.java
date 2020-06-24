package tests.tDataStruct;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import dataStructures.MapTreeAVL;
import dataStructures.isom.MultiISOMRetangularCaching;
import dataStructures.isom.MultiISOMRetangularMap;
import dataStructures.isom.MultiISOMRetangularMap.MISOMLocatedInSpace;
import dataStructures.isom.ObjLocatedCollectorIsom;
import dataStructures.isom.matrixBased.MISOM_SingleObjInNode;
import dataStructures.isom.matrixBased.MatrixInSpaceObjectsManager;
import geometry.implementations.shapes.ShapeRectangle;
import geometry.pointTools.impl.ObjCollector;
import stuffs.logic.AtomLogicProposition;
import tools.Comparators;
import tools.NumberManager;

public class Test_MultiISOMRetangularMap_V3 {

	public static final int MAXIMUM_SUBMAPS_EACH_SECTION = 4, MINIMUM_DIMENSION_MAP = 4;

	public static final void main(String[] args) {
		int factor;
		MyRectangle[] rects;
		MultiISOMRetangularMap<Double> t;
		T_MISOM_GUI gui;
		rects = new MyRectangle[] { //
				new MyRectangle(-5, -3, 10, 8), // very wide, up part
				new MyRectangle(4, 5, 2, 5), // on right, vertical
				new MyRectangle(-2, 5, 4, 13), // on left, very high (vertical)
				new MyRectangle(2, 12, 7, 4), // on bottom, wide
		};
		System.out.println("rectangles:");
		for (MyRectangle r : rects) {
			System.out.println(r);
		}

		rects = new MyRectangle[] { //
				new MyRectangle(0, 0, 20, 80).setName("a"), //
				new MyRectangle(20, 12, 40, 8).setName("b"), //
				new MyRectangle(60, 4, 25, 24).setName("c"), //
				new MyRectangle(56, 28, 12, 20).setName("d"), // d
				new MyRectangle(48, 36, 8, 8).setName("e"), //
				new MyRectangle(32, 32, 16, 24).setName("f"), // f
				new MyRectangle(26, 36, 6, 20).setName("g"), //
				new MyRectangle(24, 56, 12, 12).setName("h"), //
				new MyRectangle(50, 48, 34, 10).setName("i"), // i
				new MyRectangle(38, 64, 10, 16).setName("l"), //
				new MyRectangle(52, 58, 8, 28).setName("m"), //
				new MyRectangle(68, 60, 5, 12).setName("n"), //
				new MyRectangle(36, -10, 20, 7).setName("o"), // o
				new MyRectangle(28, 4, 8, 8).setName("p"), //
				new MyRectangle(20, 76, 12, 8).setName("q"), //
				new MyRectangle(72, 28, 12, 16).setName("r"), // r
				new MyRectangle(60, 72, 16, 8).setName("s"), //
				new MyRectangle(45, -3, 5, 15).setName("t"), //
				new MyRectangle(48, 70, 4, 5).setName("u"), //
				new MyRectangle(73, 70, 1, 1).setName("v"), //
				new MyRectangle(73, 68, 1, 1).setName("x"), //
				new MyRectangle(73, 66, 1, 1).setName("y"), //
				new MyRectangle(73, 64, 1, 1).setName("w"), //
				new MyRectangle(73, 62, 1, 1).setName("z"), // z
				new MyRectangle(40, 56, 5, 5).setName("h2"), //
		};
		factor = 8;
		System.out.println("start mega add " + rects.length);
		for (MyRectangle r : rects) {
			r.x *= factor;
			r.y *= factor;
			r.width *= factor;
			r.height *= factor;
			System.out.println("- rectangle: " + r);
//			System.out.println(r.x + ", " + r.y + ", " + r.width + ", " + r.height);
		}
		t =
				// new Test_MultiISOMRetangularMap_V3(MAXIMUM_SUBMAPS_EACH_SECTION);
				new MultiISOMRetangularCaching<Double>(MAXIMUM_SUBMAPS_EACH_SECTION);
//		t.addMaps(rects.stream().map(r->{
//			new MatrixInSpaceObjectsManager<Double>() {
//			};

//		t = new Test_MultiISOMRetangularMap_V1();
		System.out.println("\n\nadded");
		gui = new T_MISOM_GUI(t);
		gui.rebuildGUI();
		gui.resetRects(rects);
		System.out.println(t);
		System.out.println("\n\n\n mega add");
	}

	//

	//

	//

	//
	static class T_MISOM_GUI {
		static int idProgNewRect = 100;
		boolean isStartPathfind;
		JFrame win;
		JPanel jp;
		JScrollPane jsp;
		Map<Integer, MyRectangle> rects = null;
		MultiISOMRetangularMap<Double> t;
		MyRectangle newRect = null;
		Point pStartDrawningRect = null, pEndDrawningRect = null;
		Point startPathfind, endPathfind;
//		List<Point > pathFound;
		int[] xPath, yPath;

		public T_MISOM_GUI(MultiISOMRetangularMap<Double> t) {
			super();
			this.t = t;
			isStartPathfind = true;
			startPathfind = endPathfind = null;
//			pathFound=null;
			xPath = yPath = null;
		}

		void rebuildGUI() {
			MouseAdapter ma;
			win = new JFrame("Test Multi ISOM");
			win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			jp = new JPanel() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					paintRectsAndSections(g);
				}
			};
			jsp = new JScrollPane(jp);
			jsp.setViewportView(jp);
			win.add(jsp);
			win.addComponentListener(new ComponentAdapter() {
				@Override
				public void componentResized(ComponentEvent e) {
					Dimension d;
					d = win.getSize();
					d.width -= 10;
					d.height -= 25;
					jsp.setSize(d);
					jsp.setPreferredSize(d);
				}
			});
			ma = new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) { onMouseClick(e); }

				@Override
				public void mousePressed(MouseEvent e) {
					System.out.println("____ pressed on " + e.getPoint());
					onStartDrawningNewRectangle(e.getPoint());
					jp.repaint();
				}

				@Override
				public void mouseDragged(MouseEvent e) {
					pEndDrawningRect = e.getPoint();
//					System.out.println("����dragged on " + pEndDrawningRect);
					if (t.getxLeftTop() < 0) { pEndDrawningRect.x += t.getxLeftTop(); }
					if (t.getyLeftTop() < 0) { pEndDrawningRect.y += t.getyLeftTop(); }
					updateStartEndPoinNewRectangle();
					jp.repaint();
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					System.out.println("____ released on " + e.getPoint());
					onEndDrawningNewRectangle(e.getPoint(), e.getButton() == 1);
					jp.repaint();
				}
			};
			jp.addMouseListener(ma);
			jp.addMouseMotionListener(ma);
			win.setSize(500, 500);
			win.setVisible(true);
		}

		//

		void addRects(MyRectangle r) {
			MatrixInSpaceObjectsManager<Double> map;
			MISOMLocatedInSpace<Double> w;
			if (rects == null) {
				rects = MapTreeAVL.newMap(MapTreeAVL.Optimizations.MinMaxIndexIteration,
						Comparators.INTEGER_COMPARATOR);
			}
			map = new MISOM_SingleObjInNode<Double>(true, r.width, r.height, NumberManager.getDoubleManager());
			map.setWeightManager(NumberManager.getDoubleManager());
//			map.setProviderShapeRunner(ProviderShapeRunnerImpl.getInstance());
//			map.setProviderShapesIntersectionDetector(ProviderShapesIntersectionDetector.getInstance());
			map.setPathFinder(t.getPathFinder());
			w = t.addMap(map, r.x, r.y);
			System.out.println("adding: " + r.name + ", and id: " + w.ID);
			rects.put(//
					w.IDInteger, r);
		}

		void resetRects(MyRectangle[] recs) {
			t.clear();
			for (MyRectangle r : recs) {
				addRects(r);
			}
			jp.setSize(t.getWidth() + 200, t.getHeight() + 200);
			jp.setPreferredSize(jp.getSize());
		}

		void paintRectsAndSections(Graphics g) {
			Color c;
			int minx, miny;
			minx = t.getxLeftTop() - 1;
			miny = t.getyLeftTop() - 1;
			c = Color.GREEN.darker();
			if (t != null && t.getMisomsHeld() != null) {
//				for (MyRectangle r : rects) {
				t.getMapsLocatedInSpace().forEach((id, wrapper) -> {
					MyRectangle r;
					r = rects.get(id);
					if (r != null) {
						g.setColor(c);
//					g.drawRect(r.x - minx, r.y - miny, r.width, r.height);
						g.fillRect(r.x - minx, r.y - miny, r.width, r.height);
						g.setColor(Color.BLACK);
						g.drawString(r.name, ((r.x - minx) + (r.width >> 1)), ((r.y - miny) + (r.height >> 1)));
						g.drawString(r.name, (r.x - minx), (r.y - miny));
					}
//					else
//						System.out.println("can't find the r with id :" + id);
				}//
				);
			}
			if (this.newRect != null) {
				g.setColor(Color.RED);
				g.drawRect(//
						(t.getxLeftTop() >= 0) ? this.newRect.x : //
								this.newRect.x - t.getxLeftTop(),
						(t.getyLeftTop() >= 0) ? this.newRect.y : //
								this.newRect.y - t.getyLeftTop(), //
						this.newRect.width, this.newRect.height);
			}
			if (t != null && t.getRoot() != null) { paintSubdivision(g); }

			if (xPath != null && xPath.length > 0) { g.drawPolyline(xPath, yPath, xPath.length); }
		}

		void paintSubdivision(Graphics g) {
			int halfDepth, minx, miny;
			minx = t.getxLeftTop() - 1;
			miny = t.getyLeftTop() - 1;
//			NodeMultiISOMRectangular n;
			MultiISOMRetangularMap<Double>.NodeQuadtreeMultiISOMRectangular n;
			LinkedList<MultiISOMRetangularMap<Double>.NodeQuadtreeMultiISOMRectangular> nodes;
			nodes = new LinkedList<>();
			n = t.getRoot();
			nodes.add(n);
			while (!nodes.isEmpty()) {
				n = nodes.removeFirst();
				g.setColor(Color.RED);
				g.drawString(Integer.toString(n.getDepth()), n.getXMiddle() - minx, n.getYMiddle() - miny);
				if (!n.isLeaf()) {
					g.setColor(Color.LIGHT_GRAY);
					halfDepth = n.getDepth() >> 1;
					// horizonta
					g.fillRect(n.getX() - minx, n.getYMiddle() - (halfDepth + miny), n.getWidth(),
							1 + (t.getMaxDepth() - n.getDepth()));
//vertical
					g.fillRect(n.getXMiddle() - (halfDepth + minx), n.getY() - miny,
							1 + (t.getMaxDepth() - n.getDepth()), n.getHeight());
					n.forEachSubsection((s) -> {
						if (s != null)
							nodes.add(s);
					});
				}
			}
		}

		void onMouseClick(MouseEvent me) {
			int mouseButton;
			Point whereClicked;
//			MyRectangle mapFound;
			MISOMLocatedInSpace<Double> mapWrapper;
//			MatrixInSpaceObjectsManager<Double> map;
			whereClicked = me.getPoint();
			// consider the offset
			if (t.getxLeftTop() < 0) { whereClicked.x += t.getxLeftTop(); }
			if (t.getyLeftTop() < 0) { whereClicked.y += t.getyLeftTop(); }
			System.out.println("finding a map on " + whereClicked + " ..");
//			map = t.getMISOMContaining(whereClicked);
			mapWrapper = t.getMapLocatedContaining(whereClicked);
			System.out.println(".. map found: " + (mapWrapper == null ? "null" : rects.get(mapWrapper.IDInteger)));
			mouseButton = me.getButton();
			System.out.println("me.getButton(): " + mouseButton);
			if (mouseButton == MouseEvent.BUTTON1 || mouseButton == MouseEvent.BUTTON2) {
				performPathfind(whereClicked);
			} else if (mouseButton == MouseEvent.BUTTON3) {
				if (mapWrapper != null) {
					t.removeMap(mapWrapper);
					rects.remove(mapWrapper.IDInteger);
				}
			}
//			mapFound =    rects.get(map.id);
		}

		void updateStartEndPoinNewRectangle() {
			int temp;
			Point ps, pe;
			if (this.pStartDrawningRect.x == this.pEndDrawningRect.x
					|| this.pStartDrawningRect.y == this.pEndDrawningRect.y)
				return;
			ps = new Point(this.pStartDrawningRect);
			pe = new Point(this.pEndDrawningRect);
			if (ps.x > pe.x) {
				temp = ps.x;
				ps.x = pe.x;
				pe.x = temp;
			}
			if (ps.y > pe.y) {
				temp = ps.y;
				ps.y = pe.y;
				pe.y = temp;
			}
			if (newRect == null) {
				this.newRect = new MyRectangle(ps, new Dimension( //
						pe.x - ps.x, //
						pe.y - ps.y));
				this.newRect.setName( // use the atom to make the name cool
						(new AtomLogicProposition(true, idProgNewRect++)).getName());
			} else {
				this.newRect.x = ps.x;
				this.newRect.y = ps.y;
				this.newRect.width = pe.x - ps.x;
				this.newRect.height = pe.y - ps.y;
			}
		}

		void onStartDrawningNewRectangle(Point pointEnd) {
//			this.isRectangleDrawning = true;
			this.newRect = null;
			this.pEndDrawningRect = null;
			this.pStartDrawningRect = pointEnd;
			// consider the offset
			if (t.getxLeftTop() < 0) { pointEnd.x += t.getxLeftTop(); }
			if (t.getyLeftTop() < 0) { pointEnd.y += t.getyLeftTop(); }
		}

		void onEndDrawningNewRectangle(Point pointEnd, boolean isLeftClick) {
			this.pEndDrawningRect = pointEnd;
			// consider the offset
			if (t.getxLeftTop() < 0) { pointEnd.x += t.getxLeftTop(); }
			if (t.getyLeftTop() < 0) { pointEnd.y += t.getyLeftTop(); }
			updateStartEndPoinNewRectangle();
//			this.isRectangleDrawning = false;
			if (isLeftClick)
				buildAndAddMap();
			else {
//				collectAndDeleteObjects();
				collectAndDeleteMaps();
			}
			this.pStartDrawningRect = null;
			this.pEndDrawningRect = null;
		}

		void collectAndDeleteObjects() {
			ObjLocatedCollectorIsom<Double> c;
			ShapeRectangle shape;
			c = t.newObjLocatedCollector(null);
			shape = new ShapeRectangle(0, (int) newRect.getCenterX(), (int) newRect.getCenterY(), true,
					(int) newRect.getWidth(), (int) newRect.getHeight());
			t.// newNodeIsomProviderCaching().//
					runOnShape(shape, c);

			if (c.getCollectedObjects().size() > 0)
				c.getCollectedObjects().forEach(ol -> {
					MISOMLocatedInSpace<Double> w;
					System.out.println("\n\n got ol: " + ol);
					w = t.getMapLocatedContaining(ol.getLocation());
					System.out.println("and then removing object into map: " + (w == null ? "null" : w.ID));
//					t.removeMap(w);
//					this.rects.remove(w.IDInteger);
					w.misom.remove(ol);
				});
			else {
				System.out.println(" no objects found");
			}
		}

		void collectAndDeleteMaps() {
			MapRemover mr;
			ShapeRectangle shape;
//			c = t.newObjLocatedCollector(null);
			if (newRect == null)
				return;
			shape = new ShapeRectangle(0, (int) newRect.getCenterX(), (int) newRect.getCenterY(), true,
					(int) newRect.getWidth(), (int) newRect.getHeight());
			mr = new MapRemover(t);
			t.// newNodeIsomProviderCaching().//
					runOnShape(shape, mr);
			if (mr.mapsCollected.isEmpty()) {
				System.out.println("No map collected");
			} else {
				System.out.println("collected " + mr.mapsCollected.size() + " maps:");
				mr.mapsCollected.forEach(mlis -> {
					t.removeMap(mlis);
					this.rects.remove(mlis.IDInteger);
				});
			}
//			if (c.getCollectedObjects().size() > 0)
//				c.getCollectedObjects().forEach(ol -> {
//					MISOMLocatedInSpace<Double> w;
//					System.out.println("\n\n got ol: " + ol);
//					w = t.getMISOMLocatedInSpaceContaining(ol.getLocation());
//					System.out.println("and then removing: " + (w == null ? "null" : w.ID));
//					t.removeMap(w);
//					this.rects.remove(w.IDInteger);
//				});
//			else {
//				System.out.println(" no objects found");
//			}
		}

		void buildAndAddMap() {
			if (this.newRect == null)
				return;
			System.out.println("\n\n adding new map: " + newRect);
//			t.addMap(newRect);
			addRects(newRect);
			this.newRect = null;
			jp.setSize(t.getWidth() + 200, t.getHeight() + 200);
			jp.setPreferredSize(jp.getSize());
			jp.repaint();
		}

		// TODO OOOOOOOOOOOOOOOOOOO
		// FARE PATH FINDING CHE IL CLICK SINISTRO IMPOSTA, IN MODO ALTERNATO,
		// INIZIO E FINE DEL PERCORSO .. E POI DISEGNA IL POLILINE

		void performPathfind(Point whereClicked) {
			int len;
			List<Point> pathFound;
			if (this.isStartPathfind)
				this.startPathfind = whereClicked;
			else
				this.endPathfind = whereClicked;
			if (this.startPathfind != null && this.endPathfind != null) {
				final int[] i = { 0 };
				pathFound = t.getPath(startPathfind, endPathfind);
				if (pathFound == null)
					return;
				len = pathFound.size();
				xPath = new int[len];
				yPath = new int[len];
				pathFound.forEach(p -> {
					xPath[i[0]] = p.x;
					yPath[i[0]++] = p.x;
				});
			}
		}

		//

		protected class MapRemover implements ObjCollector<MISOMLocatedInSpace<Double>> {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			protected MapRemover(MultiISOMRetangularMap<Double> nodeIsomProvider) {
				super();
				this.nodeIsomProvider = nodeIsomProvider;
				this.mapBackMapsCollected = MapTreeAVL.newMap(MapTreeAVL.Optimizations.MinMaxIndexIteration,
						Comparators.INTEGER_COMPARATOR);
				this.mapsCollected = this.mapBackMapsCollected.toSetValue(m -> m.getID());
				filter = m -> m != null;
			}

			protected MultiISOMRetangularMap<Double> nodeIsomProvider;
			protected Set<MISOMLocatedInSpace<Double>> mapsCollected;
			protected MapTreeAVL<Integer, MISOMLocatedInSpace<Double>> mapBackMapsCollected;
			protected Predicate<MISOMLocatedInSpace<Double>> filter;

			// @Override
			public MultiISOMRetangularMap<Double> getNodeIsomProvider() { return nodeIsomProvider; }

//			@Override
			public void setNodeIsomProvider(MultiISOMRetangularMap<Double> nodeIsomProvider) {
				this.nodeIsomProvider = nodeIsomProvider;
			}

			@Override
			public Set<MISOMLocatedInSpace<Double>> getCollectedObjects() { return mapsCollected; }

			@Override
			public Predicate<MISOMLocatedInSpace<Double>> getTargetsFilter() { return filter; }

			@Override
			public MISOMLocatedInSpace<Double> getAt(Point location) {
				return nodeIsomProvider.getMapLocatedContaining(location);
			}
		}
	}
}