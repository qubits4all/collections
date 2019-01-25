package info.willdspann.collections.concurrent.queue;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import info.willdspann.collections.utils.HeapBitReversedCounter;

/**
 * A thread-safe priority queue, which uses node-level locking. A
 * bit-reversed counter is used to scatter inserts and deletes among the
 * leaf nodes. This implementation is based on an algorithm by Galen C.
 * Hunt, et al., described in the paper
 * <a href="http://www.cs.rochester.edu/u/scott/papers/1996_IPL_heaps.pdf">
 * "An efficient algorithm for concurrent priority queue heaps"</a>.
 *
 * @author Will D. Spann
 * @version 1.2
 */
@ThreadSafe
public class ConcurrentHeapPriorityQueue<E> implements Queue<E> {
	private static final int DEFAULT_INIT_CAP = 10;
	
	private final ArrayList<Node<E>> heap;
	@GuardedBy("this.countLock") private final HeapBitReversedCounter
			count;
	private final ReentrantReadWriteLock countLock;  // Lock on 'count'
	
	private Comparator<? super E> comp;
	
	
	public ConcurrentHeapPriorityQueue() {
		this(DEFAULT_INIT_CAP);
	}
    
    
    /**
     * @param initialCapacity the minimum initial capacity. Must be greater
     *    than 0.
     *    
     * @throws IllegalArgumentException if {@code initialCapacity} is less
     *    than 0. 
     */
    public ConcurrentHeapPriorityQueue(int initialCapacity) {
    	if (initialCapacity < 1) {
    		throw new IllegalArgumentException("initialCapacity must be "
    				+ ">= 1");
    	}
    	
    	this.heap = new ArrayList<Node<E>>(initialCapacity);
    	this.count = HeapBitReversedCounter.newInstance();
    	this.countLock = new ReentrantReadWriteLock();
    	ensureCapacity(initialCapacity);
    	this.heap.set(0, new Node<E>());  // Set empty root Node
    	this.comp = null;
    }
    
    
    /**
     * @param initialCapacity the minimum initial capacity. Must be greater
     *    than 0.
     * @param comparator 
     *    
     * @throws IllegalArgumentException if {@code initialCapacity} is less
     *    than 0.
     * @throws NullPointerException if {@code comparator} is {@code null}.
     */
    public ConcurrentHeapPriorityQueue(int initialCapacity,
    		Comparator<? super E> comparator)
    {
    	this(initialCapacity);
    	
    	if (comparator == null)
    		throw new NullPointerException();
    	
    	this.comp = comparator;
    }
    
    
    public boolean offer(E item) {
    	privOffer(item);
    	return true;
    }
    
    
    public boolean add(E item) {
    	privOffer(item);
    	return true;
    }
    
    
    public boolean addAll(Collection<? extends E> c) {
    	if (c == null)
    		throw new NullPointerException();
    	
    	boolean changed = false;
    	for (E item : c)
    		changed |= add(item);
    	return changed;
    }
    
    
    public E poll() {
    	return delete();
    }
    
    
    public E remove() {
    	E ret = delete();
    	if (ret != null)
    		return ret;
    	else
    		throw new NoSuchElementException();
    }
    
    
    public E peek() {
    	return privPeek();
    }
    
    
    public E element() {
    	E ret = privPeek();
    	if (ret != null)
    		return ret;
    	else
    		throw new NoSuchElementException();
    }
    
    
    /**
     * Removes a single instance of the specified element from this queue,
     * if it is present. Returns {@code true} if this queue contained the
     * specified element.
     * <p>
     * Version: 2.0
     * 
     * @param o element to be removed from this queue, if present.
     * @return {@code true} if an element was removed as a result of this
     *    call.
     * 
     * @throws ClassCastException if the type of {@code o} is incompatible
     *    with this queue.
     * @throws NullPointerException if {@code o} is {@code null}.
     */
    /*
     * NOTE: The method is no longer supported, because it can't be implemented correctly without introducing deadlock.
     *   It would need to call a deleteNode(Node<E> n, int pos) method, which would delete a given Node, while holding
     *   the 'n' Node's lock.
     */
    public boolean remove(Object o) {
    	throw new UnsupportedOperationException();
    }
    
    /**
     * Removes all of this queue's elements that are also contained in the
     * specified {@code Collection<?>}. After this call returns, this
     * queue will contain no elements in common with the specified
     * {@code Collection<?>}.
     * <p>
     * Version: 2.0
     * 
     * @param c {@code Collection<?>} containing elements to be removed
     *    from this queue.
     * @return {@code true} if this queue changed as a result of the call.
     * 
     * @throws UnsupportedOperationException because this operation is not
     *    supported.
     */
    public boolean removeAll(Collection<?> c) {
    	throw new UnsupportedOperationException();
    }
    
    /**
     * Version: 1.0
     * 
     * @throws UnsupportedOperationException because this operation is not
     *    supported.
     */
    public boolean retainAll(Collection<?> c) {
    	throw new UnsupportedOperationException();
    }
    
    
    public int drainTo(Collection<? super E> c) {
    	if (c == null)
    		throw new NullPointerException();
    	
    	int count = 0;
    	while (!isEmpty()) {
    		try {
    			c.add(deleteMin());
    			count++;
    		} catch (NoSuchElementException e) {
    			break;
    		}
    	}
    	return count;
    }
    
    
    public int drainTo(Collection<? super E> c, int maxElements) {
    	if (c == null)
    		throw new NullPointerException();
    	
    	int count = 0;
    	while (!isEmpty() && maxElements-- > 0) {
    		try {
    			c.add(deleteMin());
    			count++;
    		} catch (NoSuchElementException e) {
    			break;
    		}
    	}
    	return count;
    }
    
    
    public void clear() {
    	this.countLock.writeLock().lock();
    	try {
    		this.count.reset();
    		this.heap.clear();
    	} finally {
    		this.countLock.writeLock().unlock();
    	}
    }
    
    
    public boolean contains(Object o) {
    	return indexOf(o) >= 0;
    }
    
    
    public boolean containsAll(Collection<?> c) {
    	boolean missingAny = false;
    	for (Object o : c) {
    		missingAny |= !contains(o);
    	}
    	return !missingAny;
    }
    
    
    /**
     * Returns an {@code Iterator} over the elements in this queue. The
     * {@code Iterator} does not return the elements in any particular order
     * The returned {@code Iterator} will never throw
     * {@link ConcurrentModificationException}, and guarantees to
     * to traverse elements as they existed upon construction of the
     * iterator, and may (but is not guaranteed to) reflect any
     * modifications subsequent to construction.
     * 
     * @return an {@code Iterator} over the elements in this queue.
     */
    public Iterator<E> iterator() {
    	return new Iter();
    }
    
    
    public Object[] toArray() {
    	return new SnapshotCreator().getList().toArray();
    }
    
    
    public <T> T[] toArray(T[] a) {
    	return new SnapshotCreator().getList().toArray(a);
    }
    
    
    public int size() {
    	this.countLock.readLock().lock();
    	try {
    		return this.count.getNonreversedCount();
    	} finally {
    		this.countLock.readLock().unlock();
    	}
    }
    
    
    /**
     * Version: 2.0
     */
    public boolean isEmpty() {
		return privPeek() == null;
    }

    
    private void privOffer(E item) {
    	if (item == null)
    		throw new NullPointerException();
    	if (this.comp == null && !(item instanceof Comparable<?>))
    		throw new ClassCastException(); // 'item' is incomparable.
    		
    	insert(item);
    }

    private E privPeek() {
    	Node<E> root = this.heap.get(0);
    	root.lock.lock();
    	try {
    		if (root.getTag() != Tag.EMPTY)
    			return root.getData();
    		else
    			return null;
    	} finally {
    		root.lock.unlock();
    	}
    }
    
    /**
     * Returns the position of the specified item in the min-heap's backing
     * array, or -1 if the item was not found.
     * <p>
     * Version: 1.0.1
     * 
     * @param o the item for which to find the position.
     * @return the position of the specified item in the min-heap's backing
     *    array, or -1 if the item was not found.
     * 
     * @throws NullPointerException if {@code o} is {@code null}.
     */
    private int indexOf(Object o) {
    	if (o == null)
    		throw new NullPointerException();
    	
    	E data;
    	int i = 0;
    	for (Node<E> n : this.heap) {
    		if (n != null) {
	    		n.lock.lock();
	    		try {
		    		if (n.getTag() != Tag.EMPTY)
		    			data = n.getData();
		    		else
		    			data = null;
	    		} finally {
	    			n.lock.unlock();
	    		}
	    		if (data != null && (data == o || data.equals(o)))
	    			return i;
	    	}
    		i++;
    	}
    	return -1;  // Indicate 'o' wasn't found
    }
    
    /**
	 * Version: 1.2
	 * <p>
	 * Notes on Locking: The 'countLock' is held until we've acquired
	 *   'node's lock, and is then released. The 'node' lock is first held
	 *   while its members are set, and is then released. While 'node' isn't
	 *   root & its data is smaller than its parent's data, 'parent' is
	 *   locked, then 'node' 
	 */
    private void insert(E item) {
    	// Get our current Thread's ID, which is used to uniquely identify which
    	// thread is inserting the new Node. 
    	long threadID = Thread.currentThread().getId();
		Node<E> node;    // Node being inserted w/ its data equal to 'item'  
		int nodePos;    // 'node's pos. in the backing array-based heap 
    	this.countLock.writeLock().lock();
    	try {
    		// Get the next leaf insert pos. from the bit-reversed counter
    		nodePos = this.count.getAndIncrement();
    		// Ensure sufficient heap capacity
    		ensureCapacity(nodePos + 1);
    		// Get 'node' from 'nodePos', which may be 'null' or Tag.EMPTY:
    		node = this.heap.get(nodePos);    		
    		// If 'node' is 'null', create new Node & put in the heap:
    		if (node == null) {
    			node = new Node<E>();
    			node.lock.lock(); // Lock the new node before placing it in heap
    			this.heap.set(nodePos, node); 
    		} else {
    			node.lock.lock();
    		}
    	} finally {
    		this.countLock.writeLock().unlock();
    	}
		try {
			// Set 'node's data to the inserting 'item'
			node.setData(item);
			// Set its Tag to current Thread's ID
	    	node.setTag(Tag.newThreadIDTag(threadID));
		} finally {
			node.lock.unlock();
		}
		
		// Propagate 'node' towards heap's root while its data is "less than"
		// its parent's.
		nodePos = propagateInsert(nodePos, node);
		
    	// If 'node' is now root, set its Tag to Tag.AVAILABLE:
    	if (nodePos == 0) {
    		Node<E> root = this.heap.get(0);
    		root.lock.lock();
    		try {
    			// Only set 'root's tag to AVAILABLE if it's "our" node. (It
    			// could have been replaced with another node due to a
    			// concurrent insert or delete.)
        		if (root.getTag().getValue() == threadID)
        			root.setTag(Tag.AVAILABLE);
    		} finally {
    			root.lock.unlock();
    		}
    	}
    }
    
    /**
     * Removes and returns the minimum item in this queue.
     * <p>
     * Version: 3.0
     * 
     * @return the minimum item in this queue, after removing it; or
     *    {@code null} if the queue is empty.
     */
    private E delete() {
    	try {
    		return deleteMin();
    	} catch (NoSuchElementException e) {
    		return null;
    	}
    }

    /**
     * Removes and returns the item at the given position, in the backing
     * array-based min-heap. This is a generalized version of the prior
     * delete() method, which supports deletion of any item in the queue.
     * The optional {@code item} parameter specifies the item to be deleted,
     * and if specified is used to make sure the position given by
     * {@code pos} is correct.
     * <p>
     * Version: 2.1
     * <p>
     * <u>Notes on Locking</u>: The 'countLock' is released as soon as we've
     * acquired the 'bottom' lock, or immediately if the queue's size is found
     * to be 0. Then the 'bottom' lock is released as soon we've copied its data
     * and set it to EMPTY. Then the 'node' lock at position 'pos' is acquired.
     * The 'node' lock is released immediately if 'node' is tagged EMPTY and
     * thereby is the sole Node (and equal to 'bottom'). In this case,
     * 'bottomData' is returned. Otherwise, the 'node' lock is held until the
     * method returns, or an exception is thrown.
     * 
     * @param pos index of item to be deleted, in the backing array-based
     *    min-heap. 
     * @return the deleted item, which was at position {@code pos}, in the
     *    backing array-based min-heap.
     * @throws NoSuchElementException if the queue is empty.
     * @throws IndexOutOfBoundsException if {@code pos} is greater than or equal
     *    to the queue's size.
     */
    /* NOTE: This method is now deprecated, because it was only needed by remove(Object), which is no longer supported,
     *   because it was unpredictable. */
    @Deprecated
    private E delete(int pos) {
    	Node<E> bottom, node;
    	E bottomData, nodeData;
    	int bottomPos;
    	
    	/* Grab an item from the bottom of the heap to replace the to-be-deleted
    	 * item at 'pos'. */
    	this.countLock.writeLock().lock();
    	try {
	    	int size = this.count.getNonreversedCount();
	    	// Throw NoSuchElementException if the queue is empty.
	    	if (size == 0) {
	    		this.countLock.writeLock().unlock();
	    		throw new NoSuchElementException();
	    	} else if (pos >= size) {
	    		this.countLock.writeLock().unlock();
	    		throw new IndexOutOfBoundsException();
	    	}
	    	bottomPos = this.count.decrementAndGet();
	    	bottom = this.heap.get(bottomPos);
	    	bottom.lock.lock();
    	} finally {
    		if (this.countLock.writeLock().isHeldByCurrentThread())
    			this.countLock.writeLock().unlock();
    	}
    	
    	// Get 'bottom' data & set the Node empty, then release its lock:
    	bottomData = bottom.getData();
    	bottom.setTag(Tag.EMPTY);
    	bottom.setData(null);
    	bottom.lock.unlock();
    	
    	// Lock item at 'pos':
    	node = this.heap.get(pos);
    	node.lock.lock();
    	try {
    		// Stop if it was only item in the heap (and thereby was 'bottom')
    		if (node.getTag() == Tag.EMPTY) {
    			node.lock.unlock();
    			return bottomData;
    		}
    		// Replace node's item with the item that was in 'bottom':
    		nodeData = node.getData();
    		node.setData(bottomData);
    		node.setTag(Tag.AVAILABLE);
    		// Adjust heap starting at 'pos', while holding the lock on 'node'.
    		heapify(pos, node);
    		// Return deleted item
    		return nodeData;
    	} finally {
    		if (node.lock.isHeldByCurrentThread())
    			node.lock.unlock();
    	}
    }
    
    /**
     * Removes the minimum-priority item from the queue and returns this item.
     * <p>
     * Impl. Notes: This method is based closely on Galen C. Hunt's
     * {@code concurrent_delete(heap_t)} pseudo-code.
     * 
     * @return the removed minimum-priority item.
     * @throws NoSuchElementException if the queue is empty.
     */
    private E deleteMin() {
    	Node<E> bottom, root;
    	E bottomData, rootData;
    	int bottomPos;
    	
    	/* Grab an item from the bottom of the heap to replace the to-be-deleted
    	 * top item. */
    	this.countLock.writeLock().lock();
    	try {
	    	int size = this.count.getNonreversedCount();
	    	if (size == 0) {
	    		this.countLock.writeLock().unlock();
	    		throw new NoSuchElementException();
	    	}
	    	bottomPos = this.count.decrementAndGet();
	    	bottom = this.heap.get(bottomPos);
	    	bottom.lock.lock();
    	} finally {
    		if (this.countLock.writeLock().isHeldByCurrentThread())
    			this.countLock.writeLock().unlock();
    	}
    	
    	// Get 'bottom' data & set the Node empty, then release its lock:
    	bottomData = bottom.getData();
    	bottom.setTag(Tag.EMPTY);
    	bottom.setData(null);
    	bottom.lock.unlock();
    	
    	// Lock first item:
    	root = this.heap.get(0);
    	root.lock.lock();
    	try {
    		// Stop if it was only item in the heap (and thereby was 'bottom')
    		if (root.getTag() == Tag.EMPTY) {
    			root.lock.unlock();
    			return bottomData;
    		}
    		// Replace the top item with the item stored from the bottom:
    		rootData = root.getData();
    		root.setData(bottomData);
    		root.setTag(Tag.AVAILABLE);
    		
    		// Adjust the heap starting at the top. We always hold a lock on the
    		// item being adjusted. (In this case 'root'.)
    		heapify(0, root);
    		
    		// Return minimum item
    		return rootData;
    	} finally {
    		if (root.lock.isHeldByCurrentThread())
    			root.lock.unlock();
    	}
    }
    
    /**
     * Heapify the specified {@code Node}.
     * <p>
     * Note: The {@code Node} 'node' remains locked after this method
     *   completes.  
     * <p>
     * Version: 1.2.1
     * 
     * @param pos {@code node}'s position in the heap.
     * @param node {@code Node} to be heapified, which must be locked when
     *    this method is called.
     */
    private void heapify(int pos, Node<E> node) {
    	// Caller should hold Lock on 'node'
    	assert node.lock.isHeldByCurrentThread();
    	
		Node<E> left,   // 'node's left child
				right,  // 'node's right child
				child;  // 'node's smaller child
		int i = pos,       // pos. of 'node'
				 leftPos,   // pos. of 'node's left child
				 rightPos,  // pos. of 'node's right child
				 childPos;  // pos. of 'node's smaller child
		while (true) {
			// Get pos. of 'node's left child
			leftPos = (i << 1) + 1;   // i * 2 + 1
			// Get pos. of 'node's right child
			rightPos = (i << 1) + 2;  // i * 2 + 2
			// Make sure we don't try to read past the end of the heap
			if (leftPos >= this.heap.size())
				left = null;
			else
				left = this.heap.get(leftPos);
			// Make sure we don't try to read past the end of the heap
			if (rightPos >= this.heap.size())
				right = null;
			else
				right = this.heap.get(rightPos);
			// If left child exists
			if (left != null) {
				left.lock.lock();
				// If 'left' is empty, 'node' has no valid children.
				if (left.getTag() == Tag.EMPTY) {
					left.lock.unlock();
					return;
				}
			}
			// Otherwise, 'node' has no children.
			else {
				return;
			}
			// If right child exists
			if (right != null) {
				right.lock.lock();
				// If 'right' is empty or 'left.data' <= 'right.data'
				// Note: 'left' is still locked
				if (right.getTag() == Tag.EMPTY
						|| compare(left, right) <= 0)
				{
					right.lock.unlock();  // unlock; we're done w/ 'right'
					// Assign 'node's smaller child:
					childPos = leftPos;
					child = left;
				}
				// Otherwise, 'right.data' < 'left.data'
				else {
					left.lock.unlock();  // unlock; we're done w/ 'left'
					// Assign 'node's smaller child:
					childPos = rightPos;
					child = right;
				}
			}
			// Otherwise, 'left' is sole child
			// Note: 'left' is still locked.
			else {
				childPos = leftPos;
				child = left;  // 'child' is locked
			}
			
			// If child is less than parent, swap Nodes.
			// Note: 'child' is locked
			if (compare(child, node) < 0) {
				swapNodes(childPos, i);  // swap 'node' w/ 'child'
				child.lock.unlock();  // 'child' is now 'node's parent
				i = childPos;  // 'i' is new pos. of 'node'
			}
			// If not, stop. We're done.
			else {
				child.lock.unlock();
				return;
			}
		}
    }

    /**
     * Propagates the specified newly inserted {@code Node} up the backing heap
     * towards its root node via successive node swaps, while its data is
     * "less than" its parent {@code Node}'s data (according to either this
     * queue's {@code Comparator} or the data's natural ordering), in order to
     * move the node to its correct location in the heap.
     * 
     * @param nodePos the current position, in the backing array-based heap, of
     *    the {@code Node} to propagate up the heap.
     * @param node the {@code Node} to propagate up the heap.
     * @return the specified {@code Node}'s new position in the backing
     *    array-based heap.
     */
    private int propagateInsert(int nodePos, Node<E> node) {
    	long threadID = Thread.currentThread().getId();
    	Node<E> parent; // 'node's parent Node
    	int parentPos;  // parent node's position in backing array-based heap
    	
    	// Move 'node' towards heap's root while smaller than its parent
    	while (nodePos > 0) {
    		// Calc. 'node's parent's pos.
    		parentPos = (nodePos - 1) >>> 1;  // (nodePos-1)/2
    		// Get 'node's parent from heap
    		parent = this.heap.get(parentPos);
    		parent.lock.lock();  // Note: Locking order is important.
    		node.lock.lock();
    		try {
    			// If 'parent' isn't currently being inserted or deleted &
    			// 'node' is still "our" node ...
        		if (parent.getTag() == Tag.AVAILABLE
        				&& node.getTag().getValue() == threadID)
        		{
        			// If 'node' is less than 'parent', swap nodes
        			if (compare(node, parent) < 0) {
        				swapNodes(nodePos, parentPos);
        				nodePos = parentPos;
        			}
        			// Otherwise, we're done.
        			else {
        				node.setTag(Tag.AVAILABLE);
        				break;
        			}
        		}
        		// Otherwise, if 'parent's tag is EMPTY, there's a concurrent
        		// delete, so break or we'll deadlock w/ the deleting thread.
        		else if (parent.getTag() == Tag.EMPTY) {
        			break;
        		}
        		// Otherwise, if 'node's tag isn't our Thread ID, then a
        		// concurrent insert has swapped 'node' w/ 'parent', so update
        		// 'nodePos' w/ 'node's new position, which equals 'parentPos'.
        		else if (node.getTag().getValue() != threadID) {
        			nodePos = parentPos;
        		}
    		} finally {
    			node.lock.unlock();    // Note: Unlocking order is important.
    			parent.lock.unlock();
    		}
    	}
    	return nodePos;
    }

    /**
     * Swaps the nodes at the specified positions.
     * <p>
     * Version: 1.0.1
     * 
     * @param node1pos the first node, which will be swapped with
     *    {@code node2pos}
     * @param node2pos the second node, which will be swapped with
     *    {@code node1pos}
     */
    private void swapNodes(int node1pos, int node2pos) {
    	/* Assert the current Thread holds the locks for the Nodes at the
    	 * specified positions. */
    	assert this.heap.get(node1pos).lock.isHeldByCurrentThread();
    	assert this.heap.get(node2pos).lock.isHeldByCurrentThread();
    	
    	Node<E> temp = this.heap.get(node1pos);
    	this.heap.set(node1pos, this.heap.get(node2pos));
    	this.heap.set(node2pos, temp);
    }

    /**
     * Increases the backing {@code ArrayList}'s capacity, if
     * {@code minCapacity} is greater than its current capacity, and adds
     * nulls to the resized {@code ArrayList}, filling it to the new capacity.
     * <p> 
     * Version: 1.0.1
     * 
     * @param minCapacity a new capacity for the backing {@code ArrayList},
     *    which should be greater than {@code heap.size()}. Note: Calling this
     *    method with a value less than or equal to {@code heap.size()} has no
     *    effect.
     */
    @GuardedBy("this.countLock")
    private void ensureCapacity(int minCapacity) {
    	this.heap.ensureCapacity(minCapacity);
    	// Fill with 'null's, increasing size to capacity.
    	for (int i = this.heap.size(); i < minCapacity; i++)
    		this.heap.add(null);
    }

    /**
     * Compares two {@code Node}s' data, using a {@code Comparator}'s
     * {@code compare(E,E)} if one is set, or {@code Comparable}'s
     * {@code compareTo(E)} if not. Returns a positive integer, zero, or a
     * negative integer, if {@code item1}'s data is greater than, equal, or
     * less than {@code item2}'s data.
     * 
     * @param item1 the first {@code Node}, whose data will be compared to
     *    {@code item2}'s data
     * @param item2 the second {@code Node}, whose data {@code item1}'s data
     *    will be compared to
     * @return a positive integer, zero, or a negative integer, if
     *    {@code item1}'s data is greater than, equal, or less than
     *    {@code item2}'s data.
     */
    @SuppressWarnings("unchecked")
    private int compare(Node<E> item1, Node<E> item2) {
    	// Caller should hold Lock on 'item1' & 'item2':
    	assert item1.lock.isHeldByCurrentThread();
    	assert item2.lock.isHeldByCurrentThread();
    	
    	if (this.comp != null)
    		return this.comp.compare(item1.data, item2.data);
    	else
    		return ((Comparable<E>) item1.data).compareTo(item2.data); // unchecked cast
    }


    /**
     * Version: 1.1
     */
    private static class Node<E> {
    	final ReentrantLock lock;
    	
    	private E data;
    	private Tag tag;

    	private Node() {
    		this.data = null;
    		this.tag = Tag.EMPTY;
    		this.lock = new ReentrantLock(true);  // Create a "fair" Lock
    	}

    	E getData() {
    		// Calling Thread should hold the Lock
    		assert this.lock.isHeldByCurrentThread();
    		
    		return this.data;
    	}
    	
    	void setData(E data) {
    		// Calling Thread should hold the Lock
    		assert this.lock.isHeldByCurrentThread();
    		
    		this.data = data;
    	}
    	
    	Tag getTag() {
    		// Calling Thread should hold the Lock
    		assert this.lock.isHeldByCurrentThread();
    		
    		return this.tag;
    	}
    	
    	void setTag(Tag tag) {
    		// Calling Thread should hold the Lock
    		assert this.lock.isHeldByCurrentThread();
    		
    		this.tag = tag;
    	}
    }
    
    
    /**
     * This class creates a snapshot of this queue's elements. The resulting
     * {@code List<E>} is obtained via its {@code getList()} method.
     * <p>
     * <u>Implementation Notes</u>: To create the snapshot, this class
     * performs a locking breadth-first traversal of this queue's backing
     * heap. Each row of nodes in the heap are locked in succession, with
     * the previous row's nodes being unlocked once the current row's nodes
     * have been successfully locked. This locking of nodes during traversal
     * will cause some concurrent inserts and deletes to temporarily block.
     * However, this approach provides a consistent view of the queue, such
     * that concurrent inserts and/or deletes will not cause any items to be
     * skipped, or visited more than once.
     */
    private class SnapshotCreator {
    	private HeapBitReversedCounter bufCount; // Our pos. in the heap
    	private List<E> buf; // Snapshot of queue's items
    	private List<Integer> curRow, prevRow; // Lists of buffered Node indexes
    	private int curRowCap; // Heap max. capacity of current row

    	private SnapshotCreator() {
    		this.bufCount = HeapBitReversedCounter.newInstance();
    		this.buf = new LinkedList<E>();
    		this.prevRow = null;
    		this.curRow = new LinkedList<Integer>();
    		this.curRowCap = 1;
    		
    		readSnapshot();
    	}
    	
    	List<E> getList() {
    		return this.buf;
    	}

    	/**
    	 * Version: 1.0.1
    	 */
    	private void readSnapshot() {
    		// Buffer items:
    		int rowSz = 0, exp = 0;
    		while (rowSz == exp) {
    			if (exp > 0)
    				exp <<= 1;  // exp *= 2
    			else
    				exp = 1;
    			
    			rowSz = bufferRow();
    			
    			this.prevRow = this.curRow;
	    		this.curRow.clear();
	    		this.curRowCap <<= 1;  // curRowCap *= 2
    		}
    		
    		/* Unlock all Nodes we locked: */
    		// Unlock previous row's Nodes:
    		if (this.prevRow != null) {
    			for (int index : this.prevRow) {
    				ConcurrentHeapPriorityQueue.this.heap.get(index).lock
    						.unlock();
    			}
    		}
    		// Unlock current row's Nodes:
    		for (int index : this.curRow) {
				ConcurrentHeapPriorityQueue.this.heap.get(index).lock
						.unlock();
			}
    	}
    	
    	/**
    	 * <p>
    	 * Version: 1.0.1
    	 * 
    	 * @return the number of items added to the buffer.
    	 */
    	private int bufferRow() {
    		if (this.prevRow != null) {
	    		// Unlock previous row's Nodes:
	    		for (int index : this.prevRow) {
	    			ConcurrentHeapPriorityQueue.this.heap.get(index).lock
	    					.unlock();
	    		}
    		}
    		
    		Node<E> n;
    		int index;
    		// Lock current row's Nodes & buffer their data values:
    		for (int i = 0; i < this.curRowCap; i++) {
    			index = this.bufCount.getAndIncrement();
    			// Don't try to buffer nonexistent Nodes (beyond the heap)
    			if (index >= ConcurrentHeapPriorityQueue.this.heap.size())
    				return i + 1; // End of row
    			n = ConcurrentHeapPriorityQueue.this.heap.get(index);
    			
    			if (n != null) {
    				this.curRow.add(index);
    				n.lock.lock();
    				if (n.getTag() != Tag.EMPTY) {
    					this.buf.add(n.getData());
    				}
    				// Otherwise, the Node has been deleted & we've reached the
    				// end of the row
    				else {
    					return i + 1;
    				}
    			}
    			// We've reached the end of the row
    			else {
    				return i + 1;
    			}
    		}
    		
    		return this.curRow.size();
    	}
    }

    
    /**
     * An {@code Iterator} over the elements in this queue. The
     * {@code Iterator} does not return the elements in any particular order
     * The returned {@code Iterator} will never throw
     * {@link ConcurrentModificationException}, and guarantees to
     * to traverse elements as they existed upon construction of the
     * iterator, and may (but is not guaranteed to) reflect any
     * modifications subsequent to construction.
     * <p>
     * <u>Implementation Notes</u>: This iterator uses SnapshotCreator to
     * generate a snapshot of this queue, in a consistent manner. This class
     * guarantees that any concurrent inserts and/or deletes will not cause
     * any items to be skipped, or visited more than once. This snapshot is
     * then what is iterated over. This approach should limit the time locks
     * need to be held on the queue's nodes.
     * <p>
     * Version: 1.1
     */
    private class Iter implements Iterator<E> {
    	private List<E> buf;  // Snapshot of queue's items
    	private int iterIndex;

    	private Iter() {
    		this.buf = new SnapshotCreator().getList();
    		this.iterIndex = 0;
    	}
    	
    	public boolean hasNext() {
    		return this.iterIndex < this.buf.size();
    	}

    	public E next() {
    		if (this.iterIndex < this.buf.size()) {
    			return this.buf.get(this.iterIndex++);
    		}
    		else
    			throw new NoSuchElementException();
    	}
    	
    	/**
    	 * @throws UnsupportedOperationException because this operation is
    	 *    not supported.
    	 */
    	public void remove() {
    		throw new UnsupportedOperationException();
    	}
    }

    
	@Immutable
    private static class Tag {
    	static Tag EMPTY = new Tag(0L);
    	static Tag AVAILABLE = new Tag(-1L);

    	private final long val;

    	private Tag(long value) {
    		this.val = value;
    	}
    	
    	static Tag newThreadIDTag(long id) {
    		if (id < 1L)
    			return null;
    		
    		return new Tag(id);
    	}
    	
    	long getValue() {
    		return this.val;
    	}

    	boolean isThreadIDTag() {
    		return this.val > 0L;
    	}
    }

}
