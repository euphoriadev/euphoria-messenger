package ru.euphoria.messenger.api;

import android.util.Log;

import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Auth {
    private static final String TAG = "Kate.Auth";
    public static String redirect_url = "https://oauth.vk.com/blank.html";

    public static String getUrl(int apiId, String settings) {
        return "https://oauth.vk.com/authorize?client_id="
                + apiId + "&display=mobile&scope="
                + settings + "&redirect_uri="
                + URLEncoder.encode(redirect_url)
                + "&response_type=token"
                + "&v=" + URLEncoder.encode(VKApi.API_VERSION);
    }

    public static String[] parseRedirectUrl(String url) throws Exception {
        String access_token = extractPattern(url, "access_token=(.*?)&");
        Log.i(TAG, "access_token=" + access_token);
        String user_id = extractPattern(url, "id=(\\d*)");
        Log.i(TAG, "id=" + user_id);
        if (user_id == null || user_id.length() == 0 || access_token == null || access_token.length() == 0)
            throw new Exception("Failed to parse redirect url " + url);
        return new String[]{access_token, user_id};
    }

    public static String extractPattern(String string, String pattern) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(string);
        if (!m.find())
            return null;
        return m.toMatchResult().group(1);
    }
}