package ru.euphoria.messenger.api.method;

import ru.euphoria.messenger.util.ArrayUtil;

/**
 * Method setter for apps
 */
public class AppMethodSetter extends MethodSetter {

    /**
     * Creates a new Method Setter
     *
     * @param name the vk method name, e.g. users.get
     */
    public AppMethodSetter(String name) {
        super(name);
    }


    /** Setters for apps.get */

    /**
     * Application ID
     */
    public AppMethodSetter appId(int id) {
        put("app_id", id);
        return this;
    }

    /**
     * List of application ID. The max number of elements allowed is 100
     */
    public AppMethodSetter appIds(int... ids) {
        put("app_ids", ArrayUtil.toString(ids));
        return this;
    }

    /**
     * 1 — to return additional fields screenshots.
     * 0 — not to return additional fields (default).
     */
    public AppMethodSetter extended(boolean value) {
        put("extended", value);
        return this;
    }

    /**
     * 1 — to return additional fields friends,
     * profile (access token is required).
     * 0 — not to return additional fields (default).
     */
    public AppMethodSetter returnFriends(boolean friends) {
        put("return_friends", friends);
        return this;
    }

}