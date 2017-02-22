package ru.euphoria.messenger.api.model;


import java.io.Serializable;

import ru.euphoria.messenger.json.JsonObject;

/**
 * Describes a application object from VK.
 */
public class VKApp extends VKModel implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Application ID. */
    public int id;

    /** Application title. */
    public String title;

    /** Application screen name (idXXXXXXX if screen name not selected). */
    public String screen_name;

    /** Application description. */
    public String description;

    /** Application type, see {@link Type} */
    public int type;

    /** Application author's page URL */
    public int author_id;

    /** Official community's ID. */
    public int author_group;

    /** Number of app members (installed count?). */
    public int members_count;

    /** Published date (in unix time). */
    public int published_date;

    /** Array of photo objects describing app screenshots (unused). */
    public String screenshots;

    /** Information whether the app is multi-language */
    public boolean international;

    /** Application ID in store */
    public int platform_id;

    /** URL of the app icon with 16 px in width. */
    public String icon_16;

    /** URL of the app icon with 50 px in width. */
    public String icon_50;

    /** URL of the app icon with 75 px in width. */
    public String icon_75;

    /** URL of the app icon with 100 px in width. */
    public String icon_100;

    /** URL of the app icon with 200 px in width. */
    public String icon_200;

    /** URL of the app icon with 256 px in width. */
    public String icon_256;

    public VKApp() {

    }

    /**
     * Creates a new application model with fields from json source.
     *
     * @param source the json source to parse
     */
    public VKApp(JsonObject source) {
        this.id = source.optInt("id");
        this.title = source.optString("title");
        this.description = source.optString("description");
        this.screen_name = source.optString("screen_name");
        this.author_id = source.optInt("author_id");
        this.author_group = source.optInt("author_group");
        this.members_count = source.optInt("members_count");
        this.published_date = source.optInt("published_date");
        this.international = source.optInt("international") == 1;
        this.platform_id = source.optInt("platform_id");

        String type = source.optString("type", "app");
        switch (type) {
            case "app":
                this.type = Type.APP;
                break;
            case "game":
                this.type = Type.GAME;
                break;
            case "site":
                this.type = Type.SITE;
                break;
            case "standalone":
                this.type = Type.STANDALONE;
                break;
        }

        this.icon_16 = source.optString("icon_16");
        this.icon_50 = source.optString("icon_50");
        this.icon_75 = source.optString("icon_75");
        this.icon_100 = source.optString("icon_100");
        this.icon_200 = source.optString("icon_200");
        this.icon_256 = source.optString("icon_256");
    }


    /**
     * Types of applications.
     */
    public static class Type {
        public final static int APP = 0;
        public final static int GAME = 1;
        public final static int SITE = 2;
        public final static int STANDALONE = 2;

        private Type() {
        }
    }
}