package info.willdspann.collections.queue;

import java.util.*;  // Queue, List, ArrayList, Collections, Arrays, Random

import org.junit.*;  // Test, Before
import org.junit.experimental.categories.*;

import static org.junit.Assert.*;

/**
 *
 *
 * @author Will D. Spann
 * @version 1.0
 */
public class PriorityQueueCorrectnessTester2<Q extends Queue<String>> {
	private static final int COUNT = 5; // NOTE: COUNT should be >= 3 & < 9.
	
	private Class<Q> queueType;
	private Queue<String> q;

	/**
	 * @throws NullPointerException if {@code queueType} is {@code null}.
	 */
    protected PriorityQueueCorrectnessTester2(Class<Q> queueType) {
    	// Check argument validity:
    	if (queueType == null)
    		throw new NullPointerException();
    	
    	this.queueType = queueType;
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


    public interface RequiredQueueTests { }
    
    public interface OptionalQueueTests { }
    
    public interface AddTests { }
    
    
    @Category(RequiredQueueTests.class)
	public abstract static class PriorityQueueReqdCorrectnessTester<
    		Q extends Queue<String>>
    {
    	private Class<Q> queueType;
    	private Queue<String> q;
    	
    	
    	protected PriorityQueueReqdCorrectnessTester(Class<Q> queueType) {
    		this.queueType = queueType;
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
}
