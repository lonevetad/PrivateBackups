package dataStructures;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Stack;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import dataStructures.MapTreeAVL.MapTreeAVLFactory;
import dataStructures.mtAvl.MapTreeAVLFull;
import dataStructures.mtAvl.MapTreeAVLLightweight;
import dataStructures.mtAvl.MapTreeAVLMinIter;
import dataStructures.mtAvl.MapTreeAVLQueuable;

/**REMOVED COMMENTS <p>
 * There's a bunch of ways to iterate all over this map.
 * <ul>
 * <li>{@link Iterator} : Using {@link IteratorReturnType} enumeration's values,
 * instances of {@link Iterator} can provide keys, values or both through
 * {@link Entry} instances. Use, for example, {@link #iteratorEntry()}.<br>
 * Note: iterators can iterate in backward sense: just specify {@code false} in
 * {@link #iterator(IteratorReturnType, boolean)}. on</li>
 * <li>{@link Map#forEach(BiConsumer)}: Use {@link ForEachMode} enumeration's
 * values to specify how to iterate through all keys, values or both through
 * {@link Entry} instances.</li>
 * </ul>
 * <p>
 * */

/**
 * This data structure provides three main features:
 * <ul>
 * <li>it's a {@link Map}</li>
 * <li>linear iteration (performed in {@code O(n)} time, where {@code n} is
 * {@link #size()}})</li>
 * <li>could be seen as a {@link SortedSet} of keys of {@link Entry} or a
 * {@link Queue} or {@link List} of keys, values or {@link Entry}</li>
 * </ul>
 * <p>
 * This class is an implementation of a map ({@link SortedMap} in particular)
 * and a instance of {@link Comparator} is needed to be provided in the
 * constructor to define key's order. If not, a {@link IllegalArgumentException}
 * will be thrown.
 * <p>
 * Specifying a value of {@link MapTreeAVLFactory.BehaviourOnKeyCollision}
 * enumeration on constructor, the map can behave differently on trying to add
 * yet existing keys: the old pair key-value could be conserved or fully
 * replaced.
 * <p>
 * This implementation provides a enhanced iteration: it's performed in linear
 * time, so it takes {@code O(n)} (where {@code n} is {@link #size()}}), at a
 * slight cost of time during {@code add} and {@code remove} operations and of
 * space (more or less {@code 4*X} bytes, where {@code X} is the amount of byte
 * needed to define a pointer).
 * <p>
 */
public interface MapTreeAVL<K, V> extends Serializable, SortedMap<K, V>, Function<K, V>, Iterable<Entry<K, V>> {

	public static interface MapTreeAVLFactory {
		public default <Key, Val> MapTreeAVL<Key, Val> newMap(Comparator<Key> comp) {
			return newMap(DEFAULT_BEHAVIOUR, comp);
		}

		public <Key, Val> MapTreeAVL<Key, Val> newMap(BehaviourOnKeyCollision b, Comparator<Key> comp);
	}

	/***/
	public static final boolean NORMAL_DIRECTION_WALKING = true;
	/**
	 * The default value of {@link BehaviourOnKeyCollision}:
	 * {@link BehaviourOnKeyCollision#Replace}.
	 */
	public static final BehaviourOnKeyCollision DEFAULT_BEHAVIOUR = BehaviourOnKeyCollision.Replace;
	/**
	 * The default value of {@link Optimizations}:
	 * {@link Optimizations#FullButHeavyNodes}.
	 */
	public static final Optimizations DEFAULT_OPTIMIZATION = Optimizations.MinMaxIndexIteration;

	// ENUMS

	/**
	 * Specify the action to perform if the key is yet present.<br>
	 * Currently, two values are designed:
	 * <ul>
	 * <li>KeepPrevious: keep the previous pair key-value, ignoring the given one,
	 * if the given key in comparatively equal.</li>
	 * <li>Replace: forgot and replace the old key-value inserted</li>
	 * <li>AddItsNotASet: put the pair, no matters. WARNING: USE AT YOUR OWN RISK:
	 * this option make the map unpredictable if more entries are added and forbids
	 * to perform {@link MapTreeAVL#keySet()}.</li>
	 * </ul>
	 */
	public static enum BehaviourOnKeyCollision {
		KeepPrevious, Replace, AddItsNotASet/* it's senseless */
	}

	/**
	 * Specify how to iterate over this map on methods like
	 * {@link MapTreeAVL #forEach(ForEachMode, BiConsumer)} (this is the most common
	 * version).<br>
	 * Pairs of key and values could be provided:
	 * <ul>
	 * <li>in a sorted order (sorted through the key, as would be natural in a
	 * common Binary Search Tree)</li>
	 * <li>ordered through the <i>insertion time</i>: this map could be used as a
	 * {@link java.util.Queue} or a {@link java.util.Stack}</li>
	 * <li>in "breadth first" order, useful for retrieving all element to be stored
	 * (like in a file) end re-added in a further moment optimizing the time
	 * (because the structure won't need to re-balance itself through rotating
	 * internal subtrees).</li>
	 * </ul>
	 */
	public static enum ForEachMode {
		SortedGrowing, SortedDecreasing, Queue, Stack, BreadthGrowing, BreadthDecreasing
	}

	/**
	 * This huge class can alter its behavior to tune some aspects.<br>
	 * Set its behavior choosing from one of this states.
	 * <p>
	 * The default value is {@link MapTreeAVLFactory#DEFAULT_OPTIMIZATION}.
	 */
	public static enum Optimizations implements MapTreeAVLFactory {

		/**
		 * A simple TreeMap, without additional informations to nodes: less memory (RAM)
		 * usage, less heavy.<br>
		 * - WARNING: random access and iterations are NOT optimized, just the memory
		 * usage and add/remove operations are a bit optimized!
		 */
		Lightweight(new MapTreeAVLFactory() {
			@Override
			public <Key, Val> MapTreeAVL<Key, Val> newMap(BehaviourOnKeyCollision b, Comparator<Key> comp) {
				return new MapTreeAVLLightweight<Key, Val>(b, comp);
			}
		}),
		/**
		 * Guarantee:
		 * <ul>
		 * <li>Can be used as a {@link Set} of keys.</li>
		 * <li><i>O(1)</i> time for {@link MapTreeAVL#peekMinimum()} and
		 * {@link MapTreeAVL#peekMaximum()} instead of <i>O(log(n))</i>.</li>
		 * <li><i>O(log(n))</i> time for {@link MapTreeAVL#removeMinimum()},
		 * {@link MapTreeAVL#removeMaximum()} and random access through an index, like
		 * {@link MapTreeAVL#get(int)}. The last ones has been enhanced since it was
		 * <i>O(n)</i>.</li>
		 * <li><i>O(n)</i> iteration through all key-value pairs instead of
		 * <i>O(n*log(n))</i>.</li>
		 * </ul>
		 */
		MinMaxIndexIteration(new MapTreeAVLFactory() {
			@Override
			public <Key, Val> MapTreeAVL<Key, Val> newMap(BehaviourOnKeyCollision b, Comparator<Key> comp) {
				return new MapTreeAVLMinIter<Key, Val>(b, comp);
			}
		}),
		/**
		 * This tree map can be used to create a {@link Queue}, but has not the features
		 * provided by {@link #MinMaxIndexIteration}, except for the iteration: it's
		 * still <i>O(n)</i>, but follows the "FIFO" ordering.<br>
		 * To make this map behave as a {@link Stack}, just call
		 * {@link MapTreeAVL#iteratorBackward()} instead of
		 * {@link MapTreeAVL#iterator()} and call
		 * {@link MapTreeAVL#forEach(ForEachMode, Consumer)} passing
		 * {@link ForEachMode#Stack} instead of {@link ForEachMode#Queue}
		 */
		ToQueueFIFOIterating(new MapTreeAVLFactory() {
			@Override
			public <Key, Val> MapTreeAVL<Key, Val> newMap(BehaviourOnKeyCollision b, Comparator<Key> comp) {
				return new MapTreeAVLQueuable<Key, Val>(b, comp);
			}
		}),

		/**
		 * All features of {@link #MinMaxIndexIteration} and
		 * {@link #ToQueueFIFOIterating} together, but at the price of more memory usage
		 * due to the additional informations stored.
		 */
		FullButHeavyNodes(new MapTreeAVLFactory() {
			@Override
			public <Key, Val> MapTreeAVL<Key, Val> newMap(BehaviourOnKeyCollision b, Comparator<Key> comp) {
				return new MapTreeAVLFull<Key, Val>(b, comp);
			}
		});

		//

		//
		Optimizations(MapTreeAVLFactory delegator) {
			this.delegator = delegator;
		}

		final MapTreeAVLFactory delegator;

		@Override
		public <Key, Val> MapTreeAVL<Key, Val> newMap(BehaviourOnKeyCollision b, Comparator<Key> comp) {
			return delegator.newMap(b, comp);
		}

	}

	public static <K, V> MapTreeAVL<K, V> newMap(Comparator<K> comp) throws IllegalArgumentException {
		return newMap(DEFAULT_OPTIMIZATION, DEFAULT_BEHAVIOUR, comp);
	}

	public static <K, V> MapTreeAVL<K, V> newMap(BehaviourOnKeyCollision b, Comparator<K> comp)
			throws IllegalArgumentException {
		return newMap(DEFAULT_OPTIMIZATION, b, comp);
	}

	public static <K, V> MapTreeAVL<K, V> newMap(Optimizations optimization, Comparator<K> comp)
			throws IllegalArgumentException {
		return newMap(optimization, DEFAULT_BEHAVIOUR, comp);
	}

	public static <K, V> MapTreeAVL<K, V> newMap(Optimizations optimization, BehaviourOnKeyCollision b,
			Comparator<K> comp) throws IllegalArgumentException {
		if (comp == null)
			throw new IllegalArgumentException("Comparator cannot be null");
		if (optimization == null)
			optimization = DEFAULT_OPTIMIZATION;
		if (b == null)
			b = DEFAULT_BEHAVIOUR;
		return optimization.newMap(b, comp);
	}

	//

	//

	public interface ExtracterValueFromNodeByIRT {
		public <Kk, Vv> Object extract(Entry<Kk, Vv> n);
	}

	/**
	 * Iterator and for-each function are both build in a general way. To specify
	 * what is desired to iterate on, specify
	 */
	public enum IteratorReturnType implements ExtracterValueFromNodeByIRT {
		Key(new ExtracterValueFromNodeByIRT() {
			@Override
			public <Kk, Vv> Object extract(Entry<Kk, Vv> n) {
				return n.getKey();
			}
		}), Value(new ExtracterValueFromNodeByIRT() {
			@Override
			public <Kk, Vv> Object extract(Entry<Kk, Vv> n) {
				return n.getValue();
			}
		}), Entry(new ExtracterValueFromNodeByIRT() {
			@Override
			public <Kk, Vv> Object extract(Entry<Kk, Vv> n) {
				return n;
			}
		});

		IteratorReturnType(ExtracterValueFromNodeByIRT e) {
			this.delegate = e;
		}

		final ExtracterValueFromNodeByIRT delegate;

		@Override
		public <Kk, Vv> Object extract(Entry<Kk, Vv> n) {
			return delegate.extract(n);
		}

		/*
		 * @SuppressWarnings("unchecked") protected static <E, Kk, Vv> E
		 * nodeToElement(IteratorReturnType irt, TreeAVL<Kk, Vv>.NodeAVL n) { E e; e =
		 * null; if (irt == Key) e = (E) n.k; else if (irt == Entry) e = (E) n; else if
		 * (irt == IteratorReturnType.Value) e = (E) n.v; else return null; }
		 */
	}

	public Comparator<K> getComparator();

	public Comparator<Entry<K, V>> getEntryComparator();

	@Override
	public Comparator<? super K> comparator();

	public V delete(K k);

	public Entry<K, V> getAt(int i);

	/**
	 * Similar to {@link #forEach(java.util.function.Consumer)}, but allow to
	 * iterate over the elements in different ways.<br>
	 * Only one subclass of the sub-package "mtAvl", {@link MapTreeAVLFull},
	 * implements all of those iteration modes, other provided subclasses implements
	 * just a subset of all modes.<br>
	 * If the mode is null, the natural iteration should be provided
	 */
	public void forEach(ForEachMode mode, Consumer<Entry<K, V>> action);

	/**
	 * Same as {@link #iterator()}, but iterating entries in decreasing order
	 */
	public Iterator<Entry<K, V>> iteratorBackward();

	/**
	 * Same as {@link #iterator()}, but iterating only keys
	 */
	public Iterator<K> iteratorKey();

	/**
	 * Same as {@link #iteratorKey()}, but iterating keys in decreasing order
	 */
	public Iterator<K> iteratorKeyBackward();

	/**
	 * Same as {@link #iterator()}, but iterating only values
	 */
	public Iterator<V> iteratorValue();

	/**
	 * Same as {@link #iteratorValue()}, but iterating values in decreasing order
	 */
	public Iterator<V> iteratorValueBackward();

	/**
	 * Retrieves but not removes the minimum value in this priority.<br>
	 * This method does not alter the collection, rather than
	 * {@link #removeMinimum()}.
	 */
	public Entry<K, V> peekMinimum();

	/**
	 * Retrieves but not removes the maximum value in this priority.<br>
	 * This method does not alter the collection, rather than
	 * {@link #removeMaximum()}.
	 */
	public Entry<K, V> peekMaximum();

	/**
	 * Retrieves AND removes the minimum value in this priority.<br>
	 * This method alters the collection, rather than {@link #peekMinimum()}.
	 */
	public Entry<K, V> removeMinimum();

	/**
	 * Retrieves AND removes the maximum value in this priority.<br>
	 * This method alters the collection, rather than {@link #peekMaximum()}.
	 */
	public Entry<K, V> removeMaximum();

	public Entry<K, V> getLastInserted();

	public Entry<K, V> getFirstInserted();

	/**
	 * Returns the lowest {@link Entry} that could be the root of the subtree
	 * holding the given pair of keys.<br Special cases:
	 * <ul>
	 * <li>If the tree is empty, <code>null</code> is returned</li>
	 * <li>If the keys are the same (a comparison is performed), then the leaf node
	 * best-matching a possible result is provided</li>
	 * </ul>
	 */
	public Entry<K, V> getLowesCommonAncestor(K k1, K k2);

	/**
	 * Merge the smaller tree in the bigger ones. The smaller one will be cleared
	 * after the call.
	 * <P>
	 * Returns the instance of the bigger tree upon success, {@code null} otherwise
	 * (i.e. the given tree is null).
	 */
	public MapTreeAVL<K, V> merge(MapTreeAVL<K, V> tOther);

	/**
	 * If the first key (first parameter), called {@code kOld}, is present, then
	 * that key is removed and its previously associated value will be associated
	 * with the second given key, called {@code kNew}.<br>
	 * Returns a instance of {@link Entry} associated with the previous key (first
	 * parameter).
	 *
	 * @param kOld the previous key, that will be removed
	 * @param kNew the new key that will be associated to {@code kOld}'s value, if
	 *             it's present, replacing it
	 * @return the previous pair key-value, if present
	 */
	public Entry<K, V> changeKey(K kOld, K kNew);

	/**
	 * If the first key (first parameter), called {@code key}, is present, then that
	 * associated value will be replaced with the given value (second
	 * parameter).<br>
	 * Returns a instance of {@link Entry} associated with the previous value.
	 *
	 * @param key  the key of the previously pair key-value.
	 * @param vNew If the given key is present, then the old value is replaced with
	 *             the new provided ones
	 * @return the previous pair key-value, if present
	 */
	public Entry<K, V> changeValue(K key, V vNew);

	public Object[] toArray();

	/**
	 * Convert this map into a back-mapping instance of {@link Queue} of keys.
	 */
	public Queue<K> toQueue();

	/**
	 * As like {@link #toQueue()}, but for {@link Entry} of both keys and values.
	 */
	public Queue<Entry<K, V>> toQueueEntry();

	/**
	 * As like {@link #toQueue()}, but for values.<br>
	 * Since keys are used to map values, a <i>"key extractor"</i> is required to
	 * get the key from a specific value.<br>
	 * e For further details, see this class's documentation: {@link MapTreeAVL}.
	 *
	 * @param keyExtractor way to extract the key from a value. Could simply return
	 *                     its hash or itself.
	 */
	public Queue<V> toQueueValue(Function<V, K> keyExtractor);

	/**
	 * Convert this map into a back-mapping instance of {@link List} of keys.
	 */
	public List<K> toList();

	/**
	 * As like {@link #toList()}, but for {@link Entry} of both keys and values.
	 */
	public List<Entry<K, V>> toListEntry();

	/**
	 * As like {@link #toValues()}, but for values. <br>
	 * See {@link #toQueueValue(Function)}.
	 */
	public List<V> toListValue(Function<V, K> keyExtractor);

	public Set<K> toSetKey();

	public Set<Entry<K, V>> toSetEntry();

	public Set<V> toSetValue(Function<V, K> keyExtractor);

	// ENDCOLLECTION CONVERTITORS

	@Override
	public void putAll(Map<? extends K, ? extends V> m);

	public boolean containsAll(Collection<?> c);

	public default Stream<Entry<K, V>> stream() {
//		return new StreamTreeAVL<Entry<K, V>>();
		throw new UnsupportedOperationException("Operation not implemented yet");
	}

}