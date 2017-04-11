package ru.euphoria.messenger.api.model;

import java.io.Serializable;

import ru.euphoria.messenger.json.JsonObject;

/**
 * Describes a sticker object from VK
 */

public class VKSticker extends VKModel implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Sticker ID. */
    public int id;

    /** Set ID. */
    public int product_id;

    /** URL of the image with height of 64 px. */
    public String photo_64;

    /** URL of the image with height of 128 px. */
    public String photo_128;

    /** URL of the image with height of 256 px. */
    public String photo_256;

    /** URL of the image with height of 352 px. */
    public String photo_352;

    /** Height in px. */
    public int width;

    /** Height in px. */
    public int height;

    /**
     * Creates a new sticker model with fields from json source.
     *
     * @param source the json source to parse
     */
    public VKSticker(JsonObject source) {
        this.id = source.optInt("id");
        this.product_id = source.optInt("product_id");
        this.photo_64 = source.optString("photo_64");
        this.photo_128 = source.optString("photo_128");
        this.photo_256 = source.optString("photo_256");
        this.photo_352 = source.optString("photo_352");
        this.width = source.optInt("width");
        this.height = source.optInt("height");
    }
}
