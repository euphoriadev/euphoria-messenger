package ru.euphoria.messenger.util;


import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

/**
 * Static utils methods for array.
 *
 * For powerful collection util - use {@link Collections}
 *
 * @author Igor Morozkin
 * @since 1.0
 */
public class ArrayUtil {
    private static final CharsetDecoder DECODER = Charset.defaultCharset().newDecoder();

    /** An immutable empty arrays */
    public static final Object[] EMPTY_OBJECTS = new Object[0];
    public static final String[] EMPTE_STRINGS = new String[0];
    public static final byte[]   EMPTY_BYTES   = new byte[0];
    public static final char[]   EMPTY_CHARS   = new char[0];
    public static final short[]  EMPTY_SHORTS  = new short[0];
    public static final int[]    EMPTE_INTS    = new int[0];
    public static final long[]   EMPTY_LONGS   = new long[0];
    public static final float[]  EMPTY_FLOATS  = new float[0];
    public static final double[] EMPTY_DOUBLES = new double[0];

    /** The index for linear and binary search, if the value is not found */
    public static final int VALUE_NOT_FOUND = -1;

    // uses only static methods
    private ArrayUtil() {}

    /**
     * Performs a linear search for value in the ascending array.
     * Beware that linear search returns only the first found element.
     *
     * @param array the array to search
     * @param value the value fo find
     * @return the non-negative index of value, or -1 if value not found
     */
    public static int linearSearch(byte[] array, byte value) {
        return linearSearch(array, value, 0, array.length);
    }

    /**
     * Performs a linear search for value in the ascending array.
     * Beware that linear search returns only the first found element.
     *
     * @param array the array to search
     * @param value the value fo find
     * @param start the start index (inclusive)
     * @param end   the end index (exclusive)
     * @return the non-negative index of value, or -1 if value not found
     */
    public static int linearSearch(byte[] array, byte value, int start, int end) {
        for (int i = start; i < end; i++) {
            if (array[i] == value) {
                return i;
            }
        }
        return VALUE_NOT_FOUND;
    }

    /**
     * Performs a linear search for value in the ascending array.
     * Beware that linear search returns only the first found element.
     *
     * @param array the array to search
     * @param value the value fo find
     * @return the non-negative index of value, or -1 if value not found
     */
    public static int linearSearch(char[] array, char value) {
        return linearSearch(array, value, 0, array.length);
    }

    /**
     * Performs a linear search for value in the ascending array.
     * Beware that linear search returns only the first found element.
     *
     * @param array the array to search
     * @param value the value fo find
     * @param start the start index (inclusive)
     * @param end   the end index (exclusive)
     * @return the non-negative index of value, or -1 if value not found
     */
    public static int linearSearch(char[] array, char value, int start, int end) {
        for (int i = start; i < end; i++) {
            if (array[i] == value) {
                return i;
            }
        }
        return VALUE_NOT_FOUND;
    }

    /**
     * Performs a linear search for value in the ascending array.
     * Beware that linear search returns only the first found element.
     *
     * @param array the array to search
     * @param value the value fo find
     * @return the non-negative index of value, or -1 if value not found
     */
    public static int linearSearch(short[] array, short value) {
        return linearSearch(array, value, 0, array.length);
    }

    /**
     * Performs a linear search for value in the ascending array.
     * Beware that linear search returns only the first found element.
     *
     * @param array the array to search
     * @param value the value fo find
     * @param start the start index (inclusive)
     * @param end   the end index (exclusive)
     * @return the non-negative index of value, or -1 if value not found
     */
    public static int linearSearch(short[] array, short value, int start, int end) {
        for (int i = start; i < end; i++) {
            if (array[i] == value) {
                return i;
            }
        }
        return VALUE_NOT_FOUND;
    }

    /**
     * Performs a linear search for value in the ascending array.
     * Beware that linear search returns only the first found element.
     *
     * @param array the array to search
     * @param value the value fo find
     * @return the non-negative index of value, or -1 if value not found
     */
    public static int linearSearch(int[] array, int value) {
        return linearSearch(array, value, 0, array.length);
    }

    /**
     * Performs a linear search for value in the ascending array.
     * Beware that linear search returns only the first found element.
     *
     * @param array the array to search
     * @param value the value fo find
     * @param start the start index (inclusive)
     * @param end   the end index (exclusive)
     * @return the non-negative index of value, or -1 if value not found
     */
    public static int linearSearch(int[] array, int value, int start, int end) {
        for (int i = start; i < end; i++) {
            if (array[i] == value) {
                return i;
            }
        }
        return VALUE_NOT_FOUND;
    }

    /**
     * Performs a linear search for value in the ascending array.
     * Beware that linear search returns only the first found element.
     *
     * @param array the array to search
     * @param value the value fo find
     * @return the non-negative index of value, or -1 if value not found
     */
    public static int linearSearch(long[] array, long value) {
        return linearSearch(array, value, 0, array.length);
    }

    /**
     * Performs a linear search for value in the ascending array.
     * Beware that linear search returns only the first found element.
     *
     * @param array the array to search
     * @param value the value fo find
     * @param start the start index (inclusive)
     * @param end   the end index (exclusive)
     * @return the non-negative index of value, or -1 if value not found
     */
    public static int linearSearch(long[] array, long value, int start, int end) {
        for (int i = start; i < end; i++) {
            if (array[i] == value) {
                return i;
            }
        }
        return VALUE_NOT_FOUND;
    }

    /**
     * Performs a linear search for value in the ascending array.
     * Beware that linear search returns only the first found element.
     *
     * @param array the array to search
     * @param value the value fo find
     * @return the non-negative index of value, or -1 if value not found
     */
    public static int linearSearch(float[] array, float value) {
        return linearSearch(array, value, 0, array.length);
    }

    /**
     * Performs a linear search for value in the ascending array.
     * Beware that linear search returns only the first found element.
     *
     * @param array the array to search
     * @param value the value fo find
     * @param start the start index (inclusive)
     * @param end   the end index (exclusive)
     * @return the non-negative index of value, or -1 if value not found
     */
    public static int linearSearch(float[] array, float value, int start, int end) {
        for (int i = start; i < end; i++) {
            if (Float.compare(array[i], value) == 0) {
                return i;
            }
        }
        return VALUE_NOT_FOUND;
    }

    /**
     * Performs a linear search for value in the ascending array.
     * Beware that linear search returns only the first found element.
     *
     * @param array the array to search
     * @param value the value fo find
     * @return the non-negative index of value, or -1 if value not found
     */
    public static int linearSearch(double[] array, double value) {
        return linearSearch(array, value, 0, array.length);
    }

    /**
     * Performs a linear search for value in the ascending array.
     * Beware that linear search returns only the first found element.
     *
     * @param array the array to search
     * @param value the value fo find
     * @param start the start index (inclusive)
     * @param end   the end index (exclusive)
     * @return the non-negative index of value, or -1 if value not found
     */
    public static int linearSearch(double[] array, double value, int start, int end) {
        for (int i = start; i < end; i++) {
            if (Double.compare(array[i], value) == 0) {
                return i;
            }
        }
        return VALUE_NOT_FOUND;
    }

    /**
     * Performs a linear search for value in the ascending array.
     * Beware that linear search returns only the first found element.
     *
     * @param array the array to search
     * @param value the value fo find
     * @return the non-negative index of value, or -1 if value not found
     */
    public static int linearSearch(Object[] array, Object value) {
        return linearSearch(array, value, 0, array.length);
    }

    /**
     * Performs a linear search for value in the ascending array.
     * Beware that linear search returns only the first found element.
     *
     * @param array the array to search
     * @param value the value fo find
     * @param start the start index (inclusive)
     * @param end   the end index (exclusive)
     * @return the non-negative index of value, or -1 if value not found
     */
    public static int linearSearch(Object[] array, Object value, int start, int end) {
        for (int i = start; i < end; i++) {
            Object o = array[i];
            if (o == value || o.equals(value)) {
                return i;
            }
        }
        return VALUE_NOT_FOUND;
    }

    /**
     * Performs a binary search for value in the array. The array con be unsorted.
     * Beware that binary search returns only the first found element
     * <p/>
     * This algorithm differs from the classical binary search
     * is that, it is more optimized for unsorted arrays.
     * As the classic method - the search starts at the half-size of array
     * <p/>
     * NOTE: If array is sorted, use {@link Arrays#binarySearch(int[], int)}
     *
     * @param array the array to search
     * @param value the value fo find
     * @return the non-negative index of value, or -1 if value not found
     */
    public static int binarySearch(int[] array, int value) {
        int high = array.length - 1;
        int startIndex = high / 2;
        int index = startIndex;
        while (index >= 0 && index <= high) {
            if (array[index] == value) {
                return index;
            }

            if (index > startIndex) {
                index = index - ((index - startIndex) * 2);
            } else {
                index = index + ((startIndex - index) * 2) + 1;
            }
        }
        return VALUE_NOT_FOUND;
    }


    /**
     * Performs a binary search for value in the array. The array con be unsorted.
     * Beware that binary search returns only the first found element
     * <p/>
     * This algorithm differs from the classical binary search
     * is that, it is more optimized for unsorted arrays.
     * As the classic method - the search starts at the half-size of array
     * <p/>
     * NOTE: If array is sorted, use {@link Arrays#binarySearch(long[], long)}
     *
     * @param array the array to search
     * @param value the value fo find
     * @return the non-negative index of value, or -1 if value not found
     */
    public static int binarySearch(long[] array, long value) {
        int high = array.length - 1;
        int startIndex = high / 2;
        int index = startIndex;
        while (index >= 0 && index <= high) {
            if (array[index] == value) {
                return index;
            }

            if (index > startIndex) {
                index = index - ((index - startIndex) * 2);
            } else {
                index = index + ((startIndex - index) * 2) + 1;
            }
        }
        return VALUE_NOT_FOUND;
    }


    /**
     * Performs a binary search for value in the array. The array con be unsorted.
     * Beware that binary search returns only the first found element
     * <p/>
     * This algorithm differs from the classical binary search
     * is that, it is more optimized for unsorted arrays.
     * As the classic method - the search starts at the half-size of array
     * <p/>
     * NOTE: If array is sorted, use {@link Arrays#binarySearch(short[], short)}
     *
     * @param array the array to search
     * @param value the value fo find
     * @return the non-negative index of value, or -1 if value not found
     */
    public static int binarySearch(short[] array, short value) {
        int high = array.length - 1;
        int startIndex = high / 2;
        int index = startIndex;
        while (index >= 0 && index <= high) {
            if (array[index] == value) {
                return index;
            }

            if (index > startIndex) {
                index = index - ((index - startIndex) * 2);
            } else {
                index = index + ((startIndex - index) * 2) + 1;
            }
        }
        return VALUE_NOT_FOUND;
    }


    /**
     * Performs a binary search for value in the array. The array con be unsorted.
     * Beware that binary search returns only the first found element
     * <p/>
     * This algorithm differs from the classical binary search
     * is that, it is more optimized for unsorted arrays.
     * As the classic method - the search starts at the half-size of array
     * <p/>
     * NOTE: If array is sorted, use {@link Arrays#binarySearch(char[], char)}
     *
     * @param array the array to search
     * @param value the value fo find
     * @return the non-negative index of value, or -1 if value not found
     */
    public static int binarySearch(char[] array, char value) {
        int high = array.length - 1;
        int startIndex = high / 2;
        int index = startIndex;
        while (index >= 0 && index <= high) {
            if (array[index] == value) {
                return index;
            }

            if (index > startIndex) {
                index = index - ((index - startIndex) * 2);
            } else {
                index = index + ((startIndex - index) * 2) + 1;
            }
        }
        return VALUE_NOT_FOUND;
    }

    /**
     * Performs a binary search for value in the array. The array con be unsorted.
     * Beware that binary search returns only the first found element
     * <p/>
     * This algorithm differs from the classical binary search
     * is that, it is more optimized for unsorted arrays.
     * As the classic method - the search starts at the half-size of array
     * <p/>
     * NOTE: If array is sorted, use {@link Arrays#binarySearch(byte[], byte)}
     *
     * @param array the array to search
     * @param value the value fo find
     * @return the non-negative index of value, or -1 if value not found
     */
    public static int binarySearch(byte[] array, byte value) {
        int high = array.length - 1;
        int startIndex = high / 2;
        int index = startIndex;
        while (index >= 0 && index <= high) {
            if (array[index] == value) {
                return index;
            }

            if (index > startIndex) {
                index = index - ((index - startIndex) * 2);
            } else {
                index = index + ((startIndex - index) * 2) + 1;
            }
        }
        return VALUE_NOT_FOUND;
    }

    /**
     * Returns true if the array has the specified value
     *
     * @param array the array to search specified value
     * @param value the value to search for
     */
    public static boolean contains(byte[] array, byte value) {
        return linearSearch(array, value) != VALUE_NOT_FOUND;
    }

    /**
     * Returns true if the array has the specified value
     *
     * @param array the array to search specified value
     * @param value the value to search for
     */
    public static boolean contains(char[] array, char value) {
        return linearSearch(array, value) != VALUE_NOT_FOUND;
    }

    /**
     * Returns true if the array has the specified value
     *
     * @param array the array to search specified value
     * @param value the value to search for
     */
    public static boolean contains(short[] array, short value) {
        return linearSearch(array, value) != VALUE_NOT_FOUND;
    }

    /**
     * Returns true if the array has the specified value
     *
     * @param array the array to search specified value
     * @param value the value to search for
     */
    public static boolean contains(int[] array, int value) {
        return linearSearch(array, value) != VALUE_NOT_FOUND;
    }

    /**
     * Returns true if the array has the specified value
     *
     * @param array the array to search specified value
     * @param value the value to search for
     */
    public static boolean contains(long[] array, long value) {
        return linearSearch(array, value) != VALUE_NOT_FOUND;
    }

    /**
     * Returns true if the array has the specified value
     *
     * @param array the array to search specified value
     * @param value the value to search for
     */
    public static boolean contains(float[] array, float value) {
        return linearSearch(array, value) != VALUE_NOT_FOUND;
    }

    /**
     * Returns true if the array has the specified value
     *
     * @param array the array to search specified value
     * @param value the value to search for
     */
    public static boolean contains(double[] array, double value) {
        return linearSearch(array, value) != VALUE_NOT_FOUND;
    }

    /**
     * Returns true if the array has the specified value
     *
     * @param array the array to search specified value
     * @param value the value to search for
     */
    public static boolean contains(Object[] array, Object value) {
        return linearSearch(array, value) != VALUE_NOT_FOUND;
    }

    /**
     * Returns the number of elements in the specified array that match the
     * specified value passed.
     *
     * @param array the array to search
     * @param value the value to find for
     */
    public static int frequency(byte[] array, byte value) {
        if (isEmpty(array)) return 0;

        int count = 0;
        int index = 0;
        while ((index = linearSearch(array, value, index, array.length)) != VALUE_NOT_FOUND) {
            count++;
            index++;
        }
        return count;
    }

    /**
     * Returns the number of elements in the specified array that match the
     * specified value passed.
     *
     * @param array the array to search
     * @param value the value to find for
     */
    public static int frequency(char[] array, char value) {
        if (isEmpty(array)) return 0;

        int count = 0;
        int index = 0;
        while ((index = linearSearch(array, value, index, array.length)) != VALUE_NOT_FOUND) {
            count++;
            index++;
        }
        return count;
    }

    /**
     * Returns the number of elements in the specified array that match the
     * specified value passed.
     *
     * @param array the array to search
     * @param value the value to find for
     */
    public static int frequency(int[] array, int value) {
        if (isEmpty(array)) return 0;

        int count = 0;
        int index = 0;
        while ((index = linearSearch(array, value, index, array.length)) != VALUE_NOT_FOUND) {
            count++;
            index++;
        }
        return count;
    }

    /**
     * Returns the number of elements in the specified array that match the
     * specified value passed.
     *
     * @param array the array to search
     * @param value the value to find for
     */
    public static int frequency(long[] array, long value) {
        if (isEmpty(array)) return 0;

        int count = 0;
        int index = 0;
        while ((index = linearSearch(array, value, index, array.length)) != VALUE_NOT_FOUND) {
            count++;
            index++;
        }
        return count;
    }

    /**
     * Returns the number of elements in the specified array that match the
     * specified value passed.
     *
     * @param array the array to search
     * @param value the value to find for
     */
    public static int frequency(float[] array, float value) {
        if (isEmpty(array)) return 0;

        int count = 0;
        int index = 0;
        while ((index = linearSearch(array, value, index, array.length)) != VALUE_NOT_FOUND) {
            count++;
            index++;
        }
        return count;
    }

    /**
     * Returns the number of elements in the specified array that match the
     * specified value passed.
     *
     * @param array the array to search
     * @param value the value to find for
     */
    public static int frequency(double[] array, double value) {
        if (isEmpty(array)) return 0;

        int count = 0;
        int index = 0;
        while ((index = linearSearch(array, value, index, array.length)) != VALUE_NOT_FOUND) {
            count++;
            index++;
        }
        return count;
    }

    /**
     * Returns the number of elements in the specified array that match the
     * specified value passed.
     *
     * @param array the array to search
     * @param value the value to find for
     */
    public static int frequency(Object[] array, Object value) {
        if (isEmpty(array)) return 0;

        int count = 0;
        int index = 0;
        while ((index = linearSearch(array, value, index, array.length)) != VALUE_NOT_FOUND) {
            count++;
            index++;
        }
        return count;
    }

    /**
     * Returns a new single array containing all values
     * from the specified arrays.
     *
     * @param arrays the arrays to concatenate
     */
    public static byte[] concat(byte[]... arrays) {
        int length = 0;
        for (byte[] array : arrays) {
            length += array.length;
        }

        byte[] result = new byte[length];
        int index = 0;
        for (byte[] array : arrays) {
            System.arraycopy(array, 0, result, index, array.length);
            index += array.length;
        }

        return result;
    }

    /**
     * Returns a new single array containing all values
     * from the specified arrays.
     *
     * @param arrays the arrays to concatenate
     */
    public static char[] concat(char[]... arrays) {
        int length = 0;
        for (char[] array : arrays) {
            length += array.length;
        }

        char[] result = new char[length];
        int index = 0;
        for (char[] array : arrays) {
            System.arraycopy(array, 0, result, index, array.length);
            index += array.length;
        }

        return result;
    }

    /**
     * Returns a new single array containing all values
     * from the specified arrays.
     *
     * @param arrays the arrays to concatenate
     */
    public static short[] concat(short[]... arrays) {
        int length = 0;
        for (short[] array : arrays) {
            length += array.length;
        }

        short[] result = new short[length];
        int index = 0;
        for (short[] array : arrays) {
            System.arraycopy(array, 0, result, index, array.length);
            index += array.length;
        }

        return result;
    }

    /**
     * Returns a new single array containing all values
     * from the specified arrays.
     *
     * @param arrays the arrays to concatenate
     */
    public static int[] concat(int[]... arrays) {
        int length = 0;
        for (int[] array : arrays) {
            length += array.length;
        }

        int[] result = new int[length];
        int index = 0;
        for (int[] array : arrays) {
            System.arraycopy(array, 0, result, index, array.length);
            index += array.length;
        }

        return result;
    }


    /**
     * Returns a new single array containing all values
     * from the specified arrays.
     *
     * @param arrays the arrays to concatenate
     */
    public static long[] concat(long[]... arrays) {
        int length = 0;
        for (long[] array : arrays) {
            length += array.length;
        }

        long[] result = new long[length];
        int index = 0;
        for (long[] array : arrays) {
            System.arraycopy(array, 0, result, index, array.length);
            index += array.length;
        }

        return result;
    }

    /**
     * Returns a new single array containing all values
     * from the specified arrays.
     *
     * @param arrays the arrays to concatenate
     */
    public static float[] concat(float[]... arrays) {
        int length = 0;
        for (float[] array : arrays) {
            length += array.length;
        }

        float[] result = new float[length];
        int index = 0;
        for (float[] array : arrays) {
            System.arraycopy(array, 0, result, index, array.length);
            index += array.length;
        }

        return result;
    }

    /**
     * Returns a new single array containing all values
     * from the specified arrays.
     *
     * @param arrays the arrays to concatenate
     */
    public static double[] concat(double[]... arrays) {
        int length = 0;
        for (double[] array : arrays) {
            length += array.length;
        }

        double[] result = new double[length];
        int index = 0;
        for (double[] array : arrays) {
            System.arraycopy(array, 0, result, index, array.length);
            index += array.length;
        }

        return result;
    }

    /**
     * Returns a new single array containing all values
     * from the specified arrays.
     *
     * @param arrays the arrays to concatenate
     */
    public static Object[] concat(Object[]... arrays) {
        int length = 0;
        for (Object[] array : arrays) {
            length += array.length;
        }

        Object[] result = new Object[length];
        int index = 0;
        for (Object[] array : arrays) {
            System.arraycopy(array, 0, result, index, array.length);
            index += array.length;
        }

        return result;
    }

    /**
     * Searches for the maximum element of specified array using linear search.
     * To search in {@link Collection} you should
     * use {@link java.util.Collections#max(Collection)}.
     *
     * @param array the array to search
     * @return the max element in specified array
     */
    public static byte max(byte... array) {
        byte max = array[0];

        for (int i = 1; i < array.length; i++) {
            byte value = array[i];
            if (max < value) {
                max = value;
            }
        }
        return max;
    }

    /**
     * Searches for the maximum element of specified array using linear search.
     * To search in {@link Collection} you should
     * use {@link java.util.Collections#max(Collection)}.
     *
     * @param array the array to search
     * @return the max element in specified array
     */
    public static int max(int... array) {
        int max = array[0];

        for (int i = 1; i < array.length; i++) {
            int value = array[i];
            if (max < value) {
                max = value;
            }
        }
        return max;
    }

    /**
     * Searches for the maximum element of specified array using linear search.
     * To search in {@link Collection} you should
     * use {@link java.util.Collections#max(Collection)}.
     *
     * @param array the array to search
     * @return the max element in specified array
     */
    public static long max(long... array) {
        long max = array[0];

        for (int i = 1; i < array.length; i++) {
            long value = array[i];
            if (max < value) {
                max = value;
            }
        }
        return max;
    }

    /**
     * Searches for the maximum element of specified array using linear search.
     * To search in {@link Collection} you should
     * use {@link java.util.Collections#max(Collection)}.
     *
     * @param array the array to search
     * @return the max element in specified array
     */
    public static char max(char... array) {
        char max = array[0];

        for (int i = 1; i < array.length; i++) {
            char value = array[i];
            if (max < value) {
                max = value;
            }
        }
        return max;
    }

    /**
     * Searches for the maximum element of specified array using linear search.
     * To search in {@link Collection} you should
     * use {@link java.util.Collections#max(Collection)}.
     *
     * @param array the array to search
     * @return the max element in specified array
     */
    public static short max(short... array) {
        short max = array[0];

        for (int i = 1; i < array.length; i++) {
            short value = array[i];
            if (max < value) {
                max = value;
            }
        }
        return max;
    }

    /**
     * Searches for the maximum element of specified array using linear search.
     * To search in {@link Collection} you should
     * use {@link java.util.Collections#max(Collection)}.
     *
     * @param array the array to search
     * @return the max element in specified array
     */
    public static float max(float... array) {
        float max = array[0];

        for (int i = 1; i < array.length; i++) {
            float value = array[i];
            if (Float.compare(value, max) >= 1) {
                max = value;
            }
        }
        return max;
    }

    /**
     * Searches for the maximum element of specified array using linear search.
     * To search in {@link Collection} you should
     * use {@link java.util.Collections#max(Collection)}.
     *
     * @param array the array to search
     * @return the max element in specified array
     */
    public static double max(double... array) {
        double max = array[0];

        for (int i = 1; i < array.length; i++) {
            double value = array[i];
            if (Double.compare(value, max) >= 1) {
                max = value;
            }
        }
        return max;
    }

    /**
     * Searches for the minimum element of specified array using linear search.
     * To search in {@link Collection} you should
     * use {@link java.util.Collections#max(Collection)}.
     *
     * @param array the array to search
     * @return the min element in specified array
     */
    public static int min(int... array) {
        int min = array[0];

        for (int i = 1; i < array.length; i++) {
            int value = array[i];
            if (min > value) {
                min = value;
            }
        }
        return min;
    }

    /**
     * Searches for the minimum element of specified array using linear search.
     * To search in {@link Collection} you should
     * use {@link java.util.Collections#max(Collection)}.
     *
     * @param array the array to search
     * @return the min element in specified array
     */
    public static long min(long... array) {
        long min = array[0];

        for (int i = 1; i < array.length; i++) {
            long value = array[i];
            if (min > value) {
                min = value;
            }
        }
        return min;
    }

    /**
     * Searches for the minimum element of specified array using linear search.
     * To search in {@link Collection} you should
     * use {@link java.util.Collections#max(Collection)}.
     *
     * @param array the array to search
     * @return the min element in specified array
     */
    public static double min(double... array) {
        double min = array[0];

        for (int i = 1; i < array.length; i++) {
            double value = array[i];
            if (min > value) {
                min = value;
            }
        }
        return min;
    }

    /**
     * Searches for the minimum element of specified array using linear search.
     * To search in {@link Collection} you should
     * use {@link java.util.Collections#max(Collection)}.
     *
     * @param array the array to search
     * @return the min element in specified array
     */
    public static float min(float... array) {
        float min = array[0];

        for (int i = 1; i < array.length; i++) {
            float value = array[i];
            if (min > value) {
                min = value;
            }
        }
        return min;
    }

    /**
     * Searches for the minimum element of specified array using linear search.
     * To search in {@link Collection} you should
     * use {@link java.util.Collections#max(Collection)}.
     *
     * @param array the array to search
     * @return the min element in specified array
     */
    public static char min(char... array) {
        char min = array[0];

        for (int i = 1; i < array.length; i++) {
            char value = array[i];
            if (min > value) {
                min = value;
            }
        }
        return min;
    }

    /**
     * Searches for the minimum element of specified array using linear search.
     * To search in {@link Collection} you should
     * use {@link java.util.Collections#max(Collection)}.
     *
     * @param array the array to search
     * @return the min element in specified array
     */
    public static short min(short... array) {
        short min = array[0];

        for (int i = 1; i < array.length; i++) {
            short value = array[i];
            if (min > value) {
                min = value;
            }
        }
        return min;
    }


    /**
     * Searches for the minimum element of specified array using linear search.
     * To search in {@link Collection} you should
     * use {@link java.util.Collections#max(Collection)}.
     *
     * @param array the array to search
     * @return the min element in specified array
     */
    public static byte min(byte... array) {
        byte min = array[0];

        for (int i = 1; i < array.length; i++) {
            byte value = array[i];
            if (min > value) {
                min = value;
            }
        }
        return min;
    }

    /**
     * Returns the total sum of specified array using linear search
     *
     * @param array the array to calculate
     */
    public static long total(byte... array) {
        int total = 0;
        for (int value : array) {
            total += value;
        }
        return total;
    }

    /**
     * Returns the total sum of specified array using linear search
     *
     * @param array the array to calculate
     */
    public static long total(int... array) {
        int total = 0;
        for (int value : array) {
            total += value;
        }
        return total;
    }

    /**
     * Returns the total sum of specified array using linear search
     *
     * @param array the array to calculate
     */
    public static long total(long... array) {
        long total = 0;
        for (long value : array) {
            total += value;
        }
        return total;
    }

    /**
     * Returns the total sum of specified array using linear search
     *
     * @param array the array to calculate
     */
    public static long total(short... array) {
        long total = 0;
        for (short value : array) {
            total += value;
        }
        return total;
    }

    /**
     * Returns the total sum of specified array using linear search
     *
     * @param array the array to calculate
     */
    public static long total(float... array) {
        long total = 0;
        for (float value : array) {
            total += value;
        }
        return total;
    }

    /**
     * Returns the total sum of specified array using linear search
     *
     * @param array the array to calculate
     */
    public static long total(double... array) {
        long total = 0;
        for (double value : array) {
            total += value;
        }
        return total;
    }


    /**
     * Moves every element of the specified array to a random new position
     *
     * @param array the array to manipulate
     */
    public static void shuffle(int... array) {
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);

            swap(array, i, index);
        }
    }

    /**
     * Moves every element of the specified array to a random new position
     *
     * @param array the array to manipulate
     */
    public static void shuffle(long... array) {
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);

            swap(array, i, index);
        }
    }

    /**
     * Moves every element of the specified array to a random new position
     *
     * @param array the array to manipulate
     */
    public static void shuffle(float... array) {
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);

            swap(array, i, index);
        }
    }

    /**
     * Moves every element of the specified array to a random new position
     *
     * @param array the array to manipulate
     */
    public static void shuffle(double... array) {
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);

            swap(array, i, index);
        }
    }

    /**
     * Moves every element of the specified array to a random new position
     *
     * @param array the array to manipulate
     */
    public static void shuffle(char... array) {
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);

            swap(array, i, index);
        }
    }

    /**
     * Moves every element of the specified array to a random new position
     *
     * @param array the array to manipulate
     */
    public static void shuffle(byte... array) {
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);

            swap(array, i, index);
        }
    }

    /**
     * Swaps the value of specified array from index {@code i} to {@code j}
     *
     * @param array the array to manipulate
     * @param i     the index of element to swap on {@code j}
     * @param j     the position of the second value
     */
    public static void swap(byte[] array, int i, int j) {
        byte temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    /**
     * Swaps the value of specified array from index {@code i} to {@code j}
     *
     * @param array the array to manipulate
     * @param i     the index of element to swap on {@code j}
     * @param j     the position of the second value
     */
    public static void swap(short[] array, int i, int j) {
        short temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    /**
     * Swaps the value of specified array from index {@code i} to {@code j}
     *
     * @param array the array to manipulate
     * @param i     the index of element to swap on {@code j}
     * @param j     the position of the second value
     */
    public static void swap(char[] array, int i, int j) {
        char temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    /**
     * Swaps the value of specified array from index {@code i} to {@code j}
     *
     * @param array the array to manipulate
     * @param i     the index of element to swap on {@code j}
     * @param j     the position of the second value
     */
    public static void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    /**
     * Swaps the value of specified array from index {@code i} to {@code j}
     *
     * @param array the array to manipulate
     * @param i     the index of element to swap on {@code j}
     * @param j     the position of the second value
     */
    public static void swap(long[] array, int i, int j) {
        long temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    /**
     * Swaps the value of specified array from index {@code i} to {@code j}
     *
     * @param array the array to manipulate
     * @param i     the index of element to swap on {@code j}
     * @param j     the position of the second value
     */
    public static void swap(float[] array, int i, int j) {
        float temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    /**
     * Swaps the value of specified array from index {@code i} to {@code j}
     *
     * @param array the array to manipulate
     * @param i     the index of element to swap on {@code j}
     * @param j     the position of the second value
     */
    public static void swap(double[] array, int i, int j) {
        double temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    /**
     * Swaps the value of specified array from index {@code i} to {@code j}
     *
     * @param array the array to manipulate
     * @param i     the index of element to swap on {@code j}
     * @param j     the position of the second value
     */
    public static void swap(Object[] array, int i, int j) {
        Object temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    public static int average(int... array) {
        return (int) (total(array) / array.length);
    }

    public static long average(long... array) {
        return (total(array) / array.length);
    }

    /**
     * Modifies the specified {@code array} by reversing the order of the
     * values.
     *
     * @param array the array to reverse.
     */
    public static void reverse(int... array) {
        for (int i = array.length - 1, j = 0; i >= j; i--, j++) {
            swap(array, i, j);
        }
    }

    /**
     * Modifies the specified {@code array} by reversing the order of the
     * values.
     *
     * @param array the array to reverse.
     */
    public static void reverse(long... array) {
        for (int i = array.length - 1, j = 0; i >= j; i--, j++) {
            swap(array, i, j);
        }
    }

    /**
     * Modifies the specified {@code array} by reversing the order of the
     * values.
     *
     * @param array the array to reverse.
     */
    public static void reverse(short... array) {
        for (int i = array.length - 1, j = 0; i >= j; i--, j++) {
            swap(array, i, j);
        }
    }

    /**
     * Modifies the specified {@code array} by reversing the order of the
     * values.
     *
     * @param array the array to reverse.
     */
    public static void reverse(char... array) {
        for (int i = array.length - 1, j = 0; i >= j; i--, j++) {
            swap(array, i, j);
        }
    }

    /**
     * Modifies the specified {@code array} by reversing the order of the
     * values.
     *
     * @param array the array to reverse.
     */
    public static void reverse(byte... array) {
        for (int i = array.length - 1, j = 0; i >= j; i--, j++) {
            swap(array, i, j);
        }
    }

    /**
     * Modifies the specified {@code array} by reversing the order of the
     * values.
     *
     * @param array the array to reverse.
     */
    public static void reverse(float... array) {
        for (int i = array.length - 1, j = 0; i >= j; i--, j++) {
            swap(array, i, j);
        }
    }

    /**
     * Modifies the specified {@code array} by reversing the order of the
     * values.
     *
     * @param array the array to reverse.
     */
    public static void reverse(double... array) {
        for (int i = array.length - 1, j = 0; i >= j; i--, j++) {
            swap(array, i, j);
        }
    }

    /**
     * Modifies the specified {@code array} by reversing the order of the
     * values.
     *
     * @param array the array to reverse.
     */
    public static void reverse(Object... array) {
        for (int i = array.length - 1, j = 0; i >= j; i--, j++) {
            swap(array, i, j);
        }
    }

    /**
     * Creates a {@link String} representation of the specified array passed.
     * Each element is converted to a {@link String} and separated by {@code ","}.
     * Returns null if items is null or empty
     *
     * @param array the array to convert
     * @param <T>   the generic type of {@link Collection}
     * @return the {@link String} representation of items,
     * or {@code ""} if array is null or empty
     */
    @SafeVarargs
    public static <T> String toString(T... array) {
        if (array == null || array.length == 0) {
            return null;
        }

        StringBuilder buffer = new StringBuilder(array.length * 12);
        buffer.append(array[0]);
        for (int i = 1; i < array.length; i++) {
            buffer.append(',');
            buffer.append(array[i]);
        }
        return buffer.toString();
    }

    /**
     * Creates a {@link String} representation of the specified array passed.
     * Each element is converted to a {@link String} and separated by {@code ","}.
     * Returns null if items is null or empty
     *
     * @param array the array to convert
     * @return the {@link String} representation of items,
     * or {@code ""} if array is null or empty
     */
    public static String toString(int... array) {
        if (array == null || array.length == 0) {
            return null;
        }

        StringBuilder buffer = new StringBuilder(array.length * 12);
        buffer.append(array[0]);
        for (int i = 1; i < array.length; i++) {
            buffer.append(',');
            buffer.append(array[i]);
        }
        return buffer.toString();
    }

    /**
     * Returns list containing only the specified object
     *
     * @param object the object to be stored in the returned list
     */
    public static <E> ArrayList<E> singletonList(E object) {
        ArrayList<E> list = new ArrayList<>(1);
        list.add(object);

        return list;
    }

    /**
     * Returns true if the specified array is null or empty
     *
     * @param array the array to be examined
     */
    public static boolean isEmpty(byte[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Returns true if the specified array is null or empty
     *
     * @param array the array to be examined
     */
    public static boolean isEmpty(char[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Returns true if the specified array is null or empty
     *
     * @param array the array to be examined
     */
    public static boolean isEmpty(short[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Returns true if the specified array is null or empty
     *
     * @param array the array to be examined
     */
    public static boolean isEmpty(int[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Returns true if the specified array is null or empty
     *
     * @param array the array to be examined
     */
    public static boolean isEmpty(long[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Returns true if the specified array is null or empty
     *
     * @param array the array to be examined
     */
    public static boolean isEmpty(float[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Returns true if the specified array is null or empty
     *
     * @param array the array to be examined
     */
    public static boolean isEmpty(double[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Returns true if the specified array is null or empty
     *
     * @param array the array to be examined
     */
    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Returns true if the specified collection is null or empty
     *
     * @param collection the collection to be examined
     */
    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }
}
