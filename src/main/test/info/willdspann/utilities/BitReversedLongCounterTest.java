package info.willdspann.utilities;

import java.util.*;  // List, Arrays, Collections

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * JUnit unit-testing class for testing {@code BitReversedLongCounter}.
 *
 * @author Will D. Spann
 * @version 1.1
 * @see BitReversedLongCounter
 * 		BitReversedLongCounter
 */
public class BitReversedLongCounterTest {
	private static final int TEST_DATA_SZ = 16;
	private static final long NONREV_VAL = 15L;
	private static final long REV_VAL;
	private static final List<Long> incrValues
			= new ArrayList<Long>(TEST_DATA_SZ);
	private static final List<Long> decrValues;
	
	// Init. static constants
	static {
		// Add first 8 values
		Collections.addAll(incrValues,
				0x0000000000000000L, 0x4000000000000000L,
				0x2000000000000000L, 0x6000000000000000L,
				0x1000000000000000L, 0x5000000000000000L,
				0x3000000000000000L, 0x7000000000000000L
			);
		// Add values #8-15, which are simply #0-7 + 0x0800000000000000L:
		List<Long> moreIncrValues = new ArrayList<Long>(8);
		for (long val : incrValues)
			moreIncrValues.add(val + 0x0800000000000000L);
		incrValues.addAll(moreIncrValues);
		
		// Set up 'decrValues' as a List having the reverse of 'incrValues': 
		decrValues = new ArrayList<Long>(incrValues);
		Collections.reverse(decrValues);
		
		// Create default testing reversed value
		REV_VAL = incrValues.get((int) NONREV_VAL);
	}
	
	
	@Test
	public void factoryNewInstance() {
		BitReversedLongCounter count = BitReversedLongCounter.newInstance();
		
		assertNotNull(count);
		assertEquals(0L, count.get());
		assertEquals(0L, count.getNonreversedCount());
	}

	/**
	 * Version: 1.1
	 */
	@Test
	public void factoryNewAtRevCount() {
		BitReversedLongCounter count = BitReversedLongCounter
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
		BitReversedLongCounter count = BitReversedLongCounter
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
		BitReversedLongCounter count = BitReversedLongCounter
				.newAtReversedCount(-1L);
		
		assertNull(count);
    }

	/**
	 * Version: 2.0
	 */
	@Test
	public void factoryNewAtNonrevCountBelowZero() {
		BitReversedLongCounter count = BitReversedLongCounter
				.newAtNonreversedCount(-1L);
		
		assertNull(count);
    }

	
    @Test
	public void getAndIncrement() {
		BitReversedLongCounter count = BitReversedLongCounter.newInstance();
		for (long exp : incrValues)
			assertEquals(exp, count.getAndIncrement());
	}
    
    @Test
    public void incrementAndGet() {
    	BitReversedLongCounter count = BitReversedLongCounter.newInstance();
    	Iterator<Long> it = incrValues.iterator();
    	it.next();
    	long exp;
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
    	BitReversedLongCounter count = BitReversedLongCounter
    			.newAtNonreversedCount((long) TEST_DATA_SZ - 1L);
    	int i = TEST_DATA_SZ - 1;
    	long exp;
    	for (Iterator<Long> it = decrValues.iterator(); i > 0; i--) {
    		exp = it.next();
    		assertEquals(exp, count.getAndDecrement());
    	}
    	assertEquals(0L, count.get());
    }

	/**
	 * Version: 1.1
	 */
    @Test
    public void decrementAndGet() {
    	BitReversedLongCounter count = BitReversedLongCounter
				.newAtNonreversedCount((long) TEST_DATA_SZ);
    	for (long exp : decrValues)
    		assertEquals(exp, count.decrementAndGet());
    }
    
    @Test
    public void reset() {
    	BitReversedLongCounter count = BitReversedLongCounter.newInstance();
    	count.incrementAndGet();
    	count.reset();
    	
    	assertEquals(0L, count.get());
    	assertEquals(0L, count.getNonreversedCount());
    }

    @Test
    public void set() {
    	BitReversedLongCounter count = BitReversedLongCounter.newInstance();
    	count.set(REV_VAL);
    	
    	assertEquals(REV_VAL, count.get());
    	assertEquals(NONREV_VAL, count.getNonreversedCount());
    }

    @Test
    public void setNonreversed() {
    	BitReversedLongCounter count = BitReversedLongCounter.newInstance();
    	count.setNonreversed(NONREV_VAL);
    	
    	assertEquals(NONREV_VAL, count.getNonreversedCount());
    	assertEquals(REV_VAL, count.get());
    }

    @Test
    public void get() {
    	BitReversedLongCounter count = BitReversedLongCounter.newInstance();
    	count.incrementAndGet();
    	
    	assertEquals(incrValues.get(1).longValue(), count.get());
    }
    
    @Test
    public void getNonreversedCount() {
    	BitReversedLongCounter count = BitReversedLongCounter.newInstance();
    	count.incrementAndGet();
    	
    	assertEquals(1L, count.getNonreversedCount());
    }
    
    @Test(expected=IllegalStateException.class)
    public void decrementBelowZero() {
    	BitReversedLongCounter count = BitReversedLongCounter.newInstance();
    	count.decrementAndGet();
    }

	/**
	 * Version: 1.1
	 */
    @Test(expected=IllegalStateException.class)
    public void incrementAboveMax() {
    	BitReversedLongCounter count = BitReversedLongCounter
    			.newAtNonreversedCount(BitReversedLongCounter.MAX_VALUE);
    	count.incrementAndGet();
    }

    @Test(expected=IllegalArgumentException.class)
    public void setBelowZero() {
    	BitReversedLongCounter count = BitReversedLongCounter.newInstance();
    	count.set(-1L);
    }

    @Test(expected=IllegalArgumentException.class)
    public void setNonreversedBelowZero() {
    	BitReversedLongCounter count = BitReversedLongCounter.newInstance();
    	count.setNonreversed(-1L);
    }
}
