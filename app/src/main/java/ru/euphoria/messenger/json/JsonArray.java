package ru.euphoria.messenger.json;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * A dense indexed sequence of values. Values may be any mix of
 * {@link JsonObject JSONObjects}, other {@link JsonArray JSONArrays}, Strings,
 * Booleans, Integers, Longs, Doubles, {@code null} or {@link JsonObject#NULL}.
 * Values may not be {@link Double#isNaN() NaNs}, {@link Double#isInfinite()
 * infinities}, or of any type not listed here.
 * <p>
 * <p>{@code JsonArray} has the same type coercion behavior and
 * optional/mandatory accessors as {@link JsonObject}. See that class'
 * documentation for details.
 * <p>
 * <p><strong>Warning:</strong> this class represents null in two incompatible
 * ways: the standard Java {@code null} reference, and the sentinel value {@link
 * JsonObject#NULL}. In particular, {@code get} fails if the requested index
 * holds the null reference, but succeeds if it holds {@code JsonObject.NULL}.
 * <p>
 * <p>Instances of this class are not thread safe. Although this class is
 * nonfinal, it was not designed for inheritance and should not be subclassed.
 * In particular, self-use by overridable methods is not specified. See
 * <i>Effective Java</i> Item 17, "Design and Document or inheritance or else
 * prohibit it" for further information.
 */
public class JsonArray implements Iterable<Object>, Serializable {
    private final List<Object> values;

    /**
     * Creates a {@code JsonArray} with no values.
     */
    public JsonArray() {
        values = new ArrayList<>();
    }

    /**
     * Creates a new {@code JsonArray} by copying all values from the given
     * collection.
     *
     * @param from a collection whose values are of supported types.
     *             Unsupported values are not permitted and will yield an array in an
     *             inconsistent state.
     */
    /* Accept a raw type for API compatibility */
    public JsonArray(Collection from) {
        this();
        if (from != null) {
            for (Object value : from) {
                put(JsonObject.wrap(value));
            }
        }
    }

    /**
     * Creates a new {@code JsonArray} with values from the next array in the
     * parser.
     *
     * @param from a tokener whose nextValue() method will yield a
     *             {@code JsonArray}.
     * @throws JsonException if the parse fails or doesn't yield a
     *                       {@code JsonArray}.
     */
    public JsonArray(JsonParser from) throws JsonException {
        /*
         * Getting the parser to populate this could get tricky. Instead, just
         * parse to temporary JsonArray and then steal the data from that.
         */
        Object object = from.nextValue();
        if (object instanceof JsonArray) {
            values = ((JsonArray) object).values;
        } else {
            throw Json.typeMismatch(object, "JsonArray");
        }
    }

    /**
     * Creates a new {@code JsonArray} with values from the JSON string.
     *
     * @param json a JSON-encoded string containing an array.
     * @throws JsonException if the parse fails or doesn't yield a {@code
     *                       JsonArray}.
     */
    public JsonArray(String json) throws JsonException {
        this(new JsonParser(json));
    }

    /**
     * Creates a new {@code JsonArray} with values from the given primitive array.
     */
    public JsonArray(Object array) throws JsonException {
        if (!array.getClass().isArray()) {
            throw new JsonException("Not a primitive array: " + array.getClass());
        }
        final int length = Array.getLength(array);
        values = new ArrayList<>(length);
        for (int i = 0; i < length; ++i) {
            put(JsonObject.wrap(Array.get(array, i)));
        }
    }

    /**
     * Returns the number of values in this array.
     */
    public int length() {
        return values.size();
    }

    /**
     * Appends {@code value} to the end of this array.
     *
     * @return this array.
     */
    public JsonArray put(boolean value) {
        values.add(value);
        return this;
    }

    /**
     * Appends {@code value} to the end of this array.
     *
     * @param value a finite value. May not be {@link Double#isNaN() NaNs} or
     *              {@link Double#isInfinite() infinities}.
     * @return this array.
     */
    public JsonArray put(double value) throws JsonException {
        values.add(Json.checkDouble(value));
        return this;
    }

    /**
     * Appends {@code value} to the end of this array.
     *
     * @return this array.
     */
    public JsonArray put(int value) {
        values.add(value);
        return this;
    }

    /**
     * Appends {@code value} to the end of this array.
     *
     * @return this array.
     */
    public JsonArray put(long value) {
        values.add(value);
        return this;
    }

    /**
     * Appends {@code value} to the end of this array.
     *
     * @param value a {@link JsonObject}, {@link JsonArray}, String, Boolean,
     *              Integer, Long, Double, {@link JsonObject#NULL}, or {@code null}. May
     *              not be {@link Double#isNaN() NaNs} or {@link Double#isInfinite()
     *              infinities}. Unsupported values are not permitted and will cause the
     *              array to be in an inconsistent state.
     * @return this array.
     */
    public JsonArray put(Object value) {
        values.add(value);
        return this;
    }

    /**
     * Same as {@link #put}, with added validity checks.
     */
    void checkedPut(Object value) throws JsonException {
        if (value instanceof Number) {
            Json.checkDouble(((Number) value).doubleValue());
        }

        put(value);
    }

    /**
     * Sets the value at {@code index} to {@code value}, null padding this array
     * to the required length if necessary. If a value already exists at {@code
     * index}, it will be replaced.
     *
     * @return this array.
     */
    public JsonArray put(int index, boolean value) throws JsonException {
        return put(index, (Boolean) value);
    }

    /**
     * Sets the value at {@code index} to {@code value}, null padding this array
     * to the required length if necessary. If a value already exists at {@code
     * index}, it will be replaced.
     *
     * @param value a finite value. May not be {@link Double#isNaN() NaNs} or
     *              {@link Double#isInfinite() infinities}.
     * @return this array.
     */
    public JsonArray put(int index, double value) throws JsonException {
        return put(index, (Double) value);
    }

    /**
     * Sets the value at {@code index} to {@code value}, null padding this array
     * to the required length if necessary. If a value already exists at {@code
     * index}, it will be replaced.
     *
     * @return this array.
     */
    public JsonArray put(int index, int value) throws JsonException {
        return put(index, (Integer) value);
    }

    /**
     * Sets the value at {@code index} to {@code value}, null padding this array
     * to the required length if necessary. If a value already exists at {@code
     * index}, it will be replaced.
     *
     * @return this array.
     */
    public JsonArray put(int index, long value) throws JsonException {
        return put(index, (Long) value);
    }

    /**
     * Sets the value at {@code index} to {@code value}, null padding this array
     * to the required length if necessary. If a value already exists at {@code
     * index}, it will be replaced.
     *
     * @param value a {@link JsonObject}, {@link JsonArray}, String, Boolean,
     *              Integer, Long, Double, {@link JsonObject#NULL}, or {@code null}. May
     *              not be {@link Double#isNaN() NaNs} or {@link Double#isInfinite()
     *              infinities}.
     * @return this array.
     */
    public JsonArray put(int index, Object value) throws JsonException {
        if (value instanceof Number) {
            // deviate from the original by checking all Numbers, not just floats & doubles
            Json.checkDouble(((Number) value).doubleValue());
        }
        while (values.size() <= index) {
            values.add(null);
        }
        values.set(index, value);
        return this;
    }

    /**
     * Returns true if this array has no value at {@code index}, or if its value
     * is the {@code null} reference or {@link JsonObject#NULL}.
     */
    public boolean isNull(int index) {
        Object value = opt(index);
        return value == null || value == JsonObject.NULL;
    }

    /**
     * Returns the value at {@code index}.
     *
     * @throws JsonException if this array has no value at {@code index}, or if
     *                       that value is the {@code null} reference. This method returns
     *                       normally if the value is {@code JsonObject#NULL}.
     */
    public Object get(int index) throws JsonException {
        try {
            Object value = values.get(index);
            if (value == null) {
                throw new JsonException("Value at " + index + " is null.");
            }
            return value;
        } catch (IndexOutOfBoundsException e) {
            throw new JsonException("Index " + index + " out of range [0.." + values.size() + ")");
        }
    }

    /**
     * Returns the value at {@code index}, or null if the array has no value
     * at {@code index}.
     */
    public Object opt(int index) {
        if (index < 0 || index >= values.size()) {
            return null;
        }
        return values.get(index);
    }

    /**
     * Removes and returns the value at {@code index}, or null if the array has no value
     * at {@code index}.
     */
    public Object remove(int index) {
        if (index < 0 || index >= values.size()) {
            return null;
        }
        return values.remove(index);
    }

    /**
     * Returns the value at {@code index} if it exists and is a boolean or can
     * be coerced to a boolean.
     *
     * @throws JsonException if the value at {@code index} doesn't exist or
     *                       cannot be coerced to a boolean.
     */
    public boolean getBoolean(int index) throws JsonException {
        Object object = get(index);
        Boolean result = Json.toBoolean(object);
        if (result == null) {
            throw Json.typeMismatch(index, object, "boolean");
        }
        return result;
    }

    /**
     * Returns the value at {@code index} if it exists and is a boolean or can
     * be coerced to a boolean. Returns false otherwise.
     */
    public boolean optBoolean(int index) {
        return optBoolean(index, false);
    }

    /**
     * Returns the value at {@code index} if it exists and is a boolean or can
     * be coerced to a boolean. Returns {@code fallback} otherwise.
     */
    public boolean optBoolean(int index, boolean fallback) {
        Object object = opt(index);
        Boolean result = Json.toBoolean(object);
        return result != null ? result : fallback;
    }

    /**
     * Returns the value at {@code index} if it exists and is a double or can
     * be coerced to a double.
     *
     * @throws JsonException if the value at {@code index} doesn't exist or
     *                       cannot be coerced to a double.
     */
    public double getDouble(int index) throws JsonException {
        Object object = get(index);
        Double result = Json.toDouble(object);
        if (result == null) {
            throw Json.typeMismatch(index, object, "double");
        }
        return result;
    }

    /**
     * Returns the value at {@code index} if it exists and is a double or can
     * be coerced to a double. Returns {@code NaN} otherwise.
     */
    public double optDouble(int index) {
        return optDouble(index, Double.NaN);
    }

    /**
     * Returns the value at {@code index} if it exists and is a double or can
     * be coerced to a double. Returns {@code fallback} otherwise.
     */
    public double optDouble(int index, double fallback) {
        Object object = opt(index);
        Double result = Json.toDouble(object);
        return result != null ? result : fallback;
    }

    /**
     * Returns the value at {@code index} if it exists and is an int or
     * can be coerced to an int.
     *
     * @throws JsonException if the value at {@code index} doesn't exist or
     *                       cannot be coerced to a int.
     */
    public int getInt(int index) throws JsonException {
        Object object = get(index);
        Integer result = Json.toInteger(object);
        if (result == null) {
            throw Json.typeMismatch(index, object, "int");
        }
        return result;
    }

    /**
     * Returns the value at {@code index} if it exists and is an int or
     * can be coerced to an int. Returns 0 otherwise.
     */
    public int optInt(int index) {
        return optInt(index, 0);
    }

    /**
     * Returns the value at {@code index} if it exists and is an int or
     * can be coerced to an int. Returns {@code fallback} otherwise.
     */
    public int optInt(int index, int fallback) {
        Object object = opt(index);
        Integer result = Json.toInteger(object);
        return result != null ? result : fallback;
    }

    /**
     * Returns the value at {@code index} if it exists and is a long or
     * can be coerced to a long.
     *
     * @throws JsonException if the value at {@code index} doesn't exist or
     *                       cannot be coerced to a long.
     */
    public long getLong(int index) throws JsonException {
        Object object = get(index);
        Long result = Json.toLong(object);
        if (result == null) {
            throw Json.typeMismatch(index, object, "long");
        }
        return result;
    }

    /**
     * Returns the value at {@code index} if it exists and is a long or
     * can be coerced to a long. Returns 0 otherwise.
     */
    public long optLong(int index) {
        return optLong(index, 0L);
    }

    /**
     * Returns the value at {@code index} if it exists and is a long or
     * can be coerced to a long. Returns {@code fallback} otherwise.
     */
    public long optLong(int index, long fallback) {
        Object object = opt(index);
        Long result = Json.toLong(object);
        return result != null ? result : fallback;
    }

    /**
     * Returns the value at {@code index} if it exists, coercing it if
     * necessary.
     *
     * @throws JsonException if no such value exists.
     */
    public String getString(int index) throws JsonException {
        Object object = get(index);
        String result = Json.toString(object);
        if (result == null) {
            throw Json.typeMismatch(index, object, "String");
        }
        return result;
    }

    /**
     * Returns the value at {@code index} if it exists, coercing it if
     * necessary. Returns the empty string if no such value exists.
     */
    public String optString(int index) {
        return optString(index, "");
    }

    /**
     * Returns the value at {@code index} if it exists, coercing it if
     * necessary. Returns {@code fallback} if no such value exists.
     */
    public String optString(int index, String fallback) {
        Object object = opt(index);
        String result = Json.toString(object);
        return result != null ? result : fallback;
    }

    /**
     * Returns the value at {@code index} if it exists and is a {@code
     * JsonArray}.
     *
     * @throws JsonException if the value doesn't exist or is not a {@code
     *                       JsonArray}.
     */
    public JsonArray getJsonArray(int index) throws JsonException {
        Object object = get(index);
        if (object instanceof JsonArray) {
            return (JsonArray) object;
        } else {
            throw Json.typeMismatch(index, object, "JsonArray");
        }
    }

    /**
     * Returns the value at {@code index} if it exists and is a {@code
     * JsonArray}. Returns null otherwise.
     */
    public JsonArray optJsonArray(int index) {
        Object object = opt(index);
        return object instanceof JsonArray ? (JsonArray) object : null;
    }

    /**
     * Returns the value at {@code index} if it exists and is a {@code
     * JsonObject}.
     *
     * @throws JsonException if the value doesn't exist or is not a {@code
     *                       JsonObject}.
     */
    public JsonObject getJsonObject(int index) throws JsonException {
        Object object = get(index);
        if (object instanceof JsonObject) {
            return (JsonObject) object;
        } else {
            throw Json.typeMismatch(index, object, "JsonObject");
        }
    }

    /**
     * Returns the value at {@code index} if it exists and is a {@code
     * JsonObject}. Returns null otherwise.
     */
    public JsonObject optJsonObject(int index) {
        Object object = opt(index);
        return object instanceof JsonObject ? (JsonObject) object : null;
    }

    /**
     * Returns a new object whose values are the values in this array, and whose
     * names are the values in {@code names}. Names and values are paired up by
     * index from 0 through to the shorter array's length. Names that are not
     * strings will be coerced to strings. This method returns null if either
     * array is empty.
     */
    public JsonObject toJsonObject(JsonArray names) throws JsonException {
        JsonObject result = new JsonObject();
        int length = Math.min(names.length(), values.size());
        if (length == 0) {
            return null;
        }
        for (int i = 0; i < length; i++) {
            String name = Json.toString(names.opt(i));
            result.put(name, opt(i));
        }
        return result;
    }

    /**
     * Returns a new string by alternating this array's values with {@code
     * separator}. This array's string values are quoted and have their special
     * characters escaped. For example, the array containing the strings '12"
     * pizza', 'taco' and 'soda' joined on '+' returns this:
     * <pre>"12\" pizza"+"taco"+"soda"</pre>
     */
    public String join(String separator) throws JsonException {
        JsonStringer stringer = new JsonStringer();
        stringer.open(JsonStringer.Scope.NULL, "");
        for (int i = 0, size = values.size(); i < size; i++) {
            if (i > 0) {
                stringer.out.append(separator);
            }
            stringer.value(values.get(i));
        }
        stringer.close(JsonStringer.Scope.NULL, JsonStringer.Scope.NULL, "");
        return stringer.out.toString();
    }

    /**
     * Encodes this array as a compact JSON string, such as:
     * <pre>[94043,90210]</pre>
     */
    @Override
    public String toString() {
        try {
            JsonStringer stringer = new JsonStringer();
            writeTo(stringer);
            return stringer.toString();
        } catch (JsonException e) {
            return null;
        }
    }

    /**
     * Encodes this array as a human readable JSON string for debugging, such
     * as:
     * <pre>
     * [
     *     94043,
     *     90210
     * ]</pre>
     *
     * @param indentSpaces the number of spaces to indent for each level of
     *                     nesting.
     */
    public String toString(int indentSpaces) throws JsonException {
        JsonStringer stringer = new JsonStringer(indentSpaces);
        writeTo(stringer);
        return stringer.toString();
    }

    void writeTo(JsonStringer stringer) throws JsonException {
        stringer.array();
        for (Object value : values) {
            stringer.value(value);
        }
        stringer.endArray();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof JsonArray && ((JsonArray) o).values.equals(values);
    }

    @Override
    public int hashCode() {
        // diverge from the original, which doesn't implement hashCode
        return values.hashCode();
    }

    @Override
    public Iterator<Object> iterator() {
        return values.iterator();
    }
}
