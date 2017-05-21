package ru.euphoria.messenger.api;


import android.support.annotation.Nullable;

import java.io.File;

import ru.euphoria.messenger.common.AppGlobal;
import ru.euphoria.messenger.io.FileStreams;
import ru.euphoria.messenger.json.JsonObject;

/**
 * Account config it store the necessary data to run the query on behalf of user
 * such as the token, email, api id and user id
 */
public class UserConfig {
    public static final int EUPHORIA_ID = Identifiers.EUPHORIA;

    public static final String ACCESS_TOKEN = "access_token";
    public static final String USER_ID = "user_id";
    public static final String EMAIL = "email";
    public static final String API_ID = "api_id";

    /** String token for use in request parameters */
    public String accessToken;

    /** String current email of user */
    public String email;

    /** Current user id for this token */
    public int userId;

    /** Api id of standalone vk app, used only by authorization */
    public int apiId;

    /**
     * Creates a new user config with params
     *
     * @param accessToken the access token of current user
     * @param email       the email of current user (not necessarily)
     * @param userId      the user id of current user
     */
    public UserConfig(String accessToken, @Nullable String email, int userId, int apiId) {
        this.accessToken = accessToken;
        this.email = email;
        this.userId = userId;
        this.apiId = apiId;
    }

    /**
     * Empty Constructor
     */
    public UserConfig() {
    }

    /**
     * Create new vk account and restore properties from file on SD Card
     */
    public UserConfig(File file) {
        this.restore(file);
    }

    /**
     * Save account properties into shared preferences
     *
     * @return true if save is successful
     */
    public boolean save() {
        AppGlobal.preferences.edit()
                .putInt(USER_ID, userId)
                .putInt(API_ID, apiId)
                .putString(ACCESS_TOKEN, accessToken)
                .putString(EMAIL, email)
                .apply();

        return true;
    }

    /**
     * Save account properties into file
     *
     * @return true if save is successful
     */
    public boolean save(File file) {
        JsonObject json = new JsonObject();
        try {
            json.putOpt(ACCESS_TOKEN, accessToken);
            json.putOpt(USER_ID, userId);
            json.putOpt(API_ID, apiId);
            json.putOpt(EMAIL, email);

            FileStreams.write(json.toString(), file);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Restores account properties from Preferences
     *
     * @return this account
     */
    public static UserConfig restore() {
        int userId = AppGlobal.preferences.getInt(USER_ID, -1);
        int apiId = AppGlobal.preferences.getInt(API_ID, -1);
        String accessToken = AppGlobal.preferences.getString(ACCESS_TOKEN, null);
        String email = AppGlobal.preferences.getString(EMAIL, null);

        return new UserConfig(accessToken, email, userId, apiId);
    }

    /**
     * Restores account properties from SD
     *
     * @return this account
     */
    public UserConfig restore(File file) {
        try {
            String readText = FileStreams.read(file);
            JsonObject json = new JsonObject(readText);

            this.accessToken = json.optString(ACCESS_TOKEN);
            this.email = json.optString(EMAIL);
            this.userId = json.optInt(USER_ID);
            this.apiId = json.optInt(API_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Remove account properties from Preferences
     */
    public static void clear() {
        AppGlobal.preferences.edit()
                .remove(ACCESS_TOKEN)
                .remove(API_ID)
                .remove(USER_ID)
                .remove(EMAIL)
                .apply();
    }
}
