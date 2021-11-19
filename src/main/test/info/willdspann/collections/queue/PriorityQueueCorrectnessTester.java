/*
 * Last Modified: 12/28/09
 * Prev. Modified: 2/14/09
 * J2SE Version: 5.0
 * 
 * Version Notes: 
 *     v1.0.1: Fixed a number of bugs, three of which (in
 *   testIteratorMultiple(), testToArray1() & testToArray2()) where the order
 *   of items returned by the Iterator had been assumed to be priority order.
 *   This assumption is incorrect, because priority queues' iterators generally
 *   do not guarantee priority-order traversal.
 *       The other bugs were in testAddAll(), testRemove1stObjectWithMultiple(),
 *   testRemoveMiddleObjectWithMultiple(), & testRemoveLastObjectWithMultiple().
 *   These tests had used the queue's Iterator to verify the contents of the
 *   queue after their operations, which meant these tests were also testing
 *   the operation of the queue's Iterator, not just the intended case.
 */

package info.willdspann.collections.queue;

import java.util.*;  // Queue, List, ArrayList, Collections, Arrays, Random

import org.junit.*;  // Test, Before
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;


/**
 * A set of JUnit tests for testing a priority {@code Queue}'s single-threaded
 * correctness.
 *
 * @author Will D. Spann
 * @version 1.1
 */
@RunWith(JUnit4.class)
public abstract class PriorityQueueCorrectnessTester<Q extends Queue<String>> {
	private static final int COUNT = 5; // NOTE: COUNT should be >= 3 & < 9.
	
	private Class<Q> queueType;
	private Queue<String> q;
	

	/**
	 * @throws NullPointerException if {@code queueType} is {@code null}.
	 */
    protected PriorityQueueCorrectnessTester(Class<Q> queueType) {
    	// Check argument validity:
    	if (queueType == null)
    		throw new NullPointerException();
    	
    	this.queueType = queueType;
    }
    
    
    @Before
    public void setup() {
    	try {
    		this.q = this.queueType.newInstance();
    	} catch (InstantiationException ie) {
    		fail("Specified Queue doesn't have a no-argument constructor. "
					+ "Unable to create instance via reflection.");
    	} catch (IllegalAccessException iae) {
    		fail("Specified Queue doesn't have a public no-argument "
					+ "constructor. Unable to create instance via reflection.");
    	}
    } 
    
    
    /**
     * Returns an array of ints shuffled randomly.
     * 
     * @param count the number of ints to return.
     * @return an array of ints shuffled randomly.
     */
    protected int[] getShuffledInts(int count) {
    	List<Integer> rands = new ArrayList<Integer>(count);
    	for (int i = 0; i < count; i++)
    		rands.add(i);
    	Collections.shuffle(rands);
    	int[] ints = new int[count];
    	int j = 0;
    	for (int n : rands)
    		ints[j++] = n;
    	return ints;
    }
    
    
    @Test
    public void testIsEmptyWhenConstructed() {
    	assertTrue(q.isEmpty());
    	assertEquals(0, q.size());
    }
	
	
	@Test
	public void testPeekOnEmpty() {
		String str = q.peek();
		
		assertNull(str);
	}
	
	
	@Test
	public void testPeekWith1() {
		q.offer("test");
		String first = q.peek();
		
		assertNotNull(first);
		assertEquals("test", first);
		assertEquals(1, q.size());
	}
	
	
	@Test
	public void testPeekMultiple() {
		int[] rands = getShuffledInts(COUNT);
		for (int n : rands)
			q.offer("test" + (n+1));
		String first = q.peek();
		
		assertNotNull(first);
		assertEquals("test1", first);
		assertEquals(COUNT, q.size());
	}
	
	
	@Test(expected=NoSuchElementException.class)
	public void testElementOnEmpty() {
		q.element();
	}
	
	
	@Test
	public void testElementWith1() {
		q.offer("test");
		String first = q.element();
		
		assertNotNull(first);
		assertEquals("test", first);
		assertEquals(1, q.size());
	}
	
	
	@Test
	public void testElementMultiple() {
		int[] rands = getShuffledInts(COUNT);
		for (int n : rands)
			q.offer("test" + (n+1));
		String first = q.element();
		
		assertNotNull(first);
		assertEquals("test1", first);
		assertEquals(COUNT, q.size());
	}
	
	
	@Test
	public void testOfferOnEmpty() {
		q.offer("test");
		String first = q.peek();
		
		assertNotNull(first);
		assertEquals("test", first);
	}
	
	
	
	@Test
	public void testOfferAndPollMultiple() {
		int[] rands = getShuffledInts(COUNT);
		for (int n : rands)
			q.offer("test" + (n+1));
		String[] offered = new String[COUNT];
		for (int i = 0; i < COUNT; i++) {
			offered[i] = q.poll();
			assertNotNull(offered[i]);
			assertEquals("test" + (i+1), offered[i]);
		}
		assertTrue(q.isEmpty());
	}
	
	
	@Test
	public void testAddOnEmpty() {
		q.add("test");
		String first = q.peek();
		
		assertNotNull(first);
		assertEquals("test", first);
	}
	
	
	@Test
	public void testAddAndRemoveMultiple() {
		int[] rands = getShuffledInts(COUNT);
		for (int n : rands)
			q.add("test" + (n+1));
		String[] removed = new String[COUNT];
		for (int i = 0; i < COUNT; i++) {
			removed[i] = q.remove();
			assertNotNull(removed[i]);
			assertEquals("test" + (i+1), removed[i]);
		}
		assertTrue(q.isEmpty());
	}
	
	
	@Test
	public void testAddAllEmptyCollection() {
		List<String> emptyList = Collections.emptyList();
		
		assertFalse(q.addAll(emptyList));
	}
	
	
	/**
	 * <p>
	 * Version: 1.0.1
	 */
	@Test
	public void testAddAll() {
		int[] rands = getShuffledInts(COUNT);
		for (int n : rands)
			q.add("test" + (n+1));
		
		final int ADD_COUNT = 3;
		List<Integer> toAddInts = new ArrayList<Integer>(ADD_COUNT);
		for (int i = COUNT; i < COUNT+ADD_COUNT; i++)
			toAddInts.add(i);
		Collections.shuffle(toAddInts);
		List<String> toAdd = new ArrayList<String>(ADD_COUNT);
		for (int n : toAddInts)
			toAdd.add("test" + (n+1));
		
		String[] expected = new String[COUNT+ADD_COUNT];
		for (int i = 0; i < COUNT+ADD_COUNT; i++)
			expected[i] = "test" + (i+1);
		
		assertTrue(q.addAll(toAdd));
		for (int i = 0; i < q.size(); i++)
			assertEquals(expected[i], q.poll());
	}
	
	
	@Test
	public void testPollOnEmpty() {
		String polled = q.poll();
		
		assertNull(polled);
	}
	
	
	@Test
	public void testPollWith1() {
		q.offer("test");
		String polled = q.poll();
		
		assertNotNull(polled);
		assertEquals("test", polled);
		assertTrue(q.isEmpty());
	}
	
	
	@Test(expected=NoSuchElementException.class)
	public void testRemoveOnEmpty() {
		q.remove();
	}
	
	
	@Test
	public void testRemoveWith1() {
		q.offer("test");
		String removed = q.remove();
		
		assertNotNull(removed);
		assertEquals("test", removed);
		assertTrue(q.isEmpty());
	}
	
	
	@Test
	public void testClearOnEmpty() {
		q.clear();
		
		assertTrue(q.isEmpty());
		assertEquals(0, q.size());
	}
	
	
	@Test
	public void testClearWith1() {
		q.offer("test");
		q.clear();
		
		assertTrue(q.isEmpty());
		assertEquals(0, q.size());
	}
	
	
	@Test
	public void testClearMultiple() {
		int[] rands = getShuffledInts(COUNT);
		for (int n : rands)
			q.offer("test" + (n+1));
		q.clear();
		
		assertTrue(q.isEmpty());
		assertEquals(0, q.size());
	}
	
	
	@Test
	public void testIteratorOnEmpty() {
		for (String str : q)
			fail();
		assertTrue(true);
	}
	
	
	@Test
	public void testIteratorWith1() {
		q.offer("test");
		int i = 0;
		for (String str : q) {
			assertNotNull(str);
			assertEquals("test", str);
			i++;
		}
		assertEquals(1, i);
	}
	
	
	/**
	 * <p>
	 * Version: 1.0.1
	 */
	@Test
	public void testIteratorMultiple() {
		int[] rands = getShuffledInts(COUNT);
		boolean[] covered = new boolean[COUNT];
		for (int n : rands)
			q.offer("test" + (n+1));
		int i = 0;
		for (String str : q) {
			assertNotNull(str);
			int strNo = Integer.parseInt(str.substring(4));
			covered[strNo-1] = true;
			i++;
		}
		assertEquals(COUNT, i);
		for (int j = 0; j < COUNT; j++)
			assertTrue(covered[j]);
	}
	
	
	@Test(expected=IllegalStateException.class)
	public void testIteratorRemoveOnEmpty() {
		Iterator<String> it = q.iterator();
		it.remove();
	}
	
	
	@Test
	public void testIteratorRemoveWith1() {
		q.offer("test");
		Iterator<String> it = q.iterator();
		boolean enteredIf = false;
		if (it.hasNext()) {
			enteredIf = true;
			String str = it.next();
			assertNotNull(str);
			assertEquals("test", str);
			it.remove();
		}
		assertTrue(enteredIf);
		assertTrue(q.isEmpty());
	}
	
	
	@Test(expected=IllegalStateException.class)
	public void testIteratorRemoveTwiceWithoutAdv() {
		q.offer("test");
		Iterator<String> it = q.iterator();
		if (it.hasNext()) {
			String str = it.next();
			assertNotNull(str);
			assertEquals("test", str);
			it.remove();
			it.remove();
		}
	}
	
	
	@Test
	public void testIteratorRemoveMultipleFirst2() {
		int[] rands = getShuffledInts(COUNT);
		for (int n : rands)
			q.offer("test" + (n+1));
		
		Iterator<String> it = q.iterator();
		// Remove 1st item:
		if (it.hasNext()) {
			String str = it.next();
			assertNotNull(str);
			assertEquals("test1", str);
			it.remove();
		}
		// Remove 2nd item:
		if (it.hasNext()) {
			String str = it.next();
			assertNotNull(str);
			it.remove();
		}
		String third = q.peek();
		assertNotNull(third);
		assertEquals(COUNT-2, q.size());
	}
	
	
	@Test
	public void testIteratorRemoveMultipleFirstAndLast() {
		final int COUNT = 3;
		int[] rands = getShuffledInts(COUNT);
		for (int n : rands)
			q.offer("test" + (n+1));
		
		Iterator<String> it = q.iterator();
		// Remove 1st item:
		if (it.hasNext()) {
			String str = it.next();
			assertNotNull(str);
			assertEquals("test1", str);
			it.remove();
		}
		// Advance to 2nd item:
		if (it.hasNext())
			it.next();
		// Remove 3rd (last) item:
		if (it.hasNext()) {
			String str = it.next();
			assertNotNull(str);
			it.remove();
		}
		String middle = q.peek();
		assertNotNull(middle);
		assertEquals(1, q.size());
	}
	
	
	@Test
	public void testRemoveObjectOnEmpty() {
		boolean removed = q.remove("test");
		
		assertFalse(removed);
	}
	
	
	@Test
	public void testRemoveObjectWith1() {
		q.offer("test");
		boolean removed = q.remove("test");
		
		assertTrue(removed);
		assertTrue(q.isEmpty());
	}
	
	
	/**
	 * <p>
	 * Version: 1.0.1
	 */
	@Test
	public void testRemove1stObjectWithMultiple() {
		int[] rands = getShuffledInts(COUNT);
		for (int n : rands)
			q.offer("test" + (n+1));
		assertTrue(q.remove("test1")); // Remove 1st item
		assertEquals(COUNT-1, q.size());
		
		for (int i = 0; i < q.size(); i++)
			assertEquals("test" + (i+2), q.poll());
	}
	
	
	/**
	 * <p>
	 * Version: 1.0.1
	 */
	@Test
	public void testRemoveLastObjectWithMultiple() {
		int[] rands = getShuffledInts(COUNT);
		for (int n : rands)
			q.offer("test" + (n+1));
		assertTrue(q.remove("test" + COUNT)); // Remove last item
		assertEquals(COUNT-1, q.size());
		
		
		for (int i = 0; i < q.size(); i++)
			assertEquals("test" + (i+1), q.poll());
	}
	
	
	/**
	 * <p>
	 * Version: 1.0.1
	 */
	@Test
	public void testRemoveMiddleObjectWithMultiple() {
		final int COUNT = 3;
		int[] rands = getShuffledInts(COUNT);
		for (int n : rands)
			q.offer("test" + (n+1));
		assertTrue(q.remove("test2")); // Remove middle item
		assertEquals(COUNT-1, q.size());
		
		String[] expected = new String[] { "test1", "test3" };
		for (int i = 0; i < q.size(); i++)
			assertEquals(expected[i], q.poll());
	}
	
	
	@Test
	public void testRemoveAllOnEmpty() {
		assertFalse(q.removeAll(Arrays.asList("blah")));
	}
	
	
	@Test
	public void testRemoveAllEmptyCollection() {
		for (int i = 0; i < COUNT; i++)
			q.offer("test" + (i+1));
		List<String> emptyList = Collections.emptyList();
		
		assertFalse(q.removeAll(emptyList));
	}
	
	
	@Test
	public void testRemoveAllOnMultiple() {
		final int COUNT = 3;
		int[] rands = getShuffledInts(COUNT);
		for (int n : rands)
			q.offer("test" + (n+1));
		List<String> toRemove = Arrays.asList("test3", "test1", "blah");
		
		assertTrue(q.removeAll(toRemove));
		assertEquals(1, q.size());
		
		String remaining = q.peek();
		assertNotNull(remaining);
		assertEquals("test2", remaining);
	}
	
	
	@Test
	public void testRetainAllOnEmpty() {
		assertFalse(q.retainAll(Arrays.asList("blah")));
	}
	
	
	/*
	 * Note: Calling retainAll(...) w/ an empty Collection is equivalent to
	 *   calling clear().
	 */
	@Test
	public void testRetainAllEmptyCollection() {
		for (int i = 0; i < COUNT; i++)
			q.offer("test" + (i+1));
		List<String> emptyList = Collections.emptyList();
		
		assertTrue(q.retainAll(emptyList));
		assertTrue(q.isEmpty());
	}
	
	
	@Test
	public void testRetainAllOnMultiple() {
		final int COUNT = 3;
		int[] rands = getShuffledInts(COUNT);
		for (int n : rands)
			q.offer("test" + (n+1));
		List<String> toRetain = Arrays.asList("test3", "blah", "test1");
		
		assertTrue(q.retainAll(toRetain));
		assertEquals(2, q.size());
		
		String[] expected = new String[] { "test1", "test3" };
		int j = 0;
		for (String str : q)
			assertEquals(expected[j++], str);
	}
	
	
	@Test
	public void testContains() {
		int[] rands = getShuffledInts(COUNT);
		for (int n : rands)
			q.offer("test" + (n+1));
		
		for (int i = 0; i < COUNT; i++)
			assertTrue(q.contains("test" + (i+1)));
		assertFalse(q.contains("blah"));
	}
	
	
	@Test
	public void testContainsAllOnEmpty() {
		assertFalse(q.containsAll(Arrays.asList("blah")));
	}
	
	
	@Test
	public void testContainsAllEmptyCollection() {
		for (int i = 0; i < COUNT; i++)
			q.offer("test" + (i+1));
		List<String> emptyList = Collections.emptyList();
		
		assertTrue(q.containsAll(emptyList));
	}
	
	
	@Test
	public void testContainsAllOnMultiple() {
		final int COUNT = 3;
		int[] rands = getShuffledInts(COUNT);
		for (int n : rands)
			q.offer("test" + (n+1));
		
		List<String> strs = Arrays.asList("test1", "test3");
		assertTrue(q.containsAll(strs));
	} 
	
	
	@Test
	public void testSizeWith1() {
		q.offer("test");
		
		assertEquals(1, q.size());
	}
	
	
	@Test
	public void testSizeWithMultiple() {
		int[] rands = getShuffledInts(COUNT);
		for (int n : rands)
			q.offer("test" + (n+1));
		
		assertEquals(COUNT, q.size());
	}
	
	
	@Test
	public void testIsEmptyWithItems() {
		int[] rands = getShuffledInts(COUNT);
		for (int n : rands)
			q.offer("test" + (n+1));
		
		assertFalse(q.isEmpty());
	}
	
	
	/**
	 * <p>
	 * Version: 1.0.1
	 */
	@Test
	public void testToArray1() {
		int[] rands = getShuffledInts(COUNT);
		boolean[] covered = new boolean[COUNT];
		for (int n : rands)
			q.offer("test" + (n+1));
		Object[] retArray = q.toArray();
		assertEquals(COUNT, retArray.length);
		
		// Verify all the strings are there:
		for (Object o : retArray) {
			int strNo = Integer.parseInt(((String) o).substring(4));
			covered[strNo-1] = true;
		}
		for (int j = 0; j < COUNT; j++)
			assertTrue(covered[j]);
	}
	
	
	/**
	 * <p>
	 * Version: 1.0.1
	 */
	@Test
	public void testToArray2() {
		int[] rands = getShuffledInts(COUNT);
		boolean[] covered = new boolean[COUNT];
		for (int n : rands)
			q.offer("test" + (n+1));
		String[] retArray = q.toArray(new String[0]);
		assertEquals(COUNT, retArray.length);
		
		// Verify all the strings are there:
		for (String str : retArray) {
			int strNo = Integer.parseInt(str.substring(4));
			covered[strNo-1] = true;
		}
		for (int j = 0; j < COUNT; j++)
			assertTrue(covered[j]);
	}

}
