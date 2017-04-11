package ru.euphoria.messenger.api;

import android.text.TextUtils;

import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Auth {
    public static String REDIRECT_URL = "https://oauth.vk.com/blank.html";

    public static String getUrl(int apiId, String settings) {
        return "https://oauth.vk.com/authorize?client_id="
                + apiId + "&display=mobile&scope="
                + settings + "&redirect_uri="
                + URLEncoder.encode(REDIRECT_URL)
                + "&response_type=token"
                + "&v=" + URLEncoder.encode(VKApi.API_VERSION);
    }

    public static String[] parseRedirectUrl(String url) throws Exception {
        String access_token = extractPattern(url, "access_token=(.*?)&");
        String user_id = extractPattern(url, "id=(\\d*)");
        if (TextUtils.isEmpty(user_id) || TextUtils.isEmpty(access_token)) {
            throw new Exception("Failed to parse redirect url " + url);
        }
        return new String[]{access_token, user_id};
    }

    private static String extractPattern(String string, String pattern) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(string);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }
}