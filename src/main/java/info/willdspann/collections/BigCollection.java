package info.willdspann.collections;

/**
 * Common interface for large collections that may have as many as 2^63 - 1 elements.
 *
 * @author Will D. Spann [willdspann@gmail.com]
 * @version 1.1
 */
public interface BigCollection<E> extends Iterable<E> {

    boolean remove(Object o);  // Optional

    boolean removeAll(BigCollection<?> c);  // Optional

    /**
     * Retains only the elements in this list that are contained in the
     * specified collection (optional operation). In other words, removes
     * from this list all the elements that are not contained in the
     * specified collection.
     *
     * @param c collection containing elements to be retained in this list
     * @return {@code true} if this list changed as a result of the call
     * @throws UnsupportedOperationException if the {@code retainAll}
     *                                       operation is not supported by this list
     * @throws ClassCastException            if the class of an element of this list
     *                                       is incompatible with the specified collection
     * @throws NullPointerException          if this list contains a null element
     *                                       and the specified collection does not permit null elements
     *                                       (optional), or if the specified collection is null
     */
    boolean retainAll(BigCollection<?> c);  // Optional

    void clear();  // Optional

    boolean contains(Object o);

    boolean containsAll(BigCollection<?> c);

    long size();

    boolean isEmpty();

}
