package ru.euphoria.messenger.api.model;

import java.io.Serializable;
import java.util.Arrays;

import ru.euphoria.messenger.json.JsonObject;

/**
 * Describes a group object from VK.
 */
public class VKGroup extends VKModel implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Group ID, positive number */
    public int id;

    /** Community name */
    public String name;

    /** Screen name of the community page (e.g. apiclub or club1). */
    public String screen_name;

    /** Whether the community is closed */
    public int is_closed;

    /** Whether a user is the community manager */
    public boolean is_admin;

    /** Rights of the user, see {@link AdminLevel} */
    public int admin_level;

    /** Whether a user is a community member */
    public boolean is_member;

    /** Community type, see {@link Type} */
    public int type;

    /** True if group is verified */
    public boolean verified;

    /** URL of the 50px-wide community logo. */
    public String photo_50;

    /** URL of the 100px-wide community logo. */
    public String photo_100;

    /** URL of the 200px-wide community logo. */
    public String photo_200;

    /** Community description text */
    public String description;

    /** Number of community members */
    public long members_count;

    /** Group status. Returns a string with status text that is on the group page below its name. */
    public String status;

    public VKGroup() {
        // empty
    }

    /**
     * Creates a new group model with fields from json source.
     *
     * @param source the json source to parse
     */
    public VKGroup(JsonObject source) {
        this.id = source.optInt("id");
        this.name = source.optString("name");
        this.screen_name = source.optString("screen_name");
        this.is_closed = source.optInt("is_closed");
        this.is_admin = source.optLong("is_admin") == 1;
        this.is_member = source.optLong("is_member") == 1;
        this.verified = source.optInt("verified") == 1;
        this.admin_level = source.optInt("admin_level");

        String type = source.optString("type", "group");
        switch (type) {
            case "group":
                this.type = Type.GROUP;
                break;
            case "page":
                this.type = Type.PAGE;
                break;
            case "event":
                this.type = Type.EVENT;
                break;
        }

        this.photo_50 = source.optString("photo_50");
        this.photo_100 = source.optString("photo_100");
        this.photo_200 = source.optString("photo_200");

        this.description = source.optString("description");
        this.status = source.optString("status");
        this.members_count = source.optLong("members_count");
    }

    @Override
    public String toString() {
        return name;
    }

    public static int toGroupId(int id) {
        return (id < 0) ? Math.abs(id) : (1_000_000_000 - id);
    }

    public static boolean isGroupId(int id) {
        return id < 0;
    }


    /**
     * Access level to manage community.
     */
    public static class AdminLevel {
        public final static int MODERATOR = 1;
        public final static int EDITOR = 2;
        public final static int ADMIN = 3;

        private AdminLevel() {
        }
    }

    /**
     * Privacy status of the group.
     */
    public static class Status {
        public final static int OPEN = 0;
        public final static int CLOSED = 1;
        public final static int PRIVATE = 2;

        private Status() {
        }
    }

    /**
     * Types of communities.
     */
    public static class Type {
        public final static int GROUP = 0;
        public final static int PAGE = 1;
        public final static int EVENT = 2;

        private Type() {
        }
    }


}