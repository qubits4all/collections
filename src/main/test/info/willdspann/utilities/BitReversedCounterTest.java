/*
 * Last Modified: 7/17/08
 * Prev. Modified: 7/17/08
 * J2SE Version: 5.0
 * 
 * Version Notes: Changed incrementAboveMax() to use the
 *   newAtNonreversedCount(int) factory method instead of
 *   newAtReversedCount(int). Also, added version info to test methods that
 *   were updated in v1.1. 
 *     v1.1: Updated the tests to use BitReversedCounter's renamed
 *   newAtReversedCount(int) & newAtNonreversedCount(int) factory methods.
 *     v1.0.1: Minor changes, replacing literal integer constants with
 *   integer constants, such as TEST_DATA_SZ.
 */

package info.willdspann.utilities;

import java.util.*;  // List, Arrays, Collections

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * JUnit unit-testing class for testing {@code BitReversedCounter}.
 *
 * @author <A HREF="mailto:willdspann@yahoo.com">Will D. Spann</A>
 * @version 1.1.1
 * @see BitReversedCounter BitReversedCounter
 */
public class BitReversedCounterTest {
	private static final int TEST_DATA_SZ = 16;
	private static final int NONREV_VAL = 15;
	private static final int REV_VAL;
	private static final List<Integer> incrValues
			= new ArrayList<Integer>(TEST_DATA_SZ);
	private static final List<Integer> decrValues;
	
	// Init. static constants
	static {
		// Add first 8 values
		Collections.addAll(incrValues,
				0x00000000, 0x40000000, 0x20000000, 0x60000000, 0x10000000,
				0x50000000, 0x30000000, 0x70000000
			);
		// Add values #8-15, which are simply #0-7 + 0x08000000:
		List<Integer> moreIncrValues = new ArrayList<Integer>(8);
		for (int val : incrValues)
			moreIncrValues.add(val + 0x08000000);
		incrValues.addAll(moreIncrValues);
		
		// Set up 'decrValues' as a List having the reverse of 'incrValues': 
		decrValues = new ArrayList<Integer>(incrValues);
		Collections.reverse(decrValues);
		
		// Create default testing reversed value
		REV_VAL = incrValues.get(NONREV_VAL);
	}

	@Test
	public void factoryNewInstance() {
		BitReversedCounter count = BitReversedCounter.newInstance();
		
		assertNotNull(count);
		assertEquals(0, count.get());
		assertEquals(0, count.getNonreversedCount());
	}

	/**
	 * Version: 1.1
	 */
	@Test
	public void factoryNewAtRevCount() {
		BitReversedCounter count = BitReversedCounter
				.newAtReversedCount(REV_VAL);
		
		assertNotNull(count);
		assertEquals(REV_VAL, count.get());
		assertEquals(NONREV_VAL, count.getNonreversedCount());
	}

	/**
	 * Version: 1.1
	 */
	@Test
	public void factoryNewAtNonrevCount() {
		BitReversedCounter count = BitReversedCounter
				.newAtNonreversedCount(NONREV_VAL);
		
		assertNotNull(count);
		assertEquals(NONREV_VAL, count.getNonreversedCount());
		assertEquals(REV_VAL, count.get());
	}

	/**
	 * Version: 2.0
	 */
	@Test
	public void factoryNewAtRevCountBelowZero() {
		BitReversedCounter count = BitReversedCounter
				.newAtReversedCount(-1);
		
		assertNull(count);
    }
	
	/**
	 * Version: 2.0
	 */
	@Test
	public void factoryNewAtNonrevCountBelowZero() {
		BitReversedCounter count = BitReversedCounter
				.newAtNonreversedCount(-1);
		
		assertNull(count);
    }

    @Test
	public void getAndIncrement() {
		BitReversedCounter count = BitReversedCounter.newInstance();
		for (int exp : incrValues)
			assertEquals(exp, count.getAndIncrement());
	}
    
    @Test
    public void incrementAndGet() {
    	BitReversedCounter count = BitReversedCounter.newInstance();
    	Iterator<Integer> it = incrValues.iterator();
    	it.next();
    	int exp;
    	while (it.hasNext()) {
    		exp = it.next();
    		assertEquals(exp, count.incrementAndGet());
    	}
    }

	/**
	 * Version: 1.1
	 */
    @Test
    public void getAndDecrement() {
    	BitReversedCounter count = BitReversedCounter
    			.newAtNonreversedCount(TEST_DATA_SZ - 1);
    	int i = TEST_DATA_SZ - 1, exp;
    	for (Iterator<Integer> it = decrValues.iterator(); i > 0; i--) {
    		exp = it.next();
    		assertEquals(exp, count.getAndDecrement());
    	}
    	assertEquals(0, count.get());
    }

	/**
	 * Version: 1.1
	 */
    @Test
    public void decrementAndGet() {
    	BitReversedCounter count = BitReversedCounter
				.newAtNonreversedCount(TEST_DATA_SZ);
    	for (int exp : decrValues)
    		assertEquals(exp, count.decrementAndGet());
    }
    
    @Test
    public void reset() {
    	BitReversedCounter count = BitReversedCounter.newInstance();
    	count.incrementAndGet();
    	count.reset();
    	
    	assertEquals(0, count.get());
    	assertEquals(0, count.getNonreversedCount());
    }
    
    @Test
    public void set() {
    	BitReversedCounter count = BitReversedCounter.newInstance();
    	count.set(REV_VAL);
    	
    	assertEquals(REV_VAL, count.get());
    	assertEquals(NONREV_VAL, count.getNonreversedCount());
    }

    @Test
    public void setNonreversed() {
    	BitReversedCounter count = BitReversedCounter.newInstance();
    	count.setNonreversed(NONREV_VAL);
    	
    	assertEquals(NONREV_VAL, count.getNonreversedCount());
    	assertEquals(REV_VAL, count.get());
    }

    @Test
    public void get() {
    	BitReversedCounter count = BitReversedCounter.newInstance();
    	count.incrementAndGet();
    	
    	assertEquals(incrValues.get(1).intValue(), count.get());
    }

    @Test
    public void getNonreversedCount() {
    	BitReversedCounter count = BitReversedCounter.newInstance();
    	count.incrementAndGet();
    	
    	assertEquals(1, count.getNonreversedCount());
    }
    
    @Test(expected=IllegalStateException.class)
    public void decrementBelowZero() {
    	BitReversedCounter count = BitReversedCounter.newInstance();
    	count.decrementAndGet();
    }
    
    /**
     * Version: 1.2
     */
    @Test(expected=IllegalStateException.class)
    public void incrementAboveMax() {
    	BitReversedCounter count = BitReversedCounter
    			.newAtNonreversedCount(BitReversedCounter.MAX_VALUE);
    	count.incrementAndGet();
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void setBelowZero() {
    	BitReversedCounter count = BitReversedCounter.newInstance();
    	count.set(-1);
    }

    @Test(expected=IllegalArgumentException.class)
    public void setNonreversedBelowZero() {
    	BitReversedCounter count = BitReversedCounter.newInstance();
    	count.setNonreversed(-1);
    }
}
