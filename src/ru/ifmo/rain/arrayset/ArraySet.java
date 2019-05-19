package ru.ifmo.rain.arrayset;

import static java.util.Collections.emptyList;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.TreeSet;

public class ArraySet<T extends Comparable<? super T>> extends AbstractSet<T> implements SortedSet<T> {

  private final List<T> base;
  private final Comparator<? super T> comparator;

  public ArraySet(Collection<T> collection, Comparator<? super T> comparator) {
    this.comparator = comparator;

    if (!collection.isEmpty()) {
      TreeSet<T> ts = new TreeSet<>(comparator);
      ts.addAll(collection);
      this.base = List.copyOf(ts);
    } else {
      this.base = emptyList();
    }
  }

  public ArraySet(Collection<T> collection) {
    this(collection, null);
  }

  public ArraySet() {
    this(emptyList(), null);
  }

  private ArraySet(List<T> list, Comparator<? super T> comp) {
    this.base = list;
    this.comparator = comp;
  }

  private int inv(int i) {
    return -(i + 1);
  }

  @Override
  public Iterator<T> iterator() {
    return base.iterator();
  }

  @Override
  public Comparator<? super T> comparator() {
    return comparator;
  }

  @Override
  public SortedSet<T> headSet(T toElement) {
    int toIndex = indexOfLowerThan(toElement);

    return subSet(0, toIndex);
  }

  @Override
  public SortedSet<T> tailSet(T fromElement) {
    int fromIndex = indexOfLowerThan(fromElement);

    return subSet(fromIndex, base.size());
  }

  @Override
  public SortedSet<T> subSet(T fromElement, T toElement) {
    Comparator<? super T> c = comparator != null ? comparator : Comparator.naturalOrder();

    int comparison = c.compare(fromElement, toElement);
    if (comparison > 0) {
      throw new IllegalArgumentException();
    } else if (comparison == 0) {
      return new ArraySet<T>(emptyList(), comparator);
    } else {
      return subSet(fromElement, true, toElement, false);
    }
  }

  public SortedSet<T> subSet(T fromElement, boolean fromInclusive, T toElement, boolean toInclusive) {
    int from = fromInclusive ? indexOfLowerThan(fromElement) : indexOfHigherThan(fromElement);
    int to = toInclusive ? indexOfHigherThan(toElement) : indexOfLowerThan(toElement);

    return subSet(from, to);
  }

  /**
   * @param from – low endpoint (inclusive) of the subSet
   * @param to – high endpoint (exclusive) of the subSet
   */
  public SortedSet<T> subSet(int from, int to) {
    return new ArraySet<>(base.subList(from, to), comparator);
  }

  private int indexOfHigherThan(T e) {
    return indexOf(e, true);
  }

  private int indexOfLowerThan(T e) {
    return indexOf(e, false);
  }

  private int indexOf(T e, boolean inclusive) {
    int i = Collections.binarySearch(base, e, comparator);

    if (i >= 0) {
      return inclusive ? i + 1 : i;
    } else {
      return inv(i);
    }
  }

  @Override
  public T first() {
    if (size() == 0) {
      throw new NoSuchElementException();
    }

    return base.get(0);
  }

  @Override
  public T last() {
    if (size() == 0) {
      throw new NoSuchElementException();
    }

    return base.get(size() - 1);
  }

  @Override
  @SuppressWarnings("unchecked")
  public boolean contains(Object o) {
    return Collections.binarySearch(base, (T) o, comparator) >= 0;
  }

  @Override
  public int size() {
    return base.size();
  }

}