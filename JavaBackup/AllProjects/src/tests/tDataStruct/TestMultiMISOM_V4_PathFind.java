package tests.tDataStruct;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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
import geometry.AbstractShape2D;
import geometry.ObjectShaped;
import geometry.implementations.shapes.ShapeRectangle;
import geometry.pointTools.impl.ObjCollector;
import stuffs.logic.AtomLogicProposition;
import tools.Comparators;
import tools.NumberManager;

public class TestMultiMISOM_V4_PathFind {
	public static final int MAXIMUM_SUBMAPS_EACH_SECTION = 4, MINIMUM_DIMENSION_MAP = 4, PIXEL_EACH_CELL = 10;

	public static class RectanglesAndPathfindingPoints {
		protected RectanglesAndPathfindingPoints(MyRectangle[] rects, Point ps, Point pe) {
			super();
			this.rects = rects;
			this.ps = ps;
			this.pe = pe;
		}

		MyRectangle[] rects;
		Point ps, pe;
	}

	public interface DatasetProducersToTest {
		public RectanglesAndPathfindingPoints produceTestSet();
	}

	protected static final DatasetProducersToTest[] MyRectangle_TEST_PRODUCERS = {
			//
			() -> new RectanglesAndPathfindingPoints(new MyRectangle[] { //
					new MyRectangle(-4, -2, 6, 7).setName("a"), //
					new MyRectangle(2, 1, 4, 3).setName("b"), //
					new MyRectangle(-3, 5, 3, 5).setName("c"), //
					new MyRectangle(0, 7, 4, 2).setName("d"), //
					new MyRectangle(4, 4, 3, 8).setName("e"), //
					new MyRectangle(-1, 11, 5, 1).setName("f"), //
					new MyRectangle(1, 5, 2, 1).setName("g"), //
					new MyRectangle(7, 0, 2, 6).setName("h") //
			}, new Point(-3, -1), new Point(4, 7)), //
			() -> new RectanglesAndPathfindingPoints(new MyRectangle[] { //
					new MyRectangle(-4, -2, 6, 7).setName("a"), //
					new MyRectangle(2, 1, 4, 3).setName("b"), //
					new MyRectangle(-3, 5, 3, 5).setName("c"), //
					new MyRectangle(0, 7, 4, 2).setName("d"), //
					new MyRectangle(4, 4, 3, 8).setName("e"), //
					new MyRectangle(-1, 11, 5, 1).setName("f"), //
					new MyRectangle(1, 5, 2, 1).setName("g"), //
					new MyRectangle(7, 0, 2, 6).setName("h"), //
					new MyRectangle(-5, 8, 2, 6).setName("dx"), //
					new MyRectangle(10, -2, 5, 2).setName("dy"), //
					new MyRectangle(11, 0, 2, 4).setName("dz"), //
					new MyRectangle(13, 2, 3, 3).setName("ea"), //
					new MyRectangle(8, 8, 2, 5).setName("ed"), //
					new MyRectangle(10, 10, 2, 2).setName("ee"), //
					new MyRectangle(10, 7, 4, 2).setName("eg"), //
					new MyRectangle(13, 9, 3, 5).setName("eh"), //
					new MyRectangle(-3, 13, 5, 2).setName("ei"), //
					new MyRectangle(2, 13, 3, 1).setName("ej"), //
					new MyRectangle(5, 13, 2, 2).setName("ek"), //
					new MyRectangle(7, 14, 4, 1).setName("el"), //
					new MyRectangle(11, 13, 2, 2).setName("em"), //
					new MyRectangle(2, -5, 5, 4).setName("en"), //
					new MyRectangle(7, -5, 17, 2).setName("eo"), //
					new MyRectangle(19, -3, 2, 6).setName("ep"), //
					new MyRectangle(21, 1, 3, 8).setName("eq"), //
					new MyRectangle(18, 7, 3, 5).setName("er"), //
					new MyRectangle(16, 12, 4, 3).setName("es"), //
					new MyRectangle(13, 11, 1, 1).setName("et"), //
					new MyRectangle(21, 10, 2, 3).setName("eu"), //
					new MyRectangle(18, 4, 2, 2).setName("ev"), //
			}, new Point(5, 9), new Point(17, 13)), //
			() -> new RectanglesAndPathfindingPoints(new MyRectangle[] { //
					new MyRectangle(-4, -2, 6, 7).setName("a"), //
					new MyRectangle(2, 1, 4, 3).setName("b"), //
					new MyRectangle(-3, 5, 3, 5).setName("c"), //
					new MyRectangle(0, 7, 4, 2).setName("d"), //
					new MyRectangle(-1, 11, 5, 1).setName("f"), //
					new MyRectangle(1, 5, 2, 1).setName("g"), //
					new MyRectangle(7, 0, 2, 6).setName("h"), //
					new MyRectangle(-5, 8, 2, 6).setName("dx"), //
					new MyRectangle(10, -2, 5, 2).setName("dy"), //
					new MyRectangle(8, 8, 2, 5).setName("ed"), //
					new MyRectangle(10, 10, 2, 2).setName("ee"), //
					new MyRectangle(10, 7, 4, 2).setName("eg"), //
					new MyRectangle(13, 9, 3, 5).setName("eh"), //
					new MyRectangle(-3, 13, 5, 2).setName("ei"), //
					new MyRectangle(2, 13, 3, 1).setName("ej"), //
					new MyRectangle(5, 13, 2, 2).setName("ek"), //
					new MyRectangle(7, 14, 4, 1).setName("el"), //
					new MyRectangle(11, 13, 2, 2).setName("em"), //
					new MyRectangle(2, -5, 5, 4).setName("en"), //
					new MyRectangle(7, -5, 17, 2).setName("eo"), //
					new MyRectangle(19, -3, 2, 6).setName("ep"), //
					new MyRectangle(21, 1, 3, 8).setName("eq"), //
					new MyRectangle(18, 7, 3, 5).setName("er"), //
					new MyRectangle(16, 12, 4, 3).setName("es"), //
					new MyRectangle(21, 10, 2, 3).setName("eu"), //
					new MyRectangle(18, 4, 2, 2).setName("ev"), //
					new MyRectangle(3, -1, 2, 1).setName("dz"), //
					new MyRectangle(17, 0, 2, 2).setName("ea"), //
					new MyRectangle(16, -2, 2, 2).setName("eb"), //
					new MyRectangle(17, 7, 1, 2).setName("eg"), //
					new MyRectangle(9, 5, 5, 1).setName("eh"), //
					new MyRectangle(10, 0, 2, 4).setName("ej"), //
					new MyRectangle(12, 1, 3, 2).setName("ek"), //
					new MyRectangle(13, 4, 3, 1).setName("el"), //
					new MyRectangle(15, 5, 2, 3).setName("em"), //
					new MyRectangle(-2, -5, 2, 2).setName("en"), //
					new MyRectangle(-4, -4, 2, 2).setName("eo"), //
					new MyRectangle(4, 4, 3, 2).setName("ep"), //
					new MyRectangle(4, 8, 3, 4).setName("eq"), //
					new MyRectangle(5, 6, 2, 2).setName("er"), //
			}, new Point(0, 0), new Point(22, 11)), //
			() -> new RectanglesAndPathfindingPoints(new MyRectangle[] { // an heart
					new MyRectangle(12, 7, 3, 2).setName("c"), //
					new MyRectangle(11, 5, 2, 2).setName("d"), //
					new MyRectangle(14, 5, 2, 2).setName("e"), //
					new MyRectangle(8, 4, 3, 2).setName("f"), //
					new MyRectangle(6, 5, 2, 2).setName("g"), //
					new MyRectangle(4, 6, 2, 4).setName("h"), //
					new MyRectangle(5, 10, 2, 3).setName("i"), //
					new MyRectangle(7, 12, 2, 2).setName("l"), //
					new MyRectangle(8, 14, 2, 3).setName("m"), //
					new MyRectangle(10, 16, 2, 2).setName("n"), //
					new MyRectangle(12, 17, 3, 2).setName("o"), //
					new MyRectangle(15, 16, 2, 2).setName("p"), //
					new MyRectangle(17, 14, 2, 3).setName("q"), //
					new MyRectangle(18, 12, 2, 2).setName("r"), //
					new MyRectangle(20, 10, 2, 3).setName("s"), //
					new MyRectangle(21, 6, 2, 4).setName("t"), //
					new MyRectangle(19, 5, 2, 2).setName("u"), //
					new MyRectangle(16, 4, 3, 2).setName("v"), //
			}, new Point(6, 6), new Point(21, 11)), //
			() -> new RectanglesAndPathfindingPoints(new MyRectangle[] { //
					new MyRectangle(-4, -2, 6, 7).setName("a"), //
					new MyRectangle(2, 1, 4, 3).setName("b"), //
					new MyRectangle(-3, 5, 3, 5).setName("c"), //
					new MyRectangle(0, 7, 4, 2).setName("d"), //
					new MyRectangle(-1, 11, 5, 1).setName("f"), //
					new MyRectangle(1, 5, 2, 1).setName("g"), //
					new MyRectangle(-5, 8, 2, 6).setName("dx"), //
					new MyRectangle(10, -2, 5, 2).setName("dy"), //
					new MyRectangle(8, 8, 2, 5).setName("ed"), //
					new MyRectangle(10, 10, 2, 2).setName("ee"), //
					new MyRectangle(10, 7, 4, 2).setName("eg"), //
					new MyRectangle(13, 9, 3, 5).setName("eh"), //
					new MyRectangle(-3, 13, 5, 2).setName("ei"), //
					new MyRectangle(2, 13, 3, 1).setName("ej"), //
					new MyRectangle(5, 13, 2, 2).setName("ek"), //
					new MyRectangle(7, 14, 4, 1).setName("el"), //
					new MyRectangle(11, 13, 2, 2).setName("em"), //
					new MyRectangle(2, -5, 5, 4).setName("en"), //
					new MyRectangle(19, -3, 2, 6).setName("ep"), //
					new MyRectangle(21, 1, 3, 8).setName("eq"), //
					new MyRectangle(18, 7, 3, 5).setName("er"), //
					new MyRectangle(16, 12, 4, 3).setName("es"), //
					new MyRectangle(21, 10, 2, 3).setName("eu"), //
					new MyRectangle(18, 4, 2, 2).setName("ev"), //
					new MyRectangle(3, -1, 2, 1).setName("dz"), //
					new MyRectangle(17, 0, 2, 2).setName("ea"), //
					new MyRectangle(16, -2, 2, 2).setName("eb"), //
					new MyRectangle(17, 7, 1, 2).setName("eg"), //
					new MyRectangle(9, 5, 5, 1).setName("eh"), //
					new MyRectangle(10, 0, 2, 4).setName("ej"), //
					new MyRectangle(12, 1, 3, 2).setName("ek"), //
					new MyRectangle(13, 4, 3, 1).setName("el"), //
					new MyRectangle(15, 5, 2, 3).setName("em"), //
					new MyRectangle(-2, -5, 2, 2).setName("en"), //
					new MyRectangle(-4, -4, 2, 2).setName("eo"), //
					new MyRectangle(4, 4, 3, 2).setName("ep"), //
					new MyRectangle(4, 8, 3, 4).setName("eq"), //
					new MyRectangle(5, 6, 2, 2).setName("er"), //
					new MyRectangle(7, 2, 2, 5).setName("dw"), //
					new MyRectangle(24, -4, 2, 6).setName("dx"), //
					new MyRectangle(7, -5, 3, 2).setName("dy"), //
					new MyRectangle(22, -5, 2, 2).setName("eh"), //
					new MyRectangle(10, -4, 4, 1).setName("ei"), //
					new MyRectangle(14, -5, 2, 2).setName("ej"), //
					new MyRectangle(16, -4, 2, 1).setName("ek"), //
					new MyRectangle(17, -5, 5, 1).setName("el"), //
					new MyRectangle(21, -3, 2, 1).setName("em"), //
			}, new Point(1, 11), new Point(25, 0)), //
			() -> new RectanglesAndPathfindingPoints(new MyRectangle[] { //
					new MyRectangle(-4, -2, 6, 7).setName("a"), //
					new MyRectangle(2, 1, 4, 3).setName("b"), //
					new MyRectangle(-3, 5, 3, 5).setName("c"), //
					new MyRectangle(0, 7, 4, 2).setName("d"), //
					new MyRectangle(-1, 11, 5, 1).setName("f"), //
					new MyRectangle(1, 5, 2, 1).setName("g"), //
					new MyRectangle(-5, 8, 2, 6).setName("dx"), //
					new MyRectangle(10, -2, 5, 2).setName("dy"), //
					new MyRectangle(8, 8, 2, 5).setName("ed"), //
					new MyRectangle(10, 10, 2, 2).setName("ee"), //
					new MyRectangle(10, 7, 4, 2).setName("eg"), //
					new MyRectangle(5, 13, 2, 2).setName("ek"), //
					new MyRectangle(7, 14, 4, 1).setName("el"), //
					new MyRectangle(2, -5, 5, 4).setName("en"), //
					new MyRectangle(19, -3, 2, 6).setName("ep"), //
					new MyRectangle(21, 1, 3, 8).setName("eq"), //
					new MyRectangle(18, 4, 2, 2).setName("ev"), //
					new MyRectangle(3, -1, 2, 1).setName("dz"), //
					new MyRectangle(17, 0, 2, 2).setName("ea"), //
					new MyRectangle(16, -2, 2, 2).setName("eb"), //
					new MyRectangle(9, 5, 5, 1).setName("eh"), //
					new MyRectangle(10, 0, 2, 4).setName("ej"), //
					new MyRectangle(12, 1, 3, 2).setName("ek"), //
					new MyRectangle(13, 4, 3, 1).setName("el"), //
					new MyRectangle(15, 5, 2, 3).setName("em"), //
					new MyRectangle(-2, -5, 2, 2).setName("en"), //
					new MyRectangle(-4, -4, 2, 2).setName("eo"), //
					new MyRectangle(4, 4, 3, 2).setName("ep"), //
					new MyRectangle(4, 8, 3, 4).setName("eq"), //
					new MyRectangle(5, 6, 2, 2).setName("er"), //
					new MyRectangle(7, 2, 2, 5).setName("dw"), //
					new MyRectangle(24, -4, 2, 6).setName("dx"), //
					new MyRectangle(7, -5, 3, 2).setName("dy"), //
					new MyRectangle(22, -5, 2, 2).setName("eh"), //
					new MyRectangle(10, -4, 4, 1).setName("ei"), //
					new MyRectangle(14, -5, 2, 2).setName("ej"), //
					new MyRectangle(16, -4, 2, 1).setName("ek"), //
					new MyRectangle(17, -5, 5, 1).setName("el"), //
					new MyRectangle(21, -3, 2, 1).setName("em"), //
					new MyRectangle(19, 7, 2, 5).setName("dw"), //
					new MyRectangle(17, 7, 2, 2).setName("dx"), //
					new MyRectangle(21, 10, 4, 1).setName("ef"), //
					new MyRectangle(20, 12, 2, 2).setName("eg"), //
					new MyRectangle(18, 14, 5, 2).setName("eh"), //
					new MyRectangle(23, 12, 2, 3).setName("ei"), //
					new MyRectangle(14, 14, 1, 2).setName("en"), //
					new MyRectangle(15, 15, 3, 1).setName("eo"), //
					new MyRectangle(16, 10, 2, 2).setName("ep"), //
					new MyRectangle(13, 9, 3, 2).setName("et"), //
					new MyRectangle(11, 13, 2, 2).setName("ew"), //
					new MyRectangle(13, 12, 2, 2).setName("ex"), //
					new MyRectangle(15, 11, 1, 2).setName("ey"), //
					new MyRectangle(-3, 13, 4, 2).setName("ez"), //
					new MyRectangle(1, 13, 4, 1).setName("fa"), //
					new MyRectangle(2, 14, 2, 2).setName("fb"), //
			}, new Point(1, 11), new Point(25, 0)), //

			() -> new RectanglesAndPathfindingPoints(new MyRectangle[] { //
					new MyRectangle(0, 0, 10, 20).setName("dx"), //
					new MyRectangle(10, 2, 13, 5).setName("dy"), //
					new MyRectangle(23, 3, 5, 10).setName("d"), //
					new MyRectangle(14, 13, 11, 5).setName("ea"), //
					new MyRectangle(17, 18, 7, 6).setName("eb"), //
					new MyRectangle(3, 20, 11, 12).setName("ec"), //
					new MyRectangle(14, 24, 12, 5).setName("ee"), //
					new MyRectangle(26, 20, 12, 13).setName("ef"), //
			}, new Point(2, 7), new Point(32, 28)), //
			() -> new RectanglesAndPathfindingPoints(new MyRectangle[] { //
					new MyRectangle(1, 0, 40, 30).setName("a"), //
			}, new Point(0, 0), new Point(11, 11)), //
			() -> new RectanglesAndPathfindingPoints(new MyRectangle[] { //
					new MyRectangle(0, 0, 10, 23).setName("a"), //
					new MyRectangle(10, 3, 15, 6).setName("b"), //
					new MyRectangle(18, 9, 14, 20).setName("c"), //
			}, new Point(0, 0), new Point(11, 11)), //
			() -> new RectanglesAndPathfindingPoints(new MyRectangle[] { //
					new MyRectangle(0, 0, 15, 29).setName("a"), //
					new MyRectangle(15, 16, 13, 10).setName("b"), //
					new MyRectangle(18, 26, 21, 25).setName("c"), //
					new MyRectangle(15, 1, 50, 12).setName("d"), //
					new MyRectangle(3, 54, 30, 32).setName("e"), //
					new MyRectangle(33, 56, 34, 14).setName("f"), //
					new MyRectangle(39, 37, 14, 19).setName("g"), //
					new MyRectangle(65, 3, 34, 25).setName("h"), //
					new MyRectangle(67, 28, 35, 45).setName("i"), //
					new MyRectangle(47, 19, 18, 7).setName("j"), //
					new MyRectangle(44, 26, 16, 11).setName("k"), //
					new MyRectangle(50, 13, 11, 6).setName("l"), //
					new MyRectangle(2, 35, 17, 10).setName("m"), //
			}, new Point(0, 0), new Point(11, 11)), //
			() -> new RectanglesAndPathfindingPoints(new MyRectangle[] { //
					new MyRectangle(27, 3, 40, 10).setName("ed"), //
					new MyRectangle(20, 19, 25, 8).setName("ef"), //
					new MyRectangle(29, 13, 13, 6).setName("ek"), //
					new MyRectangle(0, 0, 27, 14).setName("en"), //
					new MyRectangle(52, 13, 21, 30).setName("eo"), //
					new MyRectangle(13, 33, 14, 8).setName("ep"), //
					new MyRectangle(19, 41, 28, 7).setName("er"), //
					new MyRectangle(0, 33, 11, 24).setName("es"), //
					new MyRectangle(59, 43, 12, 26).setName("eu"), //
					new MyRectangle(29, 48, 10, 19).setName("ev"), //
					new MyRectangle(39, 55, 20, 9).setName("ew"), //
					new MyRectangle(1, 57, 28, 8).setName("ex"), //
					new MyRectangle(3, 14, 17, 19).setName("ey"), //
					new MyRectangle(38, 33, 14, 8).setName("ez"), //
			}, new Point(0, 0), new Point(11, 11)), //
			() -> new RectanglesAndPathfindingPoints(new MyRectangle[] { //
					new MyRectangle(0, 0, 27, 14).setName("en"), //
					new MyRectangle(85, 8, 26, 17).setName("dx"), //
					new MyRectangle(54, 7, 31, 12).setName("dy"), //
					new MyRectangle(34, 9, 20, 29).setName("ea"), //
					new MyRectangle(5, 14, 14, 16).setName("eb"), //
					new MyRectangle(8, 30, 26, 12).setName("ec"), //
					new MyRectangle(40, 38, 20, 20).setName("ed"), //
					new MyRectangle(60, 42, 40, 10).setName("ee"), //
					new MyRectangle(88, 25, 19, 17).setName("ef"), //
					new MyRectangle(77, 52, 23, 31).setName("eg"), //
					new MyRectangle(39, 71, 38, 21).setName("eh"), //
					new MyRectangle(18, 64, 21, 19).setName("ei"), //
					new MyRectangle(1, 42, 31, 22).setName("ek"), //
					new MyRectangle(0, 72, 18, 21).setName("el"), //
			}, new Point(0, 0), new Point(11, 11)), //
			() -> new RectanglesAndPathfindingPoints(new MyRectangle[] { //
					new MyRectangle(0, 0, 27, 14).setName("en"), //
					new MyRectangle(3, 14, 9, 31).setName("dx"), //
					new MyRectangle(12, 25, 8, 41).setName("dy"), //
					new MyRectangle(21, 16, 11, 27).setName("dz"), //
					new MyRectangle(19, 17, 2, 25).setName("ea"), //
					new MyRectangle(39, 16, 8, 28).setName("ec"), //
					new MyRectangle(47, 37, 7, 24).setName("ed"), //
					new MyRectangle(54, 50, 6, 24).setName("ee"), //
					new MyRectangle(60, 23, 4, 36).setName("ef"), //
					new MyRectangle(67, 39, 3, 28).setName("eh"), //
					new MyRectangle(62, 5, 15, 15).setName("ej"), //
					new MyRectangle(64, 27, 3, 30).setName("ek"), //
					new MyRectangle(43, 44, 4, 14).setName("el"), //
					new MyRectangle(56, 43, 4, 7).setName("em"), //
					new MyRectangle(77, 12, 6, 8).setName("eo"), //
					new MyRectangle(83, 15, 10, 5).setName("eq"), //
					new MyRectangle(75, 22, 13, 26).setName("er"), //
					new MyRectangle(76, 20, 16, 2).setName("es"), //
					new MyRectangle(70, 30, 5, 24).setName("ev"), //
					new MyRectangle(23, 43, 9, 13).setName("ex"), //
					new MyRectangle(39, 44, 4, 6).setName("ey"), //
					new MyRectangle(32, 36, 7, 30).setName("ez"), //
			}, new Point(0, 0), new Point(11, 11)), //
	};

	public static void main(String[] args) {
		MyRectangle[] rects;
		MultiISOMRetangularMap<Double> t;
		T_MISOM_GUI_V4 gui;
		Map<Integer, MyRectangle> mapIdToMyrect;
		Point ps, pe;
		List<Point> path;
		RectanglesAndPathfindingPoints rapf;
		rapf = MyRectangle_TEST_PRODUCERS[MyRectangle_TEST_PRODUCERS.length - 1].produceTestSet();
		rects = rapf.rects;
		ps = rapf.ps;
		pe = rapf.pe;
		for (MyRectangle r : rects) {
			System.out.println("- rectangle: " + r);
		}
		t = new MultiISOMRetangularCaching<>(MAXIMUM_SUBMAPS_EACH_SECTION);
		t.setWeightManager(NumberManager.getDoubleManager());

		gui = new T_MISOM_GUI_V4(t);
		gui.rebuildGUI();
		gui.resetRects(rects);
		rects = null;
		mapIdToMyrect = gui.rects;
		System.out.println("start pathfinding with points:\n\t ps: " + ps + "\n\t pe: " + pe);
		path = t.getPath(ps, pe);
		if (path != null) {
			System.out.println("\n\n\n found " + path.size() + " points");
			path.forEach(p -> {
				System.out.println("\t p: (x:" + p.x + ", y:" + p.y //
						+ ") - contained in: " + //
				nameRectContaining(t, p, mapIdToMyrect));
			});
			gui.showPath(path);
		}
	}

	static String nameRectContaining(MultiISOMRetangularMap<Double> t, Point p,
			Map<Integer, MyRectangle> mapIdToMyrect) {
		MISOMLocatedInSpace<Double> m;
		m = t.getMapLocatedContaining(p);
		if (m == null)
			return "null";
		return mapIdToMyrect.get(m.getID()).getName();
	}

	//

	//

	//

	//

	static class T_MISOM_GUI_V4 {
		static int idProgNewRect = 100;
		boolean isStartPathfind, isPointwisePathfind;
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

		public T_MISOM_GUI_V4(MultiISOMRetangularMap<Double> t) {
			super();
			this.t = t;
			isStartPathfind = true;
			startPathfind = endPathfind = null;
//			pathFound=null;
			xPath = yPath = null;
			isPointwisePathfind = false;
		}

		void rebuildGUI() {
			MouseAdapter ma;
			KeyAdapter ka;
			win = new JFrame("Test Multi ISOM");
			win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			jp = new JPanel() {
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
					d.height -= 35;
					jsp.setSize(d);
					jsp.setPreferredSize(d);
				}
			});
			ma = new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) { onMouseClick(e); }

				@Override
				public void mousePressed(MouseEvent e) {
					onStartDrawningNewRectangle(e.getPoint());
					jp.repaint();
				}

				@Override
				public void mouseDragged(MouseEvent e) {
					pEndDrawningRect = e.getPoint();
					pEndDrawningRect.x /= PIXEL_EACH_CELL;
					pEndDrawningRect.y /= PIXEL_EACH_CELL;
					applyMultiMapOffset(pEndDrawningRect);
					updateStartEndPoinNewRectangle();
					jp.repaint();
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					Point p;
					p = e.getPoint();
					p.x /= PIXEL_EACH_CELL;
					p.y /= PIXEL_EACH_CELL;
					onEndDrawningNewRectangle(p, e.getButton() == 1);
					jp.repaint();
				}

				@Override
				public void mouseMoved(MouseEvent e) {
					Point p;
					p = e.getPoint();
					p.x /= PIXEL_EACH_CELL;
					p.y /= PIXEL_EACH_CELL;
					applyMultiMapOffset(p);
					win.setTitle("Mouse at: (x: " + p.x + ", y: " + p.y + ")");
				}
			};
			jp.addMouseListener(ma);
			jp.addMouseMotionListener(ma);
			ka = new KeyAdapter() {
//				@Override
//				public void keyTyped(KeyEvent e) { printRects(); }

				@Override
				public void keyReleased(KeyEvent e) { printRects(); }
			};
			jp.addKeyListener(ka);
			jsp.addKeyListener(ka);
			win.addKeyListener(ka);
			win.setSize(500, 500);
			win.setVisible(true);
		}

		//

		void printRects() {
			System.out.println("print rect:\n ()->new RectanglesAndPathfindingPoints(new MyRectangle[] {//");
			rects.forEach((i, r) -> {
				System.out.println(" new MyRectangle(" + r.x + ", " + r.y + ", " + r.width + ", " + r.height
						+ ").setName(\"" + r.name + "\"), //");
			});
			System.out.println("}, new Point(0,0), new Point(11,11)), //");
		}

		void addRect(MyRectangle r) {
			MatrixInSpaceObjectsManager<Double> map;
			MISOMLocatedInSpace<Double> w;
			if (rects == null) {
				rects = MapTreeAVL.newMap(MapTreeAVL.Optimizations.MinMaxIndexIteration,
						Comparators.INTEGER_COMPARATOR);
			}
			map = new MISOM_SingleObjInNode<Double>(true, r.width, r.height, NumberManager.getDoubleManager());
//			map.setPathFinder(t.getPathFinder());
			w = t.addMap(map, r.x, r.y);
			System.out.println("adding: " + r.name + ", and id: " + w.ID);
			rects.put(w.ID, r);
		}

		void resetRects(MyRectangle[] recs) {
			t.clear();
			for (MyRectangle r : recs) {
				addRect(r);
			}
			t.rebuild();
			jp.setSize(t.getWidth() * PIXEL_EACH_CELL + 200, t.getHeight() * PIXEL_EACH_CELL + 200);
			jp.setPreferredSize(jp.getSize());
		}

		void applyMultiMapOffset(Point p) {
//			if (t.getxLeftTop() < 0)
			p.x += t.getxLeftTop();
//			if (t.getyLeftTop() < 0)
			p.y += t.getyLeftTop();
		}

		// TODO paint
		void paintRectsAndSections(Graphics g) {
			Color c;
			int minx, miny;
			minx = t.getxLeftTop();
			miny = t.getyLeftTop();
			win.setTitle("MINX: " + minx + ", MINY: " + miny);
			c = Color.GREEN.darker();
			if (t != null && t.getMisomsHeld() != null) {
//				for (MyRectangle r : rects) {
				t.getMapsLocatedInSpace().forEach((id, wrapper) -> {
					MyRectangle r;
					r = rects.get(id);
					if (r != null) {
						g.setColor(c);
						g.fillRect(//
								(r.x - minx) * PIXEL_EACH_CELL//
						, (r.y - miny) * PIXEL_EACH_CELL//
						, (r.width) * PIXEL_EACH_CELL//
						, (r.height) * PIXEL_EACH_CELL//
						);
						g.setColor(Color.BLUE);
						g.drawRect(//
								(r.x - minx) * PIXEL_EACH_CELL//
						, (r.y - miny) * PIXEL_EACH_CELL//
						, (r.width) * PIXEL_EACH_CELL//
						, (r.height) * PIXEL_EACH_CELL//
						);
						g.setColor(Color.BLACK);
						g.drawString(r.name, (((r.x - minx) + (r.width >> 1)) * PIXEL_EACH_CELL),
								(((r.y - miny) + (r.height >> 1))) * PIXEL_EACH_CELL);
						g.drawString(r.name, (r.x - minx) * PIXEL_EACH_CELL, (r.y - miny) * PIXEL_EACH_CELL);
					}
//					else
//						System.out.println("can't find the r with id :" + id);
				}//
				);
			}
			if (this.newRect != null) {
				g.setColor(Color.RED);
				g.drawRect(//
						((t.getxLeftTop() >= 0) ? this.newRect.x : //
								this.newRect.x - t.getxLeftTop()) * PIXEL_EACH_CELL,
						((t.getyLeftTop() >= 0) ? this.newRect.y : //
								this.newRect.y - t.getyLeftTop()) * PIXEL_EACH_CELL, //
						(this.newRect.width) * PIXEL_EACH_CELL//
						, (this.newRect.height) * PIXEL_EACH_CELL);
			}
			if (t != null && t.getRoot() != null) { paintSubdivision(g); }
			if (xPath != null && xPath.length > 0) {
				g.setColor(Color.BLACK); // .darker()
				g.drawPolyline(xPath, yPath, xPath.length);
			}
//			GraphicTools.paintGrid(g, PIXEL_EACH_CELL * t.getWidth(), PIXEL_EACH_CELL * t.getHeight(), PIXEL_EACH_CELL);
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
				g.drawString(Integer.toString(n.getDepth())//
						, (n.getXMiddle() - minx) * PIXEL_EACH_CELL //
						, (n.getYMiddle() - miny) * PIXEL_EACH_CELL);
				// draw subsections
				if (!n.isLeaf()) {
					g.setColor(Color.LIGHT_GRAY);
					halfDepth = n.getDepth() >> 1;
					// horizontal
					g.fillRect(//
							(n.getX() - minx) * PIXEL_EACH_CELL//
							, (n.getYMiddle() - (halfDepth + miny)) * PIXEL_EACH_CELL//
							, n.getWidth() * PIXEL_EACH_CELL//
							, (1 + (t.getMaxDepth() - n.getDepth())) // * PIXEL_EACH_CELL
					);
//vertical
					g.fillRect(//
							(n.getXMiddle() - (halfDepth + minx)) * PIXEL_EACH_CELL //
							, (n.getY() - miny) * PIXEL_EACH_CELL
							//
							, (1 + (t.getMaxDepth() - n.getDepth())) // * PIXEL_EACH_CELL //
							, n.getHeight() * PIXEL_EACH_CELL);
					n.forEachSubsection((s) -> {
						if (s != null)
							nodes.add(s);
					});
				}
			}
		}

		//

		void onMouseClick(MouseEvent me) {
			int mouseButton;
			Point whereClicked;
//			MyRectangle mapFound;
			MISOMLocatedInSpace<Double> mapWrapper;
//			MatrixInSpaceObjectsManager<Double> map;
			whereClicked = me.getPoint();
			whereClicked.x /= PIXEL_EACH_CELL;
			whereClicked.y /= PIXEL_EACH_CELL;
			// consider the offset
			applyMultiMapOffset(whereClicked);
			System.out.println("finding a map on " + whereClicked + " ..");
//			map = t.getMISOMContaining(whereClicked);
			mapWrapper = t.getMapLocatedContaining(whereClicked);
			System.out.println(".. map found: " + (mapWrapper == null ? "null" : rects.get(mapWrapper.ID)));
			mouseButton = me.getButton();
			System.out.println("me.getButton(): " + mouseButton);
			if (mouseButton == MouseEvent.BUTTON1 || mouseButton == MouseEvent.BUTTON2) {
				performPathfind(whereClicked);
			} else if (mouseButton == MouseEvent.BUTTON3) {
				if (mapWrapper != null) {
					t.removeMap(mapWrapper);
					rects.remove(mapWrapper.ID);
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

		void onStartDrawningNewRectangle(Point pointStart) {
//			this.isRectangleDrawning = true;
			this.newRect = null;
			this.pEndDrawningRect = null;
			this.pStartDrawningRect = pointStart;
			pointStart.x /= PIXEL_EACH_CELL;
			pointStart.y /= PIXEL_EACH_CELL;
			// consider the offset
			applyMultiMapOffset(pointStart);
		}

		void onEndDrawningNewRectangle(Point pointEnd, boolean isLeftClick) {
			this.pEndDrawningRect = pointEnd;
			// consider the offset
			applyMultiMapOffset(pointEnd);
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
					System.out.println("removing: " + this.rects.get(mlis.ID).getName());
					this.rects.remove(mlis.ID);
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
			addRect(newRect);
			this.newRect = null;
			jp.setSize(t.getWidth() * PIXEL_EACH_CELL + 200, t.getHeight() * PIXEL_EACH_CELL + 200);
			jp.setPreferredSize(jp.getSize());
			jp.repaint();
		}

		// TODO OOOOOOOOOOOOOOOOOOO
		// FARE PATH FINDING CHE IL CLICK SINISTRO IMPOSTA, IN MODO ALTERNATO,
		// INIZIO E FINE DEL PERCORSO .. E POI DISEGNA IL POLILINE

		void performPathfind(Point whereClicked) {
			List<Point> pathFound;
// consider the multi-map offset
//			applyMultiMapOffset(whereClicked); // YET APPLIED
			if (this.isStartPathfind) {
				isStartPathfind = false;
				this.startPathfind = whereClicked;
			} else {
				isStartPathfind = true;
				this.endPathfind = whereClicked;
			}
			if (this.startPathfind != null && this.endPathfind != null) {
				if (isPointwisePathfind) {
					pathFound = t.getPath(startPathfind, endPathfind);
				} else {
					ObjectShaped os;
					ShapeRectangle sr;
					os = new ObjectShapedBase_V4();
					sr = new ShapeRectangle(0.0, startPathfind.x - 3, startPathfind.y - 2, true, 7, 4);
					System.out.println(sr);
					os.setShape(sr);
//					System.out.println("os is: " + os);
					pathFound = t.getPath(os, endPathfind);
					System.out.println("pathfind shaped ended with " + //
							((pathFound == null) ? "null" : pathFound.size()) + " steps");
				}
				showPath(pathFound);
			} else
				System.out.println("no Path found: (from: " + this.startPathfind + ", to: " + this.endPathfind + ")");
		}

		void showPath(List<Point> pathFound) {
			int dx, dy;
			int len, halfPixelSize;
			final int[] i = { 0 };
			if (pathFound == null)
				return;
			len = pathFound.size();
			if (len == 0) {
				xPath = yPath = null;
				return;
			}
//			System.out.println("----- found path with " + len + " steps");
			xPath = new int[len];
			yPath = new int[len];
			dx = t.getxLeftTop();
			dy = t.getyLeftTop();
			halfPixelSize = PIXEL_EACH_CELL >> 1;
			pathFound.forEach(p -> {
				xPath[i[0]] = (p.x - dx) * PIXEL_EACH_CELL + halfPixelSize;
				yPath[i[0]++] = (p.y - dy) * PIXEL_EACH_CELL + halfPixelSize;
//				System.out.println("\t p: (x:" + p.x + ", y:" + p.y + ")");
			});
		}

		//

		protected static class ObjectShapedBase_V4 implements ObjectShaped {
			private static final long serialVersionUID = 65152087878L;
			AbstractShape2D shape;

			@Override
			public Integer getID() { return null; }

			@Override
			public void setShape(AbstractShape2D shape) { this.shape = shape; }

			@Override
			public AbstractShape2D getShape() { return shape; }
		}

		protected class MapRemover implements ObjCollector<MISOMLocatedInSpace<Double>> {
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