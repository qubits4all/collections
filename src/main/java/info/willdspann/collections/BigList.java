package info.willdspann.collections;

import java.util.Iterator;

/**
 * Common interface for lists that support a large number of elements, as many as 2^63 - 1.
 *
 * @author Will D. Spann [willdspann@gmail.com]
 * @version 2.1
 */
public interface BigList<E> extends BigCollection<E> {

    void add(E item);

    void add(long index, E item);

    boolean addAll(BigCollection<? extends E> c);  // Optional

    boolean addAll(long index, BigCollection<? extends E> c);  // Optional

    E set(long index, E item);

    E remove(long index);

    E get(long index);

    long indexOf(Object o);

    /**
     * Returns the index of the last occurrence of the specified element in
     * this list, or -1 if this list does not contain the element (optional
     * operation). More formally, returns the highest index {@code i} such
     * that {@code (o==null ? get(i)==null : o.equals(get(i)))}, or -1 if
     * there is no such index.
     *
     * @param o element to search for
     * @return the index of the last occurrence of the specified element in
     * this list, or -1 if this list does not contain the element
     * @throws ClassCastException   if the type of the specified element is
     *                              incompatible with this list
     * @throws NullPointerException if the specified element is null and
     *                              this list does not permit null elements (optional)
     */
    long lastIndexOf(Object o);  // Optional

    /**
     * Returns a view of the portion of this list between the specified
     * {@code fromIndex}, inclusive, and {@code toIndex}, exclusive
     * (optional operation). (If {@code fromIndex} and {@code toIndex} are
     * equal, the returned list is empty.) The returned list is backed by
     * this list, so non-structural changes in the returned list are
     * reflected in this list, and vice-versa. The returned list supports
     * all of the optional list operations supported by this list.
     *
     * @param fromIndex low endpoint (inclusive) of the subList
     * @param toIndex   high endpoint (exclusive) of the subList
     * @return a view of the specified range within this list
     * @throws IndexOutOfBoundsException for an illegal endpoint index value
     *                                   ({@code fromIndex < 0 || toIndex > size || fromIndex > toIndex})
     */
    BigList<E> subList(long fromIndex, long toIndex);  // Optional

    /**
     * Returns an iterator over the elements in this list, in descending
     * order (optional operation).
     *
     * @return an iterator over the elements in this list, in descending
     * order
     */
    Iterator<E> descendingIterator();  // Optional

}
