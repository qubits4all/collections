/*
 * Last Modified: 12/28/09
 * Prev. Modified: 2/12/09
 * J2SE Version: 5.0
 * 
 * Version Notes: 
 */

package info.willdspann.collections.concurrent.queue;

import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import info.willdspann.collections.queue.PriorityQueueCorrectnessTester;

/**
 *
 *
 * @author Will D. Spann
 * @version 1.1
 */
@RunWith(JUnit4.class)
public class ConcurrentHeapPriorityQueueSTCorrectnessTest
		extends PriorityQueueCorrectnessTester<ConcurrentHeapPriorityQueue<
		String>>
{
	@SuppressWarnings("unchecked") private static final
			Class<ConcurrentHeapPriorityQueue<String>> queueType
			= (Class<ConcurrentHeapPriorityQueue<String>>)
			new ConcurrentHeapPriorityQueue<String>().getClass();
	

	/** Version: 1.1 */
    public ConcurrentHeapPriorityQueueSTCorrectnessTest() {
    	super(queueType);
    }

}
