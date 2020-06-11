package dataStructures.isom;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import dataStructures.ListMapped;
import geometry.AbstractObjectsInSpaceManager;
import geometry.AbstractShape2D;
import geometry.ObjectLocated;
import geometry.ObjectShaped;
import geometry.PathFinderIsom;
import geometry.PathOptimizer;
import geometry.implementations.shapes.ShapeLine;
import geometry.pointTools.impl.ObjCollector;
import tools.LoggerMessages;
import tools.NumberManager;
import tools.PathFinder;

public interface InSpaceObjectsManager<Distance extends Number>
//extends dataStructures.graph.GraphSimple<NodeIsom, D>
		extends AbstractObjectsInSpaceManager {

	//

	// TODO GETTER
	public LoggerMessages getLog();

	public NumberManager<Distance> getNumberManager();

	public PathOptimizer<Point> getPathOptimizer();

	public Set<ObjectLocated> getAllObjectLocated();

	public PathFinder<Point, ObjectLocated, Distance> getPathFinder();

	//

	// TODO SETTER

	public void setPathOptimizer(PathOptimizer<Point> pathOptimizer);

	public void setLog(LoggerMessages log);

	public void setPathFinder(PathFinderIsom<Point, ObjectLocated, Distance> pathFinder);

	public void setNumberManager(NumberManager<Distance> numberManager);

	//

	// TODO OTHER

	/**
	 * Could seems odd, but it's useful for {@link PathFinderIsom}s'
	 * implementations.
	 */
	public NodeIsom getNodeAt(Point location);

	public abstract ObjectLocated getObjectLocated(Integer ID);

	public abstract boolean removeAllObjects();

	/**
	 * Perform an action to each adjacent of a given node. That action should take
	 * into account not only the adjacent node, but also the distance from the given
	 * node and the specific adjacent.
	 */
	public abstract void forEachAdjacents(NodeIsom node, BiConsumer<NodeIsom, Distance> adjacentDistanceConsumer);

	//

	// TODO to-do path find

	/** Convert the given list of internal node points to a list of points. */
	public static <OL extends ObjectLocated> List<Point> listNodeToPoint(List<OL> lni) {
		ListMapped<OL, Point> lmapping;
		List<Point> ret;
		lmapping = new ListMapped<>(lni, ni -> ni.getLocation()); // map them
		ret = new LinkedList<>();
		for (Point p : lmapping) {
			ret.add(p);
		}
		return ret;
	}

	/**
	 * See
	 * {@link PathFinder#getPath(ObjectShaped, Object, NumberManager, Predicate)}.
	 */
	public default List<Point> getPath(Point start, Point destination, Predicate<ObjectLocated> isWalkableTester) {
//		List<NodeIsom> lni;
//		lni = this.getPathFinder().getPath(getNodeAt(start), getNodeAt(destination), numberManager);
//		return listNodeToPoint(lni);
		List<Point> path;
		path = this.getPath(getNodeAt(start), getNodeAt(destination), getPathFinder(), getNumberManager(),
				isWalkableTester);
		return this.getPathOptimizer().optimizePath(path);// new ListMapped<>(path, ni -> ni.getLocation()));
	}

	/**
	 * See {@link #getPath(ObjectShaped, Point, Predicate)}.
	 */
	public default List<Point> getPath(Point start, Point destination) {
		return this.getPath(start, destination, null);
	}

	/**
	 * See
	 * {@link PathFinder#getPath(ObjectShaped, Object, NumberManager, Predicate)}.
	 */
	public default List<Point> getPath(ObjectShaped objRequiringTo, Point destination,
			Predicate<ObjectLocated> isWalkableTester) {
//		List<NodeIsom> lni;
//		lni = this.getPathFinder().getPath(objRequiringTo, getNodeAt(destination), numberManager);
//		return listNodeToPoint(lni);
		List<Point> path;
		path = this.getPath(objRequiringTo, getNodeAt(destination), getPathFinder(), getNumberManager(),
				isWalkableTester);
		return this.getPathOptimizer().optimizePath(path);// new ListMapped<>(path, ni -> ni.getLocation()));
	}

	/**
	 * See {@link #getPath(ObjectShaped, Point, Predicate)}.
	 */
	public default List<Point> getPath(ObjectShaped objRequiringTo, Point destination) {
		return this.getPath(objRequiringTo, destination, null);
	}

	// TODO todo add getPath with predicate filter

	/**
	 * Refers to
	 * {@link InSpaceObjectsManagerImpl#findInPath(AbstractShape2D, dataStructures.isom.ObjLocatedCollectorIsom, List)}
	 * . <br>
	 * Sub-implementations of this class must provide a way to define
	 * {@link ObjCollector}.
	 */
	public abstract Set<ObjectLocated> findInPath(AbstractShape2D areaToLookInto, Predicate<ObjectLocated> objectFilter,
			List<Point> path);

	/**
	 * Queries all objects located in the given area, if any, moving that area along
	 * a specific path, that requires at least two point (the starting point must be
	 * provided, the last point is the end).
	 */
	@Override
	public default Set<ObjectLocated> findInPath(AbstractShape2D areaToLookInto, ObjCollector<ObjectLocated> collector,
			List<Point> path) {
		Iterator<Point> iter;
		ShapeLine subpath;
		Line2D line;
		Point pstart, pend;
		if (collector == null || path == null || path.size() < 2)
			return null;
		collector.restart();
		line = new Line2D.Double();
		iter = path.iterator();
		pstart = iter.next();
		while (iter.hasNext()) {
			pend = iter.next();
			line.setLine(pstart, pend);
			subpath = new ShapeLine(line);
			this.runOnShape(subpath, collector);
			pstart = pend;
		}
		return collector.getCollectedObjects();
	}
}