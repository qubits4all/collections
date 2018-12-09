package info.willdspann.collections;

import java.util.Iterator;
import java.util.RandomAccess;

/**
 * List interface for lists that support a large number of elements (as many as 2^63 - 1) in addition to random
 * access-patterns (i.e., querying or modifying an element by index is efficient regardless of the list's size).
 *
 * @author Will D. Spann [willdspann@gmail.com]
 * @version 2.1
 */
public interface BigArrayList<E> extends BigCollection<E>, RandomAccess {

    E set(long index, E item);

    E remove(long index);

    E get(long index);

    long indexOf(Object o);

    Iterator<E> withNullsIterator();

    void ensureCapacity(long minCapacity);

    long getCapacity();
}
