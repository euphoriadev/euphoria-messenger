package ru.euphoria.messenger.io;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Contains a constant of the most important built-in encodings instances,
 * which are guaranteed to be supported by all Java and Android platform and Android.
 *
 * @author Igor Morozkin
 * @author Mike Bostock
 * @since 1.0
 */
public class Charsets {
    private Charsets() {
    }

    /**
     * ASCII: 7-bit, American Standard Code for Information Interchange.
     *
     * @see StandardCharsets#US_ASCII
     */
    public static final Charset ASCII = Charset.forName("ASCII");

    /**
     * UTF-8: 8-bit, Unicode Transformation Format.
     *
     * @see StandardCharsets#UTF_8
     */
    public static final Charset UTF_8 = Charset.forName("UTF-8");
}
