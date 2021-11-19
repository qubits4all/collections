package info.willdspann.utilities;

/**
 * A collection of pseudo-random number generation algorithms.
 *
 * @author Will D. Spann
 * @version 1.0
 */
public class PseudoRandomUtils {

	/**
     * A medium-quality pseudo-random number generator, which is suitable
     * for testing purposes. Taken from "Java Concurrency in Practice",
     * p. 253.
     * 
     * @author Brian Goetz
     * 
     * @param y initial seed value, or subsequent previous output.
     * @return a pseudo-random number, based on the given seed or previous
     *    output.
     */
    public static int xorShift(int y) {
    	y ^= (y << 6);
    	y ^= (y >>> 21);
    	y ^= (y << 7);
    	return y;
    }
}
