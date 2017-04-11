package ru.euphoria.messenger.util;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Static utils methods for array.
 * <p>
 * For powerful collection util - use {@link Collections}
 *
 * @author Igor Morozkin
 * @since 1.0
 */
public class ArrayUtil {
    /** The index for linear and binary search, if the value is not found */
    public static final int VALUE_NOT_FOUND = -1;

    // uses only static methods
    private ArrayUtil() {
    }

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
