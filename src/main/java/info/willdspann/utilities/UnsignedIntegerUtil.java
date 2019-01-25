/*
 * Last Updated: 5/9/14
 * Prev. Update: 7/13/08 
 * Java SE: 6
 * 
 * Version Notes: Added new casting methods for byte-to-int & byte-to-long.
 *     v1.2.1: Added impl. notes to the Javadocs for the
 *   unsignedByteCompare(byte,byte), unsignedShortCompare(short,short),
 *   unsignedIntCompare(int,int) & unsignedLongCompare(long,long) methods,
 *   which cite the source of the algorithm I adapted for use in these
 *   methods: the package-private java.math.MutableBigInteger class of
 *   Java SE 6.
 *     v1.2: Added 4 new methods, for comparing integers as unsigned
 *   integers: unsignedByteCompare(byte,byte),
 *   unsignedShortCompare(short,short), unsignedIntCompare(int,int) &
 *   unsignedLongCompare(long,long).
 *       Reimplemented unsignedIntToLong(int), using the bit-mask
 *   approach correctly. This version works, unlike the last attempt at
 *   using this approach for this method.
 *     v1.1: Added the method unsignedShortToChar(short). Removed the
 *   method charToInt(char), which was unneeded since a simple cast from
 *   char to int is not sign-extending.
 *   
 * Notes on "Unsigned" Casting: Taking the input and AND'ing it with a
 *   bit-mask whose least significant bits are all set for the input's bit
 *   width, prevents sign extension ONLY if the bit-mask is wider than or
 *   equal to the width of the variable being assigned to.
 *     For example, attempting to do an unsigned cast from an int to a long
 *   will fail to work, using the first approach, but work correctly using
 *   the second.
 *   	 // This fails to work
 *       return (long) (i & 0xffffffff);
 *       // This one works
 *       return (long) (i & 0xffffffffL);
 */

package info.willdspann.utilities;


/**
 * Utility class that provides static methods for performing a widening cast
 * from a signed integer type to another integer type, while treating the
 * former value as if it were an unsigned integer. This class also provides
 * methods that perform unsigned comparisons, which effectively treat signed
 * integer types as unsigned for the purposes of the comparison.
 * <p>
 * For example, if the method {@code unsignedByteToShort(byte)} is passed
 * a byte with a value of -1, which represents 255 if treated as an
 * unsigned integer, the short returned will have the value 255, instead of
 * the -1 that would result from a simple cast.
 * <p>
 * Any widening cast from a signed integer type to another integer type
 * (signed or unsigned), performs sign extension, which causes negative
 * integers to retain their negative value.
 * 
 * @author Will D. Spann
 * @version 1.3
 */
public final class UnsignedIntegerUtil {
	
	/**
	 * Converts a byte to a short, without sign extension, as if it were an
	 * unsigned byte.
	 * <p>
	 * Version: 1.1
	 * <p>
	 * JUnit Test: SUCCEEDED.
	 * 
	 * @param b byte to be converted to a short as an unsigned integer. 
	 * @return the short that {@code b} is equivalent to if treated as an
	 *    unsigned integer.
	 */
	public static short unsignedByteToShort(byte b) {
		return (short) (b & 0xFF);
	}


	/**
	 * Converts a byte to a char, without sign extension, as if it were an
	 * unsigned byte.
	 * <p>
	 * Version: 1.0
	 * <p>
	 * JUnit Test: SUCCEEDED.
	 * 
	 * @param b byte to be converted to a char as an unsigned integer. 
	 * @return the char that {@code b} is equivalent to if treated as an
	 *    unsigned integer.
	 */
	public static char unsignedByteToChar(byte b) {
		return (char) (b & 0xFF);
	}
	
	
	/**
	 * Converts a byte to an int, without sign extension, as if it were an
	 * unsigned byte.
	 * <p>
	 * Version: 1.0
	 * <p>
	 * JUnit Test: UNTESTED
	 * 
	 * @param b byte to be converted to an int as an unsigned integer. 
	 * @return the int that {@code b} is equivalent to if treated as an
	 *    unsigned integer.
	 */
	public static int unsignedByteToInt(byte b) {
		return (int) (b & 0x000000FF);
	}
	
	
	/**
	 * Converts a byte to a long, without sign extension, as if it were an
	 * unsigned byte.
	 * <p>
	 * Version: 1.0
	 * <p>
	 * JUnit Test: UNTESTED
	 * 
	 * @param b byte to be converted to a long as an unsigned integer. 
	 * @return the long that {@code b} is equivalent to if treated as an
	 *    unsigned integer.
	 */
	public static long unsignedByteToLong(byte b) {
		return (long) (b & 0xFFL);
	}
	
	
	/**
	 * Converts a short to a char, without sign extension, as if it were an
	 * unsigned integer.
	 * <p>
	 * Version: 1.0
	 * <p>
	 * JUnit Test: SUCCEEDED.
	 * 
	 * @param s short to be converted to a char as an unsigned integer. 
	 * @return the char that {@code s} is equivalent to if treated as an
	 *    unsigned integer.
	 */
	public static char unsignedShortToChar(short s) {
		return (char) (s & 0xFFFF);
	}
	
	
	/**
	 * Converts a short to an int, without sign extension, as if it were an
	 * unsigned short (like char).
	 * <p>
	 * Version: 1.1
	 * <p>
	 * JUnit Test: SUCCEEDED.
	 * 
	 * @param s short to be converted to an int as an unsigned integer. 
	 * @return the int that {@code s} is equivalent to if treated as an
	 *    unsigned integer.
	 */
	public static int unsignedShortToInt(short s) {
		return (int) (s & 0xFFFF);
	}
	
	
	/**
	 * Converts a short to a long, without sign extension, as if it were an
	 * unsigned short.
	 * <p>
	 * Version: 1.0
	 * <p>
	 * JUnit Test: UNTESTED
	 * 
	 * @param s short to be converted to a long as an unsigned integer. 
	 * @return the long that {@code s} is equivalent to if treated as an
	 *    unsigned integer.
	 */
	public static long unsignedShortToLong(short s) {
		return (long) (s & 0xFFFFL);
	}
	
	
	/**
	 * Converts an int to a long, without sign extension, as if it were an
	 * unsigned int.
	 * <p>
	 * Version: 1.2
	 * <p>
	 * Implementation Notes: Repeated the approach used in v1.0, but
	 *   modified it to use a long mask, instead of an int mask. This
	 *   technique is used in the package-private
	 *   java.math.MutableBigInteger class, which BigInteger uses.
	 * <p>
	 * JUnit Test: SUCCEEDED.
	 * 
	 * @param i int to be converted to a long as an unsigned integer. 
	 * @return the long that {@code i} is equivalent to if treated as an
	 *    unsigned integer.
	 */
	public static long unsignedIntToLong(int i) {
		return (long) (i & 0xFFFFFFFFL);
	}
	
	
	/**
	 * Compares two bytes as if they were unsigned integers.
	 * <p>
	 * <u>Implementation Notes</u>: The algorithm used is adapted from the
	 * private {@code unsignedLongCompare(long,long)} method, contained in
	 * the package-private {@code java.math.MutableBigInteger} class of Java
	 * SE 6.
	 * <p>
	 * JUnit Test: SUCCEEDED.
	 * 
	 * @param b1 byte to compare to {@code b2,} as an unsigned integer.
	 * @param b2 byte {@code b1} will compare to, as an unsigned integer.
	 * @return 1, 0, or -1 if {@code b1} is greater, equal, or less than
	 *    {@code b2}, respectively, treating both as unsigned integers.
	 */
	public static int unsignedByteCompare(byte b1, byte b2) {
		byte b1mod = (byte) (b1 + Byte.MIN_VALUE);
		byte b2mod = (byte) (b2 + Byte.MIN_VALUE);
		
		return b1mod == b2mod ? 0 : (b1mod > b2mod ? 1 : -1);
	}
	
	
	/**
	 * Compares two shorts as if they were unsigned integers.
	 * <p>
	 * <u>Implementation Notes</u>: The algorithm used is adapted from the
	 * private {@code unsignedLongCompare(long,long)} method, contained in
	 * the package-private {@code java.math.MutableBigInteger} class of Java
	 * SE 6.
	 * <p>
	 * JUnit Test: SUCCEEDED.
	 * 
	 * @param s1 short to compare to {@code s2,} as an unsigned integer.
	 * @param s2 short {@code s1} will compare to, as an unsigned integer.
	 * @return 1, 0, or -1 if {@code s1} is greater, equal, or less than
	 *    {@code s2}, respectively, treating both as unsigned integers.
	 */
	public static int unsignedShortCompare(short s1, short s2) {
		short s1mod = (short) (s1 + Short.MIN_VALUE);
		short s2mod = (short) (s2 + Short.MIN_VALUE);
		
		return s1mod == s2mod ? 0 : (s1mod > s2mod ? 1 : -1);
	}
	
	
	/**
	 * Compares two ints as if they were unsigned integers.
	 * <p>
	 * <u>Implementation Notes</u>: The algorithm used is adapted from the
	 * private {@code unsignedLongCompare(long,long)} method, contained in
	 * the package-private {@code java.math.MutableBigInteger} class of Java
	 * SE 6.
	 * <p>
	 * JUnit Test: SUCCEEDED.
	 * 
	 * @param i1 int to compare to {@code i2,} as an unsigned integer.
	 * @param i2 int {@code i1} will compare to, as an unsigned integer.
	 * @return 1, 0, or -1 if {@code i1} is greater, equal, or less than
	 *    {@code i2}, respectively, treating both as unsigned integers.
	 */
	public static int unsignedIntCompare(int i1, int i2) {
		int i1mod = i1 + Integer.MIN_VALUE;
		int i2mod = i2 + Integer.MIN_VALUE;
		
		return i1mod == i2mod ? 0 : (i1mod > i2mod ? 1 : -1);
	}
	
	
	/**
	 * Compares two longs as if they were unsigned integers.
	 * <p>
	 * <u>Implementation Notes</u>: The algorithm used is adapted from the
	 * private {@code unsignedLongCompare(long,long)} method, contained in
	 * the package-private {@code java.math.MutableBigInteger} class of Java
	 * SE 6.
	 * <p>
	 * JUnit Test: SUCCEEDED.
	 * 
	 * @param l1 long to compare to {@code l2,} as an unsigned integer.
	 * @param l2 long {@code l1} will compare to, as an unsigned integer.
	 * @return 1, 0, or -1 if {@code l1} is greater, equal, or less than
	 *    {@code l2}, respectively, treating both as unsigned integers.
	 */
	public static int unsignedLongCompare(long l1, long l2) {
		long l1mod = l1 + Long.MIN_VALUE;
		long l2mod = l2 + Long.MIN_VALUE;
		
		return l1mod == l2mod ? 0 : (l1mod > l2mod ? 1 : -1);
	}
	
}