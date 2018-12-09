package info.willdspann.collections;

/**
 * Common queue interface that supports a large number of elements, as many as 2^63 - 1.
 *
 * @author Will D. Spann [willdspann@gmail.com]
 * @version 2.1
 */
public interface BigQueue<E> extends BigCollection<E> {

    public boolean offer(E item);

    public boolean add(E item);

    public E poll();

    public E remove();

    public E peek();

    public E element();

    public BigList<E> toList();

}
