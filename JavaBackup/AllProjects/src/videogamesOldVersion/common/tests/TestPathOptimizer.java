package common.tests;

import java.awt.Point;
import java.util.Arrays;
import java.util.LinkedList;

import common.mainTools.mOLM.PathOptimizer;
import common.mainTools.mOLM.abstractClassesMOLM.AbstractPathOptimizer;

public class TestPathOptimizer {

	public TestPathOptimizer() {
	}

	public static void main(String[] args) {
		int i, len;
		int[][] allXs, allYs;
		LinkedList<Point> res, original;
		AbstractPathOptimizer po;

		po = new PathOptimizerOLD();

		allXs = new int[][] { //
				new int[] { 4, 5 }, // y = x
				new int[] { 4, 5, 6 }, // y = x
				new int[] { 4, 5, 6, 7, 8 }, // y = x
				new int[] {
						// y = 2*x + 3
						0, 1, 2
						// y = -x + 9
						, 3, 4, 5 },
				//
				new int[] {
						// y = 3x
						0, 1, 2, 3, 4
						// y = 2x +4
						, 6, 8, 10, 11, 12
						// y = -x + 40
						, 13, 15, 20, 22, 25, 40, 43 } };

		allYs = new int[][] { //
				allXs[0], // y = x
				allXs[1], // y = x
				allXs[2], // y = x
				new int[] {
						// y = 2*x + 3
						3, 5, 7
						// y = -x + 8
						, 6, 5, 4 },
//
				new int[] {
						// y = 3x
						0, 3, 6, 9, 12
						// y = 2x +4
						, 16, 20, 24, 26, 28
						// y = -x + 40
						, 27, 25, 20, 18, 15, 0, -3 } };

		len = allXs.length;
		i = -1;
		while (++i < len) {
			original = fillListPoint(allXs[i], allYs[i]);
			System.out.println(i + " - Starting with:");
			printList(original);
			res = po.optimizePath(original, AbstractPathOptimizer.ccPoint);
			System.out.println("then: ");
			printList(res);
			System.out.println("\n\n----------");
		}
	}

	static void printList(LinkedList<Point> l) {
		System.out.println(Arrays.toString(l.toArray()));
	}

	static LinkedList<Point> fillListPoint(int[] xs, int[] ys) {
		int i, len;
		LinkedList<Point> res;
		res = new LinkedList<>();
		len = xs.length;
		i = -1;
		while (++i < len)
			res.add(new Point(xs[i], ys[i]));
		return res;
	}
}