package info.willdspann.collections.concurrent.queue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * JUnit test for multithreaded correctness testing of
 * {@code ConcurrentHeapPriorityQueue}.
 *
 * @author Will D. Spann
 * @version 1.1
 */
@RunWith(JUnit4.class)
public class ConcurrentHeapPriorityQueueMTCorrectnessTester
		extends ConcurrentQueueCorrectnessTester<ConcurrentHeapPriorityQueue<
		Integer>>
{
	private static final int NUM_PAIRS = 10, NUM_TRIALS = 10000;
	private static final int TEST_RUN_COUNT = 1000000;
	@SuppressWarnings("unchecked") private static final
			Class<ConcurrentHeapPriorityQueue<Integer>> queueType
			= (Class<ConcurrentHeapPriorityQueue<Integer>>)
			new ConcurrentHeapPriorityQueue<Integer>().getClass();
	
	
	/** Version: 2.0 */
    public ConcurrentHeapPriorityQueueMTCorrectnessTester() {
    	super(queueType, NUM_PAIRS, NUM_TRIALS);
    }
    
    
    /**
     * JUnit test that executes the superclass's {@code testCorrectness()} test
     * method {@code TEST_RUN_COUNT} times.
     */
    @Test
    public void testCorrectnessManyTimes() {
    	for (int i = 0; i < TEST_RUN_COUNT; i++)
    		testCorrectness();
    }

}
