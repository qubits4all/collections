package info.willdspann.utilities;

/**
 * A counter that counts in bit-reversed order.
 *
 * @author Will D. Spann
 * @version 2.4
 * @see BitReversalUtil BitReversalUtil
 */
public class BitReversedLongCounter {

	/** Maximum counter value, equal to {@code Long.MAX_VALUE}, which is
	 *  {@code 2^63 - 1}. */
	public static final long MAX_VALUE = Long.MAX_VALUE;  // 2^63 - 1

	/** The virtual bit-width used by this counter. In this counter's
	 *  reversed and non-reversed count values, any high bits left of the
	 *  bit index given by this constant minus one, are guaranteed to
	 *  always be 0. This constant and the maximum signed value of a long
	 *  primitive determine the counter's maximum value {@code MAX_VALUE}.*/
	public static final int SIG_BITS = 63;

	/** This counter's non-reversed count. */
	protected long count;
	/** This counter's reversed count. */
	protected long revCount;
	
	
	/**
	 * No-argument constructor, which creates a new counter starting at 0.
	 * <p>
	 * Version: 1.1
	 */
	protected BitReversedLongCounter() {
		this.count = 0;
		this.revCount = 0;
    }

	/**
	 * Constructor that creates a new counter starting at the specified
	 * {@code start} value, which represents the counter's reversed or
	 * non-reversed count, depending on the value of the {@code isReversed}
	 * parameter.
	 * <p>
	 * Version: 1.2
	 * 
	 * @param start the counter's starting value, which represents the
	 *    counter's reversed or non-reversed count, depending on the value
	 *    of the {@code isReversed} parameter. This value must not be
	 *    negative.
	 * @param isReversed whether the {@code start} parameter represents the
	 *    counter's starting reversed or non-reversed count.
	 */
	protected BitReversedLongCounter(long start, boolean isReversed) {
		assert start >= 0L;
		
		long revStart = BitReversalUtil.reverseBits(start, SIG_BITS);
		
		if (isReversed) {
			this.revCount = start;
			this.count = revStart;
		} else {
			this.count = start;
			this.revCount = revStart;
		}
	}
	
	
	/**
	 * Factory method for creating a new counter starting at 0.
	 */
	public static BitReversedLongCounter newInstance() {
		return new BitReversedLongCounter();
	}
	
	/**
	 * Factory method that creates a new counter starting at the specified
	 * bit-reversed count.
	 * <p>
	 * Version: 2.0
	 * 
	 * @param start the bit-reversed starting count to be used, which must
	 *    be nonnegative.
	 * @return a new counter starting at the specified bit-reversed count;
	 *    or {@code null} if {@code start} is negative.
	 */
	public static BitReversedLongCounter newAtReversedCount(long start) {
		if (start < 0L)
			return null;
		
		return new BitReversedLongCounter(start, true);
	}

	/**
	 * Factory method that creates a new counter starting at the specified
	 * non-bit-reversed count.
	 * <p>
	 * Version: 2.0
	 * 
	 * @param start the non-bit-reversed starting count to be used, which
	 *    must be nonnegative.
	 * @return a new counter starting at the specified non-bit-reversed
	 *    count; or {@code null} if {@code start} is negative.
	 */
	public static BitReversedLongCounter newAtNonreversedCount(long start) {
		if (start < 0L)
			return null;
		
		return new BitReversedLongCounter(start, false);
	}
	
	/**
	 * Performs a bit-reversed increment and returns the bit-reversed count
	 * from before the increment.
	 * <p>
	 * Version: 1.1
	 *
	 * @return the bit-reversed count from before the increment.
	 *
	 * @throws IllegalStateException if incrementing beyond the maximum
	 *    value {@code MAX_VALUE} is attempted (i.e., if this counter's
	 *    non-bit-reversed count is equal to {@code MAX_VALUE} when this
	 *    method is called).
	 */
	public long getAndIncrement() {
		long ret = this.revCount;
		increment();
		return ret;
	}
	
	/**
	 * Performs a bit-reversed increment and returns the new bit-reversed
	 * count.
	 * <p>
	 * Version: 1.1
	 *
	 * @return the bit-reversed count obtained by performing a
	 *    bit-reversed increment.
	 *
	 * @throws IllegalStateException if incrementing beyond the maximum
	 *    value {@code MAX_VALUE} is attempted (i.e., if this counter's
	 *    non-bit-reversed count is equal to {@code MAX_VALUE} when this
	 *    method is called).
	 */
	public long incrementAndGet() {
		increment();
		return this.revCount;
	}

	/**
	 * Performs a bit-reversed decrement and returns the bit-reversed count
	 * from before the decrement.
	 * <p>
	 * Version: 1.1
	 *
	 * @return the bit-reversed count from before the decrement.
	 *
	 * @throws IllegalStateException if decrementing below 0 is attempted
	 *    (i.e., if this counter's bit-reversed count is equal to 0 when
	 *    this method is called).
	 */
	public long getAndDecrement() {
		long ret = this.revCount;
		decrement();
		return ret;
	}

	/**
	 * Performs a bit-reversed decrement and returns the new bit-reversed
	 * count.
	 * <p>
	 * Version: 1.1
	 *
	 * @return the bit-reversed count obtained by performing a
	 *    bit-reversed decrement.
	 *
	 * @throws IllegalStateException if decrementing below 0 is attempted
	 *    (i.e., if this counter's bit-reversed count is equal to 0 when
	 *    this method is called).
	 */
	public long decrementAndGet() {
		decrement();
		return this.revCount;
	}
	
	/**
	 * Sets the counter's bit-reversed count to the specified value, which
	 * must be nonnegative.
	 * 
	 * @param reversedValue the bit-reversed value to set the counter's
	 *    bit-reversed count to, which must be nonnegative.
	 * 
	 * @throws IllegalArgumentException if {@code reversedValue < 0}.
	 */
	public void set(long reversedValue) {
		if (reversedValue < 0L) {
			throw new IllegalArgumentException("Value of 'reversedValue' "
					+ "must be >= 0.");
		}
		
		this.revCount = reversedValue;
		this.count = BitReversalUtil.reverseBits(this.revCount, SIG_BITS);
	}

	/**
	 * Sets the counter's non-bit-reversed count to the specified value,
	 * which must be nonnegative.
	 * 
	 * @param value the non-bit-reversed value to set the counter's
	 *    non-bit-reversed count to, which must be nonnegative.
	 *    
	 * @throws IllegalArgumentException if {@code value < 0}.
	 */
	public void setNonreversed(long value) {
		if (value < 0L) {
			throw new IllegalArgumentException("Value of 'value' must be "
					+ ">= 0.");
		}
		
		this.count = value;
		this.revCount = BitReversalUtil.reverseBits(this.count, SIG_BITS);
	}

	/**
	 * Resets this counter to 0.
	 */
	public void reset() {
		this.count = 0L;
		this.revCount = 0L;
	}

	/**
	 * Returns the current bit-reversed count.
	 * <p>
	 * Version: 1.1
	 * 
	 * @return the current bit-reversed count.
	 */
	public long get() {
		return this.revCount;
	}

	/**
	 * Return the current non-bit-reversed count.
	 * <p>
	 * Version: 1.1
	 */
	public long getNonreversedCount() {
		return this.count;
	}


	/**
	 * Performs a bit-reversed increment.
	 * <p>
	 * Version: 3.1
	 * 
	 * @throws IllegalStateException if incrementing beyond the maximum
	 *    value {@code MAX_VALUE} is attempted (i.e., if this counter's
	 *    non-bit-reversed count is equal to {@code MAX_VALUE} when this
	 *    method is called).
	 */
	protected void increment() {
		if (this.count == MAX_VALUE) {
			throw new IllegalStateException("Attempt to increment beyond "
					+ "the max. value: " + MAX_VALUE);
		}
		
		/* Perform bit-reversed increment of 'revCount', using the max.
		 * counter bit-width of Integer.SIZE - 1. */
		this.revCount = BitReversalUtil.bitReversedIncrement(this.revCount);
		
		// Increment non-bit-reversed counter
		this.count++;
	}
	
	/**
	 * Performs a bit-reversed decrement.
	 * <p>
	 * Version: 3.1
	 * 
	 * @throws IllegalStateException if decrementing below 0 is attempted
	 *    (i.e., if this counter's bit-reversed count is equal to 0 when
	 *    this method is called).
	 */
	protected void decrement() {
		if (this.revCount == 0L) {
			throw new IllegalStateException("Attempt to decrement below "
					+ "0.");
		}
		
		/* Perform bit-reversed decrement of 'revCount', using the max.
		 * counter bit-width of Integer.SIZE - 1. */
		this.revCount = BitReversalUtil.bitReversedDecrement(this.revCount);
		
		// Decrement non-bit-reversed counter
		this.count--;
	}
}
