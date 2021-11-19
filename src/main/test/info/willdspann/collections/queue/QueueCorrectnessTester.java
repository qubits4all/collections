/*
 * Last Modified: 2/12/09
 * J2SE Version: 5.0
 */

package info.willdspann.collections.queue;

import java.util.*;  // Queue, List, ArrayList, Collections, Arrays

import org.junit.*;  // Test, Before
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;


/**
 * A set of JUnit tests for testing a {@code Queue}'s single-threaded
 * correctness.
 *
 * @author Will D. Spann
 * @version 1.0
 */
@RunWith(JUnit4.class)
public abstract class QueueCorrectnessTester<Q extends Queue> {
	private Class<Q> queueType;
	private Queue<String> q;
	

	/**
	 * @throws NullPointerException if {@code queueType} is {@code null}.
	 */
    protected QueueCorrectnessTester(Class<Q> queueType) {
    	if (queueType == null)
    		throw new NullPointerException();
    	
    	this.queueType = queueType;
    }
    
    
    @Before
    public void setup() {
    	try {
    		this.q = (Queue<String>) this.queueType.newInstance(); // Unchecked cast
    	} catch (InstantiationException ie) {
    		fail("Specified Queue doesn't have a no-argument constructor. "
					+ "Unable to create instance via reflection.");
    	} catch (IllegalAccessException iae) {
    		fail("Specified Queue doesn't have a public no-argument "
					+ "constructor. Unable to create instance via reflection.");
    	}
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
		final int COUNT = 3;
		for (int i = 0; i < COUNT; i++)
			q.offer("test" + (i+1));
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
		final int COUNT = 3;
		for (int i = 0; i < COUNT; i++)
			q.offer("test" + (i+1));
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
		final int COUNT = 3;
		for (int i = 0; i < COUNT; i++)
			q.offer("test" + (i+1));
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
		final int COUNT = 3;
		for (int i = 0; i < COUNT; i++)
			q.add("test" + (i+1));
		String[] added = new String[COUNT];
		for (int i = 0; i < COUNT; i++) {
			added[i] = q.remove();
			assertNotNull(added[i]);
			assertEquals("test" + (i+1), added[i]);
		}
		assertTrue(q.isEmpty());
	}
	
	
	@Test
	public void testAddAllEmptyCollection() {
		List<String> emptyList = Collections.emptyList();
		
		assertFalse(q.addAll(emptyList));
	}
	
	
	@Test
	public void testAddAll() {
		final int COUNT = 3;
		List<String> expList = new ArrayList<String>();
		String str;
		for (int i = 0; i < COUNT; i++) {
			str = "test" + (i+1);
			expList.add(str);
			q.add(str);
		}
		List<String> toAdd = Arrays.asList("test5", "test4");
		expList.addAll(toAdd);
		String[] expected = expList.toArray(new String[0]);
		
		assertTrue(q.addAll(toAdd));
		assertArrayEquals(expected, q.toArray(new String[0]));
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
		final int COUNT = 3;
		for (int i = 0; i < COUNT; i++)
			q.offer("test" + (i+1));
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
	
	
	@Test
	public void testIteratorMultiple() {
		final int COUNT = 3;
		for (int i = 0; i < COUNT; i++)
			q.offer("test" + (i+1));
		int j = 0;
		for (String str : q) {
			assertNotNull(str);
			assertEquals("test" + (j+1), str);
			j++;
		}
		assertEquals(COUNT, j);
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
		final int COUNT = 3;
		for (int i = 0; i < COUNT; i++)
			q.offer("test" + (i+1));
		
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
			assertEquals("test2", str);
			it.remove();
		}
		String last = q.peek();
		assertNotNull(last);
		assertEquals("test3", last);
		assertEquals(1, q.size());
	}
	
	
	@Test
	public void testIteratorRemoveMultipleFirstAndLast() {
		final int COUNT = 3;
		for (int i = 0; i < COUNT; i++)
			q.offer("test" + (i+1));
		
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
			assertEquals("test3", str);
			it.remove();
		}
		String middle = q.peek();
		assertNotNull(middle);
		assertEquals("test2", middle);
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
	
	
	@Test
	public void testRemove1stObjectWithMultiple() {
		final int COUNT = 3;
		for (int i = 0; i < COUNT; i++)
			q.offer("test" + (i+1));
		boolean removed = q.remove("test1");
		
		assertTrue(removed);
		assertEquals(2, q.size());
		
		int j = 0;
		for (String str : q) {
			assertEquals("test" + (j+2), str);
			j++;
		}
	}
	
	
	@Test
	public void testRemoveLastObjectWithMultiple() {
		final int COUNT = 3;
		for (int i = 0; i < COUNT; i++)
			q.offer("test" + (i+1));
		boolean removed = q.remove("test" + COUNT);
		
		assertTrue(removed);
		assertEquals(2, q.size());
		
		int j = 0;
		for (String str : q) {
			assertEquals("test" + (j+1), str);
			j++;
		}
	}
	
	
	@Test
	public void testRemoveMiddleObjectWithMultiple() {
		final int COUNT = 3;
		for (int i = 0; i < COUNT; i++)
			q.offer("test" + (i+1));
		boolean removed = q.remove("test2");
		
		assertTrue(removed);
		assertEquals(2, q.size());
		
		String[] expected = new String[] { "test1", "test3" };
		int j = 0;
		for (String str : q) {
			assertEquals(expected[j], str);
			j++;
		}
	}
	
	
	@Test
	public void testRemoveAllOnEmpty() {
		assertFalse(q.removeAll(Arrays.asList("blah")));
	}
	
	
	@Test
	public void testRemoveAllEmptyCollection() {
		final int COUNT = 3;
		for (int i = 0; i < COUNT; i++)
			q.offer("test" + (i+1));
		List<String> emptyList = Collections.emptyList();
		
		assertFalse(q.removeAll(emptyList));
	}
	
	
	@Test
	public void testRemoveAllOnMultiple() {
		final int COUNT = 3;
		for (int i = 0; i < COUNT; i++)
			q.offer("test" + (i+1));
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
		final int COUNT = 3;
		for (int i = 0; i < COUNT; i++)
			q.offer("test" + (i+1));
		List<String> emptyList = Collections.emptyList();
		
		assertTrue(q.retainAll(emptyList));
		assertTrue(q.isEmpty());
	}
	
	
	@Test
	public void testRetainAllOnMultiple() {
		final int COUNT = 3;
		for (int i = 0; i < COUNT; i++)
			q.offer("test" + (i+1));
		List<String> toRetain = Arrays.asList("test3", "blah", "test1");
		
		assertTrue(q.retainAll(toRetain));
		assertEquals(2, q.size());
		
		String[] expected = new String[] { "test1", "test3" };
		assertArrayEquals(expected, q.toArray(new String[0]));
	}
	
	
	@Test
	public void testContains() {
		final int COUNT = 3;
		for (int i = 0; i < COUNT; i++)
			q.offer("test" + (i+1));
		
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
		final int COUNT = 3;
		for (int i = 0; i < COUNT; i++)
			q.offer("test" + (i+1));
		List<String> emptyList = Collections.emptyList();
		
		assertTrue(q.containsAll(emptyList));
	}
	
	
	@Test
	public void testContainsAllOnMultiple() {
		final int COUNT = 3;
		for (int i = 0; i < COUNT; i++)
			q.offer("test" + (i+1));
		
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
		q.offer("test1");
		q.offer("test2");
		q.offer("test3");
		
		assertEquals(3, q.size());
	}
	
	
	@Test
	public void testIsEmptyWithItems() {
		final int COUNT = 3;
		for (int i = 0; i < COUNT; i++)
			q.offer("test" + (i+1));
		
		assertFalse(q.isEmpty());
	}
	
	
	@Test
	public void testToArray1() {
		final int COUNT = 3;
		String[] offered = new String[COUNT];
		for (int i = 0; i < COUNT; i++) {
			offered[i] = "test" + (i+1);
			q.offer(offered[i]);
		}
		
		Object[] retArray = q.toArray();
		assertArrayEquals(offered, retArray);
	}
	
	
	@Test
	public void testToArray2() {
		final int COUNT = 3;
		String[] offered = new String[COUNT];
		for (int i = 0; i < COUNT; i++) {
			offered[i] = "test" + (i+1);
			q.offer(offered[i]);
		}
		
		String[] retArray = q.toArray(new String[0]);
		assertArrayEquals(offered, retArray);
	}
	
}
