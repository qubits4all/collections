package info.willdspann.collections.list;

import info.willdspann.collections.BigArrayList;
import info.willdspann.collections.BigCollection;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An unrolled linked-list implementation supporting a large number of elements (i.e., 2^63 - 1).
 * <p>
 * For a good description of how an unrolled linked-list works, see Derrick Coetzee's article
 *  <a href="http://blogs.msdn.com/devdev/archive/2005/08/22/454887.aspx">"Unrolled linked lists"</a>
 * </p>
 *
 * @author Will D. Spann [willdspann@gmail.com]
 * @version 1.4.1
 * @see <a href="https://en.wikipedia.org/wiki/Unrolled_linked_list">Unrolled Linked-List</a>
 */
public final class UnrolledArrayList<E> implements BigArrayList<E> {
    private static final int DEFAULT_NODE_CAP = 64;

    /** Node capacity */
    private final int nodeCap;
    /** Current number of non-null items */
    private long size;
    /** Current capacity of list */
    private long cap;
    /** Structural modification count */
    private int modCount;
    /** Node marking the start of the linked-list of element array nodes. */
    private Node head;

    public UnrolledArrayList() {
        this.nodeCap = DEFAULT_NODE_CAP;
        // Create first Node w/ its array allocated
        this.head = new Node(null, true);
        this.size = 0;
        this.cap = this.nodeCap;
        this.modCount = 0;
    }

    /**
     * @param initialCapacity the minimum initial capacity. Must be greater
     *                        than 0.
     * @throws IllegalArgumentException if {@code initialCapacity} is less
     *                                  than 0.
     */
    public UnrolledArrayList(long initialCapacity) {
        this();

        if (initialCapacity >= 0)
            ensureCapacity(initialCapacity);
        else {
            throw new IllegalArgumentException("initialCapacity must be "
                    + ">= 0");
        }
    }

    /**
     * Stores the given item at the specified position in this
     * {@code BigArrayList}.
     * <p>
     * Version: 1.1
     *
     * @param index the position to store {@code item} at in this
     *              {@code BigArrayList}.
     * @param item  the non-null item to be stored at the position
     *              {@code index}.
     * @return the item previously stored at position {@code index}, which
     * may be {@code null}.
     * @throws NullPointerException if {@code item} is {@code null}.
     */
    public E set(long index, E item) {
        // Check validity of arguments:
        if (index < 0L)
            throw new IndexOutOfBoundsException();
        if (item == null)
            throw new NullPointerException();

        if (index >= this.cap)
            ensureCapacity(index + 1L);

        int[] arrIndex = new int[1];
        Node node = getNodeFor(index, arrIndex);
        return node.set(arrIndex[0], item);
    }

    /**
     * {@inheritDoc}
     */
    public E remove(long index) {
        if (index < 0L || index >= this.cap)
            throw new IndexOutOfBoundsException();

        int[] arrIndex = new int[1];
        Node node = getNodeFor(index, arrIndex);
        return node.delete(arrIndex[0]);
    }

    /**
     * {@inheritDoc}
     */
    public boolean remove(Object o) {
        Iterator<E> it = iterator();
        E item = null;
        while (it.hasNext()) {
            item = it.next();
            if (item == o || item.equals(o)) {
                it.remove();
                return true;
            }
        }
        return false;
    }

    /**
     * Removes all of the elements in the given {@code BigCollection} if they're contained in this list, returning
     * {@code true} if this list was changed as a result of this call.
     *
     * @return {@code true} if this {@code BigArrayList} changed as a result
     * of the call; {@code false} otherwise. Note: If {@code c} only
     * contains {@code null} items, this method will return
     * {@code false}, because the removal of a {@code null} item does not
     * change a {@code BigArrayList}.
     *
     * @throws NullPointerException if {@code c} is {@code null}.
     */
    public boolean removeAll(BigCollection<?> c) {
        // Check for invalid argument:
        if (c == null)
            throw new NullPointerException();
        else if (c.isEmpty())
            return false;

        E item = null;
        boolean changed = false;
        /* Iterate over this list, removing any item contained in 'c', using
         * Iterator's remove() method: */
        for (Iterator<E> it = iterator(); it.hasNext(); ) {
            item = it.next();
            if (c.contains(item)) {
                it.remove();
                changed = true;
            }
        }
        return changed;
    }

    /**
     * Removes all of the elements in the given {@code Collection} if they're contained in this list, returning
     * {@code true} if this list was changed as a result of this call.
     *
     * @return {@code true} if this {@code BigArrayList} changed as a result
     * of the call; {@code false} otherwise. Note: If {@code c} only
     * contains {@code null} items, this method will return
     * {@code false}, because the removal of a {@code null} item does not
     * change a {@code BigArrayList}.
     *
     * @throws NullPointerException if {@code c} is {@code null}.
     */
    public boolean removeAll(Collection<?> c) {
        // Check for invalid argument:
        if (c == null)
            throw new NullPointerException();
        else if (c.isEmpty())
            return false;

        E item = null;
        boolean changed = false;
        /* Iterate over this list, removing any item contained in 'c', using
         * Iterator's remove() method: */
        for (Iterator<E> it = iterator(); it.hasNext(); ) {
            item = it.next();
            if (c.contains(item)) {
                it.remove();
                changed = true;
            }
        }
        return changed;
    }

    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException if {@code c} is {@code null}.
     */
    public boolean retainAll(BigCollection<?> c) {
        E item;
        boolean changed = false;
        for (Iterator<E> it = iterator(); it.hasNext(); ) {
            item = it.next();
            if (!c.contains(item)) {
                it.remove();
                changed = true;
            }
        }
        return changed;
    }

    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException if {@code c} is {@code null}.
     */
    public boolean retainAll(Collection<?> c) {
        E item;
        boolean changed = false;
        for (Iterator<E> it = iterator(); it.hasNext(); ) {
            item = it.next();
            if (!c.contains(item)) {
                it.remove();
                changed = true;
            }
        }
        return changed;
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        this.modCount++;
        // Recreate first Node w/ its array allocated
        this.head = new Node(null, true);
        this.size = 0;
        this.cap = (long) this.nodeCap;
    }

    /**
     * {@inheritDoc}
     */
    public E get(long index) {
        if (index < 0L || index >= this.cap)
            throw new IndexOutOfBoundsException();

        int[] arrIndex = new int[1];
        Node node = getNodeFor(index, arrIndex);
        return node.get(arrIndex[0]);
    }

    /**
     * {@inheritDoc}
     */
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    /**
     * {@inheritDoc}
     */
    public boolean containsAll(BigCollection<?> l) {
        if (l == null || l.isEmpty())
            return false;

        for (Object o : l) {
            if (!contains(o))
                return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean containsAll(Collection<?> c) {
        if (c == null || c.isEmpty())
            return false;

        for (Object o : c) {
            if (!contains(o))
                return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    /*
     * TODO: Improve this method's efficiency by using a custom non-null traversal that skips over empty nodes, but
     *   still includes their capacity in the running index calculation.
     */
    public long indexOf(Object o) {
        E item;
        long i = 0L;
        for (Iterator<E> it = withNullsIterator(); it.hasNext(); i++) {
            item = it.next();
            if (item == null ? o == null : item.equals(o))
                return i;
        }
        return -1L;  // Indicate 'o' wasn't found
    }


    /**
     * {@inheritDoc}
     *
     * Impl. Notes: The list's capacity 'this.cap' will always be an even
     *   multiple of 'this.nodeCap'.
     */
    public void ensureCapacity(long minCapacity) {
        this.modCount++;
        if (minCapacity > this.cap) {
            // Calculate no. of Nodes
            long numNodes = this.cap / (long) this.nodeCap;

            // Calculate new capacity:
            long newCapacity =            // ((numNodes * 3L)/2L + 1L)
                    (long) this.nodeCap * (((numNodes * 3L) >>> 1) + 1L);

            /* If 'newCapacity' < 'minCapacity', set 'newCapacity' to either
             * 'minCapacity' or the next multiple of 'this.nodeCap' larger
             * than 'minCapacity'. */
            if (newCapacity < minCapacity) {
                long part = minCapacity % (long) this.nodeCap;
                if (part == 0L)
                    newCapacity = minCapacity;
                else
                    newCapacity = minCapacity + (long) this.nodeCap - part;
            }

            // Get last allocated Node
            Node node = getNodeFor(this.cap - 1L, new int[1]);

            // Calculate number of Nodes to add
            long nodesToAdd = (newCapacity - this.cap) / (long) this.nodeCap;

            // Add 'nodesToAdd' Nodes after 'node':
            for (long i = 0L; i < nodesToAdd; i++) {
                // Create new Node with its array initially set to 'null'
                node.next = new Node(null, false);
                node = node.next;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public long getCapacity() {
        return this.cap;
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<E> iterator() {
        return new NonNullIter();
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<E> withNullsIterator() {
        return new WithNullsIter();
    }

    /**
     * {@inheritDoc}
     */
    public long size() {
        return this.size;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEmpty() {
        return this.size == 0;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object o) {
        if (o instanceof UnrolledArrayList<?>) {
            // safe cast
            UnrolledArrayList<?> that = (UnrolledArrayList<?>) o;

            // If their sizes differ, they're not equal.
            if (this.size != that.size)
                return false;

            // Iterate over both lists, comparing items for equality
            Iterator<E> it1 = iterator();
            Iterator<?> it2 = that.iterator();
            while (it1.hasNext() && it2.hasNext()) {
                E e1 = it1.next();
                Object e2 = it2.next();
                if (!(e1 == null ? e2 == null : e1.equals(e2)))
                    return false;
            }
            /* If neither list has more items, return 'true'; return 'false'
             * otherwise. */
            return !it1.hasNext() && !it2.hasNext();
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        int hash = 17;

        E item;
        for (Iterator<E> it = withNullsIterator(); it.hasNext(); ) {
            item = it.next();
            hash = 37 * hash + (item != null ? item.hashCode() : 0);
        }
        return hash;
    }


    private Node getNodeFor(long index, int[] arrayIndex) {
        arrayIndex[0] = (int) (index % (long) this.nodeCap);
        long nodeIndex = index / (long) this.nodeCap;
        Node n = this.head;
        for (long i = 0L; i < nodeIndex && n != null; i++)
            n = n.next;
        return n;
    }


    /**
     * @version 1.1
     */
    private class Node {
        private Node next;
        private E[] items;
        private int size;

        @SuppressWarnings("unchecked")
        Node(Node next, boolean allocate) {
            this.next = next;
            if (allocate) {
                this.items = (E[]) new Object[UnrolledArrayList.this.nodeCap]; // unchecked cast
            } else {
                this.items = null;
            }
            this.size = 0;
        }

        @SuppressWarnings("unchecked")
        E set(int index, E item) {
            E ret = null;
            if (this.items == null) {
                // Lazy allocation
                this.items = (E[]) new Object[UnrolledArrayList.this.nodeCap]; // unchecked cast
            } else {
                ret = this.items[index];
            }
            this.items[index] = item;
            if (ret == null) {
                this.size++;
                UnrolledArrayList.this.size++;
            }
            return ret;
        }

        E delete(int index) {
            if (this.items == null)
                return null;
            E ret = this.items[index];
            this.items[index] = null;
            if (ret != null) {
                this.size--;
                UnrolledArrayList.this.size--;
            }
            return ret;
        }

        E get(int index) {
            if (this.items != null)
                return this.items[index];
            return null;
        }
    }


    private class NonNullIter implements Iterator<E> {
        private boolean canRemove, advanced;
        private Node nxt;
        private int arrIndex, expectedModCount;

        private NonNullIter() {
            this.nxt = UnrolledArrayList.this.head;
            this.arrIndex = -1;
            this.expectedModCount = UnrolledArrayList.this.modCount;
            this.canRemove = false;
            advance();
        }

        public boolean hasNext() {
            if (!this.advanced)
                advance();
            return this.nxt != null;
        }

        public E next() {
            checkForComodification();
            if (!this.advanced)
                advance();
            if (this.nxt != null) {
                this.canRemove = true;
                this.advanced = false;
                return this.nxt.get(this.arrIndex);
            } else {
                throw new NoSuchElementException();
            }
        }

        public void remove() {
            if (this.canRemove) {
                checkForComodification();
                // Delete item from 'nxt' Node
                this.nxt.delete(this.arrIndex);
                this.canRemove = false;
            } else {
                throw new IllegalStateException();
            }
        }


        private void advance() {
            this.arrIndex++;
            /* Advance to next non-null item in current Node, then advance
             * past empty nodes, to the first non-empty node or the end of
             * the list. */
            do {
                if (this.nxt != null) {
                    // Advance past null items
                    while (this.arrIndex < this.nxt.size
                            && this.nxt.get(this.arrIndex) == null) {
                        this.arrIndex++;
                    }
                    // If reached end of node's array
                    if (this.arrIndex == this.nxt.size) {
                        this.nxt = this.nxt.next;
                        this.arrIndex = 0;
                    } // Otherwise, a non-null item was found.
                } else {
                    break;
                }
            } while (this.nxt != null && this.nxt.size == 0);
            this.advanced = true;
        }

        private void checkForComodification() {
            if (UnrolledArrayList.this.modCount != this.expectedModCount)
                throw new ConcurrentModificationException();
        }
    }


    private class WithNullsIter implements Iterator<E> {
        private boolean canRemove, advanced;
        private Node nxt;
        private int arrIndex, expectedModCount;

        private WithNullsIter() {
            this.nxt = UnrolledArrayList.this.head;
            this.arrIndex = 0;
            this.expectedModCount = UnrolledArrayList.this.modCount;
            this.advanced = true;
            this.canRemove = false;
        }

        public boolean hasNext() {
            if (!this.advanced)
                advance();
            return this.nxt != null;
        }

        public E next() {
            checkForComodification();
            if (!this.advanced)
                advance();
            if (this.nxt != null) {
                this.advanced = false;
                E ret = this.nxt.get(this.arrIndex);
                /* remove() can only be called after next() returns a
                 * non-null item. */
                if (ret != null)
                    this.canRemove = true;
                return ret;
            } else {
                throw new NoSuchElementException();
            }
        }

        public void remove() {
            if (this.canRemove) {
                checkForComodification();
                // Delete item from 'nxt' Node
                this.nxt.delete(this.arrIndex);
                this.canRemove = false;
            } else {
                throw new IllegalStateException();
            }
        }


        private void advance() {
            if (++this.arrIndex == UnrolledArrayList.this.nodeCap) {
                this.nxt = this.nxt.next;
                this.arrIndex = 0;
            }
            this.advanced = true;
        }

        private void checkForComodification() {
            if (UnrolledArrayList.this.modCount != this.expectedModCount)
                throw new ConcurrentModificationException();
        }
    }

}
