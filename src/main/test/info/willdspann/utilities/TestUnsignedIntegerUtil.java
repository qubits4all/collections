/*
 * Last Modified: 5/9/14
 * Prev. Modified: 6/5/08 
 * J2SE Version: 5.0
 * 
 * Version Notes: Added tests for the new testUnsignedByteToInt(int),
 *   testUnsignedByteToLong(int) & testUnsignedShortToLong(short) methods.
 *     v1.2: Added 4 new tests, which test the new unsigned integer
 *   comparison methods unsignedByteCompare(byte,byte),
 *   unsignedShortCompare(short,short), unsignedIntCompare(int,int) &
 *   unsignedLongCompare(long,long). Also added a new method
 *   getMaxUnsigned(Class<N>), which returns the maximum unsigned value for
 *   the specified integer type, and is used by these new tests.
 *     v1.1: Added 2 test methods for the new UnsignedIntegerUtil
 *   method unsignedShortToChar(short). Removed the 2 tests that tested
 *   the now removed charToInt(char) method.
 */

package info.willdspann.utilities;

import junit.framework.TestCase;

/**
 * JUnit TestCase for UnsignedIntegerUtil. 
 * 
 * @see UnsignedIntegerUtil UnsignedIntegerUtil
 * @author <A HREF="mailto:willdspann@yahoo.com">Will D. Spann</A>
 * @version 1.3
 */
public class TestUnsignedIntegerUtil extends TestCase {	

    public void testUnsignedByteToShortPosValue() {
    	final byte POS_BYTE = Byte.MAX_VALUE;  // 0x7F or 127
    	
    	short result = UnsignedIntegerUtil.unsignedByteToShort(POS_BYTE);
    	assertEquals((short) 0x007F, result);  // assert: result==127
    }
    
    public void testUnsignedByteToShortNegValue() {
    	final byte NEG_BYTE = (byte) -1;  // 0xFF or unsigned: 255
    	
    	short result = UnsignedIntegerUtil.unsignedByteToShort(NEG_BYTE);
    	assertEquals((short) 0x00FF, result);  // assert: result==255
    }
    
    public void testUnsignedByteToCharPosValue() {
    	final byte POS_BYTE = Byte.MAX_VALUE;  // 0x7F or 127
    	
    	char result = UnsignedIntegerUtil.unsignedByteToChar(POS_BYTE);
    	assertEquals((char) 0x007F, result);  // assert: result==(char)127
    }
    
    public void testUnsignedByteToCharNegValue() {
    	final byte NEG_BYTE = (byte) -1;  // 0xFF or unsigned: 255
    	
    	char result = UnsignedIntegerUtil.unsignedByteToChar(NEG_BYTE);
    	assertEquals((char) 0x00FF, result);  // assert: result==(char)255
    }
    
    public void testUnsignedByteToIntPosValue() {
    	final byte POS_BYTE = Byte.MAX_VALUE;  // 0x7F or 127
    	
    	int result = UnsignedIntegerUtil.unsignedByteToInt(POS_BYTE);
    	assertEquals(0x7F, result);  // assert: result==(int)127
    }
    
    public void testUnsignedByteToIntNegValue() {
    	final byte NEG_BYTE = (byte) -1;  // 0xFF or unsigned: 255
    	
    	int result = UnsignedIntegerUtil.unsignedByteToInt(NEG_BYTE);
    	assertEquals(0xFF, result);  // assert: result==(int)255
    }
    
    public void testUnsignedByteToLongPosValue() {
    	final byte POS_BYTE = Byte.MAX_VALUE;  // 0x7F or 127
    	
    	long result = UnsignedIntegerUtil.unsignedByteToLong(POS_BYTE);
    	assertEquals(0x7FL, result);  // assert: result==(long)127
    }
    
    public void testUnsignedByteToLongNegValue() {
    	final byte NEG_BYTE = (byte) -1;  // 0xFF or unsigned: 255
    	
    	long result = UnsignedIntegerUtil.unsignedByteToLong(NEG_BYTE);
    	assertEquals(0xFFL, result);  // assert: result==(long)255
    }
    
    public void testUnsignedShortToCharPosValue() {
    	final short POS_SHORT = Short.MAX_VALUE;  // 0x7FFF or 2^15 - 1
    	
    	char result = UnsignedIntegerUtil.unsignedShortToChar(POS_SHORT);
    	assertEquals((char) 0x7FFF, result);  // assert: result==(2^15 - 1)
    }
    
    public void testUnsignedShortToCharNegValue() {
    	final short NEG_SHORT = (short) -1; // 0xFFFF or unsigned: 2^16 - 1
    	
    	char result = UnsignedIntegerUtil.unsignedShortToChar(NEG_SHORT);
    	assertEquals((char) 0xFFFF, result);  // assert: result==(2^16 - 1)
    }
    
    public void testUnsignedShortToIntPosValue() {
    	final short POS_SHORT = Short.MAX_VALUE;  // 0x7FFF or 2^15 - 1
    	
    	int result = UnsignedIntegerUtil.unsignedShortToInt(POS_SHORT);
    	assertEquals(0x7FFF, result);  // assert: result==(2^15 - 1)
    }
    
    public void testUnsignedShortToIntNegValue() {
    	final short NEG_SHORT = (short) -1; // 0xFFFF or unsigned: 2^16 - 1
    	
    	int result = UnsignedIntegerUtil.unsignedShortToInt(NEG_SHORT);
    	assertEquals(0xFFFF, result);  // assert: result==(2^16 - 1)
    }  
    
    public void testUnsignedShortToLongPosValue() {
    	final short POS_SHORT = Short.MAX_VALUE;  // 0x7FFF or 2^15 - 1
    	
    	long result = UnsignedIntegerUtil.unsignedShortToLong(POS_SHORT);
    	assertEquals(0x7FFFL, result);  // assert: result==(2^15 - 1)
    }
    
    public void testUnsignedShortToLongNegValue() {
    	final short NEG_SHORT = (short) -1; // 0xFFFF or unsigned: 2^16 - 1
    	
    	long result = UnsignedIntegerUtil.unsignedShortToLong(NEG_SHORT);
    	assertEquals(0xFFFFL, result);  // assert: result==(2^16 - 1)
    }
    
    public void testUnsignedIntToLongPosValue() {
    	final int POS_INT = Integer.MAX_VALUE;  // 0x7FFFFFFF or 2^31 - 1
    	
    	long result = UnsignedIntegerUtil.unsignedIntToLong(POS_INT);
    	assertEquals(0x7FFFFFFFL, result); //exp: result==(2^31 - 1)
    }
    
    public void testUnsignedIntToLongNegValue() {
    	final int NEG_INT = -1;  // 0xFFFFFFFF or unsigned: 2^32 - 1
    	
    	long result = UnsignedIntegerUtil.unsignedIntToLong(NEG_INT);
    	assertEquals(0xFFFFFFFFL, result); //exp: result==(2^32 - 1)
    }

    /*
     * Version: 1.1
     */
    public void testUnsignedByteCompare() {
    	final byte MAX_UNSIGNED = getMaxUnsigned(Byte.class);
    	final byte MAX_SUB_1 = (byte) (MAX_UNSIGNED - 1);
    	final byte MIN_POS = (byte) 1;
    	
    	// 8x2 array of values for comparison testing
    	final byte[][] data = new byte[][] {
    			{ MAX_UNSIGNED, MIN_POS },
    			{ MIN_POS, MAX_UNSIGNED },
    			{ MAX_UNSIGNED, MAX_SUB_1 },
    			{ MAX_SUB_1, MAX_UNSIGNED },
    			{ Byte.MAX_VALUE, MIN_POS },
    			{ MIN_POS, Byte.MAX_VALUE },
    			{ MAX_UNSIGNED, MAX_UNSIGNED },
    			{ MIN_POS, MIN_POS } 
    	};
    	// Array of expected results
    	final int[] expected = new int[] { 1, -1, 1, -1, 1, -1, 0, 0 };
    	
    	// Array for actual results
    	int[] results = new int[8];
    	// Execute 8 comparison tests, storing the results in 'results':
    	for (int i = 0; i < 8; i++) {
    		results[i] = UnsignedIntegerUtil.unsignedByteCompare(data[i][0],
    				data[i][1]);
    	}
    	
    	// Compare each of the 8 results to its expected result:
    	for (int j = 0; j < 8; j++) {
    		assertEquals("Compared: " + data[j][0] + ", " + data[j][1],
    				expected[j], results[j]);
    	}
    }
    
    public void testUnsignedShortCompare() {
    	final short MAX_UNSIGNED = getMaxUnsigned(Short.class);
    	final short MAX_SUB_1 = (short) (MAX_UNSIGNED - 1);
    	final short MIN_POS = (short) 1;
    	
    	// 8x2 array of values for comparison testing
    	final short[][] data = new short[][] {
    			{ MAX_UNSIGNED, MIN_POS },
    			{ MIN_POS, MAX_UNSIGNED },
    			{ MAX_UNSIGNED, MAX_SUB_1 },
    			{ MAX_SUB_1, MAX_UNSIGNED },
    			{ Short.MAX_VALUE, MIN_POS },
    			{ MIN_POS, Short.MAX_VALUE },
    			{ MAX_UNSIGNED, MAX_UNSIGNED },
    			{ MIN_POS, MIN_POS } 
    	};
    	// Array of expected results
    	final int[] expected = new int[] { 1, -1, 1, -1, 1, -1, 0, 0 };
    	
    	// Array for actual results
    	int[] results = new int[8];
    	// Execute 8 comparison tests, storing the results in 'results':
    	for (int i = 0; i < 8; i++) {
    		results[i] = UnsignedIntegerUtil.unsignedShortCompare(data[i][0],
    				data[i][1]);
    	}
    	
    	// Compare each of the 8 results to its expected result:
    	for (int j = 0; j < 8; j++) {
    		assertEquals("Compared: " + data[j][0] + ", " + data[j][1],
    				expected[j], results[j]);
    	}
    }

    public void testUnsignedIntCompare() {
    	final int MAX_UNSIGNED = getMaxUnsigned(Integer.class);
    	final int MAX_SUB_1 = MAX_UNSIGNED - 1;
    	final int MIN_POS = 1;
    	
    	// 8x2 array of values for comparison testing
    	final int[][] data = new int[][] {
    			{ MAX_UNSIGNED, MIN_POS },
    			{ MIN_POS, MAX_UNSIGNED },
    			{ MAX_UNSIGNED, MAX_SUB_1 },
    			{ MAX_SUB_1, MAX_UNSIGNED },
    			{ Integer.MAX_VALUE, MIN_POS },
    			{ MIN_POS, Integer.MAX_VALUE },
    			{ MAX_UNSIGNED, MAX_UNSIGNED },
    			{ MIN_POS, MIN_POS } 
    	};
    	// Array of expected results
    	final int[] expected = new int[] { 1, -1, 1, -1, 1, -1, 0, 0 };
    	
    	// Array for actual results
    	int[] results = new int[8];
    	// Execute 8 comparison tests, storing the results in 'results':
    	for (int i = 0; i < 8; i++) {
    		results[i] = UnsignedIntegerUtil.unsignedIntCompare(data[i][0],
    				data[i][1]);
    	}
    	
    	// Compare each of the 8 results to its expected result:
    	for (int j = 0; j < 8; j++) {
    		assertEquals("Compared: " + data[j][0] + ", " + data[j][1],
    				expected[j], results[j]);
    	}
    }
    
    public void testUnsignedLongCompare() {
    	final long MAX_UNSIGNED = getMaxUnsigned(Long.class);
    	final long MAX_SUB_1 = MAX_UNSIGNED - 1L;
    	final long MIN_POS = 1L;
    	
    	// 8x2 array of values for comparison testing
    	final long[][] data = new long[][] {
    			{ MAX_UNSIGNED, MIN_POS },
    			{ MIN_POS, MAX_UNSIGNED },
    			{ MAX_UNSIGNED, MAX_SUB_1 },
    			{ MAX_SUB_1, MAX_UNSIGNED },
    			{ Long.MAX_VALUE, MIN_POS },
    			{ MIN_POS, Long.MAX_VALUE },
    			{ MAX_UNSIGNED, MAX_UNSIGNED },
    			{ MIN_POS, MIN_POS } 
    	};
    	// Array of expected results
    	final int[] expected = new int[] { 1, -1, 1, -1, 1, -1, 0, 0 };
    	
    	// Array for actual results
    	int[] results = new int[8];
    	// Execute 8 comparison tests, storing the results in 'results':
    	for (int i = 0; i < 8; i++) {
    		results[i] = UnsignedIntegerUtil.unsignedLongCompare(data[i][0],
    				data[i][1]);
    	}
    	
    	// Compare each of the 8 results to its expected result:
    	for (int j = 0; j < 8; j++) {
    		assertEquals("Compared: " + data[j][0] + ", " + data[j][1],
    				expected[j], results[j]);
    	}
    }

    /**
     * Returns the maximum unsigned value that can be stored in the
     * specified {@code Number} type, for integer types not including
     * {@code BigInteger}. For all other {@code Number} types, {@code null}
     * is returned.
     * 
     * @param <N>
     * @param numType an integer {@code Number} type, except
     *    {@code BigInteger}.
     * @return the maximum unsigned value that can be stored in the
     *    specified {@code Number} type, for integer types not including
     *    {@code BigInteger}; for all other {@code Number} types,
     *    {@code null} is returned. 
     */
    private <N extends Number> N getMaxUnsigned(Class<N> numType) {
    	if (numType.isAssignableFrom(Byte.class))
    		return (N) Byte.valueOf((byte) 0xFF);       // 255 (2^8 - 1); -1
    	else if (numType.isAssignableFrom(Short.class))
    		return (N) Short.valueOf((short) 0xFFFF);      // 2^16 - 1; -1
    	else if (numType.isAssignableFrom(Integer.class))
    		return (N) Integer.valueOf(0xFFFFFFFF);		   // 2^32 - 1; -1
    	else if (numType.isAssignableFrom(Long.class))
    		return (N) Long.valueOf(0xFFFFFFFFFFFFFFFFL);  // 2^64 - 1; -1
    	else
    		return null;
    }
}
