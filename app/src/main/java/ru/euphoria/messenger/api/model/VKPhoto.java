package ru.euphoria.messenger.api.model;

import java.io.Serializable;

import ru.euphoria.messenger.json.JsonObject;

/**
 * Describes a photo object from VK.
 */
public class VKPhoto extends VKModel implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Photo ID, positive number */
    public int id;

    /** Photo album ID. */
    public int album_id;

    /** ID of the user or community that owns the photo. */
    public int owner_id;

    /** Width (in pixels) of the original photo. */
    public int width;

    /** Height (in pixels) of the original photo. */
    public int height;

    /** Text describing the photo. */
    public String text;

    /** Date (in Unix time) the photo was added. */
    public long date;

    /** URL of image with maximum size 75x75px. */
    public String photo_75;

    /** URL of image with maximum size 130x130px. */
    public String photo_130;

    /** URL of image with maximum size 604x604px. */
    public String photo_604;

    /** URL of image with maximum size 807x807px. */
    public String photo_807;

    /** URL of image with maximum size 1280x1024px. */
    public String photo_1280;

    /** URL of image with maximum size 2560x2048px. */
    public String photo_2560;

    /** Information whether the current user liked the photo. */
    public boolean user_likes;

    /** Whether the current user can comment on the photo */
    public boolean can_comment;

    /** Number of likes on the photo. */
    public int likes;

    /** Number of comments on the photo. */
    public int comments;

    /** Number of tags on the photo. */
    public int tags;

    /** An access key using for get information about hidden objects. */
    public String access_key;

    public VKPhoto() {
        // empty
    }

    /**
     * Creates a new photo model with fields from json source
     *
     * @param source the json source to parse
     */
    public VKPhoto(JsonObject source) {
        this.id = source.optInt("id");
        this.owner_id = source.optInt("owner_id");
        this.album_id = source.optInt("album_id");
        this.date = source.optLong("date");
        this.width = source.optInt("width");
        this.height = source.optInt("height");
        this.text = source.optString("text");
        this.access_key = source.optString("access_key");
        this.can_comment = source.optInt("can_comment") == 1;

        JsonObject likes = source.optJsonObject("likes");
        if (likes != null) {
            this.likes = likes.optInt("count");
            this.user_likes = likes.optInt("user_likes") == 1;
        }
        JsonObject comments = source.optJsonObject("comments");
        if (comments != null) {
            this.comments = comments.optInt("count");
        }

        this.photo_75 = source.optString("photo_75");
        this.photo_130 = source.optString("photo_130");
        this.photo_604 = source.optString("photo_604");
        this.photo_807 = source.optString("photo_807");
        this.photo_1280 = source.optString("photo_1280");
        this.photo_2560 = source.optString("photo_2560");
    }
}
