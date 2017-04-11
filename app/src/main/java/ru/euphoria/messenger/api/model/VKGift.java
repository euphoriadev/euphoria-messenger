package ru.euphoria.messenger.api.model;

import java.io.Serializable;

import ru.euphoria.messenger.json.JsonObject;

public class VKGift extends VKModel implements Serializable {
    private static final long serialVersionUID = 1L;

    /** User id who sent the gift, or 0 if the sender is hidden */
    public int from_id;

    /** Gift ID */
    public long id;

    /** Text of the message attached to the gift */
    public String message;

    /** Time to send gift in unix time format */
    public long date;

    /** URL image gift size 48x48px; */
    public String thumb_48;

    /** URL image gift size 96x96px; */
    public String thumb_96;

    /** URL image gift size 256x256px; */
    public String thumb_256;

    /**
     * Creates a new gift with fields from json source
     *
     * @param source the json source to parse
     */
    public VKGift(JsonObject source) {
        this.id = source.optLong("id");
        this.from_id = source.optInt("from_id");
        this.date = source.optLong("date");

        if (source.has("gift")) {
            source = source.optJsonObject("gift");
        }

        this.thumb_48 = source.optString("thumb_48");
        this.thumb_96 = source.optString("thumb_96");
        this.thumb_256 = source.optString("thumb_256");
    }

}