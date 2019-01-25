/*
 * Last Modified: 10/5/08
 * Prev. Modified: 7/17/08
 * J2SE Version: 5.0
 * 
 * Version Notes: Removed debug output code, since all tests now pass. 
 */

package info.willdspann.collections.utils;

import java.util.*;  // List, Arrays, Collections

import org.junit.Test;

import info.willdspann.collections.utils.HeapBitReversedLongCounter;

import static org.junit.Assert.*;


/**
 * JUnit unit-testing class for testing
 * {@code HeapBitReversedLongCounterTest}.
 *
 * @author Will D. Spann
 * @version 1.0.1
 * 
 * @see HeapBitReversedLongCounter
 * 		HeapBitReversedLongCounter
 */
public class HeapBitReversedLongCounterTest {
	private static final int TEST_DATA_SZ = 15;
	private static final long NONREV_VAL = 14L;
	private static final long REV_VAL;
	private static final List<Long> incrValues = Arrays.asList(
			0x0000000000000000L, 0x0000000000000001L, 0x0000000000000002L,
			0x0000000000000003L, 0x0000000000000005L, 0x0000000000000004L,
			0x0000000000000006L, 0x0000000000000007L, 0x000000000000000BL,
			0x0000000000000009L, 0x000000000000000DL, 0x0000000000000008L,
			0x000000000000000CL, 0x000000000000000AL, 0x000000000000000EL
		);
	private static final List<Long> decrValues;
	
	// Init. static constants
	static {
		// Set up 'decrValues' as a List having the reverse of 'incrValues': 
		decrValues = new ArrayList<Long>(incrValues);
		Collections.reverse(decrValues);
		
		// Set REV_VAL to the last 'incrValues' value
		REV_VAL = incrValues.get(TEST_DATA_SZ - 1);
	}
	
	
	@Test
	public void factoryNewInstance() {
		HeapBitReversedLongCounter count = HeapBitReversedLongCounter
				.newInstance();
		
		assertNotNull(count);
		assertEquals(0L, count.get());
		assertEquals(0L, count.getNonreversedCount());
	}
	
	
	@Test
	public void factoryNewAtRevCount() {
		HeapBitReversedLongCounter count = HeapBitReversedLongCounter
				.newAtReversedCount(REV_VAL);
		
		assertNotNull(count);
		assertEquals(REV_VAL, count.get());
		assertEquals(NONREV_VAL, count.getNonreversedCount());
	}
	
	
	@Test
	public void factoryNewAtRevCountBelowZero() {
		HeapBitReversedLongCounter count = HeapBitReversedLongCounter
				.newAtReversedCount(-1L);
		
		assertNull(count);
	}
	
	
	@Test
	public void factoryNewAtNonrevCount() {
		HeapBitReversedLongCounter count = HeapBitReversedLongCounter
				.newAtNonreversedCount(NONREV_VAL);
		
		assertNotNull(count);
		assertEquals(NONREV_VAL, count.getNonreversedCount());
		assertEquals(REV_VAL, count.get());
	}
	
	
	@Test
	public void factoryNewAtNonrevCountBelowZero() {
		HeapBitReversedLongCounter count = HeapBitReversedLongCounter
				.newAtNonreversedCount(-1L);
		
		assertNull(count);
	}
	
	
	/**
	 * Version: 1.1
	 */
	@Test
	public void getAndIncrement() {
		HeapBitReversedLongCounter count = HeapBitReversedLongCounter
				.newInstance();
		for (long exp : incrValues)
			assertEquals(exp, count.getAndIncrement());
	}
    
    
	/**
	 * Version: 1.1
	 */
    @Test
    public void incrementAndGet() {
    	HeapBitReversedLongCounter count = HeapBitReversedLongCounter
    			.newInstance();
    	Iterator<Long> it = incrValues.iterator();
    	it.next();
    	long exp;
    	while (it.hasNext()) {
    		exp = it.next();
    		assertEquals(exp, count.incrementAndGet());
    	}
    }
    
    
    /**
     * Version: 2.0
     */
    @Test
    public void getAndDecrement() {
    	HeapBitReversedLongCounter count = HeapBitReversedLongCounter
				.newAtNonreversedCount(TEST_DATA_SZ - 1L);
    	
    	int j = TEST_DATA_SZ - 1;
    	long exp;
    	for (Iterator<Long> it = decrValues.iterator(); j > 0; j--) {
    		exp = it.next();
    		assertEquals(exp, count.getAndDecrement());
    	}
    	assertEquals(0L, count.get());
    }
    
    
//    @Test
//    public void getAndDecrement() {
//    	// Increment counter to non-reversed value TEST_DATA_SZ - 1:
//    	HeapBitReversedLongCounter count = new HeapBitReversedLongCounter();
//    	for (int i = 0; i < TEST_DATA_SZ - 1; i++)
//    		count.incrementAndGet();
//    	
//    	int j = TEST_DATA_SZ - 1;
//    	long exp;
//    	for (Iterator<Long> it = decrValues.iterator(); j > 0; j--) {
//    		exp = it.next();
//    		assertEquals(exp, count.getAndDecrement());
//    	}
//    	assertEquals(0L, count.get());
//    }
    
    
    /**
     * Version: 2.0
     */
    @Test
    public void decrementAndGet() {
    	HeapBitReversedLongCounter count = HeapBitReversedLongCounter
    			.newAtNonreversedCount(TEST_DATA_SZ);
    	
    	for (long exp : decrValues)
    		assertEquals(exp, count.decrementAndGet());
    }
    
    
//    @Test
//    public void decrementAndGet() {
//    	// Increment counter to non-reversed value TEST_DATA_SZ:
//    	HeapBitReversedLongCounter count = new HeapBitReversedLongCounter();
//    	for (int i = 0; i < TEST_DATA_SZ; i++)
//    		count.incrementAndGet();
//    	
//    	for (long exp : decrValues)
//    		assertEquals(exp, count.decrementAndGet());
//    }
    
    
    @Test
    public void set() {
    	HeapBitReversedLongCounter count = HeapBitReversedLongCounter
				.newInstance();
    	
    	count.set(REV_VAL);
    	
    	assertEquals(REV_VAL, count.get());
    	assertEquals(NONREV_VAL, count.getNonreversedCount());
    }
    
    
    @Test(expected=IllegalArgumentException.class)
    public void setBelowZero() {
    	HeapBitReversedLongCounter count = HeapBitReversedLongCounter
				.newInstance();
    	
    	count.set(-1L);
    }
    
    
    @Test
    public void setNonreversed() {
    	HeapBitReversedLongCounter count = HeapBitReversedLongCounter
				.newInstance();
    	
    	count.setNonreversed(NONREV_VAL);
    	
    	assertEquals(NONREV_VAL, count.getNonreversedCount());
    	assertEquals(REV_VAL, count.get());
    }
    
    
    @Test(expected=IllegalArgumentException.class)
    public void setNonreversedBelowZero() {
    	HeapBitReversedLongCounter count = HeapBitReversedLongCounter
				.newInstance();
    	
    	count.setNonreversed(-1L);
    }
    
    
    /**
	 * Version: 1.1
	 */
    @Test
    public void reset() {
    	HeapBitReversedLongCounter count = HeapBitReversedLongCounter
    			.newInstance();
    	count.incrementAndGet();
    	count.reset();
    	
    	assertEquals(0L, count.get());
    	assertEquals(0L, count.getNonreversedCount());
    }
    
    
    /**
	 * Version: 1.1
	 */
    @Test
    public void get() {
    	HeapBitReversedLongCounter count = HeapBitReversedLongCounter
    			.newInstance();
    	count.incrementAndGet();
    	
    	assertEquals(incrValues.get(1).longValue(), count.get());
    }
    
    
    /**
	 * Version: 1.1
	 */
    @Test
    public void getNonreversedCount() {
    	HeapBitReversedLongCounter count = HeapBitReversedLongCounter
    			.newInstance();
    	count.incrementAndGet();
    	
    	assertEquals(1L, count.getNonreversedCount());
    }
    
    
    /**
	 * Version: 1.1
	 */
    @Test(expected=IllegalStateException.class)
    public void decrementBelowZero() {
    	HeapBitReversedLongCounter count = HeapBitReversedLongCounter
    			.newInstance();
    	count.decrementAndGet();
    }
    
    
    /**
	 * Version: 2.0
	 */
    @Test(expected=IllegalStateException.class)
    public void incrementAboveMax() {
    	HeapBitReversedLongCounter count = HeapBitReversedLongCounter
    			.newAtNonreversedCount(
    				HeapBitReversedLongCounter.MAX_VALUE);
    	
    	count.incrementAndGet();
    }

}
