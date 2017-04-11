package ru.euphoria.messenger.api.method;

/**
 * Method setter for users
 */
public class UserMethodSetter extends MethodSetter {

    /**
     * Creates a new Method Setter
     *
     * @param name the vk method name, e.g. users.get
     */
    public UserMethodSetter(String name) {
        super(name);
    }

    /** Setters for users.getSubscriptions */

    /**
     * false — to return separate lists of users and communities (default)
     * true — to return a combined list of users and communities
     */
    public UserMethodSetter extended(boolean extended) {
        put("extended", extended);
        return this;
    }

    /** Setters for users.report */

    /**
     * Type of complaint:
     * porn – pornography
     * spam – spamming
     * insult – abusive behavior
     * advertisment – disruptive advertisements
     */
    public UserMethodSetter type(String type) {
        put("type", type);
        return this;
    }

    /**
     * Comment describing the complaint
     */
    public UserMethodSetter comment(String comment) {
        put("comment", comment);
        return this;
    }


    /** Setters for users.getNearby */

    /**
     * Geographic latitude of the place a user is located,
     * in degrees (from -90 to 90)
     */
    public UserMethodSetter latitude(float latitude) {
        put("latitude", latitude);
        return this;
    }

    /**
     * Geographic longitude of the place a user is located,
     * in degrees (from -90 to 90)
     */
    public UserMethodSetter longitude(float longitude) {
        put("longitude", longitude);
        return this;
    }

    /**
     * Current location accuracy in meters
     */
    public UserMethodSetter accuracy(int accuracy) {
        put("accuracy", accuracy);
        return this;
    }

    /**
     * Time when a user disappears from location search results, in seconds
     * Default 7 200
     */
    public UserMethodSetter timeout(int timeout) {
        put("timeout", timeout);
        return this;
    }

    /**
     * Search zone radius type (1 to 4)
     * <p/>
     * 1 – 300 m;
     * 2 – 2400 m;
     * 3 – 18 km;
     * 4 – 150 km.
     * <p/>
     * By default 1
     */
    public UserMethodSetter radius(int radius) {
        put("radius", radius);
        return this;
    }

}