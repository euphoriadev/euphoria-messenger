package ru.euphoria.messenger.json;

/**
 * A structure, name or value type in a JSON-encoded string.
 */
public enum JsonToken {
    /** The opening of a JSON array. */
    BEGIN_ARRAY,

    /** The closing of a JSON array. */
    END_ARRAY,

    /** The opening of a JSON object. */
    BEGIN_OBJECT,

    /** The closing of a JSON object. */
    END_OBJECT,

    /** A JSON property name. */
    NAME,

    /** A JSON string. */
    STRING,

    /** A JSON number represented in this API by a Java. */
    NUMBER,

    /** A JSON {@code true} or {@code false}. */
    BOOLEAN,

    /** A JSON {@code null}. */
    NULL,

    /** The end of the JSON stream. signal that the JSON-encoded value has no more
     * tokens */
    END_DOCUMENT
}