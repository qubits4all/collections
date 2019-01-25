/*
 * Last Modified: 12/29/09
 * Java SE 6
 */

package info.willdspann.collections.concurrent.queue;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import info.willdspann.collections.queue.PriorityQueueCorrectnessTester2.PriorityQueueReqdCorrectnessTester;
import info.willdspann.collections.queue.PriorityQueueCorrectnessTester2.RequiredQueueTests;

/**
 *
 *
 * @author <A HREF="mailto:willdspann@yahoo.com">Will D. Spann</A>
 * @version 1.0
 */
public class CHPQRequiredSTCorrectnessTester
		extends PriorityQueueReqdCorrectnessTester<ConcurrentHeapPriorityQueue<
		String>>
{
	@SuppressWarnings("unchecked") private static final
			Class<ConcurrentHeapPriorityQueue<String>> queueType
			= (Class<ConcurrentHeapPriorityQueue<String>>)
			new ConcurrentHeapPriorityQueue<String>().getClass();
	
	
    public CHPQRequiredSTCorrectnessTester() {
    	super(queueType);
    }
    
    @Category(RequiredQueueTests.class)
    @Test
    public void test() {
    	super.testAddOnEmpty();
    }

}
