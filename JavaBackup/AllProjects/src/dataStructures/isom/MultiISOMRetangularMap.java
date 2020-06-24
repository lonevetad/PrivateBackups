package dataStructures.isom;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import dataStructures.MapTreeAVL;
import dataStructures.SetMapped;
import dataStructures.isom.matrixBased.MatrixInSpaceObjectsManager;
import dataStructures.isom.matrixBased.MatrixInSpaceObjectsManager.CoordinatesDeltaForAdjacentNodes;
import dataStructures.isom.matrixBased.ObjLocatedCollectorMultimatrix;
import dataStructures.isom.pathFinders.PathFinderIsomDijkstra;
import geometry.AbstractMultiOISMRectangular;
import geometry.AbstractShape2D;
import geometry.ObjectLocated;
import geometry.implementations.shapes.ShapeRectangle;
import tests.tDataStruct.Test_MultiISOMRetangularMap_V1;
import tools.Comparators;
import tools.MathUtilities;
import tools.Stringable;

/**
 * TODO of 10/06/2020
 * <p>
 * Take inspiration from {@link MatrixInSpaceObjectsManager} to implement
 * {@link InSpaceObjectsManager} and {@link Test_MultiISOMRetangularMap_V1} to
 * implement {@link AbstractMultiOISMRectangular}.
 * <p>
 * To implement {@link NodeIsomProvider#getNodeAt(Point)}, use the
 * {@link Test_MultiISOMRetangularMap_V1
 * 
 * 
 * #getMapContaining(Point)} code.
 */
public class MultiISOMRetangularMap<Distance extends Number> extends AbstractMultiOISMRectangular<Distance>
		implements InSpaceObjectsManager<Distance> {
	private static final long serialVersionUID = -879653210489L;
	public static final int MAXIMUM_SUBMAPS_EACH_SECTION = 4, MINIMUM_DIMENSION_MAP = 4;

	public MultiISOMRetangularMap() { this(MAXIMUM_SUBMAPS_EACH_SECTION); }

	public MultiISOMRetangularMap(int maximumSubmapsEachSection) {
		super();
		if (maximumSubmapsEachSection < 1) {
			log.log("Incorrect number of maximum submaps on each section: " + maximumSubmapsEachSection);
			throw new IllegalArgumentException(
					"Incorrect number of maximum submaps on each section: " + maximumSubmapsEachSection);
		}
		this.maximumSubmapsEachSection = maximumSubmapsEachSection;
		mapsLocatedInSpace = MapTreeAVL.newMap(MapTreeAVL.Optimizations.MinMaxIndexIteration,
				Comparators.INTEGER_COMPARATOR);
		mapsAsList = mapsLocatedInSpace.toListValue(r -> r.IDInteger);
		misomsHeld = new SetMapped<>(mapsLocatedInSpace.toSetValue(w -> w.IDInteger), w -> w.misom);
		setObjectsAddedMap(MapTreeAVL.newMap(MapTreeAVL.Optimizations.Lightweight, Comparators.INTEGER_COMPARATOR));
		shapeRect = null;// new ShapeRectangle()
		clear();
		setPathFinder(new PathFinderIsomDijkstra<>(this));
//		this.pathOptimizer = new PathOptimizer<Point>() 
	}

	protected int idProg = 0;
//	protected PathFinderIsomAdapter<NodeIsom, D> pathFinder;
	// from isom
	protected Map<Integer, ObjectLocated> objectsAddedMap;
	protected Set<ObjectLocated> objectsAddedSet;
	// multi-misom stuffs
	// protected boolean neverBuilt;
	protected int maximumSubmapsEachSection;
	protected int maxDepth, xLeftTop, yLeftTop, xRightBottom, yRightBottom, width, height;
	protected NodeQuadtreeMultiISOMRectangular root;
	protected final MapTreeAVL<Integer, MISOMLocatedInSpace<Distance>> mapsLocatedInSpace;
	protected final Set<MatrixInSpaceObjectsManager<Distance>> misomsHeld;
	protected final List<MISOMLocatedInSpace<Distance>> mapsAsList;
	protected ShapeRectangle shapeRect;

	//

	// GETTER

	@Override
	public AbstractShape2D getBoundingShape() { return shapeRect; }

	public Set<MatrixInSpaceObjectsManager<Distance>> getMisomsHeld() { return misomsHeld; }

	// getters for tests

	public int getxLeftTop() { return xLeftTop; }

	/**
	 * Returns the maximum amount of {@link MatrixInSpaceObjectsManager} contained
	 * in each quadtree's sub-nodes allowed before the node is splitted into smaller
	 * areas.
	 */
	public int getMaximumSubmapsEachSection() { return maximumSubmapsEachSection; }

	public int getyLeftTop() { return yLeftTop; }

	public NodeQuadtreeMultiISOMRectangular getRoot() { return root; }

	public int getMaxDepth() { return maxDepth; }

	public int getxRightBottom() { return xRightBottom; }

	public int getyRightBottom() { return yRightBottom; }

	public int getWidth() { return width; }

	public int getHeight() { return height; }

	public Map<Integer, MISOMLocatedInSpace<Distance>> getMapsLocatedInSpace() { return mapsLocatedInSpace; }

	public ShapeRectangle getShapeRect() { return shapeRect; }

	//

	// SETTER

	/** Sets the map holding all objects in this space. */
	protected void setObjectsAddedMap(Map<Integer, ObjectLocated> objectsAdded) {
		this.objectsAddedMap = objectsAdded;
		if (objectsAdded == null) // here and below, update the set
			this.objectsAddedSet = null;
		else {
			if (objectsAdded instanceof MapTreeAVL<?, ?>)
				this.objectsAddedSet = ((MapTreeAVL<Integer, ObjectLocated>) objectsAdded).toSetValue(ol -> ol.getID());
			else
				this.objectsAddedSet = new SetMapped<>(objectsAdded.entrySet(), e -> e.getValue());
		}
	}

	//

	// TODO OTHER METHODS

	//

	/**
	 * Calls {@link #toCachingMatrixes(int)} passing the
	 * {@link #getMaximumSubmapsEachSection()}.
	 */
	public MultiISOMRetangularCaching<Distance> toCachingMatrixes() {
		return toCachingMatrixes(maximumSubmapsEachSection);
	}

	/**
	 * Returns an instance of {@link MultiISOMRetangularCaching}, see its
	 * documentation for further informations.
	 */
	public MultiISOMRetangularCaching<Distance> toCachingMatrixes(int maximumSubmapsEachSection) {
		return new MultiISOMRetangularCaching<>(maximumSubmapsEachSection);
	}

	/**
	 * See {@link #getNodeAt(int, int)}.
	 * <p>
	 * {@inheritDoc}
	 */
	@Override
	public NodeIsom getNodeAt(Point location) { return getNodeAt(location.x, location.y); }

	/**
	 * Get the {@link NodeIsom} at the specific coordinates.<br>
	 * Those coordinates are to be meant <b>absolute</b>, <i>not</i> relatively to
	 * the containing map (a map is a {@link MatrixInSpaceObjectsManager}). <br>
	 * For instance, passing the point <code>{x:5, y:10}</code> as parameter (and
	 * assuming that this point is contained by the map located at
	 * <code>{x:1, y:3}</code>) will result in the {@link NodeIsom} located at
	 * <code>{x:5, y:10} - {x:1, y:3} = {x:4, y:7}</code> so the {@link NodeIsom}
	 * located at <code>{x:4, y:7}</code> on that map will be returned.
	 */
	@Override
	public NodeIsom getNodeAt(int x, int y) {
		MISOMLocatedInSpace<Distance> ml;
		ml = getMapLocatedContaining(x, y);
		if (ml == null)
			return null;
		// consider the offset
		return ml.misom.getNodeAt(x - ml.x, y - ml.y);
	}

	@Override
	public void forEachNode(Consumer<NodeIsom> action) {
		this.mapsLocatedInSpace.forEach((i, mw) -> mw.misom.forEachNode(action));
	}

	@Override
	public Set<ObjectLocated> getAllObjectLocated() { return this.objectsAddedSet; }

	@Override
	public ObjectLocated getObjectLocated(Integer ID) { return this.objectsAddedMap.get(ID); }

	@Override
	public void forEachAdjacents(NodeIsom node, BiConsumer<NodeIsom, Distance> adjacentDistanceConsumer) {
		MatrixInSpaceObjectsManager<Distance> misom;
		Point p;
		p = new Point();
		for (CoordinatesDeltaForAdjacentNodes c : MatrixInSpaceObjectsManager.VALUES_CoordinatesDeltaForAdjacentNodes) {
			p.x = node.x + c.dx;
			p.y = node.y + c.dy;
			misom = getMISOMContaining(p);
			if (misom != null) {
				adjacentDistanceConsumer.accept(misom.getNodeAt(p), misom.getWeightManager().fromDouble(c.weight));
			}
		}
	}

	/**
	 * Apply an action to each stored map and the point (top-left corner) which it's
	 * located in.
	 */
	public void forEachMap(BiConsumer<MatrixInSpaceObjectsManager<Distance>, Point> action) {
		final Point offset;
		offset = new Point();
		this.mapsLocatedInSpace.forEach((id, wrapper) -> {
			offset.x = wrapper.x;
			offset.y = wrapper.y;
			action.accept(wrapper.misom, offset);
		});
	}

	@Override
	public ObjLocatedCollectorIsom<Distance> newObjLocatedCollector(Predicate<ObjectLocated> objectFilter) {
		return new ObjLocatedCollectorMultimatrix<Distance>(this, objectFilter);
	}

	@Override
	public Set<ObjectLocated> findInPath(AbstractShape2D areaToLookInto, Predicate<ObjectLocated> objectFilter,
			List<Point> path) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public boolean add(ObjectLocated o) {
		MatrixInSpaceObjectsManager<Distance> m;
		if (o == null)
			return false;
		m = getMISOMContaining(o.getLocation());
		if (m == null)
			return false;
		m.add(o);
		return true;
	}

	@Override
	public boolean contains(ObjectLocated o) {
		MatrixInSpaceObjectsManager<Distance> m;
		if (o == null)
			return false;
		m = getMISOMContaining(o.getLocation());
		if (m == null)
			return false;
		return m.contains(o);
	}

	@Override
	public boolean remove(ObjectLocated o) {
		MatrixInSpaceObjectsManager<Distance> m;
		if (o == null)
			return false;
		m = getMISOMContaining(o.getLocation());
		if (m == null)
			return false;
		return m.remove(o);
	}

	//

	// TODO multi-isom's methods

	//

	public void removeAllMaps() {
		mapsLocatedInSpace.clear();
		root = null;
		clearDimensionCache();
	}

	protected void clearDimensionCache() {
		// neverBuilt = true;
		shapeRect = null;
		maxDepth = 0;
		xLeftTop = yLeftTop = xRightBottom = yRightBottom = width = height = 0;
		this.objectsAddedMap.clear();
	}

	public void clear() { removeAllMaps(); }

	/**
	 * See {@link #getMapLocatedContaining(int, int)}, but returning the
	 * {@link MatrixInSpaceObjectsManager} held.
	 */
	public MatrixInSpaceObjectsManager<Distance> getMISOMContaining(Point p) {
		MISOMLocatedInSpace<Distance> mw;
		mw = getMapLocatedContaining(p);
		return (mw == null) ? null : mw.misom;
	}

	/** See {@link #getMapLocatedContaining(int, int)}. */
	public MISOMLocatedInSpace<Distance> getMapLocatedContaining(Point p) {
		return getMapLocatedContaining(p.x, p.y);
	}

	/**
	 * Returns a {@link MISOMLocatedInSpace} that can contains the given point
	 * (those coordinates are absolute, not relative in any way, nor this class's
	 * space represented).
	 */
	public MISOMLocatedInSpace<Distance> getMapLocatedContaining(int x, int y) {
		NodeQuadtreeMultiISOMRectangular n, prev;
		List<MISOMLocatedInSpace<Distance>> submaps;
		if (getRoot() == null)
			return null;
		n = prev = getRoot();
		// traverse the tree
		while (!(n == null || n.isLeaf())) {
			prev = n;
			if (x >= n.xMiddle) { // east
				n = (y >= n.yMiddle) ? n.sse : n.sne;
			} else {// west
				n = (y >= n.yMiddle) ? n.ssw : n.snw;
			}
		}
		// get the collection of submaps
		submaps = (n != null) ? n.submaps : prev.submaps;
		if (submaps == null)
			return null;
		// if any holds that point, then return it
		for (MISOMLocatedInSpace<Distance> r : submaps) {
			if (r.contains(x, y)) // MathUtilities.isInside(r, p))//
				return r;
		}
		return null; // Error 404
	}

	/**
	 * Add a map (a {@link MatrixInSpaceObjectsManager}) which is located, into the
	 * space, at the given point.<br>
	 * This point is the map's offset because the map's class's indexes (i.e. those
	 * passed to {@link MatrixInSpaceObjectsManager#getNodeAt(Point)})) are relative
	 * to its origin.
	 * <p>
	 * See {@link #getNodeAt(int, int)} to more informations and examples about the
	 * meaning of <i>offset</i> and <i>coordinates</i> both relative and absolute.
	 */
	public MISOMLocatedInSpace<Distance> addMap(MatrixInSpaceObjectsManager<Distance> map, int x, int y) {
		int c;
		MISOMLocatedInSpace<Distance> r;
		if (map == null || map.getWidth() < 1 || map.getWidth() < 1)
			return null;
		r = new MISOMLocatedInSpace<Distance>(this, map, x, y);
		c = updateBoundingBox(r);
		mapsLocatedInSpace.put(r.IDInteger, r);
		if (c > 0)
			rebuild();
		else
			addNotRebuilding(r);
		return r;
	}

	/** See {@link #addMap(MatrixInSpaceObjectsManager, int, int)}. */
	public MISOMLocatedInSpace<Distance> addMap(MatrixInSpaceObjectsManager<Distance> map, Point locationLeftTop) {
		if (map == null || map.getWidth() < 1 || map.getWidth() < 1)
			return null;
		return addMap(map, locationLeftTop.x, locationLeftTop.y);
	}

	public void addMaps(Collection<MISOMLocatedInSpace<Distance>> mapsList) {
		int[] cc = { -1 };
		mapsList.forEach(r -> {
			int c;
			if (r != null) {
				c = updateBoundingBox(r);
				if (c >= 0) {
					if (cc[0] < c)
						cc[0] = c;
					mapsLocatedInSpace.put(r.IDInteger, r);
				}
			}
		});
		if (cc[0] > 0)
			rebuild();
		else
			mapsList.forEach(this::addNotRebuilding);
	}

	public void removeMap(MISOMLocatedInSpace<Distance> r) {
		if (mapsLocatedInSpace.containsKey(r.IDInteger)) {
			mapsLocatedInSpace.remove(r.IDInteger);
			recalculateBoundingBox();
			/*
			 * TODO should perform a more fine action like adding maps
			 * ("if the map change the bounding box or not")
			 */
			rebuild();
		}
	}

	public void removeMapLocatedIn(MatrixInSpaceObjectsManager<Distance> map, Point location) {
		this.removeMapLocatedIn(map, location.x, location.y);
	}

	public void removeMapLocatedIn(MatrixInSpaceObjectsManager<Distance> map, int x, int y) {
		MISOMLocatedInSpace<Distance> r;
		r = getMapLocatedContaining(x, y);
		if (r != null) { removeMap(r); }
	}

	public void removeMaps(Collection<MISOMLocatedInSpace<Distance>> mapsList) {
		boolean[] cc = { false };
		mapsList.forEach(r -> {
			if (mapsLocatedInSpace.containsKey(r.IDInteger)) {
				cc[0] = true;
				mapsLocatedInSpace.remove(r.IDInteger);
			}
		});
		if (cc[0]) {
			recalculateBoundingBox();
			rebuild();
		}
	}

	protected void resetShape(boolean mustReallocate) {
		if (mustReallocate)
			shapeRect = new ShapeRectangle(0.0, 0, 0, true, 0, 0);
		shapeRect.setWidth(width);
		shapeRect.setHeight(height);
		shapeRect.setLeftTopCorner(getxLeftTop(), getyLeftTop());
	}

	protected void recalculateBoundingBox() {
		clearDimensionCache();
		mapsAsList.forEach(this::updateBoundingBox);
	}

	protected int updateBoundingBox(MISOMLocatedInSpace<Distance> r) {
		boolean changed;
		int t;
		if (r.width < 1 || r.height < 1) { return -1; }
		if (shapeRect == null) {
			xLeftTop = r.x;
			yLeftTop = r.y;
			width = r.width;
			height = r.height;
			xRightBottom = (r.x + r.width) - 1;
			yRightBottom = (r.y + r.height) - 1;
			resetShape(true);
			return 1;
		}
		changed = false;
		if (r.x < getxLeftTop()) {
			changed = true;
			xLeftTop = r.x;
		}
		if (r.y < getyLeftTop()) {
			changed = true;
			yLeftTop = r.y;
		}
		t = r.x + r.width;
		if (t > xRightBottom) {
			changed = true;
			xRightBottom = t;
		}
		t = r.y + r.height;
		if (t > yRightBottom) {
			changed = true;
			yRightBottom = t;
		}
		this.width = xRightBottom - getxLeftTop();
		this.height = yRightBottom - getyLeftTop();
		if (changed || getRoot() == null) {
			resetShape(false);
			return 1;
		}
		return 0;
	}

	protected void rebuild() {
		this.maxDepth = 0;
		this.root = rebuild(null, mapsAsList, //
				getxLeftTop(), getyLeftTop(), xRightBottom, yRightBottom, width, height//
				, getxLeftTop() + (width >> 1)//
				, getyLeftTop() + (height >> 1)); // clear all
	}

	protected NodeQuadtreeMultiISOMRectangular rebuild(NodeQuadtreeMultiISOMRectangular father,
			List<MISOMLocatedInSpace<Distance>> submaps, //
			int xLeftTop, int yLeftTop, int xRightBottom, int yRightBottom, int width, int height, //
			int xMiddle, int yMiddle) {
		final int mxw, mxe, myn, mys, widthWest, widthEst, heightNorth, heightSouth, yMiddlemmmm, xMiddlemmmm;
		NodeQuadtreeMultiISOMRectangular n;
		List<MISOMLocatedInSpace<Distance>> snw, sne, ssw, sse;
		n = new NodeQuadtreeMultiISOMRectangular(father);
		if (this.getMaxDepth() < n.depth) { this.maxDepth = n.depth; }
		n.x = xLeftTop;
		n.y = yLeftTop;
		n.width = width;
		n.height = height;
		n.xMiddle = xMiddle;
		n.yMiddle = yMiddle;
		if (submaps.size() <= maximumSubmapsEachSection //
				|| width <= MINIMUM_DIMENSION_MAP || height <= MINIMUM_DIMENSION_MAP) {
			n.submaps = submaps;
			return n;
		}
		snw = new ArrayList<>(maximumSubmapsEachSection);
		sne = new ArrayList<>(maximumSubmapsEachSection);
		ssw = new ArrayList<>(maximumSubmapsEachSection);
		sse = new ArrayList<>(maximumSubmapsEachSection);
		yMiddlemmmm = yMiddle;
		xMiddlemmmm = xMiddle;
		mxw = xLeftTop + ((widthWest = (xMiddle - xLeftTop)) >> 1);
		myn = yLeftTop + ((heightNorth = (yMiddle - yLeftTop)) >> 1);
		mxe = ++xMiddle + ((widthEst = 1 + (xRightBottom - xMiddle)) >> 1);
		mys = ++yMiddle + ((heightSouth = 1 + (yRightBottom - yMiddle)) >> 1);
		xMiddle = xMiddlemmmm;
		yMiddle = yMiddlemmmm;
		submaps.forEach(r -> {
			if (MathUtilities.intersects(xLeftTop, yLeftTop, widthWest, heightNorth, r.x, r.y, r.width, r.height)) {
				snw.add(r);
			}
			if (MathUtilities.intersects(xMiddlemmmm + 1, yLeftTop, widthEst, heightNorth, r.x, r.y, r.width,
					r.height)) {
				sne.add(r);
			}
			if (MathUtilities.intersects(xMiddlemmmm + 1, yMiddlemmmm + 1, widthEst, heightSouth, r.x, r.y, r.width,
					r.height)) {
				sse.add(r);
			}
			if (MathUtilities.intersects(xLeftTop, yMiddlemmmm + 1, widthWest, heightSouth, r.x, r.y, r.width,
					r.height)) {
				ssw.add(r);
			}
		});
		if (snw.size() > 0)
			n.snw = rebuild(n, snw, //
					xLeftTop, yLeftTop, xMiddlemmmm, yMiddlemmmm, // corner points
					widthWest, heightNorth, //
					mxw, myn); // middle point
		if (sne.size() > 0)
			n.sne = rebuild(n, sne, //
					(xMiddlemmmm + 1), yLeftTop, xRightBottom, yMiddlemmmm, // corner points
					widthEst, heightNorth, // dimensions
					mxe, myn); // middle point
		if (ssw.size() > 0)
			n.ssw = rebuild(n, ssw, //
					xLeftTop, yMiddlemmmm + 1, xMiddlemmmm, yRightBottom, // corner points
					widthWest, heightSouth, // dimensions
					mxw, mys); // middle point
		if (sse.size() > 0)
			n.sse = rebuild(n, sse, //
					xMiddlemmmm + 1, yMiddlemmmm + 1, xRightBottom, yRightBottom, // corner points
					widthEst, heightSouth, // dimensions
					mxe, mys); // middle point
		return n;
	}

	/**
	 * EMPTY METHOD
	 * <P>
	 * Should be a method to add a map without a full rebuild, but I don't know how
	 * to do it in a smart way.
	 */
	protected void addNotRebuilding(MISOMLocatedInSpace<Distance> map) {
		if (getRoot() == null)
			rebuild();
		else
			root = addNotRebuilding(map, getRoot());
	}

	protected NodeQuadtreeMultiISOMRectangular newNodeWith(MISOMLocatedInSpace<Distance> map,
			NodeQuadtreeMultiISOMRectangular currentNode) {
		NodeQuadtreeMultiISOMRectangular newNode;
		newNode = new NodeQuadtreeMultiISOMRectangular(currentNode);
		if (this.getMaxDepth() < newNode.depth) { this.maxDepth = newNode.depth; }
		newNode.submaps = new ArrayList<>(maximumSubmapsEachSection);
		newNode.submaps.add(map);
		return newNode;
	}

	// the recursive part
	// returns the node given or a newly created one
	protected NodeQuadtreeMultiISOMRectangular addNotRebuilding(MISOMLocatedInSpace<Distance> map,
			NodeQuadtreeMultiISOMRectangular currentNode) {
		if (currentNode.isLeaf()) {
			if (currentNode.submaps.size() < this.maximumSubmapsEachSection) {
				// just add and nothing more
				currentNode.submaps.add(map);
			} else {
				// else, build ricoursively
				currentNode.submaps.add(map);
				currentNode = rebuild(currentNode.father, currentNode.submaps, currentNode.x, currentNode.y,
						currentNode.x + currentNode.width, currentNode.y + currentNode.width, currentNode.width,
						currentNode.height, currentNode.xMiddle, currentNode.yMiddle);
				if (this.getMaxDepth() < currentNode.depth) { this.maxDepth = currentNode.depth; }
			}

			// V2, later discarded
//			List<MISOMWrapper<Distance>> newSetSubmaps;
//			if (currentNode.isLeaf()) {
//				newSetSubmaps = currentNode.submaps;
//				if (newSetSubmaps.size() >= this.maximumSubmapsEachSection) {
//					final List<MISOMWrapper<Distance>> tempSubmaps; // a final field is required
//					tempSubmaps = new LinkedList<>();
//					newSetSubmaps.forEach(mw_ -> tempSubmaps.add(mw_));
//					newSetSubmaps = tempSubmaps;
//				} // else just add and nothing more
//				newSetSubmaps.add(r);
//				// rebuild if necessary
//				currentNode = rebuild(currentNode.father, newSetSubmaps, currentNode.x, currentNode.y,
//						currentNode.x + currentNode.w, currentNode.y + currentNode.w, currentNode.w, currentNode.h,
//						currentNode.xMiddle, currentNode.yMiddle);
//			} else		{
		} else {
			int hw, hh, x_w_1, y_h_1;
			hw = currentNode.width >> 1;
			hh = currentNode.height >> 1;
			x_w_1 = currentNode.x + hw + 1;
			y_h_1 = currentNode.y + hh + 1;
			// for each subsection ..
			if ((currentNode.snw != null && currentNode.snw.intersectsWithMap(map)) //
					|| // or that area does NOT exists BUT could hold the new map
					(currentNode.snw == null && //
							MathUtilities.intersects(currentNode.x, currentNode.y, hw, hh, map.x, map.y, map.width,
									map.height))) {
				currentNode.snw = (currentNode.snw == null) ? newNodeWith(map, currentNode)
						: addNotRebuilding(map, currentNode.snw);
			}
			if ((currentNode.sne != null && currentNode.sne.intersectsWithMap(map)) //
					|| // or that area does NOT exists BUT could hold the new map
					(currentNode.sne == null && //
							MathUtilities.intersects(x_w_1, currentNode.y, currentNode.width - hw, hh, map.x, map.y,
									map.width, map.height))) {
				currentNode.sne = (currentNode.sne == null) ? newNodeWith(map, currentNode)
						: addNotRebuilding(map, currentNode.sne);
			}
			if ((currentNode.ssw != null && currentNode.ssw.intersectsWithMap(map)) //
					|| // or that area does NOT exists BUT could hold the new map
					(currentNode.ssw == null && //
							MathUtilities.intersects(currentNode.x, y_h_1, hw, currentNode.height - hh, map.x, map.y,
									map.width, map.height))) {
				currentNode.ssw = (currentNode.ssw == null) ? newNodeWith(map, currentNode)
						: addNotRebuilding(map, currentNode.ssw);
			}
			if ((currentNode.sse != null && currentNode.sse.intersectsWithMap(map)) //
					|| // or that area does NOT exists BUT could hold the new map
					(currentNode.sse == null && //
							MathUtilities.intersects(x_w_1, y_h_1, currentNode.width - hw, currentNode.height - hh,
									map.x, map.y, map.width, map.height))) {
				currentNode.sse = (currentNode.sse == null) ? newNodeWith(map, currentNode)
						: addNotRebuilding(map, currentNode.sse);
			}
		}
		return currentNode;
	}

	//

	//

	// TODO CLASSES

	//

	//

	/**
	 * Wrapper of a {@link MatrixInSpaceObjectsManager} that specify where that
	 * matrix-map is located in space: this class it's a {@link Rectangle} in
	 * fact.<br>
	 * Mainly used for internal stuffs like:
	 * <ul>
	 * <li>{@link MultiISOMRetangularMap#addMap(MatrixInSpaceObjectsManager, Point)}</li>
	 * <li>{@link NodeIsomProviderCachingMISOM}</li>
	 * <li>Produced {@link NodeIsomProviderCachingMISOM#getMisomAt(Point)}</li>
	 * </ul>
	 * <p>
	 * NOTE: as like as it's said in
	 * {@link MultiISOMRetangularMap#getNodeAt(int, int)}, each matrix's coordinates
	 * are relative to it, so a point like <code>{x:5, y:10}</code> of a map located
	 * at <code>{x:1, y:3}</code> is translated (using a notation abuse) to
	 * <code>{x:5, y:10} - {x:1, y:3} = {x:4, y:7}</code>, so the {@link NodeIsom}
	 * located at <code>{x:4, y:7}</code> will be taken into account.
	 */
	public static class MISOMLocatedInSpace<Dd extends Number> extends Rectangle implements ObjectLocated {
		private static final long serialVersionUID = 560804524L;
		public static final Function<MISOMLocatedInSpace<?>, Integer> ID_EXTRACTOR = w -> w.IDInteger;
		public static final Function<MISOMLocatedInSpace<?>, MatrixInSpaceObjectsManager<?>> MISOM_EXTRACTOR = w -> w.misom;

		/** See calls the constructor considering */
		public MISOMLocatedInSpace(MultiISOMRetangularMap<Dd> multi, MatrixInSpaceObjectsManager<Dd> misom, int x,
				int y) {
			super(x, y, misom.getWidth(), misom.getHeight());
			this.misom = misom;
			this.multi = multi;
			this.IDInteger = (this.ID = this.multi.idProg++);
		}

		/**
		 * See {@link MISOMLocatedInSpace} for informations about the given
		 * {@link Point}.
		 */
		public MISOMLocatedInSpace(MultiISOMRetangularMap<Dd> multi, MatrixInSpaceObjectsManager<Dd> misom, Point p) {
			this(multi, misom, p.x, p.y);
		}

		public final int ID;
		public final Integer IDInteger;
		protected final MultiISOMRetangularMap<Dd> multi;
		public final MatrixInSpaceObjectsManager<Dd> misom;

		@Override
		public Integer getID() { return IDInteger; }

		public MatrixInSpaceObjectsManager<Dd> getMatrix() { return misom; }

		public MatrixInSpaceObjectsManager<Dd> getISOM() { return misom; }
	}

	/**
	 * Node subsection of this quad-tree like class.<br>
	 * Public just for test stuffs
	 */
	public class NodeQuadtreeMultiISOMRectangular implements Stringable {
		private static final long serialVersionUID = 1L;
		protected int x, y, width, height, xMiddle, yMiddle, depth;
		List<MISOMLocatedInSpace<Distance>> submaps;
		NodeQuadtreeMultiISOMRectangular father, snw, sne, ssw, sse;

		NodeQuadtreeMultiISOMRectangular(NodeQuadtreeMultiISOMRectangular father) {
			this.father = father;
			submaps = null;
			depth = (father == null) ? 1 : (father.depth + 1);
		}

		public int getX() { return x; }

		public int getY() { return y; }

		public int getWidth() { return width; }

		public int getHeight() { return height; }

		public int getXMiddle() { return xMiddle; }

		public int getYMiddle() { return yMiddle; }

		public int getDepth() { return depth; }

		public List<MISOMLocatedInSpace<Distance>> getSubmaps() { return submaps; }

//		public NodeMultiISOMRectangular getSnw() { return snw; }
//		public NodeMultiISOMRectangular getSne() { return sne; }
//		public NodeMultiISOMRectangular getSsw() { return ssw; }
//		public NodeMultiISOMRectangular getSse() { return sse; }

		public boolean isLeaf() { return submaps != null; }

		/** Beware of <code>null</code>s! */
		public void forEachSubsection(Consumer<NodeQuadtreeMultiISOMRectangular> action) {
			action.accept(snw);
			action.accept(sne);
			action.accept(ssw);
			action.accept(sse);
		}

		public void recalculateEachSubsection(
				Function<NodeQuadtreeMultiISOMRectangular, NodeQuadtreeMultiISOMRectangular> action) {
			snw = action.apply(snw);
			sne = action.apply(sne);
			ssw = action.apply(ssw);
			sse = action.apply(sse);
		}

		@Override
		public String toString() { return "Node--[x=" + x + ", y=" + y + ", w=" + width + ", h=" + height + "]"; }

		public boolean intersectsWithMap(MISOMLocatedInSpace<Distance> map) {
			return MathUtilities.intersects(x, y, width, height, map.x, map.y, map.width, map.height);
		}

		@Override
		public void toString(StringBuilder sb, int tabLevel) {
			sb.append('\n');
			addTab(sb, tabLevel);
			sb.append("Node at level ").append(tabLevel).append(", is:");
			addTab(sb, tabLevel + 1);
			sb.append(toString());
			addTab(sb, tabLevel);
			sb.append("and has subparts:");
			if (isLeaf()) {
				int t;
				sb.append(" is leaf:n");
				t = 1 + tabLevel;
				this.submaps.forEach(r -> {
//					sb.append('\n');
					addTab(sb, t);
					sb.append(r);
				});
//				sb.append(t);
			} else {
				tabLevel++;
				if (snw != null) { snw.toString(sb, tabLevel); }
				if (sne != null) { sne.toString(sb, tabLevel); }
				if (ssw != null) { ssw.toString(sb, tabLevel); }
				if (sse != null) { sse.toString(sb, tabLevel); }
			}
		}
	} // end NodeQuadtreeMultiISOMRectangular
}