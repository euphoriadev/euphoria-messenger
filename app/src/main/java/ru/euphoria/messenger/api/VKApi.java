package ru.euphoria.messenger.api;

import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;

import ru.euphoria.messenger.BuildConfig;
import ru.euphoria.messenger.api.method.AppMethodSetter;
import ru.euphoria.messenger.api.method.MessageMethodSetter;
import ru.euphoria.messenger.api.method.MethodSetter;
import ru.euphoria.messenger.api.method.UserMethodSetter;
import ru.euphoria.messenger.api.model.VKApp;
import ru.euphoria.messenger.api.model.VKGroup;
import ru.euphoria.messenger.api.model.VKLongPollServer;
import ru.euphoria.messenger.api.model.VKMessage;
import ru.euphoria.messenger.api.model.VKModel;
import ru.euphoria.messenger.api.model.VKUser;
import ru.euphoria.messenger.common.AppGlobal;
import ru.euphoria.messenger.concurrent.ThreadExecutor;
import ru.euphoria.messenger.json.JsonArray;
import ru.euphoria.messenger.json.JsonObject;
import ru.euphoria.messenger.net.HttpRequest;
import ru.euphoria.messenger.util.ArrayUtil;

/**
 * Created by Igor on 06.02.17.
 */
public class VKApi {
    public static final String TAG = "Euphoria.VKApi";
    public static final String BASE_URL = "https://api.vk.com/method/";
    public static final String API_VERSION = "5.62";

    public static UserConfig config;
    public static String lang = AppGlobal.locale.getLanguage();

    @SuppressWarnings("umchecked")
    public static <T> ArrayList<T> execute(String url, Class<T> cls) throws Exception {
        if (BuildConfig.DEBUG) {
            Log.w(TAG, "url: " + url);
        }
        StringBuilder buffer = new StringBuilder();

        HttpRequest.get(url)
                .acceptGzipEncoding()
                .uncompress(true)
                .receive(buffer)
                .disconnect();

        if (BuildConfig.DEBUG) {
            Log.i(TAG, "json: " + buffer.toString());
        }

        JsonObject json = new JsonObject(buffer.toString());
        checkError(json, url);

        if (cls == null) {
            return null;
        }

        if (cls == VKLongPollServer.class) {
            VKLongPollServer server = new VKLongPollServer(json.optJsonObject("response"));
            return (ArrayList<T>) ArrayUtil.singletonList(server);
        }

        if (cls == Boolean.class) {
            boolean value = json.optInt("response") == 1;
            return (ArrayList<T>) ArrayUtil.singletonList(value);
        }

        if (cls == Long.class) {
            long value = json.optLong("response");
            return (ArrayList<T>) ArrayUtil.singletonList(value);
        }

        if (cls == Integer.class) {
            int value = json.optInt("response");
            return (ArrayList<T>) ArrayUtil.singletonList(value);
        }

        JsonArray array = optItems(json);
        ArrayList<T> models = new ArrayList<>(array.length());

        if (cls == VKUser.class) {
            for (int i = 0; i < array.length(); i++) {
                models.add((T) new VKUser(array.optJsonObject(i)));
            }
        } else if (cls == VKMessage.class) {
            VKMessage.count = json.optJsonObject("response").optInt("count");
            for (int i = 0; i < array.length(); i++) {
                JsonObject source = array.optJsonObject(i);
                int unread = source.optInt("unread");
                if (source.has("message")) {
                    source = source.optJsonObject("message");
                }
                VKMessage message = new VKMessage(source);
                message.unread = unread;
                models.add((T) message);
            }
        } else if (cls == VKGroup.class) {
            for (int i = 0; i < array.length(); i++) {
                models.add((T) new VKGroup(array.optJsonObject(i)));
            }
        } else if (cls == VKApp.class) {
            for (int i = 0; i < array.length(); i++) {
                models.add((T) new VKApp(array.optJsonObject(i)));
            }
        }
        return models;
    }

    public static <E> void execute(final String url, final Class<E> cls,
                                                   final OnResponseListener<E> listener) {
        ThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    ArrayList<E> models = execute(url, cls);
                    if (listener != null) {
                        AppGlobal.handler.post(new SuccessCallback<E>(listener, models));
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                    if (listener != null) {
                        AppGlobal.handler.post(new ErrorCallback(listener, e));
                    }
                }
            }
        });
    }

    private static JsonArray optItems(JsonObject source) {
        Object response = source.opt("response");
        if (response instanceof JsonArray) {
            return (JsonArray) response;
        }

        if (response instanceof JsonObject) {
            JsonObject json = (JsonObject) response;
            return json.optJsonArray("items");
        }

        return null;
    }

    private static void checkError(JsonObject json, String url) throws VKException {
        if (json.has("error")) {
            JsonObject error = json.optJsonObject("error");

            int code = error.optInt("error_code");
            String message = error.optString("error_msg");

            VKException e = new VKException(url, message, code);
            if (code == ErrorCodes.CAPTCHA_NEEDED) {
                e.captchaImg = error.optString("captcha_img");
                e.captchaSid = error.optString("captcha_sid");
            }
            if (code == ErrorCodes.VALIDATION_REQUIRED) {
                e.redirectUri = error.optString("redirect_uri");
            }
            throw e;
        }
    }

    /** Methods for users */
    public static VKUsers users() {
        return new VKUsers();
    }

    /** Methods for messages */
    public static VKMessages messages() {
        return new VKMessages();
    }

    /** Methods for groups */
    public static VKGroups groups() {
        return new VKGroups();
    }

    /** Methods for apps */
    public static VKApps apps() {
        return new VKApps();
    }

    /** Methods for account */
    public static VKAccounts account() {
        return new VKAccounts();
    }


    public static class VKUsers {
        private VKUsers() {

        }

        public UserMethodSetter get() {
            return new UserMethodSetter("users.get");
        }
    }

    public static class VKMessages {
        private VKMessages() {

        }

        /** Returns a list of the current user's incoming or outgoing private messages */
        public MessageMethodSetter get() {
            return new MessageMethodSetter("messages.get");
        }

        /** Returns the list of dialogs of the current user */
        public MessageMethodSetter getDialogs() {
            return new MessageMethodSetter("messages.getDialogs");
        }

        /** Returns messages by their IDs */
        public MessageMethodSetter getById() {
            return new MessageMethodSetter("messages.getById");
        }

        /**
         * Returns a list of the current user's private messages,
         * that match search criteria
         */
        public MessageMethodSetter search() {
            return new MessageMethodSetter("messages.search");
        }

        /**
         * Returns a list of the current user's private messages,
         * that match search criteria
         */
        public MessageMethodSetter getHistory() {
            return new MessageMethodSetter("messages.getHistory");
        }

        /**
         * Returns media files from the dialog or group chat
         * <p/>
         * Result:
         * Returns a list of photo, video, audio or doc objects depending
         * on media_type parameter value
         * and additional next_from field containing new offset value
         */
        public MessageMethodSetter getHistoryAttachments() {
            return new MessageMethodSetter("messages.getHistoryAttachments");
        }

        /**
         * Sends a message
         */
        public MessageMethodSetter send() {
            return new MessageMethodSetter("messages.send");
        }

        /**
         * Sends a sticker
         * <p/>
         * Result:
         * After successful execution, returns the sent message ID (id).
         * <p/>
         * Error codes:
         * 900	Cannot send sticker to user from blacklist
         */
        public MessageMethodSetter sendSticker() {
            return new MessageMethodSetter("messages.sendSticker");
        }

        /**
         * Deletes one or more messages
         * <p/>
         * http://vk.com/dev/messages.delete
         */
        public MessageMethodSetter delete() {
            return new MessageMethodSetter("messages.delete");
        }

        /**
         * Deletes all private messages in a conversation
         * NOTE: If the number of messages exceeds the maximum,
         * the method shall be called several times
         */
        public MessageMethodSetter deleteDialog() {
            return new MessageMethodSetter("messages.deleteDialog");
        }

        /** Restores a deleted message */
        public MessageMethodSetter restore() {
            return new MessageMethodSetter("messages.restore");
        }

        /** Marks messages as read */
        public MessageMethodSetter markAsRead() {
            return new MessageMethodSetter("messages.markAsRead");
        }

        /**
         * Marks messages as new (unread)
         * (This method is deprecated and may be disabled soon, please avoid using it)
         */
        @Deprecated
        public MessageMethodSetter markAsNew() {
            return new MessageMethodSetter("messages.markAsNew");
        }

        /** Marks and unmarks messages as important (starred) */
        public MessageMethodSetter markAsImportant() {
            return new MessageMethodSetter("messages.markAsImportant");
        }

        /**
         * Returns data required for connection to a Long Poll server.
         * With Long Poll connection,
         * you can immediately know about incoming messages and other events.
         * <p/>
         * Result:
         * Returns an object with key, server, ts fields.
         * With such data you can connect to an instant message server
         * to immediately receive incoming messages and other events
         */
        public MessageMethodSetter getLongPollServer() {
            return new MessageMethodSetter("messages.getLongPollServer");
        }

        /**
         * Returns updates in user's private messages.
         * To speed up handling of private messages,
         * it can be useful to cache previously loaded messages on
         * a user's mobile device/desktop, to prevent re-receipt at each call.
         * With this method, you can synchronize a local copy of
         * the message list with the actual version.
         * <p/>
         * Result:
         * Returns an object that contains the following fields:
         * 1 — history:     An array similar to updates field returned
         * from the Long Poll server,
         * with these exceptions:
         * - For events with code 4 (addition of a new message),
         * there are no fields except the first three.
         * - There are no events with codes 8, 9 (friend goes online/offline)
         * or with codes 61, 62 (typing during conversation/chat).
         * <p/>
         * 2 — messages:    An array of private message objects that were found
         * among events with code 4 (addition of a new message)
         * from the history field.
         * Each object of message contains a set of fields described here.
         * The first array element is the total number of messages
         */
        public MessageMethodSetter getLongPollHistory() {
            return new MessageMethodSetter(("messages.getLongPollHistory"));
        }

        /**
         * Returns information about a chat
         * <p/>
         * Returns a list of chat objects.
         * If the fields parameter is set,
         * the users field contains a list of user objects with
         * an additional invited_by field containing the ID of the user who
         * invited the current user to chat.
         * <p/>
         * http://vk.com/dev/messages.getChat
         */
        public MessageMethodSetter getChat() {
            return new MessageMethodSetter("messages.getChat");
        }

        /**
         * Creates a chat with several participants
         * <p/>
         * Returns the ID of the created chat (chat_id).
         * <p/>
         * Errors:
         * 9	Flood control
         * http://vk.com/dev/messages.createChat
         */
        public MessageMethodSetter createChat() {
            return new MessageMethodSetter("messages.createChat");
        }

        /**
         * Edits the title of a chat
         * <p/>
         * Result:
         * Returns 1
         * <p/>
         * http://vk.com/dev/messages.editChat
         */
        public MessageMethodSetter editChat() {
            return new MessageMethodSetter("messages.editChat");
        }

        /**
         * Returns a list of IDs of users participating in a chat
         * <p/>
         * Result:
         * Returns a list of IDs of chat participants.
         * <p/>
         * If fields is set, the user fields contains a list of user objects
         * with an additional invited_by field containing the ID
         * of the user who invited the current user to chat.
         * <p/>
         * http://vk.com/dev/messages.getChatUsers
         */
        public MessageMethodSetter getChatUsers() {
            return new MessageMethodSetter("messages.getChatUsers");
        }

        /**
         * Changes the status of a user as typing in a conversation
         * <p/>
         * Result:
         * Returns 1.
         * "User N is typing..." is shown for 10 seconds
         * after the method is called, or until the message is sent.
         * <p/>
         * http://vk.com/dev/messages.setActivity
         */
        public MessageMethodSetter setActivity() {
            return new MessageMethodSetter("messages.setActivity").type(true);
        }

        /**
         * Adds a new user to a chat.
         * <p/>
         * Result:
         * Returns 1.
         * <p/>
         * Errors:
         * 103	Out of limits
         * <p/>
         * See https://vk.com/dev/messages.addChatUser
         */
        public MessageMethodSetter addChatUser() {
            return new MessageMethodSetter("messages.addChatUser");
        }

        /**
         * Allows the current user to leave a chat or, if the current user started the chat,
         * allows the user to remove another user from the chat.
         * <p/>
         * Result:
         * Returns 1
         */
        public MessageMethodSetter removeChatUser() {
            return new MessageMethodSetter("messages.removeChatUser");
        }
    }

    public static class VKGroups {
        public MethodSetter getById() {
            return new MethodSetter("groups.getById");
        }

        public MethodSetter join() {
            return new MethodSetter("groups.join");
        }
    }

    public static class VKApps {
        /**
         * Returns information about applications on platform vk
         */
        public AppMethodSetter get() {
            return new AppMethodSetter("apps.get");
        }
    }

    public static class VKAccounts {

        /** Marks a current user as offline. */
        public MethodSetter setOffline() {
            return new MethodSetter("account.setOffline");
        }

        /** Marks the current user as online for 15 minutes. */
        public MethodSetter setOnline() {
            return new MethodSetter("account.setOnline");
        }
    }

    /**
     * Callback for Async execute
     */
    public interface OnResponseListener<E> {
        /**
         * Called when successfully receiving the response from web
         *
         * @param models parsed json objects
         */
        void onSuccess(ArrayList<E> models);

        /**
         * Called when an error occurs on the server side
         * Visit website to get description of error codes: http://vk.com/dev/errors
         * and {@link ErrorCodes}
         * It is useful if the server requires you to enter a captcha
         *
         * @param ex the information of error
         */
        void onError(Exception ex);
    }

    private static class SuccessCallback<E> implements Runnable {
        private ArrayList<E> models;
        private OnResponseListener<E> listener;

        public SuccessCallback(OnResponseListener<E> listener, ArrayList<E> models) {
            this.models = models;
            this.listener = listener;
        }

        @Override
        public void run() {
            if (listener == null) {
                return;
            }

            listener.onSuccess(models);
        }
    }

    private static class ErrorCallback implements Runnable {
        private OnResponseListener listener;
        private Exception ex;

        public ErrorCallback(OnResponseListener listener, Exception ex) {
            this.listener = listener;
            this.ex = ex;
        }

        @Override
        public void run() {
            if (listener == null) {
                return;
            }

            listener.onError(ex);
        }
    }
}
