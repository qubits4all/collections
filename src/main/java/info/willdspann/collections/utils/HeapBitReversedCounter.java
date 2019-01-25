/*
 * Last Modified: 1/11/09
 * Prev. Modified: 
 * J2SE Version: 5.0
 * 
 * Version Notes: 
 */


package info.willdspann.collections.utils;

import info.willdspann.utilities.BitReversalUtil;


/**
 * A heap bit-reversed counter. This class is a special type of bit-reversed
 * counter, designed with array-stored heaps in mind. If such a heap's next
 * insert/delete leaf node position is indicated by one of these counters,
 * consecutive inserts/deletes will take place on independent paths to/from
 * the root node.
 *
 * @author Will D. Spann
 * @version 1.0
 */
public class HeapBitReversedCounter {
	/** Maximum counter value, equal to {@code Integer.MAX_VALUE}, which is
	 *  {@code 2^31 - 1} or {@value MAX_VALUE}. */
	public static final int MAX_VALUE = Integer.MAX_VALUE;  // 2^31 - 1
	
	protected int count, nonrevCount;
	/** Reversed count within the current row of the heap. */
	protected int rowCount;
	/** Non-reversed count within the current row of the heap. */
	protected int nonrevRowCount;
	/** Size of the current row of the heap. */
	protected int rowSz;
	/** Current level in the heap (where 0 is the root level). This is also
	 *  the virtual bit-width used when incrementing or decrementing the
	 *  bit-reversed {@code rowCount} counter. */
	protected int bitWidth;
	
	
	/**
	 * Creates a new counter with an initial value of 0.
	 */
	protected HeapBitReversedCounter() {
		this.count = 0;
		this.nonrevCount = 0;
		this.rowCount = 0;
		this.nonrevRowCount = 0;
		this.rowSz = 1;
		this.bitWidth = 0;
	}
	
	
	/**
	 * Creates a new counter with an initial value of {@code start}, which
	 * is the starting bit-reversed or non-bit-reversed count, depending on
	 * the value of the {@code isReversed} boolean parameter.
	 * 
	 * @param start the counter's starting count, which is the bit-reversed
	 *    or non-bit-reversed count, depending on the value of
	 *    {@code isReversed}. 
	 * @param isReversed whether the specified initial value {@code start}
	 *    is the bit-reversed or non-bit-reversed starting count.
	 */
	protected HeapBitReversedCounter(int start, boolean isReversed) {
		/* 'start' should be >= 0 && <= MAX_VALUE
		 * (Note: if 'start' > MAX_VALUE it will be negative.) */
		assert start >= 0;
		
		if (isReversed)
			privSet(start);
		else
			privSetNonrev(start);
	}
	
	
	/**
	 * Returns a new counter starting at 0.
	 * <p>
	 * JUnit Test: SUCCEEDED.
	 * 
	 * @return a new counter starting at 0.
	 */
	public static HeapBitReversedCounter newInstance() {
		return new HeapBitReversedCounter();
	}
	
	
	/**
	 * Returns a new counter starting at the specified bit-reversed count.
	 * <p>
	 * Version: 1.1
	 * <p>
	 * JUnit Tests: SUCCEEDED.
	 * 
	 * @param count the bit-reversed starting count, which must be greater
	 *    than or equal to 0.
	 * @return a new counter starting at the specified bit-reversed count;
	 *    or {@code null} if {@code count < 0}.
	 */
	public static HeapBitReversedCounter newAtReversedCount(int count)
	{
		// Return 'null' on invalid 'count'
		if (count < 0)
			return null;
		
		return new HeapBitReversedCounter(count, true);
	}
	
	
	/**
	 * Returns a new counter starting at the specified non-bit-reversed
	 * count.
	 * <p>
	 * Version: 1.1
	 * <p>
	 * JUnit Tests: SUCCEEDED.
	 * 
	 * @param nonreversedCount the non-bit-reversed starting count, which
	 *    must be greater than or equal to 0.
	 * @return a new counter starting at the specified non-bit-reversed
	 *    count; or {@code null} if {@code nonreversedCount < 0}.
	 */
	public static HeapBitReversedCounter newAtNonreversedCount(
			int nonreversedCount)
	{
		// Return 'null' on invalid 'nonreversedCount'
		if (nonreversedCount < 0)
			return null;
		
		return new HeapBitReversedCounter(nonreversedCount, false);
	}
	
	
	/**
	 * Increments the counter, then returns its pre-increment value.
	 * <p>
	 * Version: 1.2
	 * <p>
	 * JUnit Test: SUCCEEDED.
	 * 
	 * @return the count, prior to incrementing the counter.
	 * 
	 * @throws IllegalStateException if incrementing past the maximum value
	 *    {@code MAX_VALUE} is attempted.
	 */
	public int getAndIncrement() {
		int ret = this.count;
		increment();
		return ret;
	}
	
	
	/**
	 * Increments the counter, then returns the new count.
	 * <p>
	 * Version: 1.2
	 * <p>
	 * JUnit Tests: SUCCEEDED.
	 * 
	 * @return the new count, after incrementing the counter.
	 * 
	 * @throws IllegalStateException if incrementing past the maximum value
	 *    {@code MAX_VALUE} is attempted.
	 */
	public int incrementAndGet() {
		increment();
		return this.count;
	}
	
	
	/**
	 * Decrements the counter, then returns its pre-decrement value. If the
	 * counter is equal to zero when this method is called, its value
	 * remains unchanged.
	 * <p>
	 * JUnit Test: SUCCEEDED.
	 * 
	 * @return the count, prior to decrementing the counter.
	 */
	public int getAndDecrement() {
		int ret = this.count;
		decrement();
		return ret;
	}
	
	
	/**
	 * Decrements the counter, then returns the new count. If the counter is
	 * equal to zero when this method is called, its value remains
	 * unchanged.
	 * <p>
	 * JUnit Tests: SUCCEEDED.
	 * 
	 * @return the new count, after decrementing the counter.
	 */
	public int decrementAndGet() {
		decrement();
		return this.count;
	}
	
	
	/**
	 * Sets the counter to the specified bit-reversed value.
	 * <p>
	 * JUnit Tests: SUCCEEDED.
	 * 
	 * @param count the bit-reversed value to set the counter to, which must
	 *    be greater than or equal to 0.
	 *    
	 * @throws IllegalArgumentException if {@code count < 0}.
	 */
	public void set(int count) {
		if (count < 0) {
			throw new IllegalArgumentException("Value of 'count' must be "
					+ ">= 0.");
		}
		
		privSet(count);
	}
	
	
	/**
	 * Sets the counter to the specified non-bit-reversed value.
	 * <p>
	 * JUnit Tests: SUCCEEDED.
	 * 
	 * @param nonreversedCount the non-bit-reversed value to set the counter
	 *    to, which must be greater than or equal to 0.
	 *    
	 * @throws IllegalArgumentException if {@code nonreversedCount < 0}.
	 */
	public void setNonreversed(int nonreversedCount) {
		if (nonreversedCount < 0) {
			throw new IllegalArgumentException("Value of 'nonreversedCount'"
					+ " must be >= 0");
		}
		
		privSetNonrev(nonreversedCount);
	}
	
	
	/**
	 * Resets this counter to its initial value of 0.
	 * <p>
	 * JUnit Test: SUCCEEDED.
	 */
	public void reset() {
		this.count = 0;
		this.nonrevCount = 0;
		this.rowCount = 0;
		this.nonrevRowCount = 0;
		this.rowSz = 1;
		this.bitWidth = 0;
	}
	
	
	/**
	 * Returns the counter's current value, which is a heap bit-reversed
	 * count.
	 * <p>
	 * JUnit Test: SUCCEEDED.
	 * 
	 * @return the counter's current value, which is a heap bit-reversed
	 *    count.
	 */
	public int get() {
		return this.count;
	}
	
	
	/**
	 * Returns the counter's current non-bit-reversed value.
	 * <p>
	 * JUnit Test: SUCCEEDED.
	 * 
	 * @return the counter's current non-bit-reversed value.
	 */
	public int getNonreversedCount() {
		return this.nonrevCount;
	}
	
	
	/**
	 * Increments the heap bit-reversed counter. 
	 * <p>
	 * Version: 1.2
	 * 
	 * @throws IllegalStateException if incrementing past the maximum value
	 *    {@code MAX_VALUE} is attempted.
	 */
	protected void increment() {
		// Disallow incrementing past the max. value MAX_VALUE:
		if (this.nonrevCount == MAX_VALUE) {
			throw new IllegalStateException("Attempt to increment past "
					+ "maximum counter value MAX_VALUE: 2^" + (Integer.SIZE - 1)
					+ " - 1 or " + MAX_VALUE);
		}
		
		if (this.nonrevRowCount == this.rowSz - 1) {
			this.rowCount = 0;
			this.nonrevRowCount = 0;
			this.bitWidth++;
			this.rowSz <<= 1; // this.rowSz *= 2
		} else {
			if (this.bitWidth >= 1) {
				this.rowCount = BitReversalUtil.bitReversedIncrement(
						this.rowCount, this.bitWidth);
			} else {
				this.rowCount++;
			}
			this.nonrevRowCount++;
		}
		this.nonrevCount++;
		this.count = this.rowCount + this.rowSz - 1;
	}
	
	
	/**
	 * Decrements the heap bit-reversed counter. If the counter is equal to
	 * zero when this method is called, its value remains unchanged.
	 * <p>
	 * Version: 1.2
	 * 
	 * @throws IllegalStateException if decrementing below 0 is attempted
	 *    (i.e., if this counter's bit-reversed count is equal to 0 when
	 *    this method is called).
	 */
	protected void decrement() {
		// Never decrement below zero
		if (this.nonrevCount == 0) {
			throw new IllegalStateException("Attempt to decrement below 0.");
		}
		
		if (this.nonrevRowCount == 0) {
			this.bitWidth--;
			this.rowSz >>>= 1; // this.rowSz /= 2
			this.nonrevRowCount = this.rowSz - 1;
			if (this.bitWidth >= 1) {
				this.rowCount = BitReversalUtil.reverseBits(
						this.nonrevRowCount, this.bitWidth);
			}
		} else {
			if (this.bitWidth >= 1) {
				this.rowCount = BitReversalUtil.bitReversedDecrement(
						this.rowCount, this.bitWidth);
			}
			this.nonrevRowCount--;
		}
		if (this.bitWidth < 1)
			this.rowCount = this.nonrevRowCount;
		this.nonrevCount--;
		this.count = this.rowCount + this.rowSz - 1;
	}
	
	
	/**
	 * Sets the counter to the specified bit-reversed value.
	 * <p>
	 * Version: 1.0.1
	 * 
	 * @param count the bit-reversed value to set the counter to, which must
	 *    be greater than or equal to 0. 
	 */
	private void privSet(int count) {
		/* 'count' should be >= 0 && <= MAX_VALUE
		 * (Note: if 'count' > MAX_VALUE it will be negative.) */
		assert count >= 0;
		
		this.count = count;
		this.bitWidth = Integer.SIZE - Integer.numberOfLeadingZeros(
				this.count + 1) - 1;
		this.rowSz = 1 << this.bitWidth;
		this.rowCount = this.count - this.rowSz + 1;
		this.nonrevRowCount = BitReversalUtil.reverseBits(this.rowCount,
				this.bitWidth);
		this.nonrevCount = this.nonrevRowCount + this.rowSz - 1;
	}
	
	
	/**
	 * Sets the counter to the specified non-bit-reversed value.
	 * 
	 * @param nonrevCount the non-bit-reversed value to set the counter
	 *    to, which must be greater than or equal to 0.
	 */
	private void privSetNonrev(int nonrevCount) {
		/* 'nonrevCount' should be >= 0 && <= MAX_VALUE
		 * (Note: if 'nonrevCount' > MAX_VALUE it will be negative.) */
		assert nonrevCount >= 0;
		
		this.nonrevCount = nonrevCount;
		this.bitWidth = Integer.SIZE - Integer.numberOfLeadingZeros(
				this.nonrevCount + 1) - 1;
		this.rowSz = 1 << this.bitWidth;
		this.nonrevRowCount = this.nonrevCount - this.rowSz + 1;
		this.rowCount = BitReversalUtil.reverseBits(this.nonrevRowCount,
				this.bitWidth);
		this.count = this.rowCount + this.rowSz - 1;
	}

}