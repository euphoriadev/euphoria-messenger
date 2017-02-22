package ru.euphoria.messenger.api.model;

import android.text.TextUtils;

import java.io.Serializable;

import ru.euphoria.messenger.json.JsonObject;

/**
 * A video object describes an video file.
 */
public class VKVideo extends VKModel implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Video ID. */
    public int id;

    /** Video owner ID. */
    public int owner_id;

    /** Video album ID. */
    public int album_id;

    /** Video title. */
    public String title;

    /** Text describing video. */
    public String description;

    /** Duration of the video in seconds. */
    public int duration;

    /** String with video+vid key. */
    public String link;

    /** Date when the video was added, as unix time. */
    public long date;

    /** Number of views of the video. */
    public int views;

    /**
     * URL of the page with a player that can be used to play a video in the browser.
     * Flash and HTML5 video players are supported; the player is always zoomed to fit
     * the window size.
     */
    public String player;

    /** URL of the video cover image with the size of 130x98px. */
    public String photo_130;

    /** URL of the video cover image with the size of 320x240px. */
    public String photo_320;

    /** URL of the video cover image with the size of 640x480px (if available). */
    public String photo_640;

    /** An access key using for get information about hidden objects. */
    public String access_key;

    /** Number of comments on the video. */
    public int comments;

    /** Whether the current user can comment on the video */
    public boolean can_comment;

    /** Whether the current user can re-post this video */
    public boolean can_repost;

    /** Information whether the current user liked the video. */
    public boolean user_likes;

    /** Information whether the the video should be repeated. */
    public boolean repeat;

    /** Number of likes on the video. */
    public int likes;

    /** Privacy to view of this video. */
    public int privacy_view;

    /** Privacy to comment of this video. */
    public int privacy_comment;

    /** URL of video with height of 240 pixels. Returns only if you use direct auth. */
    public String mp4_240;

    /** URL of video with height of 360 pixels. Returns only if you use direct auth. */
    public String mp4_360;

    /** URL of video with height of 480 pixels. Returns only if you use direct auth. */
    public String mp4_480;

    /** URL of video with height of 720 pixels. Returns only if you use direct auth. */
    public String mp4_720;

    /** URL of video with height of 1080 pixels. Returns only if you use direct auth. */
    public String mp4_1080;

    /** URL of the external video link. */
    public String external;

    /**
     * Creates a new video model with fields from json source
     *
     * @param source the json source to parse
     */
	public VKVideo(JsonObject source) {
        this.id = source.optInt("id");
        this.owner_id = source.optInt("owner_id");
        this.title = source.optString("title");
        this.description = source.optString("description");
        this.duration = source.optInt("duration");
        this.link = source.optString("link");
        this.date = source.optLong("date");
        this.views = source.optInt("views");
        this.comments = source.optInt("comments");
        this.player = source.optString("player");
        this.access_key = source.optString("access_key");
        this.album_id = source.optInt("album_id");

        this.photo_130 = source.optString("photo_130");
        this.photo_320 = source.optString("photo_320");
        this.photo_640 = source.optString("photo_640");

        JsonObject likes = source.optJsonObject("likes");
        if(likes != null) {
            this.likes = likes.optInt("count");
            this.user_likes = likes.optInt("user_likes") == 1;
        }
        this.can_comment = source.optInt("can_comment") == 1;
        this.can_repost = source.optInt("can_repost") == 1;
        this.repeat = source.optInt("repeat") == 1;

        JsonObject files = source.optJsonObject("files");
        if (files != null) {
            this.mp4_240 = files.optString("mp4_240");
            this.mp4_360 = files.optString("mp4_360");
            this.mp4_480 = files.optString("mp4_480");
            this.mp4_720 = files.optString("mp4_720");
            this.mp4_1080 = files.optString("mp4_1080");
            this.external = files.optString("external");
        }
	}

    public CharSequence toAttachmentString() {
        StringBuilder result = new StringBuilder("video").append(owner_id).append('_').append(id);
        if (!TextUtils.isEmpty(access_key)) {
            result.append('_');
            result.append(access_key);
        }
        return result;
    }

    @Override
    public String toString() {
        return title;
    }
}
