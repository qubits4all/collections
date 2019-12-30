/*
 * Last Modified: 7/26/08
 * Prev. Modified: 7/17/08
 * J2SE Version: 5.0
 * 
 * Version Notes: Updated the Node member class, to no longer be
 *   parameterized, because, as a member class, Node inherently has access
 *   to this class' type parameter E. Updated all other member classes
 *   and methods to use the new unparameterized Node class.
 *       Also, added assertions to the redistItems(Node,Node,Node) method,
 *   which test if this private method is being used correctly. These
 *   replace the previous throwing of an AssertionError when an unallowed
 *   state occurred. Also added an assertion that tests the validity of the
 *   arguments. During JUnit testing, the unit tests can be run with
 *   assertions enabled to test that this method is being used correctly,
 *   instead of relying on an uncaught AssertionError occurring sometime at
 *   run-time, causing any program using this class to exit unexpectedly. 
 *       Also, assertions have been added to the private methods
 *   getNodeAtItemIndex(long,int[]), delete(long),
 *   calcDeleteNumToMove(int,int,boolean), mergeNodes(Node,Node),
 *   moveItems(Node,Node,int,boolean), moveItemsToNewNode(Node,Node,int), to
 *   assert that valid arguments have been passed to them.
 *     v1.3.1: Added Javadoc for DescIter's findLastNode(long[]) method.
 *     v1.3: Implemented the retainAll(BigCollection<?>) &
 *   retainAll(Collection<?>) methods.
 *       Added a 'prev' Node field to the Node class, and updated
 *   its constructor's signature to Node(Node<E>,Node<E>), which now takes
 *   'prev' & 'next' Node arguments. Updated the UnrolledLinkedList()
 *   constructor, and the insert(long,E) & clear() methods, to use the new
 *   Node constructor. Updated the Iter & DescIter Iterator classes to use
 *   'node.prev', instead of calling getNodeAtNodeIndex(long).
 *       Fixed a bug in get(long), where it didn't throw an
 *   IndexOutOfBoundsException if the 'index' argument equaled size().
 *   Modified remove(Object), indexOf(Object) & lastIndexOf(Object) to allow
 *   for 'null' elements. Updated containsAll(BigCollection<?>) &
 *   containsAll(Collection<?>), so they now throw NullPointerException if
 *   'c' is 'null'. 
 *       Simplified the implementation of set(long,E). Made
 *   removeAll(BigCollection<?>) & removeAll(Collection<?>) more efficient,
 *   by iterating over this list & using Iterator's remove() method, instead
 *   of iterating over the specified collection. Made small change to the
 *   delete(long) method. Made the hashCode() method more readable.
 *       Commented out the getNodeAtNodeIndex(long) method, which is no
 *   longer used. It'll be deleted in the next version.
 *     v1.2.1: Fixed a bug in insert(long,E), where a call to
 *   Arrays.fill(...) was given an incorrect 3rd argument. Added a new
 *   private method moveItemsToNewNode(Node<E>,Node<E>,int), which the new
 *   version of insert(long,E) now calls. This private method is actually
 *   where the bug has been fixed.
 *     v1.2: Implemented the descendingIterator() & lastIndexOf(E)
 *   methods, which previously threw UnsupportedOperationException. 
 *     v1.1.2: Fixed the implementation of
 *   addAll(BigCollection<? extends E>) & addAll(Collection<? extends E>),
 *   so that the BigCollection's or Collection's elements are correctly
 *   appended to the end of this BigList. The previous version added them
 *   to the beginning of the list. 
 *     v1.1.1: Changed all methods & constructors that took a BigList to
 *   take a BigCollection instead, since BigList inherits from
 *   BigCollection.
 *       Milestone: First successful compile!
 *     v1.1: Rewrote the equals(Object) method, so that it now uses
 *   a safe cast, instead of an unchecked one. Updated the remove(Object) &
 *   contains(Object) methods to no longer require a cast. Changed
 *   indexOf(E) to indexOf(Object) & lastIndexOf(E) to lastIndexOf(Object),
 *   to be consistent with BigList v1.1, which updates these method
 *   signatures to be consistent with the java.util.List interface.
 *       Labeled the Node class's set(int,E), delete(int) & get(int)
 *   methods' unchecked casts with a line comment. Also annotated these
 *   methods with the SuppressWarnings annotation (specifying "unchecked"),
 *   so that the compiler will not display warnings for these casts.
 * 
 * TODO #1: Remove the commented out code from the
 *   redistItems(Node,Node,Node) method.
 * TODO #2: JUnit test this class, with assertions enabled.
 * TODO #3: Add a private 'tail' field to this class, to improve the
 *   efficiency of lastIndexOf(Object) & descendingIterator(). Use this
 *   field in DescIter, instead of calling findLastNode(long[]). Update
 *   insert(long,E) & delete(long), to update the 'tail' field whenever a
 *   new Node is added or the last Node is removed, respectively.
 * TODO #4: Implement the subList(long,long) method.
 */


package info.willdspann.collections.list;

import java.util.*;  // Arrays, Collection, Iterator
import info.willdspann.collections.*;  // BigCollection, BigList


/**
 * A {@code BigList}, implemented as an unrolled linked-list.
 * For a good description of how an unrolled linked-list works, see Derrick
 * Coetzee's article
 * <a href="http://blogs.msdn.com/devdev/archive/2005/08/22/454887.aspx">
 * "Unrolled linked lists"</a>, or the Wikipedia
 * <a href="http://en.wikipedia.org/wiki/Unrolled_linked_list">article</a>
 * of the same name.
 * <p>
 * This class implements the optional {@code BigList} methods, except
 * {@code subList(long,long)}, and the optional {@code BigCollection}
 * methods, except {@code retainAll(BigCollection<?>)}. It permits all
 * elements (including {@code null}).
 * <p>
 * The {@code Iterator}s returned by this class's {@code iterator()} and
 * {@code descendingIterator()} methods are <em>fail-fast</em>: if the list
 * is modified at any time after the iterator is created, in any way except
 * through the iterator's own {@code remove()} method, the {@code Iterator}
 * will throw a {@code ConcurrentModificationException}. Thus, in the face
 * of concurrent modification, the iterator fails quickly and cleanly,
 * rather than risking arbitrary, non-deterministic behavior at an
 * undetermined time in the future.
 * <p>
 * Note that the fail-fast behavior of an iterator cannot be guaranteed as
 * it is, generally speaking, impossible to make any hard guarantees in the
 * presence of unsynchronized concurrent modification. Fail-fast iterators
 * throw {@code ConcurrentModificationException} on a best-effort basis.
 * Therefore, it would be wrong to write a program that depended on this
 * exception for its correctness: <em>the fail-fast behavior of iterators
 * should be used only to detect bugs.</em> 
 *
 * @author Will D. Spann [willdspann@gmail.com]
 * @version 1.3.2
 */
public final class UnrolledLinkedList<E> implements BigList<E> {
	private static final int DEFAULT_MAX_ITEMS = 64;

	private final int maxItems;  // Max. items allowed per node
	private final int halfFull;  // Half the max. items allowed per node 
	
	private Node head;
	private long size;
	private int modCount;  // Structural modification count
	

	/**
	 * <p>
	 * Version: 1.2
	 */
	public UnrolledLinkedList() {
		this.head = new Node(null, null);
		this.maxItems = DEFAULT_MAX_ITEMS;
		this.halfFull = this.maxItems >>> 1;  // this.maxItems / 2
    }
	
	
	/**
	 * @throws NullPointerException if {@code c} is {@code null}.
	 */
	public UnrolledLinkedList(Collection<? extends E> c) {
		this();
		addAll(c);  // Will throw NullPointerException if 'c' is null
	}
	
	
	/**
	 * @throws NullPointerException if {@code c} is {@code null}.
	 */
	public UnrolledLinkedList(BigCollection<? extends E> c) {
		this();
		addAll(c);  // Will throw NullPointerException if 'c' is null
	}
	
	
	/**
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *    ({@code index < 0 || index > size()}).
	 */
	public void add(long index, E item) {
		// Check validity of arguments:
		if (index < 0L || index > this.size)
			throw new IndexOutOfBoundsException();
		
		this.modCount++;
		insert(index, item);
	}
	
	
	public void add(E item) {
		this.modCount++;
		insert(this.size, item);
	}
	
	
	/**
	 * @throws NullPointerException if {@code c} is {@code null}.
	 * @throws IndexOutOfBoundsException if {@code index < 0} or
	 *    {@code index > size()}
	 */
	public boolean addAll(long index, BigCollection<? extends E> c) {
		// Check for invalid arguments:
		if (c == null)
			throw new NullPointerException();
		if (index < 0L || index > this.size)
			throw new IndexOutOfBoundsException();
		
		boolean mod = false;
		for (E e : c) {
			add(index++, e);
			mod = true;
		}
		return mod;
	}
	
	
	/**
	 * @throws NullPointerException if {@code c} is {@code null}.
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *    ({@code index < 0 || index > size()}).
	 */
	public boolean addAll(long index, Collection<? extends E> c) {
		// Check for invalid arguments:
		if (c == null)
			throw new NullPointerException();
		if (index < 0L || index > this.size)
			throw new IndexOutOfBoundsException();
		
		boolean mod = false;
		for (E e : c) {
			add(index++, e);
			mod = true;
		}
		return mod;
	}
	
	
	/**
	 * <p>
	 * Version: 1.1
	 * 
	 * @throws NullPointerException if {@code c} is {@code null}.
	 */
	public boolean addAll(BigCollection<? extends E> c) {
		if (c == null)
			throw new NullPointerException();
		
		boolean mod = false;
		for (E e : c) {
			add(e);
			mod = true;
		}
		return mod;
	}
	
	
	/**
	 * <p>
	 * Version: 1.1
	 * 
	 * @throws NullPointerException if {@code c} is {@code null}.
	 */
	public boolean addAll(Collection<? extends E> c) {
		if (c == null)
			throw new NullPointerException();
		
		boolean mod = false;
		for (E e : c) {
			add(e);
			mod = true;
		}
		return mod;
	}
	
	
	/**
	 * Stores the given item at the specified location in this
	 * {@code BigList}, and returns the item previously stored there.
	 * <p>
	 * Version: 1.2
	 * 
	 * @param index the position to store {@code item} at in this
	 *    {@code BigList}.
	 * @param item the item to store at position {@code index}.
	 * @return the previous item stored at {@code index}.
	 * 
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *    ({@code index < 0 || index >= size()}).
	 */
	public E set(long index, E item) {
		// Check for invalid arguments:
		if (index < 0L || index >= this.size)
			throw new IndexOutOfBoundsException();
			
		// Get Node & its array index for 'index':
		int[] arrIndexHolder = new int[1];
		Node node = getNodeAtItemIndex(index, arrIndexHolder);
		
		// Store 'item' & return previous item
		return node.set(arrIndexHolder[0], item);
	}
	
	
	/**
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *    ({@code index < 0 || index >= size()}).
	 */
	public E remove(long index) {
		if (index < 0L || index >= this.size)
			throw new IndexOutOfBoundsException();
		
		this.modCount++;
		return delete(index);
	}	
	
	
	/**
	 * <p>
	 * Version: 1.2
	 */
	public boolean remove(Object o) {
		Iterator<E> it = iterator();
		E cur = null;
		while (it.hasNext()) {
			cur = it.next();
			if (cur == null ? o == null : cur.equals(o)) {
				it.remove();
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * <p>
	 * Version: 1.1
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
	 * <p>
	 * Version: 1.1
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
	 * <p>
	 * Version: 2.0
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
	 * <p>
	 * Version: 1.2
	 */
	public void clear() {
		this.modCount++;
		this.head = new Node(null, null);
		this.size = 0;
	}
	
	
	/**
	 * Returns the item at position {@code index} in this list.
	 * <p>
	 * Version: 1.1
	 * 
	 * @param index the position in this list of the item to be returned.
	 * @return the item at position {@code index} in this list.
	 * 
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *    ({@code index < 0 || index >= size()}).
	 */
	public E get(long index) {
		// Check for invalid argument:
		if (index < 0L || index >= this.size)
			throw new IndexOutOfBoundsException();
		
		int[] arrIndexHolder = new int[1];
		Node node = getNodeAtItemIndex(index, arrIndexHolder);
		return node.get(arrIndexHolder[0]);
	}
	
	
	/**
	 * <p>
	 * Version: 1.1
	 */
	public boolean contains(Object o) {
		return indexOf(o) >= 0;
	}
	
	
	/**
	 * <p>
	 * Version: 1.1
	 * 
	 * @throws NullPointerException if {@code c} is {@code null}.
	 */
	public boolean containsAll(BigCollection<?> c) {
		// Check for invalid argument:
		if (c == null)
			throw new NullPointerException();
		else if (c.isEmpty())
			return false;
		
		for (Object o : c) {
			if (!contains(o))
				return false;
		}
		return true;
	}
	
	
	/**
	 * <p>
	 * Version: 1.1
	 * 
	 * @throws NullPointerException if {@code c} is {@code null}.
	 */
	public boolean containsAll(Collection<?> c) {
		// Check for invalid argument:
		if (c == null)
			throw new NullPointerException();
		else if (c.isEmpty())
			return false;
		
		for (Object o : c) {
			if (!contains(o))
				return false;
		}
		return true;
	}
	
	
	/**
	 * <p>
     * Version: 1.2
     */
    public long indexOf(Object o) {
    	long i = 0L;
    	for (E e : this) {
    		if (e == null ? o == null : e.equals(o))
    			return i;
    		i++;
    	}
    	return -1L;  // Indicate 'o' wasn't found
    }
    
    
    /**
     * <p>
     * Version: 2.1
     */
    public long lastIndexOf(Object o) {
    	E item;
    	long i = this.size - 1L;
    	for (Iterator<E> dit = descendingIterator(); dit.hasNext(); i--) {
    		item = dit.next();
    		if (item == null ? o == null : item.equals(o))
    			return i;
    	}
    	return -1L;  // Indicate 'o' wasn't found
    }
	
	
	/**
	 * @throws UnsupportedOperationException
	 */
	public BigList<E> subList(long fromIndex, long toIndex) {
		throw new UnsupportedOperationException();
	}
	
	
	public Iterator<E> iterator() {
		return new Iter();
	}
	
	
	/**
	 * <p>
	 * Version: 2.0
	 */
	public Iterator<E> descendingIterator() {
		return new DescIter();
	}
	
	
	public long size() { return this.size; }
	
	
	public boolean isEmpty() { return this.size == 0; }
	
	
	/**
	 * <p>
     * Version: 2.1
     */
    public boolean equals(Object o) {
    	if (o instanceof UnrolledLinkedList<?>) {
    		// safe cast
    		UnrolledLinkedList<?> that = (UnrolledLinkedList<?>) o;
    		
    		// If their sizes differ, they're not equal.
        	if (this.size != that.size)
        		return false;
    		
        	// Iterate over both lists, comparing items for equality
    		Iterator<E> it1 = iterator();
    		Iterator<?> it2 = that.iterator();
    		E e1;
    		Object e2;
    		while (it1.hasNext() && it2.hasNext()) {
    			e1 = it1.next();
    			e2 = it2.next();
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
     * <p>
     * Version: 1.1
     */
    public int hashCode() {
    	int hash = 17;
    	
    	E item;
    	for (Iterator<E> it = iterator(); it.hasNext(); ) {
    		item = it.next();
    		hash = 37 * hash + (item != null ? item.hashCode() : 0);
    	}
    	return hash;
    }
	
	
	/**
	 * Returns the {@code Node} at the specified item index. Also, the
	 * item's array index is stored in the {@code arrayIndex int[]}
	 * parameter.
	 * <p>
	 * Version: 1.1
	 * 
	 * @return the {@code Node} at the specified item index.
	 *   
	 * @throws IndexOutOfBoundsException if {@code index} is out of bounds
	 *    ({@code index < 0 || index >= size()}).
	 * @throws ConcurrentModificationException if this list was concurrently
	 *    modified by another thread.
	 */
	private Node getNodeAtItemIndex(long index, int[] arrayIndex) {
		// Assert valid arguments:
		assert index >= 0 && index < this.size;
		assert arrayIndex != null && arrayIndex.length >= 1;
		
		int modCount = this.modCount;  // Store the 'modCount' at start
		long curIndex = index;
		Node curNode = this.head;
		
		while (curIndex >= 0 && curNode != null) {
			// The Node containing item at pos. 'index' has been reached.
			if (curIndex < (long) curNode.size) {
				arrayIndex[0] = (int) curIndex;
				return curNode;
			}
			/* Otherwise, decrement 'curIndex' by 'curNode.size' & traverse
			 * to the next Node. */
			else {
				curIndex = curIndex - (long) curNode.size;
				curNode = curNode.next;
			}
		}
		
		/* Note: We should never get to here, unless the list is modified
		 * concurrently, or 'index' is out of bounds: */
		if (index < 0L || index >= this.size)
			throw new IndexOutOfBoundsException();
		else {
			// Assert a concurrent modification has occurred
			assert modCount != this.modCount;
			// Throw the Exception
			throw new ConcurrentModificationException();
		}
	}
	
	
	/**
	 * <p>
	 * Version: 1.3
	 *   
	 * @throws ConcurrentModificationException if this list was concurrently
	 *    modified by another thread.
	 */
	private void insert(long index, E item) {
		// Assert valid 'index'
		assert index >= 0 && index <= this.size;
		
		int[] arrIndexHolder = new int[1];
		int arrIndex = -1;
		Node node = null;
		
		// If doing an insert
		if (index < this.size) {
			node = getNodeAtItemIndex(index, arrIndexHolder);
			arrIndex = arrIndexHolder[0];
		}
		// Otherwise, doing an append
		else {
			node = getNodeAtItemIndex(index - 1, arrIndexHolder);
			arrIndex = arrIndexHolder[0];
			arrIndex++;
			
			// If last Node is full
			if (arrIndex == this.maxItems) {
				// Insert new last Node after 'node':
				Node next = new Node(node, null);
				node.next = next;
				
				// Insert item in new Node 'next'
				next.insert(0, item);
				return;
			}
		}
		
		// If 'node.items' is full
		if (node.size == this.maxItems) {
			// Insert new Node after 'node':
			Node next = new Node(node, node.next);
			node.next = next;
			next.next.prev = next;
			
			// Move items in 2nd 1/2 of 'node' to 'next'
			moveItemsToNewNode(node, next, this.halfFull);			
			
			/* If 'arrIndex' would put item in 2nd half of
			 * 'node.items', insert it in 'next' instead. */
			if (arrIndex >= node.size) {
				arrIndex = arrIndex - node.size;
				next.insert(arrIndex, item);
				return;
			}
		}
		// Add the item to 'node.items'
		node.insert(arrIndex, item);
	}
	
	
	/**
	 * <p>
	 * Version: 1.2
	 * <p>
	 * Version Notes: Made small change to if statements inside the
	 *   "{@code if(curIndex < (long)curNode.size)}" block.
	 *   
	 * @throws ConcurrentModificationException if this list was concurrently
	 *    modified by another thread.
	 */
	private E delete(long index) {
		// Assert the argument is valid
		assert index >= 0 && index < this.size;
		
		long curIndex = index;
		Node curNode = this.head;
		Node prevNode = null;
		
		while (curIndex >= 0L && curNode != null) {
			// The node we want to delete from has been reached
			if (curIndex < (long) curNode.size) {	
				// Delete item at list pos. 'index' from 'curNode'
				E retVal = curNode.delete((int) curIndex);
			
				// If only 1 node
				if (prevNode == null && curNode.next == null) {
					return retVal;
				}
				/* If 'curNode' is now less than half full & there is more
				 * than one node */
				else if (curNode.size < this.halfFull && (prevNode != null
						|| curNode.next != null))
				{
					redistItems(prevNode, curNode, curNode.next);
					return retVal;
				}
			}
			
			// Advance nodes & decrease 'curIndex':
			prevNode = curNode;
			curIndex = curIndex - (long) curNode.size;
			curNode = curNode.next;
		}
		
		/* If we got to here, the list was concurrently modified by another
		 * thread. */
		throw new ConcurrentModificationException();
	}
	
	
	/**
	 * <p>
	 * Version: 1.1
	 * 
	 * @return {@code true} if {@code cur} was merged into {@code prev};
	 *    {@code false} if {@code cur} was merged into {@code next}, or if
	 *    items were only transferred between 2 {@code Node}s via
	 *    {@code moveItems(Node,Node,int,boolean)}.
	 */
	private boolean redistItems(Node prev, Node cur, Node next) {
		/* Assertions on the arguments: */
		// If prev != null, assert prev.size >= halfFull
		assert (prev != null) ? (prev.size >= this.halfFull) : true;
		// If prev != null && next == null, assert prev.size >= halfFull
		assert (prev != null && next == null) ? (prev.size >= this.halfFull)
				: true;
		// Assert 'prev' & 'next' should never both be 'null' 
		assert !(prev == null && next == null);
		
		// If deleting from middle
		if (prev != null && next != null) {
			// If both neighbors are more than half full
			if (prev.size > this.halfFull && next.size > this.halfFull) {
				if (prev.size > next.size) {
					int numToMove = calcDeleteNumToMove(cur.size, prev.size,
							true);
					moveItems(prev, cur, numToMove, true);
				}
				else {  // next.size >= prev.size
					int numToMove = calcDeleteNumToMove(cur.size, next.size,
							false);
					moveItems(next, cur, numToMove, false);
				}
			}
			else if (prev.size > this.halfFull
					&& next.size <= this.halfFull)
			{
				mergeNodes(cur, next);
			}
			else if (prev.size == this.halfFull) {
				mergeNodes(prev, cur);
				return true;
			}
//			else {  // prev.size < this.halfFull
//				// This condition should never be true
//				throw new AssertionError(prev.size < this.halfFull);
//			}
		}
		// Otherwise, deleting from start or end
		else {  // prev == null || next == null
			if (next == null) {
				if (prev.size > this.halfFull) {
					int numToMove = calcDeleteNumToMove(cur.size, prev.size,
							true);
					moveItems(prev, cur, numToMove, true);
				}
				else if (prev.size == this.halfFull) {
					mergeNodes(prev, cur);
					return true;
				}
//				else {  // prev.size < this.halfFull
//					// This condition should never be true
//					throw new AssertionError(prev.size < this.halfFull);
//				}
			}
			else {  // prev == null
				if (next.size > this.halfFull) {
					int numToMove = calcDeleteNumToMove(cur.size, next.size,
							false);
					moveItems(next, cur, numToMove, false);
				}
				else {  // next.size <= this.halfFull
					mergeNodes(cur, next);
				}
			}
		}
		
		return false;
	}
	
	
	/**
	 * <p>
	 * Version: 1.1
	 */
	private void moveItems(Node src, Node dest, int numToMove,
			boolean leftToRight)
	{
		// Assert valid arguments:
		assert src != null && dest != null;
		assert numToMove > 0 && numToMove <= src.size;
		
		/* Move items from 'src' Node to its next Node 'dest'. (Moves
		 * 'numToMove' items from last 1/2 of 'src' to start of 'dest'.) */
		if (leftToRight) {
			// Create vacancies at start of 'dest.items'
			System.arraycopy(dest.items, 0, dest.items, numToMove,
					numToMove);
			// Copy items from 'src.items' into vacancies.
			System.arraycopy(src.items, src.size - numToMove, dest.items, 0,
					numToMove);
			// Fill vacancies in 'src.items' w/ 'null'.
			Arrays.fill(src.items, src.size - numToMove, src.size, null);
		}
		/* Move items from 'src' Node to its previous Node 'dest'. (Moves
		 * 'numToMove' items from 1st 1/2 of 'src' to last 1/2 of 'dest') */
		else {
			// Copy items from 'src.items' into vacancies in 'dest.items'
			System.arraycopy(src.items, 0, dest.items,
					dest.size, numToMove);
			// Copy items from 'src.items' into vacancies at its start
			System.arraycopy(src.items, numToMove, src.items, 0, numToMove);
			// Fill vacancies in 'src.items' w/ 'null'.
			Arrays.fill(src.items, src.size, src.size + numToMove, null);
		}
		
		// Update Nodes' sizes
		src.size -= numToMove;
		dest.size += numToMove;
	}
	
	
	/**
	 * <p>
	 * Version: 1.1
	 */
	private void moveItemsToNewNode(Node src, Node dest,
			int numToMove)
	{
		// Assert valid arguments:
		assert src != null && dest != null;
		assert numToMove > 0 && numToMove <= src.size;
		
		/* Copy items in 2nd half of 'src.items' to new Node 'dest's
		 * 'items'. */
		System.arraycopy(src.items, this.maxItems - numToMove, dest.items,
				0, numToMove);
				
		// Set the now vacated locations in 'src' to 'null'
		Arrays.fill(src.items, this.maxItems - numToMove, this.maxItems,
				null);
		
		// Update Nodes' sizes
		src.size -= numToMove;
		dest.size += numToMove;
	}
	
	
	/**
	 * <p>
	 * Version: 1.1
	 */
	private void mergeNodes(Node left, Node right) {
		// Assert valid arguments
		assert left != null && right != null;
		
		System.arraycopy(right.items, 0, left.items, left.size, right.size);
		left.size += right.size;
		left.next = right.next;
	}
	
	
	/**
	 * <p>
	 * Version: 1.2
	 * <p>
	 * Version Notes: This is a more efficient implementation, which uses
	 *   right shifts in place of division by 2.
	 */
	private int calcDeleteNumToMove(int nodeSize, int neighborSize,
			boolean isPrev)
	{
		// Assert valid arguments:
		assert nodeSize >= 0 && nodeSize <= this.maxItems;
		assert neighborSize >= 0 && neighborSize <= this.maxItems;
		
		int diff = neighborSize - nodeSize;
		if (isPrev) {
			return diff >>> 1;  		  // diff / 2 
		} else {
			if ((diff & 1) == 1)  // if ((diff % 2) == 1)
				return (diff >>> 1) + 1;  // (diff / 2) + 1
			else
				return diff >>> 1;  	  // diff / 2 
		}
	}
	
	
	/**
	 * <p>
	 * Version: 2.0
	 * <p>
	 * Version Notes: Added a 'prev' Node field and updated the constructor
	 *   to have 'prev' & 'next' Node parameters.  
	 */
	private class Node {
		private Node prev, next;
		private Object[] items;
		private int size;
		
		
		/**
		 * <p>
		 * Version: 2.1
		 * <p>
		 * Version Notes: Updated so it now has 'prev' & 'next' Node
		 *   parameters.
		 */
		Node(Node prev, Node next) {
			this.prev = prev;
			this.next = next;
			this.items = new Object[UnrolledLinkedList.this.maxItems];
			this.size = 0;
		}
		
		
		/**
		 * Usage Notes: This method assumes {@code index} is within allowed
		 *   bounds ({@code index >= 0 && index <= this.size}).
		 */
		void insert(int index, E item) {
			int numToMove = this.size - index;
			if (numToMove > 0) {
				System.arraycopy(this.items, index, this.items, index + 1,
						numToMove);
			}
			this.items[index] = item;
			this.size++;
			UnrolledLinkedList.this.size++;
		}
		
		
		/**
		 * Usage Notes: This method assumes {@code index} is within bounds
		 *   ({@code index >= 0 && index < this.size}).
		 * 
		 * @return the previous item at position {@code index}, in this
		 *    {@code Node}.
		 */
		@SuppressWarnings("unchecked")
		E set(int index, E item) {
			E ret = (E) this.items[index];  // unchecked cast
			this.items[index] = item;
			return ret;
		}
		
		
		/**
		 * Usage Notes: This method assumes {@code index} is within bounds
		 *   ({@code index >= 0 && index < this.size}).
		 *   
		 * @return the previous item at position {@code index}, in this
		 *    {@code Node}.
		 */
		@SuppressWarnings("unchecked")
		E delete(int index) {
			E ret = (E) this.items[index];  // unchecked cast
			int numToMove = this.size - index - 1;
			if (numToMove > 0) {
				System.arraycopy(this.items, index + 1, this.items, index,
						numToMove);
			}
			this.items[--this.size] = null;
			UnrolledLinkedList.this.size--;
			return ret;
		}
		
		
		/**
		 * Usage Notes: This method assumes {@code index} is within bounds
		 *   ({@code index >= 0 && index < this.size}).
		 */
		@SuppressWarnings("unchecked")
		E get(int index) {
			return (E) this.items[index];  // unchecked cast
		}
	}
	
	
	
	/**
	 * <p>
	 * Version: 1.3
	 * <p>
	 * Version Notes: Updated remove(), so that it now uses Node's new
	 *   'prev' field, instead of calling getNodeAtNodeIndex(long).
	 */
	private class Iter implements Iterator<E> {
		private boolean canRemove, advanced;
		private Node nxt, prv;
		private long nodeIndex;
		private int arrIndex, expectedModCount;
		
		
		/**
		 * Version: 1.0.1
		 * <p>
		 * Version Notes: Fixed a bug, where 'this.expectedModCount' was
		 *   set to 0, instead of 'UnrolledLinkedList.this.modCount'. This
		 *   would cause a ConcurrentModificationException to be thrown by
		 *   next() in every case, except where this list is empty when the
		 *   Iterator is created.
		 */
		private Iter() {
			this.prv = null;
			this.nxt = UnrolledLinkedList.this.head;
			this.nodeIndex = 0L;
			this.arrIndex = 0;
			this.expectedModCount = UnrolledLinkedList.this.modCount;
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
				this.canRemove = true;
				this.advanced = false;
				return this.nxt.get(this.arrIndex);
			} else {
				throw new NoSuchElementException();
			}
		}
		
		
		/**
		 * Version: 1.1
		 */
		/*
		 * Version Notes: Updated to use Node's new 'prev' field, instead
		 *   of calling "getNodeAtNodeIndex(nodeIndex - 1)".
		 */
		public void remove() {
			if (this.canRemove) {
				checkForComodification();
				// Delete item from 'nxt' Node
				this.nxt.delete(this.arrIndex);
				this.arrIndex--;
				
				// Update 'modCount':
				UnrolledLinkedList.this.modCount++;
				this.expectedModCount = UnrolledLinkedList.this.modCount;
				
				/* If 'nxt' is now less than 1/2 full, redistribute items
				 * if 'this.prv' or 'this.nxt.next' is not 'null': */
				if (this.nxt.size < UnrolledLinkedList.this.halfFull
					&& (this.prv != null || this.nxt.next != null))
				{
					/* Redistribute items. If 'nxt' Node is merged into
					 * 'prv', update Node refs and counters. */
					if (redistItems(this.prv, this.nxt, this.nxt.next)) {
						int prevSz = this.prv.size;
						this.nodeIndex--;
						this.nxt = this.prv;
						this.prv = this.prv.prev;
						this.arrIndex = this.arrIndex + prevSz;
					}
				}
				this.canRemove = false;
			} else {
				throw new IllegalStateException();
			}
		}
		
		
		private void advance() {
			this.arrIndex++;
			if (this.arrIndex == this.nxt.size) {
				this.prv = this.nxt;
				this.nxt = this.nxt.next;
				this.nodeIndex++;
				this.arrIndex = 0;
			}
			this.advanced = true;
		}
		
		
		private final void checkForComodification() {
		    if (UnrolledLinkedList.this.modCount != this.expectedModCount)
		    	throw new ConcurrentModificationException();
		}
	}

	
	
	/**
	 * <p>
	 * Version: 1.3
	 * <p>
	 * Version Notes: Updated the advance() & remove() methods to use Node's
	 *   new 'prev' field, instead of calling getNodeAtNodeIndex(long).
	 */
	private class DescIter implements Iterator<E> {
		private boolean canRemove, advanced;
		private Node nxt,  // current Node
						prv;  // "previous" Node ("nxt.next")
		private long nodeIndex;
		private int arrIndex, expectedModCount;
		
		
		private DescIter() {
			this.prv = null;
			
			// Get last Node & its node index:
			long[] nodeIndexArr = new long[1];
			this.nxt = findLastNode(nodeIndexArr);
			
			this.nodeIndex = nodeIndexArr[0];
			this.arrIndex = this.nxt.size - 1;
			this.expectedModCount = UnrolledLinkedList.this.modCount;
			this.advanced = true;
			this.canRemove = false;
		}
		
		
		public boolean hasNext() {
			if (!this.advanced)
				advance();
			return this.nxt != null && this.arrIndex >= 0;
		}
		
		
		public E next() {
			checkForComodification();
			if (!this.advanced)
				advance();
			if (this.nxt != null && this.arrIndex >= 0) {
				this.canRemove = true;
				this.advanced = false;
				return this.nxt.get(this.arrIndex);
			} else {
				throw new NoSuchElementException();
			}
		}
		
		
		/**
		 * <p>
		 * Version: 1.2
		 */
		public void remove() {
			if (this.canRemove) {
				checkForComodification();
				// Delete item from 'nxt' Node
				this.nxt.delete(this.arrIndex);
				
				// Update 'modCount':
				UnrolledLinkedList.this.modCount++;
				this.expectedModCount = UnrolledLinkedList.this.modCount;
				
				// Get 'nxt' Node's previous Node, as 'nextNext'
				Node nextNext = this.nxt.prev;
				
				/* If 'nxt' is now less than 1/2 full, redistribute items
				 * if 'nextNext' or 'this.prv' is not 'null': */
				if (this.nxt.size < UnrolledLinkedList.this.halfFull
					&& (nextNext != null || this.prv != null))
				{
					/* Redistribute items. If 'nxt' Node is merged into
					 * 'nextNext', update Node refs and counters. */
					if (redistItems(nextNext, this.nxt, this.prv)) {
						this.nodeIndex--;
						this.nxt = nextNext;
						this.arrIndex = this.arrIndex + nextNext.size;
					}
				}
				this.canRemove = false;
			}
			else {
				throw new IllegalStateException();
			}
		}
		
		
		/**
		 * Version: 1.1
		 * <p>
		 * Version Notes: Updated to use Node's new 'prev' field, instead
		 *   of calling getNodeAtNodeIndex(long).
		 */
		private void advance() {
			this.arrIndex--;
			if (this.arrIndex < 0) {
				this.prv = this.nxt;
				this.nodeIndex--;
				if (nodeIndex >= 0) {
					this.nxt = this.nxt.prev;
					this.arrIndex = this.nxt.size - 1;
				}
				else {
					this.nxt = null;
					this.arrIndex = -1;
				}
			}
			this.advanced = true;
		}
		
		
		/**
		 * Finds and returns the last {@code Node}, and its node index (via
		 * the {@code nodeIndex} parameter).
		 * <p>
		 * Version: 1.1
		 *   
		 * @param nodeIndex a non-null {@code long[]} of length >= 1, which is used to
		 *    return the found {@code Node}'s node index.
		 *    
		 * @return the last {@code Node}.
		 */
		private Node findLastNode(long[] nodeIndex) {
			Node prv = null;
			Node cur = UnrolledLinkedList.this.head;
			long i;
			for (i = -1L; cur != null; cur = cur.next, i++) {
				prv = cur;
			}
			nodeIndex[0] = i;
			return prv;
		}
		
		
		private final void checkForComodification() {
		    if (UnrolledLinkedList.this.modCount != this.expectedModCount)
		    	throw new ConcurrentModificationException();
		}
	}
	
}
