package dataStructures.graph.pathfind;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import dataStructures.MapTreeAVL;
import dataStructures.PriorityQueueKey;
import dataStructures.graph.GraphSimple;
import dataStructures.graph.GraphSimple.ColorDijkstra;
import dataStructures.graph.GraphSimpleAsynchronized;
import dataStructures.graph.PathFindStrategy;
import dataStructures.graph.PathGraph;

public class PathFindAStar<E> implements PathFindStrategy<E> {
	private static final long serialVersionUID = 56409561023300330L;

	public PathFindAStar(BiFunction<E, E, Double> heuristic) {
		super();
		this.heuristic = heuristic;
	}

	protected BiFunction<E, E, Double> heuristic;

	public BiFunction<E, E, Double> getHeuristic() {
		return heuristic;
	}

	public PathFindAStar<E> setHeuristic(BiFunction<E, E, Double> heuristic) {
		this.heuristic = heuristic;
		return this;
	}

	//

	@SuppressWarnings("unchecked")
	@Override
	public PathGraph<E> getPath(GraphSimple<E> graph, E start, E dest) {
//		final double distanceTotal;
		PathGraph<E> p;
		GraphSimpleAsynchronized<E>.NodeGraphSimpleAsynchronized s, d;
		NodeInfoAStar<E> ss, dd;
		final Map<E, NodeInfoAStar<E>> nodeInfos;
		PriorityQueueKey<NodeInfoAStar<E>, Double> frontier;
		UsynchronizedAdjacentForEacherAStar<E> forAdjacents;
		{ // scope for the heuristic, to pick the variable as soon as possible and then
			// free its space on the stack
			BiFunction<E, E, Double> h;
			h = heuristic;
			if (h == null)
				return null;

			nodeInfos = MapTreeAVL.newMap(MapTreeAVL.Optimizations.Lightweight,
					MapTreeAVL.BehaviourOnKeyCollision.KeepPrevious, graph.getComparatorElements());
			frontier = new PriorityQueueKey<>((i1, i2) -> graph.getCompNodeGraph().compare(i1.thisNode, i2.thisNode),
					GraphSimple.DOUBLE_COMPARATOR, no -> no.fScore);
			forAdjacents = new UsynchronizedAdjacentForEacherAStar<E>(nodeInfos, frontier, h);
		}
		s = (GraphSimpleAsynchronized<E>.NodeGraphSimpleAsynchronized) graph.getNode(start);
		d = (GraphSimpleAsynchronized<E>.NodeGraphSimpleAsynchronized) graph.getNode(dest);
		if (s == null || d == null || d == s)
			return null;
		ss = new NodeInfoAStar<E>(s);
		ss.father = ss;
		ss.distFromFather = ss.distFromStart = 0.0;
		ss.fScore = Double.valueOf(0);
		frontier.put(ss);
		nodeInfos.put(start, ss);
		dd = new NodeInfoAStar<E>(d);
		nodeInfos.put(dest, dd);

		while ((!frontier.isEmpty()) && ((dd.father == null) ||
		/*
		 * continue if there's a path to reach the destination shorter to the already
		 * found one. This condition will force to exit from the cycle if the "minimum"
		 * node is already the destination because of the lack of equality check
		 */
				(frontier.peekMinimum().getKey().fScore < dd.fScore))//
		) {
			final NodeInfoAStar<E> n;
			n = frontier.removeMinimum().getKey();
			n.color = ColorDijkstra.Black;
			/*
			 * do not waste time computing nodes that have longer path of the alredy
			 * discovered ones
			 */
			if (dd.father == null || dd.fScore > n.fScore) {
				forAdjacents.setCurrentNode(n);
				n.thisNode.forEachAdjacents(forAdjacents);
			} else
				/*
				 * destination has got a father and the node with MINIMUM "distance from start"
				 * has a distance greater than the destination itself -> iterating is useless ->
				 * empty the frontier
				 */
				frontier.clear();
		}
		if (dd.father == null)
			return null;
		//
		nodeInfos.clear();
		p = new PathGraph<E>();
//		distanceTotal = dd.distFromStart;
		while (dd != ss) {
			p.addStep(dd.thisNode.getElem(), dd.distFromFather);
			dd = dd.father;
		}
		p.setStartStep(s.getElem());
//		p.setDistanceTotal(distanceTotal);
		return p;
	}

//	protected static boolean existsAShorterPath()

	//

	// subclasses

	protected static class NodeInfoAStar<E> {
		protected ColorDijkstra color;
		protected double distFromStart, distFromFather;
		protected Double fScore;
		protected GraphSimple<E>.NodeGraph thisNode;
		protected NodeInfoAStar<E> father;

		protected NodeInfoAStar(GraphSimple<E>.NodeGraph thisNode) {
			this.thisNode = thisNode;
			color = ColorDijkstra.White;
			distFromStart = distFromFather = 0.0;
			fScore = null;
			father = null;
		}
	}

	private static class UsynchronizedAdjacentForEacherAStar<E>
			implements BiConsumer<GraphSimple<E>.NodeGraph, Integer> {

		private NodeInfoAStar<E> currentNode;
		private final Map<E, NodeInfoAStar<E>> nodeInfos;
		private final PriorityQueueKey<NodeInfoAStar<E>, Double> frontier;
		private final BiFunction<E, E, Double> heuristic;

		UsynchronizedAdjacentForEacherAStar(Map<E, NodeInfoAStar<E>> nodeInfos,
				PriorityQueueKey<NodeInfoAStar<E>, Double> frontier, BiFunction<E, E, Double> heuristic) {
			this.nodeInfos = nodeInfos;
			this.frontier = frontier;
			this.heuristic = heuristic;
		}

		public void setCurrentNode(NodeInfoAStar<E> n) {
			this.currentNode = n;
		}

//		public void accept(GraphSimpleAsynchronized<E>.NodeGraphSimpleAsynchronized nod, Integer distToAdj) {
		@SuppressWarnings("unchecked")
		@Override
		public void accept(GraphSimple<E>.NodeGraph nnn, Integer distToAdj) {
			double distToNo, distToAdjDouble;
			E e;
			GraphSimpleAsynchronized<E>.NodeGraphSimpleAsynchronized no;
			NodeInfoAStar<E> neighbourInfo;
			no = (GraphSimpleAsynchronized<E>.NodeGraphSimpleAsynchronized) nnn;
			e = no.getElem();
			if (nodeInfos.containsKey(e))
				neighbourInfo = nodeInfos.get(e);
			else
				nodeInfos.put(e, neighbourInfo = new NodeInfoAStar<E>(no));

//			if (neighbourInfo.color == ColorDijkstra.Black) // equivalent of being in closed set
//				return;
			distToAdjDouble = distToAdj;
			distToNo = distToAdjDouble + currentNode.distFromStart;
			// create the new node or try to re-opening it
			if (neighbourInfo.father == null || distToNo < neighbourInfo.distFromStart) {
				final Double newDistanceFromStart, fScore;
				// update
				newDistanceFromStart = Double.valueOf(distToNo);
				neighbourInfo.father = currentNode;
				neighbourInfo.distFromFather = distToAdjDouble;
				neighbourInfo.distFromStart = newDistanceFromStart;
				fScore = newDistanceFromStart
						+ this.heuristic.apply(currentNode.thisNode.getElem(), neighbourInfo.thisNode.getElem());
				if (neighbourInfo.color == ColorDijkstra.White) {
					// track that's in open set, e.g. it has been seen almost one time
					neighbourInfo.color = ColorDijkstra.Grey;
					neighbourInfo.fScore = fScore;
					// add on queue
					frontier.put(neighbourInfo);
				} else // it's grey, it's actually in the queue / open set
					frontier.alterKey(neighbourInfo, nodd -> nodd.fScore = fScore);
			}
		}
	}
}