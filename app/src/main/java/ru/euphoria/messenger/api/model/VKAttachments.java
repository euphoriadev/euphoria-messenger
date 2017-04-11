package ru.euphoria.messenger.api.model;

import java.util.ArrayList;

import ru.euphoria.messenger.json.JsonArray;
import ru.euphoria.messenger.json.JsonObject;

/**
 * Describes a attachment object from VK.
 */
public class VKAttachments {
    /** Attachment is a photo. */
    public static final String TYPE_PHOTO = "photo";

    /** Attachment is a video. */
    public static final String TYPE_VIDEO = "video";

    /** Attachment is an audio. */
    public static final String TYPE_AUDIO = "audio";

    /** Attachment is a document. */
    public static final String TYPE_DOC = "doc";

    /** Attachment is a wall post. */
    public static final String TYPE_POST = "wall";

    /** Attachment is a posted photo. */
    public static final String TYPE_POSTED_PHOTO = "posted_photo";

    /** Attachment is a link */
    public static final String TYPE_LINK = "link";

    /** Attachment is a note. */
    public static final String TYPE_NOTE = "note";

    /** Attachment is an application content. */
    public static final String TYPE_APP = "app";

    /** Attachment is a poll. */
    public static final String TYPE_POLL = "poll";

    /** Attachment is a WikiPage. */
    public static final String TYPE_WIKI_PAGE = "page";

    /** Attachment is a PhotoAlbum. */
    public static final String TYPE_ALBUM = "album";

    /** Attachment is a Sticker. */
    public static final String TYPE_STICKER = "sticker";

    /** Attachment is a Gift. */
    public static final String TYPE_GIFT = "gift";

    public static ArrayList<VKModel> parse(JsonArray array) {
        ArrayList<VKModel> attachments = new ArrayList<>(array.length());

        for (int i = 0; i < array.length(); i++) {
            JsonObject attach = array.optJsonObject(i);
            if (attach.has("attachment")) {
                attach = attach.optJsonObject("attachment");
            }

            String type = attach.optString("type");
            JsonObject object = attach.optJsonObject(type);

            switch (type) {
                case TYPE_PHOTO:
                    attachments.add(new VKPhoto(object));
                    break;
                case TYPE_AUDIO:
                    attachments.add(new VKAudio(object));
                    break;
                case TYPE_VIDEO:
                    attachments.add(new VKVideo(object));
                    break;
                case TYPE_DOC:
                    attachments.add(new VKDoc(object));
                    break;
                case TYPE_STICKER:
                    attachments.add(new VKSticker(object));
                    break;
                case TYPE_LINK:
                    attachments.add(new VKLink(object));
                    break;
                case TYPE_GIFT:
                    attachments.add(new VKGift(object));
                    break;
            }
        }

        return attachments;
    }
}
