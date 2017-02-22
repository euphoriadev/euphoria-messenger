package ru.euphoria.messenger.api.model;

import android.text.TextUtils;

import java.io.Serializable;

import ru.euphoria.messenger.json.JsonArray;
import ru.euphoria.messenger.json.JsonObject;

/**
 * Describes a document object from VK.
 */
public class VKDoc extends VKModel implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int TYPE_NONE = 0;
    public static final int TYPE_TEXT = 1;
    public static final int TYPE_ARCHIVE = 2;
    public static final int TYPE_GIF = 3;
    public static final int TYPE_IMAGE = 4;
    public static final int TYPE_AUDIO = 5;
    public static final int TYPE_VIDEO = 6;
    public static final int TYPE_BOOK = 7;
    public static final int TYPE_UNKNOWN = 8;

    /** Document ID. */
    public long id;

    /** ID of the user or group who uploaded the document. */
    public long owner_id;

    /** Document title. */
    public String title;

    /** Document size (in bytes). */
    public long size;

    /** Document extension. */
    public String ext;

    /** Document URL for downloading. */
    public String url;

    /** URL of the 100x75px image (if the file is graphical). */
    public String photo_100;

    /** URL of the 130x100px image (if the file is graphical). */
    public String photo_130;

    /** An access key using for get information about hidden objects. */
    public String access_key;

    /** The document type (audio, video, book) */
    public int type;

    /** Images for preview */
    public VKPhotoSizes photo_sizes;

    /**
     * Creates a new document model with fields from json source.
     *
     * @param source the json source to parse
     */
    public VKDoc(JsonObject source) {
        this.id = source.optLong("id");
        this.owner_id = source.optLong("owner_id");
        this.title = source.optString("title");
        this.url = source.optString("url");
        this.size = source.optLong("size");
        this.type = source.optInt("type");
        this.ext = source.optString("ext");
        this.photo_130 = source.optString("photo_130");
        this.photo_100 = source.optString("photo_100");
        this.access_key = source.optString("access_key");
        this.type = source.optInt("type");

        JsonObject preview = source.optJsonObject("preview");
        if (preview != null && preview.has("photo")) {
            JsonArray sizes = preview.optJsonObject("photo")
                    .optJsonArray("sizes");

            photo_sizes = new VKPhotoSizes(sizes);
        }
    }

    public String toAttachmentString() {
        StringBuilder result = new StringBuilder("doc").append(owner_id).append('_').append(id);
        if (!TextUtils.isEmpty(access_key)) {
            result.append('_');
            result.append(access_key);
        }
        return result.toString();
    }

    @Override
    public String toString() {
        return title;
    }
}