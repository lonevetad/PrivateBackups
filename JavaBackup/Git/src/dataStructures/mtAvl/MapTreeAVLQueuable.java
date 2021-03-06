package dataStructures.mtAvl;

import java.util.Comparator;
import java.util.Iterator;
import java.util.function.Consumer;

import dataStructures.MapTreeAVL;

public class MapTreeAVLQueuable<K, V> extends MapTreeAVLIndexable<K, V> {
	private static final long serialVersionUID = 4806840784056L;

	@SuppressWarnings("unchecked")
	public MapTreeAVLQueuable(MapTreeAVL.BehaviourOnKeyCollision b, Comparator<K> comp)
			throws IllegalArgumentException {
		super(b, comp);
		lastInserted = (NodeAVL_Queuable) NIL;
		((NodeAVL_Queuable) NIL).nextInserted = ((NodeAVL_Queuable) NIL).prevInserted = (NodeAVL_Queuable) NIL;
	}

	protected NodeAVL_Queuable lastInserted; // stack-like

	//

	// TODO OVERRIDES

	/* inherited overrides yet ready: insertFixup */

	@Override
	protected NodeAVL newNode(K k, V v) {
		NodeAVL n;
		n = new NodeAVL_Queuable(k, v);
		// n.father = n.left = n.right = NIL;
		return n;
	}

	@Override
	public Entry<K, V> getLastInserted() {
		// if (size == 0) return null;
		return lastInserted;
	}

	@Override
	public Entry<K, V> getFirstInserted() {
		// if (size == 0) return null;
		return lastInserted.prevInserted;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void clear() {
		super.clear();
		lastInserted = (NodeAVL_Queuable) NIL;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected V put(NodeAVL nnn) {
//		boolean notAdded, side;
		int prevSize;
		V v;
		NodeAVL_Queuable n;
		n = (NodeAVL_Queuable) nnn;
		v = n.v;
		if (size == 0) {
			super.put(n);
			lastInserted = n;
			n.nextInserted = n.prevInserted = n;// self linking
			return null;
		}
		prevSize = this.size;
		v = super.put(n);
		if (prevSize != Integer.MAX_VALUE && prevSize != size) {
			// node really added
			n.nextInserted = lastInserted;
			lastInserted.prevInserted.nextInserted = n;
			n.prevInserted = lastInserted.prevInserted;
			lastInserted.prevInserted = n;
			lastInserted = n;
		}
		((NodeAVL_Queuable) NIL).nextInserted = ((NodeAVL_Queuable) NIL).prevInserted = (NodeAVL_Queuable) NIL;
		return v;
	}

	/**
	 * Use with care.
	 * <p>
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected V delete(NodeAVL nnn) {
		boolean hasLeft, hasRight;
		V v;
		NodeAVL_Queuable nToBeDeleted;
		if (root == NIL || nnn == NIL)
			return null;
		v = null;
		nToBeDeleted = (NodeAVL_Queuable) nnn;
		v = nToBeDeleted.v;
		if (size == 1 && comp.compare(root.k, nToBeDeleted.k) == 0) {
			v = super.delete(nToBeDeleted);
			lastInserted = (NodeAVL_Queuable) NIL;
			((NodeAVL_Queuable) NIL).prevInserted = ((NodeAVL_Queuable) NIL).nextInserted = (NodeAVL_Queuable) NIL;
			return v;
		}
		// real deletion starts here:
		hasLeft = nToBeDeleted.left != NIL;
		hasRight = nToBeDeleted.right != NIL;
		v = super.delete(nnn);
		if (hasLeft || hasRight) {
			NodeAVL_Queuable temp, nodeDeleted;
			nodeDeleted = hasRight ? (MapTreeAVLQueuable<K, V>.NodeAVL_Queuable) successorSorted(nToBeDeleted)
					: (MapTreeAVLQueuable<K, V>.NodeAVL_Queuable) predecessorSorted(nToBeDeleted);
			/*
			 * during the deletion, the value of "nnn" and "nodeDeleted" are swapped. Also
			 * the links must be swapped. But before update "lastInserted" if needed
			 */
			if (lastInserted == nodeDeleted)
				lastInserted = nodeDeleted.nextInserted;
			// swap
			temp = nodeDeleted.nextInserted;
			nodeDeleted.nextInserted = nToBeDeleted.nextInserted;
			nToBeDeleted.nextInserted = temp;
			temp = nodeDeleted.prevInserted;
			nodeDeleted.prevInserted = nToBeDeleted.prevInserted;
			nToBeDeleted.prevInserted = temp;

			// then remove the effectively node
			nodeDeleted.nextInserted.prevInserted = nodeDeleted.prevInserted;
			nodeDeleted.prevInserted.nextInserted = nodeDeleted.nextInserted;

			nodeDeleted.nextInserted = nodeDeleted.prevInserted = (NodeAVL_Queuable) NIL; // break links
		} else {
			// remember: the removed node is "nnn" itself
			if (lastInserted == nToBeDeleted)
				lastInserted = nToBeDeleted.nextInserted;
			nToBeDeleted.nextInserted.prevInserted = nToBeDeleted.prevInserted;
			nToBeDeleted.prevInserted.nextInserted = nToBeDeleted.nextInserted;
			nToBeDeleted.nextInserted = nToBeDeleted.prevInserted = (NodeAVL_Queuable) NIL; // break links
		}

		((NodeAVL_Queuable) NIL).nextInserted = ((NodeAVL_Queuable) NIL).prevInserted = (NodeAVL_Queuable) NIL;
		if (root == NIL) {
			lastInserted = (NodeAVL_Queuable) NIL;
			return v;
		}
		return v;
	}

	@Override
	public Entry<K, V> getAt(int i) {
		NodeAVL_Queuable n;
		if (i < 0 || i >= size)
			throw new IndexOutOfBoundsException("Index: " + i);
		if (size == 0)
			return null;
		if (size == 1)
			return root;
		n = lastInserted;
		// if(i == 0) return n;
		while ((--i >= 0) && ((n = n.nextInserted) != NIL))
			;
		return n;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected NodeAVL successorForIterator(NodeAVL n) {
		return n == NIL ? NIL : ((NodeAVL_Queuable) n).nextInserted;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected NodeAVL predecessorForIterator(NodeAVL n) {
		return n == NIL ? NIL : ((NodeAVL_Queuable) n).prevInserted;
	}

	@Override
	public boolean containsValue(Object value) {
		boolean valueNull;
		NodeAVL_Queuable n;
		n = lastInserted;
		valueNull = value == null;
		if (n != NIL)
			do {
				if ((valueNull && value == n.v) || (value.equals(n.v)))
					return true;
			} while ((n = n.nextInserted) != NIL);
		return false;
	}

	@Override
	public void forEach(ForEachMode mode, Consumer<Entry<K, V>> action) {
		NodeAVL_Queuable start, current;
		NodeAVL n;
		if (root != NIL && action != null) {
			switch (mode) {
			/*
			 * the sorted iterations are copy-pasted from superclass, BUT with a
			 * modification: the predecessor and successor calls are the "super"ones. This
			 * is due to avoid the dynamic binding to call this class's overrides.
			 */
			case SortedDecreasing:
				n = root;
				if (n.right != NIL)// descend to maximum
					while ((n = n.right) != NIL)
						;
				action.accept(n);
				while ((n = super.predecessorSorted(n)) != NIL)
					action.accept(n);
				break;
			case SortedGrowing:
				n = root;
				if (n.left != NIL)// descend to minimum
					while ((n = n.left) != NIL)
						;
				action.accept(n);
				while ((n = super.successorSorted(n)) != NIL)
					action.accept(n);
				break;
			case BreadthGrowing:
			case BreadthDecreasing:
				super.forEach(mode, action);
				return;
			case Stack:
				start = current = lastInserted;
				do {
					action.accept(current);
				} while ((current = current.nextInserted) != start);
				return;
			case Queue:
			default:
//				forEach(action);
				start = current = (lastInserted.prevInserted);
				do {
					action.accept(current);
				} while ((current = current.prevInserted) != start);
				return;
			}
		}
	}

	// TODO iterator cambiare le classi restituite

	@Override
	protected <E> Iterator<E> iteratorGeneric(IteratorReturnType irt, boolean directionIteration, E justANullElement) {
		return new Iterator_Queuable<E>(irt, directionIteration);
	}

	//

	@SuppressWarnings("unchecked")
	@Override
	protected void mergeOnSameClass(Iterator<Entry<K, V>> iter, MapTreeAVLLightweight<K, V> tThis,
			MapTreeAVLLightweight<K, V> tOther) {
		NodeAVL_Queuable n, ttNIL;
		ttNIL = (MapTreeAVLQueuable<K, V>.NodeAVL_Queuable) tThis.NIL;
		while (iter.hasNext()) {
			n = (NodeAVL_Queuable) iter.next();
			n.father = n.left = n.right = n.prevInserted = n.nextInserted = ttNIL;
			tThis.put(n);
		}
	}

	//

	// TODO CLASSES

	protected class NodeAVL_Queuable extends NodeAVL_Indexable {
		private static final long serialVersionUID = 65120329080000L;

		@SuppressWarnings("unchecked")
		public NodeAVL_Queuable(K k, V v) {
			super(k, v);
			prevInserted = nextInserted = (NodeAVL_Queuable) NIL;
		}

		public NodeAVL_Queuable prevInserted, nextInserted;
	}

	public class Iterator_Queuable<E> extends IteratorAVLGeneric<E> {

		public Iterator_Queuable() {
			this(true);
		}

		public Iterator_Queuable(boolean normalOrder) {
			super(normalOrder);
		}

		public Iterator_Queuable(IteratorReturnType irt) {
			super(irt);
		}

		public Iterator_Queuable(IteratorReturnType irt, boolean normalOrder) {
			super(irt, normalOrder);
		}

		@Override
		protected void restart() {
			jumps = 0;
			canRemove = false;
			current = end = normalOrder ? lastInserted : lastInserted.prevInserted;
		}

		@Override
		public boolean hasNext() {
			return (size > 0) && (current != end || jumps == 0);
		}
	}
}