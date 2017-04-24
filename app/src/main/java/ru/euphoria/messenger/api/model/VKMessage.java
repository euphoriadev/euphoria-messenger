package ru.euphoria.messenger.api.model;

import java.io.Serializable;
import java.util.ArrayList;

import ru.euphoria.messenger.json.JsonArray;
import ru.euphoria.messenger.json.JsonObject;

/**
 * Describes a message object from VK.
 */
public class VKMessage extends VKModel implements Serializable {
    private static final long serialVersionUID = 1L;

    // total messages count
    public static int count;
    public static int lastHistoryCount;

    public static final int UNREAD = 1;       // message unread
    public static final int OUTBOX = 2;       // исходящее сообщение
    public static final int REPLIED = 4;      // на сообщение был создан ответ
    public static final int IMPORTANT = 8;    // помеченное сообщение
    public static final int CHAT = 16;        // сообщение отправлено через диалог
    public static final int FRIENDS = 32;     // сообщение отправлено другом
    public static final int SPAM = 64;        // сообщение помечено как "Спам"
    public static final int DELETED = 128;    // сообщение удалено (в корзине)
    public static final int FIXED = 256;      // сообщение проверено пользователем на спам
    public static final int MEDIA = 512;      // сообщение содержит медиаконтент
    public static final int BESEDA = 8192;    // беседа

    public static final String ACTION_CHAT_CREATE = "chat_create";
    public static final String ACTION_CHAT_INVITE_USER = "chat_invite_user";
    public static final String ACTION_CHAT_KICK_USER = "chat_kick_user";

    public static final String ACTION_CHAT_TITLE_UPDATE = "chat_title_update";
    public static final String ACTION_CHAT_PHOTO_UPDATE = "chat_photo_update";
    public static final String ACTION_CHAT_PHOTO_REMOVE = "chat_photo_remove";

    /** Message ID. (Not returned for forwarded messages), positive number */
    public int id;

    /** For an incoming message, the user ID of the author. For an outgoing message, the user ID of the receiver. */
    public int user_id;

    /** Date (in Unix time) when the message was sent. */
    public long date;

    /** Title of message or chat. */
    public String title;

    /** Message text */
    public String body;

    /** Message status (false — not read, true — read). (Not returned for forwarded messages */
    public boolean read_state;

    /** Type (false — received, true — sent). (Not returned for forwarded messages.) */
    public boolean is_out;

    /** Chat ID */
    public int chat_id;

    /** User IDs of chat participants */
    public int[] chat_members;

    /** ID of user who started the chat. */
    public long admin_id;

    /** Number of chat participants. */
    public int users_count;

    /** Whether the message is deleted (false — no, true — yes). */
    public boolean is_deleted;

    /** Whether the message is important */
    public boolean is_important;

    /** Whether the message contains smiles (false — no, true — yes). */
    public boolean emoji;

    /** Array of forwarded messages (if any). */
    public ArrayList<VKMessage> fws_messages;

    /** List of media-attachments. */
    public ArrayList<VKModel> attachments;

    /** URL of chat image with width size of 50px */
    public String photo_50;

    /** URL of chat image with width size of 100px */
    public String photo_100;

    /** URL of chat image with width size of 200px */
    public String photo_200;

    /** The count of unread messages. */
    public int unread;

    /** Field transferred, if a service message. */
    public String action;

    /** The chat name, for service messages. */
    public String action_text;

    /** User ID (if > 0) or email (if < 0), who was invited or kicked. */
    public int action_mid;

    public int flags;

    /**
     * Creates a new empty message instance
     */
    public VKMessage() {
    }

    /**
     * Creates a new message model with fields from json source.
     *
     * @param source the json source to parse
     */
    public VKMessage(JsonObject source) {
        this.id = source.optInt("id");
        this.user_id = source.optInt("user_id");
        this.chat_id = source.optInt("chat_id");
        this.date = source.optLong("date");
        this.is_out = source.optLong("out") == 1;
        this.read_state = source.optLong("read_state") == 1;
        this.title = source.optString("title");
        this.body = source.optString("body");
        this.admin_id = source.optInt("admin_id");
        this.users_count = source.optInt("users_count");
        this.is_deleted = source.optInt("deleted") == 1;
        this.is_important = source.optInt("important") == 1;
        this.emoji = source.optLong("emoji") == 1;
        this.action = source.optString("action");
        this.action_text = source.optString("action_text");
        this.action_mid = source.optInt("action_mid");
        this.photo_50 = source.optString("photo_50");
        this.photo_100 = source.optString("photo_100");
        this.photo_200 = source.optString("photo_200");

        JsonArray active = source.optJsonArray("chat_active");
        if (active != null) {
            this.chat_members = new int[active.length()];
            for (int i = 0; i < active.length(); i++) {
                chat_members[i] = active.optInt(i);
            }
        }

        JsonArray messages = source.optJsonArray("fwd_messages");
        if (messages != null) {
            fws_messages = new ArrayList<>(messages.length());
            for (int i = 0; i < messages.length(); i++) {
                fws_messages.add(new VKMessage(messages.optJsonObject(i)));
            }
        }

        JsonArray attachs = source.optJsonArray("attachments");
        if (attachs != null) {
            attachments = VKAttachments.parse(attachs);
        }

        // from_id returns only when we get history
        int from_id = source.optInt("from_id", -1);
        if (from_id != -1 && this.chat_id != 0) {
            this.user_id = from_id;
        }
    }

    // parse from long poll (update[])
    public static VKMessage parse(JsonArray a) {
        VKMessage m = new VKMessage();
        m.id = a.optInt(1);
        m.flags = a.optInt(2);
        m.user_id = a.optInt(3);
        m.date = a.optLong(4);
        m.title = a.optString(5);
        m.body = a.optString(6);
        m.read_state = ((m.flags & UNREAD) == 0);
        m.is_out = (m.flags & OUTBOX) != 0;
        if ((m.flags & BESEDA) != 0) {
            m.chat_id = a.optInt(3) - 2000000000;// & 63;//cut 6 last digits
            JsonObject o = a.optJsonObject(7);
            m.user_id = o.optInt("from");
        }
        return m;
    }

    /**
     * Returns true if this message from chat
     */
    public boolean isChat() {
        return chat_id != 0;
    }

    /**
     * Returns true if this message are deleted.
     *
     * @param flags the message flags
     */
    public static boolean isDeleted(int flags) {
        return (flags & DELETED) != 0;
    }

    /**
     * Returns true if this message are important.
     *
     * @param flags the message flags
     */
    public static boolean isImportant(int flags) {
        return (flags & IMPORTANT) != 0;
    }

    /**
     * Returns true if this message are unread.
     *
     * @param flags the message flags
     */
    public static boolean isUnread(int flags) {
        return (flags & UNREAD) != 0;
    }
}

