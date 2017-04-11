package ru.euphoria.messenger.api.model;

import java.io.Serializable;
import java.util.ArrayList;

import ru.euphoria.messenger.json.JsonArray;
import ru.euphoria.messenger.json.JsonObject;

/**
 * User object describes a user profile.
 *
 * @since 1.1
 */
public class VKUser extends VKModel implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String DEFAULT_FIELDS = "photo_50, photo_100, photo_200, status, screen_name, online, online_mobile, last_seen, verified, sex";

    /** User object with empty name; */
    public static final VKUser EMPTY = new VKUser() {
        @Override public String toString() {
            return "";
        }
    };

    /** User ID, positive number. */
    public int id;

    /** First name of user. */
    public String first_name;

    /** Last name of user. */
    public String last_name;

    /** User page's screen name (sub domain) */
    public String screen_name;

    /** Information whether the user is online. */
    public boolean online;

    /** If user utilizes a mobile application or site mobile version. */
    public boolean online_mobile;

    /** ID of mobile application, if user is online */
    public int online_app;

    /** URL of default square photo of the user with 50 pixels in width. */
    public String photo_50;

    /** URL of default square photo of the user with 100 pixels in width. */
    public String photo_100;

    /** URL of default square photo of the user with 200 pixels in width. */
    public String photo_200;

    /** Status of user */
    public String status;

    /** Last visit date in unix time */
    public long last_seen;

    /** True if the profile is verified, false if not */
    public boolean verified;

    /** Not null if user banned or deleted */
    public String deactivated;

    /** User sex (1 — female, 2 — male, 0 — not specified) */
    public int sex;

    public static ArrayList<VKUser> parse(JsonArray array) {
        ArrayList<VKUser> users = new ArrayList<>(array.length());
        for (Object value : array) {
            users.add(new VKUser((JsonObject) value));
        }

        return users;
    }

    /**
     * Creates a new User model without fields
     */
    public VKUser() {
        // empty
    }

    /**
     * Creates a new user model with fields from json source
     *
     * @param source the json source to parse
     */
    public VKUser(JsonObject source) {
        this.id = source.optInt("id");
        this.first_name = source.optString("first_name", "DELETED");
        this.last_name = source.optString("last_name", "DELETED");
        this.photo_50 = source.optString("photo_50");
        this.photo_100 = source.optString("photo_100");
        this.photo_200 = source.optString("photo_200");
        this.screen_name = source.optString("screen_name");
        this.online = source.optInt("online") == 1;
        this.status = source.optString("status");
        this.online_mobile = source.optInt("online_mobile") == 1;
        this.verified = source.optInt("verified") == 1;
        this.deactivated = source.optString("deactivated");
        this.sex = source.optInt("sex");
        if (this.online_mobile) {
            this.online_app = source.optInt("online_app");
        }
        JsonObject lastSeen = source.optJsonObject("last_seen");
        if (lastSeen != null) {
            this.last_seen = lastSeen.optLong("time");
        }
    }

    @Override
    public String toString() {
        return first_name + " " + last_name;
    }

    public static class Sex {
        public static final int NONE = 0;
        public static final int FEMALE = 1;
        public static final int MALE = 2;
    }
}

