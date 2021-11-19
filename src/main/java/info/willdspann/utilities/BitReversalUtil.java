package info.willdspann.utilities;

/**
 * <p>
 * This utility class provides a static methods for reversing the order of bits in an integer, plus support for
 * performing bit-reversed increment and decrement operations.</p>
 * <p>
 * Support for custom virtual "bit-widths" is also provided, allowing for bit-reversed increment of a 4-bit wide
 * bit-reversed counter, for example.
 *
 * @author Will D. Spann
 * @version 2.1
 */
public final class BitReversalUtil {
	private static final int INT_WIDTH = 32;   // Width of an int in bits
	private static final int LONG_WIDTH = 64;  // Width of a long in bits

	/**
	 * Ensure uninstantiability.
	 */
	private BitReversalUtil() { }

	/**
	 * <p>
	 * Returns a copy of the given {@code byte} with its bits in the
	 * reverse order.</p>
	 * <p>
	 * Based on code by: <a href="mailto:Dan.Pop@ifh.de">Dan Pop</a><br>
	 * Posted on The Scripts Forum:
	 * <a href="http://www.thescripts.com/forum/thread214890.html">
	 * http://www.thescripts.com/forum/thread214890.html</a> on 11/13/05.
	 * <p>
	 * Version: 1.1.1</p>
	 * <p>
	 * Impl. Notes: The assignment statements can be executed in any order.</p>
	 * 
	 * @param b {@code byte} to have its reverse bit-order {@code byte}
	 *    calculated.
	 * @return a copy of the given {@code byte} with its bits in the
	 *    reverse order.
	 */
	public static byte reverseBits(byte b) {
		int res = (int) b;
		// Swap adjacent 4-bit fields (nibbles)
		res = (res & 0x0F) << 4 | (res & 0xF0) >>> 4;
		// Swap adjacent 2-bit fields
		res = (res & 0x33) << 2 | (res & 0xCC) >>> 2;
		// Swap adjacent bits
		res = (res & 0x55) << 1 | (res & 0xAA) >>> 1;
		
		return (byte) res;
	}
	
	/**
	 * Returns a copy of the given {@code short} with its bits in the
	 * reverse order.
	 * <p>
	 * Version: 1.1.1
	 * <p>
	 * Impl. Notes: The assignment statements can be executed in any order.
	 * 
	 * @param s {@code short} to have its reverse bit-order {@code short}
	 *    calculated.
	 * @return a copy of the given {@code short} with its bits in the
	 *    reverse order.
	 */
	public static short reverseBits(short s) {
		int res = (int) s;
		// Swap adjacent bytes
		res = (res & 0x00FF) << 8 | (res & 0xFF00) >>> 8;
		// Swap adjacent 4-bit fields (nibbles)
		res = (res & 0x0F0F) << 4 | (res & 0xF0F0) >>> 4;
		// Swap adjacent 2-bit fields
		res = (res & 0x3333) << 2 | (res & 0xCCCC) >>> 2;
		// Swap adjacent bits
		res = (res & 0x5555) << 1 | (res & 0xAAAA) >>> 1;
		
		return (short) res;
	}
	
	/**
	 * Returns a copy of the given {@code int} with its bits in the
	 * reverse order.
	 * <p>
	 * Version: 1.0
	 * 
	 * @param i an {@code int} to have its reverse bit-order {@code int}
	 *    calculated.
	 * @return a copy of the given {@code int} with its bits in the
	 *    reverse order.
	 */
	public static int reverseBits(int i) {
		// Swap adjacent half-words (16-bit fields)
		i = (i & 0x0000FFFF) << 16 | (i & 0xFFFF0000) >>> 16;
		// Swap adjacent bytes (8-bit fields)
		i = (i & 0x00FF00FF) << 8 | (i & 0xFF00FF00) >>> 8;
		// Swap adjacent 4-bit fields (nibbles)
		i = (i & 0x0F0F0F0F) << 4 | (i & 0xF0F0F0F0) >>> 4;
		// Swap adjacent 2-bit fields
		i = (i & 0x33333333) << 2 | (i & 0xCCCCCCCC) >>> 2;
		// Swap adjacent bits
		i = (i & 0x55555555) << 1 | (i & 0xAAAAAAAA) >>> 1;
		
		return i;
	}

	/**
	 * Reverses the bits of the given integer, only applying the reversal to
	 * its first (least significant) {@code bitWidth} bits. The
	 * {@code bitWidth} parameter also determines the maximum integer that
	 * will be returned, which is {@code 2^bitWidth - 1}.
	 * <p>
	 * For example, {@code reverseBits(i, 4)} will only return integers in
	 * the range [0,15], since {@code 2^4 - 1 = 15}.
	 * <p>
	 * Version: 2.1.1
	 * 
	 * @param i integer to be reversed, which must be less than
	 *    {@code 2^bitWidth}, if {@code bitWidth < 32}. 
	 * @param bitWidth number of bits of {@code i} to use in calculating its
	 *    reverse. Must be between 1 and 32, inclusive.
	 * @return the bit reversal of {@code i}, with the reversal only applied
	 *    to its first (least significant) {@code bitWidth} bits.
	 *    
	 * @throws IllegalArgumentException if {@code bitWidth < 1} or
	 *    {@code bitWidth > 32}; or if {@code i >= 2^bitWidth} and
	 *    {@code bitWidth < 32}.
	 */
	public static int reverseBits(int i, int bitWidth) {
		/* Verify validity of arguments: */
		// Verify valid 'bitWidth':
		if (bitWidth < 1) {
			throw new IllegalArgumentException("bitWidth must be > 0");
		}
		else if (bitWidth > INT_WIDTH) {
			throw new IllegalArgumentException("bitWidth must be <= "
					+ INT_WIDTH);
		}
		
		/* Verify valid 'i': */
		// If bitWidth < INT_WIDTH && 'i' >= 2^bitWidth
		if (bitWidth < INT_WIDTH
				&& UnsignedIntegerUtil.unsignedIntCompare(
					i, 1 << bitWidth) >= 0)
		{
			throw new IllegalArgumentException("i must be < 2^bitWidth, if "
					+ "bitWidth < " + INT_WIDTH);
		}
		
		/* If it's worth doing (bitWidth > 1), do bit-shifted reversal. The
		 * case bitWidth==INT_WIDTH results in the 2nd step being >>>0,
		 * which is a no-op. */
		if (bitWidth > 1) {
			return reverseBits(i) >>> (INT_WIDTH - bitWidth);
		}
		/* Else, simply return 'i' unchanged. This is the same result, since
		 * 'i' passed its argument validity test, which guarantees that
		 * 0 <= i < 2. */
		else {  // bitWidth == 1
			return i;
		}
	}

	/**
	 * Returns the bit-reversed increment of the specified bit-reversed
	 * value, using the maximum virtual bit-width for a counter, which is
	 * {@code Integer.SIZE - 1} or 31.
	 * <p>
	 * This method uses an adaptation of an algorithm specified in
	 * <em>Hacker's Delight</em> by Henry S. Warren, Jr. (Sec. 7.1, Last
	 * algorithm in section), adding support for the virtual bit-width.
	 * 
	 * @param revCount a bit-reversed value to be bit-reverse incremented,
	 *    which must be greater than or equal to zero and less than
	 *    {@code Integer.MAX_VALUE}.
	 * @return the bit-reversed increment of the specified bit-reversed
	 *    value, using the maximum virtual bit-width for a counter.
	 *    
	 * @throws IllegalArgumentException if {@code revCount < 0} or
	 *    {@code revCount == Integer.MAX_VALUE}.
	 */
	public static int bitReversedIncrement(int revCount) {
		/* Verify valid 'revCount':
		 * (Note: if 'revCount' > Integer.MAX_VALUE, it will be negative.)*/
		if (revCount < 0) {
			throw new IllegalArgumentException("revCount must be >= 0.");
		}
		else if (revCount == Integer.MAX_VALUE) {
			throw new IllegalArgumentException("revCount must be < "
					+ "Integer.MAX_VALUE or 2^31 - 1.");
		}
		
		return bitReversedIncrement(revCount, Integer.SIZE - 1);
	}

	/**
	 * Returns the bit-reversed increment of the specified bit-reversed
	 * value, using the given virtual bit-width.
	 * <p>
	 * This method uses an adaptation of an algorithm specified in
	 * <em>Hacker's Delight</em> by Henry S. Warren, Jr. (Sec. 7.1, Last
	 * algorithm in section), adding support for the virtual bit-width.
	 * <p>
	 * Version: 1.0.1
	 * 
	 * @param revCount a bit-reversed value to be bit-reverse incremented,
	 *    which must be greater than or equal to zero and less than
	 *    {@code 2^bitWidth - 1}.
	 * @param bitWidth the virtual bit-width to be used, which must be
	 *    greater than zero and less than {@code INT_WIDTH}.
	 * @return the bit-reversed increment of the specified bit-reversed
	 *    value, using the given virtual bit-width.
	 *    
	 * @throws IllegalArgumentException if {@code bitWidth < 1} or
	 *    {@code bitWidth > 31}; or if {@code revCount < 0} or
	 *    {@code revCount >= 2^bitWidth - 1}.
	 */
	public static int bitReversedIncrement(int revCount, int bitWidth) {
		/* Verify validity of arguments: */
		// Verify valid 'bitWidth':
		if (bitWidth < 1) {
			throw new IllegalArgumentException("bitWidth must be > 0");
		}
		else if (bitWidth >= INT_WIDTH) {
			throw new IllegalArgumentException("bitWidth must be < "
					+ INT_WIDTH);
		}
		/* Verify valid 'revCount': */
		if (revCount < 0) {
			throw new IllegalArgumentException("revCount must be >= 0.");
		}
		// If 'revCount' >= 2^bitWidth - 1
		if (UnsignedIntegerUtil.unsignedLongCompare(revCount,
				(1L << bitWidth) - 1) >= 0)
		{
			throw new IllegalArgumentException("revCount must be < "
					+ "2^bitWidth - 1.");
		}
		
		// Take shortcut if bitWidth == 1
		if (bitWidth == 1)
			return ++revCount;
		
		/* Convert bit-reversed 'revCount' from 'bitWidth'-bit to 32-bit
		 * width. */
		int curAs32bit = revCount << (INT_WIDTH - bitWidth);
		
		// Do bit-reversed (32-bit width) increment of 'curAs32bit':
		int shift = Integer.numberOfLeadingZeros(~curAs32bit);
		int updateAs32bit = curAs32bit ^ (0x80000000 >> shift);
		
		// Convert result back to 'bitWidth'-bit width
		return updateAs32bit >>> (INT_WIDTH - bitWidth);
	}
	
	/**
	 * Returns the bit-reversed decrement of the specified bit-reversed
	 * value, using the maximum virtual bit-width for a counter, which is
	 * {@code Integer.SIZE - 1} or 31.
	 * <p>
	 * This method uses an adaptation of an algorithm specified in
	 * <em>Hacker's Delight</em> by Henry S. Warren, Jr. (Sec. 7.1, Last
	 * algorithm in section), adding support for the virtual bit-width.
	 * 
	 * @param revCount a bit-reversed value to be bit-reverse decremented,
	 *    which must be greater than zero.
	 * @return the bit-reversed decrement of the specified bit-reversed
	 *    value, using the maximum virtual bit-width for a counter.
	 *    
	 * @throws IllegalArgumentException if {@code revCount <= 0}.
	 */
	public static int bitReversedDecrement(int revCount) {
		/* Verify valid 'revCount'
		 * (Note: If 'revCount' > Integer.MAX_VALUE, it will be negative.)*/
		if (revCount <= 0) {
			throw new IllegalArgumentException("revCount must be > 0. "
					+ "(Decrementing below 0 is not supported.)");
		}
		
		return bitReversedDecrement(revCount, Integer.SIZE - 1);
	}
	
	/**
	 * Returns the bit-reversed decrement of the specified bit-reversed
	 * value, using the given virtual bit-width.
	 * <p>
	 * This method uses an algorithm derived from the bit-reversed
	 * increment algorithm, specified in <em>Hacker's Delight</em> by Henry
	 * S. Warren, Jr. (Sec. 7.1, Last algorithm in section), which has been
	 * adapted to support for the virtual bit-width.
	 * <p>
	 * Version: 1.0.1
	 * 
	 * @param revCount a bit-reversed value to be bit-reverse decremented,
	 *    which must be greater than zero and less than or equal to
	 *    {@code 2^bitWidth - 1}.
	 * @param bitWidth the virtual bit-width to be used, which must be
	 *    greater than zero and less than {@code INT_WIDTH}.
	 * @return the bit-reversed decrement of the specified bit-reversed
	 *    value, using the given virtual bit-width.
	 *    
	 * @throws IllegalArgumentException if {@code bitWidth < 1} or
	 *    {@code bitWidth > 31}; or if {@code revCount <= 0} or
	 *    {@code revCount > 2^bitWidth - 1}.
	 */
	public static int bitReversedDecrement(int revCount, int bitWidth) {
		/* Verify validity of arguments: */
		// Verify valid 'bitWidth':
		if (bitWidth < 1) {
			throw new IllegalArgumentException("bitWidth must be > 0");
		}
		else if (bitWidth > INT_WIDTH) {
			throw new IllegalArgumentException("bitWidth must be <= "
					+ INT_WIDTH);
		}
		// Verify valid 'revCount':
		if (revCount <= 0) {
			throw new IllegalArgumentException("revCount must be > 0. "
					+ "(Decrementing below 0 is not supported.)");
		}
		// If 'revCount' > 2^bitWidth - 1
		else if (UnsignedIntegerUtil.unsignedLongCompare(revCount,
				(1L << bitWidth) - 1) > 0)
		{
			throw new IllegalArgumentException("revCount must be <= "
					+ "2^bitWidth - 1.");
		}
		
		// Take shortcut if bitWidth == 1
		if (bitWidth == 1)
			return --revCount;
		
		/* Convert bit-reversed 'revCount' from 'bitWidth'-bit to 32-bit
		 * width. */
		int curAs32bit = revCount << (INT_WIDTH - bitWidth);
		
		// Do bit-reversed (32-bit width) increment of 'curAs32bit':
		int shift = Integer.numberOfLeadingZeros(curAs32bit);
		int updateAs32bit = curAs32bit ^ (0x80000000 >> shift);
		
		// Convert result back to 'bitWidth'-bit width
		return updateAs32bit >>> (INT_WIDTH - bitWidth);
	}
	
	/**
	 * Returns a copy of the given {@code long} with its bits in the
	 * reverse order.
	 * <p>
	 * Version: 1.0
	 * 
	 * @param n a {@code long} to have its reverse bit-order {@code long}
	 *    calculated.
	 * @return a copy of the given {@code long} with its bits in the
	 *    reverse order.
	 */
	public static long reverseBits(long n) {
		// Swap adjacent words (32-bit fields)
		n = (n & 0x00000000FFFFFFFFL) << 32 | (n & 0xFFFFFFFF00000000L) >>> 32;
		// Swap adjacent half-words (16-bit fields)
		n = (n & 0x0000FFFF0000FFFFL) << 16 | (n & 0xFFFF0000FFFF0000L) >>> 16;
		// Swap adjacent bytes (8-bit fields)
		n = (n & 0x00FF00FF00FF00FFL) << 8 | (n & 0xFF00FF00FF00FF00L) >>> 8;
		// Swap adjacent 4-bit fields (nibbles)
		n = (n & 0x0F0F0F0F0F0F0F0FL) << 4 | (n & 0xF0F0F0F0F0F0F0F0L) >>> 4;
		// Swap adjacent 2-bit fields
		n = (n & 0x3333333333333333L) << 2 | (n & 0xCCCCCCCCCCCCCCCCL) >>> 2;
		// Swap adjacent bits
		n = (n & 0x5555555555555555L) << 1 | (n & 0xAAAAAAAAAAAAAAAAL) >>> 1;
		
		return n;
	}
	
	/**
	 * Reverses the bits of the given long integer, only applying the
	 * reversal to its first (least significant) {@code bitWidth} bits. The
	 * {@code bitWidth} parameter also determines the maximum long integer
	 * that will be returned, which is {@code 2^bitWidth - 1}.
	 * <p>
	 * For example, {@code reverseBits(n, 4)} will only return long integers
	 * in the range [0,15], since {@code 2^4 - 1 = 15}.
	 * <p>
	 * Version: 2.1.2
	 * 
	 * @param n long integer to be reversed, which must be less than
	 *    {@code 2^bitWidth}, if {@code bitWidth < 64}. 
	 * @param bitWidth number of bits of {@code n} to use in calculating its
	 *    reverse. Must be between 1 and 64, inclusive.
	 * @return the bit reversal of {@code n}, with the reversal only applied
	 *    to its first (least significant) {@code bitWidth} bits.
	 *    
	 * @throws IllegalArgumentException if {@code bitWidth < 1} or
	 *    {@code bitWidth > 64}; or if {@code n >= 2^bitWidth} and
	 *    {@code bitWidth < 64}.
	 */
	public static long reverseBits(long n, int bitWidth) {
		/* Verify validity of arguments: */
		// Verify valid 'bitWidth':
		if (bitWidth < 1) {
			throw new IllegalArgumentException("bitWidth must be > 0");
		}
		else if (bitWidth > LONG_WIDTH) {
			throw new IllegalArgumentException("bitWidth must be <= "
					+ LONG_WIDTH);
		}
		
		/* Verify valid 'n': */
		if (bitWidth < LONG_WIDTH - 1
				&& UnsignedIntegerUtil.unsignedLongCompare(
						n, 1L << bitWidth) >= 0)
		{
			throw new IllegalArgumentException("n must be < 2^bitWidth "
					+ "if bitWidth < " + (LONG_WIDTH - 1) + ", or "
					+ "be nonnegative if bitWidth == " + (LONG_WIDTH - 1));
		}
		
		/* If it's worth doing (bitWidth > 1), do bit-shifted reversal. The
		 * case bitWidth==LONG_WIDTH results in the 2nd step being >>>0,
		 * which is a no-op. */
		if (bitWidth > 1) {
			return reverseBits(n) >>> (LONG_WIDTH - bitWidth);
		}
		/* Else, simply return 'n' unchanged. This is the same result, since
		 * 'n' passed its argument validity test, which guarantees that
		 * 'n' is 0 or 1. */
		else {  // bitWidth == 1
			return n;
		}
	}
	
	/**
	 * Returns the bit-reversed increment of the specified bit-reversed
	 * value, using the maximum virtual bit-width for a counter, which is
	 * {@code Long.SIZE - 1} or 63.
	 * <p>
	 * This method uses an adaptation of an algorithm specified in
	 * <em>Hacker's Delight</em> by Henry S. Warren, Jr. (Sec. 7.1, Last
	 * algorithm in section), adding support for the virtual bit-width.
	 * 
	 * @param revCount a bit-reversed value to be bit-reverse incremented,
	 *    which must be greater than or equal to zero and less than
	 *    {@code Long.MAX_VALUE}.
	 * @return the bit-reversed increment of the specified bit-reversed
	 *    value, using the maximum virtual bit-width for a counter.
	 *    
	 * @throws IllegalArgumentException if {@code revCount < 0} or
	 *    {@code revCount == Long.MAX_VALUE}.
	 */
	public static long bitReversedIncrement(long revCount) {
		/* Verify valid 'revCount':
		 * (Note: if 'revCount' > Long.MAX_VALUE, it will be negative.)*/
		if (revCount < 0L) {
			throw new IllegalArgumentException("revCount must be >= 0.");
		}
		else if (revCount == Long.MAX_VALUE) {
			throw new IllegalArgumentException("revCount must be < "
					+ "Long.MAX_VALUE or 2^63 - 1.");
		}
		
		return bitReversedIncrement(revCount, Long.SIZE - 1);
	}

	/**
	 * Returns the bit-reversed increment of the specified bit-reversed
	 * value, using the given virtual bit-width.
	 * <p>
	 * This method uses an adaptation of an algorithm specified in
	 * <em>Hacker's Delight</em> by Henry S. Warren, Jr. (Sec. 7.1, Last
	 * algorithm in section), adding support for the virtual bit-width.
	 * <p>
	 * Version: 1.0.1
	 * 
	 * @param revCount a bit-reversed value to be bit-reverse incremented,
	 *    which must be greater than or equal to zero and less than
	 *    {@code 2^bitWidth - 1}.
	 * @param bitWidth the virtual bit-width to be used, which must be
	 *    greater than zero and less than {@code LONG_WIDTH}.
	 * @return the bit-reversed increment of the specified bit-reversed
	 *    value, using the given virtual bit-width.
	 *    
	 * @throws IllegalArgumentException if {@code bitWidth < 1} or
	 *    {@code bitWidth > 63}; or if {@code revCount < 0} or
	 *    {@code revCount >= 2^bitWidth - 1}.
	 */
	public static long bitReversedIncrement(long revCount, int bitWidth) {
		/* Verify validity of arguments: */
		// Verify valid 'bitWidth':
		if (bitWidth < 1) {
			throw new IllegalArgumentException("bitWidth must be > 0");
		}
		else if (bitWidth >= LONG_WIDTH) {
			throw new IllegalArgumentException("bitWidth must be < "
					+ LONG_WIDTH);
		}
		/* Verify valid 'revCount': */
		if (revCount < 0L) {
			throw new IllegalArgumentException("revCount must be >= 0.");
		}
		// If 'revCount' >= 2^bitWidth - 1
		if (UnsignedIntegerUtil.unsignedLongCompare(revCount,
				(1L << bitWidth) - 1L) >= 0)
		{
			throw new IllegalArgumentException("revCount must be < "
					+ "2^bitWidth - 1.");
		}
		
		// Take shortcut if bitWidth == 1
		if (bitWidth == 1)
			return ++revCount;
		
		/* Convert bit-reversed 'revCount' from 'bitWidth'-bit to 64-bit
		 * width. */
		long curAs64bit = revCount << (LONG_WIDTH - bitWidth);
		
		// Do bit-reversed (64-bit width) increment of 'curAs64bit':
		int shift = Long.numberOfLeadingZeros(~curAs64bit);
		long updateAs64bit = curAs64bit ^ (0x8000000000000000L >> shift);
		
		// Convert result back to 'bitWidth'-bit width
		return updateAs64bit >>> (LONG_WIDTH - bitWidth);
	}

	/**
	 * Returns the bit-reversed decrement of the specified bit-reversed
	 * value, using the maximum virtual bit-width for a counter, which is
	 * {@code Long.SIZE - 1} or 63.
	 * <p>
	 * This method uses an adaptation of an algorithm specified in
	 * <em>Hacker's Delight</em> by Henry S. Warren, Jr. (Sec. 7.1, Last
	 * algorithm in section), adding support for the virtual bit-width.
	 * 
	 * @param revCount a bit-reversed value to be bit-reverse decremented,
	 *    which must be greater than zero.
	 * @return the bit-reversed decrement of the specified bit-reversed
	 *    value, using the maximum virtual bit-width for a counter.
	 *    
	 * @throws IllegalArgumentException if {@code revCount <= 0}.
	 */
	public static long bitReversedDecrement(long revCount) {
		/* Verify valid 'revCount'
		 * (Note: If 'revCount' > Long.MAX_VALUE, it will be negative.)*/
		if (revCount <= 0L) {
			throw new IllegalArgumentException("revCount must be > 0. "
					+ "(Decrementing below 0 is not supported.)");
		}
		
		return bitReversedDecrement(revCount, Long.SIZE - 1);
	}

	/**
	 * Returns the bit-reversed decrement of the specified bit-reversed
	 * value, using the given virtual bit-width.
	 * <p>
	 * This method uses an algorithm derived from the bit-reversed
	 * increment algorithm, specified in <em>Hacker's Delight</em> by Henry
	 * S. Warren, Jr. (Sec. 7.1, Last algorithm in section), which has been
	 * adapted to support for the virtual bit-width.
	 * <p>
	 * Version: 1.0.1
	 * 
	 * @param revCount a bit-reversed value to be bit-reverse decremented,
	 *    which must be greater than zero and less than or equal to
	 *    {@code 2^bitWidth - 1}.
	 * @param bitWidth the virtual bit-width to be used, which must be
	 *    greater than zero and less than {@code LONG_WIDTH}.
	 * @return the bit-reversed decrement of the specified bit-reversed
	 *    value, using the given virtual bit-width.
	 *    
	 * @throws IllegalArgumentException if {@code bitWidth < 1} or
	 *    {@code bitWidth > 63}; or if {@code revCount <= 0} or
	 *    {@code revCount > 2^bitWidth - 1}.
	 */
	public static long bitReversedDecrement(long revCount, int bitWidth) {
		/* Verify validity of arguments: */
		// Verify valid 'bitWidth':
		if (bitWidth < 1) {
			throw new IllegalArgumentException("bitWidth must be > 0");
		}
		else if (bitWidth > LONG_WIDTH) {
			throw new IllegalArgumentException("bitWidth must be <= "
					+ LONG_WIDTH);
		}
		// Verify valid 'revCount':
		if (revCount <= 0L) {
			throw new IllegalArgumentException("revCount must be > 0. "
					+ "(Decrementing below 0 is not supported.)");
		}
		// If 'revCount' > 2^bitWidth - 1
		else if (UnsignedIntegerUtil.unsignedLongCompare(revCount,
				(1L << bitWidth) - 1L) > 0)
		{
			throw new IllegalArgumentException("revCount must be <= "
					+ "2^bitWidth - 1.");
		}
		
		// Take shortcut if bitWidth == 1
		if (bitWidth == 1)
			return --revCount;
		
		/* Convert bit-reversed 'revCount' from 'bitWidth'-bit to 64-bit
		 * width. */
		long curAs64bit = revCount << (LONG_WIDTH - bitWidth);
		
		// Do bit-reversed (64-bit width) increment of 'curAs64bit':
		int shift = Long.numberOfLeadingZeros(curAs64bit);
		long updateAs64bit = curAs64bit ^ (0x8000000000000000L >> shift);
		
		// Convert result back to 'bitWidth'-bit width
		return updateAs64bit >>> (LONG_WIDTH - bitWidth);
	}


	/**
	 * More efficient version of {@code reverseBits(int)}, using a
	 * modification of the algorithm used in v1.0, as specified in
	 * <em>Hacker's Delight</em> by Henry S. Warren, Jr. (Sec. 7.1, Fig.
	 * 7-1).
	 * <p>
	 * <u>Implementation Notes</u>: The assignment statements can be
	 * executed in any order. This algorithm is more efficient than
	 * {@code reverseBits(int)} v1.0, by using half the number of large
	 * constants in lines 1-3 (#3-5 in {@code reverseBits(int)}), and by
	 * replacing the first 2 lines, which perform the reversal of the bytes,
	 * with a more straightforward approach, in line 4.
	 * <p>
	 * Note: This turns out to be the exact implementation used in the Java
	 * SE 5 & 6 standard libraries, where it is implemented in the static
	 * method {@code Integer.reverse(int)}.
	 * 
	 * @param i an {@code int} to have its reverse bit-order {@code int}
	 *    calculated.
	 * @return a copy of the given {@code int} with its bits in the
	 *    reverse order.
	 *    
	 * @see #reverseBits(int)
	 * @see Integer#reverse(int) Integer.reverse(int)
	 */
	static int reverseBits_HSWarrenJr(int i) {
		// Swap adjacent bits
		i = (i & 0x55555555) << 1 | ((i >>> 1) & 0x55555555); 
		// Swap adjacent 2-bit fields
		i = (i & 0x33333333) << 2 | ((i >>> 2) & 0x33333333); 
		// Swap adjacent 4-bit fields (nibbles)
		i = (i & 0x0F0F0F0F) << 4 | ((i >>> 4) & 0x0F0F0F0F); 
		// Reverse the order of the bytes
		i = (i << 24) | ((i & 0xFF00) << 8) | ((i >> 8) & 0xFF00)
				| (i >> 24);
		
		return i;
	}

	/**
	 * More efficient version of {@code reverseBits(long)}, using an
	 * adaptation of an algorithm specified in <em>Hacker's Delight</em> by
	 * Henry S. Warren, Jr. (Sec. 7.1, Fig. 7-1), which is a modification of
	 * the algorithm used in {@code reverseBits(long)} v1.0.
	 * <p>
	 * <u>Implementation Notes</u>: The assignment statements can be
	 * executed in any order. This algorithm is more efficient than
	 * {@code reverseBits(long)} v1.0, by using half the number of large
	 * constants in lines 1-4 (#3-6 in {@code reverseBits(long)}), and by
	 * replacing the first 2 lines, which perform the reversal of the
	 * half-words (16-bit fields), with a more straightforward approach, in
	 * line 5. 
	 * <p>
	 * Note: This turns out to be the exact implementation used in the Java
	 * SE 5 & 6 standard libraries, where it is implemented in the static
	 * method {@code Long.reverse(long)}.
	 * 
	 * @param l a {@code long} to have its reverse bit-order {@code long}
	 *    calculated.
	 * @return a copy of the given {@code long} with its bits in the
	 *    reverse order.
	 *    
	 * @see #reverseBits(long)
	 * @see Long#reverse(long) Long.reverse(long)
	 */
	static long reverseBits_HSWarrenJr(long l) {
		// Swap adjacent bits
		l = (l & 0x5555555555555555L) << 1
				| ((l >>> 1) & 0x5555555555555555L); 
		// Swap adjacent 2-bit fields
		l = (l & 0x3333333333333333L) << 2
				| ((l >>> 2) & 0x3333333333333333L); 
		// Swap adjacent 4-bit fields (nibbles)
		l = (l & 0x0F0F0F0F0F0F0F0FL) << 4
				| ((l >>> 4) & 0x0F0F0F0F0F0F0F0FL);
		// Swap adjacent bytes (8-bit fields)
		l = (l & 0x00FF00FF00FF00FFL) << 8
				| ((l >>> 8) & 0x00FF00FF00FF00FFL);
		// Reverse the order of the half-words (16-bit fields)
		l = (l << 48) | ((l & 0xFFFF0000L) << 16)
				| ((l >>> 16) & 0xFFFF0000L) | (l >>> 48);
				
		return l;
	}

	/**
	 * Returns a copy of the given {@code byte} with its bits in the
	 * reverse order.
	 * <p>
	 * Based on code by: <a href="http://ivan.vecerina.com/">Ivan Vecerina
	 * </a><br>
	 * Posted on The Scripts Forum:
	 * <a href="http://www.thescripts.com/forum/thread214890.html">
	 * http://www.thescripts.com/forum/thread214890.html</a> on 11/13/05.
	 * <p>
	 * Notes: The author claims O(lg(N)) time for this algorithm, where N is
	 *   the number of bits, which in this implementation is 8. However, a
	 *   comment in the original C code says it hasn't been tested, so I'm
	 *   skeptical.
	 * <p>
	 * Version: 1.1
	 * 
	 * @param b a {@code byte} to have its reverse bit-order {@code byte}
	 *    calculated.
	 * @return a copy of the given {@code byte} with its bits in the
	 *    reverse order.
	 */
	static byte reverseBits_Vecerina(byte b) {
		int res = (int) b;
		res = ((res >>> 1) & 0x00000055) | ((res << 1) & 0x000000AA);
		res = ((res >>> 2) & 0x00000033) | ((res << 2) & 0x000000CC);
		res = (res >>> 4) | (res << 4) ;
		
		return (byte) res;
	}
}
