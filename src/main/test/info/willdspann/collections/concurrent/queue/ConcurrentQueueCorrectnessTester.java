/*
 * Last Modified: 12/28/09
 * Prev. Modified: 2/12/09
 * J2SE Version: 5.0
 * 
 * Version Notes: 
 */

package info.willdspann.collections.concurrent.queue;

import java.util.Queue;
import java.util.concurrent.*;  // ExecutorService, Executors, CyclicBarrier
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.*;  // Test, Before, After
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import info.willdspann.utilities.PseudoRandomUtils;

import static org.junit.Assert.*;


/**
 * JUnit test for testing a {@code Queue}'s multithreaded correctness.
 *
 * @author Will D. Spann
 * @version 1.1
 */
@RunWith(JUnit4.class)
public abstract class ConcurrentQueueCorrectnessTester<Q extends Queue<Integer>> {
	protected static final int DEFAULT_NUM_PAIRS = 10,
							   DEFAULT_NUM_TRIALS = 10000;
	private Class<Q> queueType;
	private ExecutorService pool;   // Init. by setup().
	private int numPairs, numTrials;
	

    protected ConcurrentQueueCorrectnessTester(Class<Q> queueClass,
    		int numPairs, int numTrials)
    {
    	// Check validity of arguments:
    	if (queueClass == null)
    		throw new NullPointerException();
    	if (numPairs < 1) {
    		throw new IllegalArgumentException("'numPairs' must be greater " +
    				"or equal to 1.");
    	}
    	if (numTrials < 1) {
    		throw new IllegalArgumentException("'numTrials' must be greater " +
    				"or equal to 1.");
    	}
    	
    	this.queueType = queueClass;
    }
    
    
    /** Version: 2.0 */
    protected ConcurrentQueueCorrectnessTester(Class<Q> queueClass) {
    	this(queueClass, DEFAULT_NUM_PAIRS, DEFAULT_NUM_TRIALS);
    }
    
    
    @Before
    public void setup() {
    	this.pool = Executors.newCachedThreadPool();
    }
    
    
    /**
     * JUnit multithreaded correctness test. This method should be called by
     * subclasses within an {@code @Test}-annotated JUnit test method.
     * <p>
     * Version: 1.1
     */
    protected void testCorrectness() {
		OfferPollTest tester = null;
		try {
			tester = new OfferPollTest(this.numPairs, this.numTrials,
					this.pool);
		} catch (IllegalAccessException iae) {
			fail("Specified Queue doesn't have a public no-argument "
					+ "constructor. Unable to create instance via reflection.");
		} catch (InstantiationException ie) {
			fail("Specified Queue doesn't have a no-argument constructor. "
					+ "Unable to create instance via reflection.");
		}
		tester.test();
    }
    
    
    @After
    public void shutdown() {
    	this.pool.shutdown();
    }
    
    
    
    /**
	 * Producter-consumer tester, based on the PutTakeTest class by Brian Goetz
	 * and Tim Peierls, from "Java Concurrency in Practice", p. 255-256. This
	 * class has been modified to work with non-blocking concurrent
	 * {@code Queue}s. (See the changes in the Consumer member class.)
	 */
	protected class OfferPollTest {
		protected final ExecutorService pool;
		protected final AtomicInteger offerSum = new AtomicInteger(0);
		protected final AtomicInteger pollSum = new AtomicInteger(0);
		protected final Queue<Integer> q;
		protected final int nTrials, nPairs;
		protected CyclicBarrier barrier;

	    public OfferPollTest(int numPairs, int numTrials, ExecutorService pool)
	    		throws IllegalAccessException, InstantiationException
	    {
	    	this.q = ConcurrentQueueCorrectnessTester.this.queueType
	    			.newInstance();
	    	this.pool = pool;
	    	this.nTrials = numTrials;
	    	this.nPairs = numPairs;
	    	this.barrier = new CyclicBarrier(numPairs * 2 + 1);
	    }
	    
	    public void test() {
	    	try {
	    		for (int i = 0; i < this.nPairs; i++) {
	    			pool.execute(new Producer());
	    			pool.execute(new Consumer());
	    		}
	    		this.barrier.await(); // Wait for all threads to be ready.
	    		this.barrier.await(); // Wait for all threads to finish.
	    		assertEquals(this.offerSum.get(), this.pollSum.get());
	    	} catch (Exception e) {
	    		throw new RuntimeException(e);
	    	}
	    }
	    
	    
	    private class Producer implements Runnable {
	    	public void run() {
	    		try {
	    			int seed = (this.hashCode() ^ (int) System.nanoTime());
	    			int sum = 0;
	    			barrier.await();
	    			for (int i = nTrials; i > 0; --i) {
	    				q.offer(seed);
	    				sum += seed;
	    				seed = PseudoRandomUtils.xorShift(seed);
	    			}
	    			offerSum.getAndAdd(sum);
	    			barrier.await();
	    		} catch (Exception e) {
	    			throw new RuntimeException(e);
	    		}
	    	}
	    }
	    
	    
	    /** Version: 1.1 */
//	    private class Consumer implements Runnable {
//	    	public void run() {
//	    		try {
//	    			barrier.await();
//	    			int sum = 0;
//	    			Integer n = null;
//	    			for (int i = nTrials; i > 0; --i) {
//	    				while (true) {
//	    					n = q.poll();
//	    					if (n != null)
//	    						break;
//	    					Thread.yield();
//	    				}
//	    				sum += n.intValue();
//	    			}
//	    			pollSum.getAndAdd(sum);
//	    			barrier.await();
//	    		} catch (Exception e) {
//	    			throw new RuntimeException(e);
//	    		}
//	    	}
//	    }
	    
	    
	    /* Version: 1.0 */
	    private class Consumer implements Runnable {
	    	public void run() {
	    		try {
	    			barrier.await();
	    			int sum = 0;
	    			Integer n = null;
	    			for (int i = nTrials; i > 0; --i) {
	    				do {
	    					n = q.poll();
	    				} while (n == null);
	    				sum += n.intValue();
	    			}
	    			pollSum.getAndAdd(sum);
	    			barrier.await();
	    		} catch (Exception e) {
	    			throw new RuntimeException(e);
	    		}
	    	}
	    }
	}

}
