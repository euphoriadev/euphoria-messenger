package ru.euphoria.messenger.api.model;

import java.io.Serializable;

import ru.euphoria.messenger.json.JsonObject;

/**
 * Object describes attached link
 */
public class VKLink extends VKModel implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Link URL. */
    public String url;

    /** Link title. */
    public String title;

    /** Link caption (if any). */
    public String caption;

    /** Link description. */
    public String description;

    /** ID of the wiki page with the content for preview ("owner_id_page_id"). */
    public String preview_page;

    /** URL of the page for preview. */
    public String preview_url;

    public VKPhoto photo;

    public VKLink(JsonObject source) {
        this.url = source.optString("url");
        this.title = source.optString("title");
        this.caption = source.optString("caption");
        this.preview_url = source.optString("preview_url");

        JsonObject linkPhoto = source.optJsonObject("photo");
        if (linkPhoto != null) {
            this.photo = new VKPhoto(linkPhoto);
        }
    }
}
