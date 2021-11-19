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
