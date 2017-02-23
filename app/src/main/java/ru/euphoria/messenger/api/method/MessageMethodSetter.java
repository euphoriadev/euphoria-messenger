package ru.euphoria.messenger.api.method;

import java.util.Collection;

import ru.euphoria.messenger.util.ArrayUtil;

/**
 * Method setter for users
 */

public class MessageMethodSetter extends MethodSetter {

    /**
     * Creates a new Method Setter
     *
     * @param name the vk method name, e.g. users.get
     */
    public MessageMethodSetter(String name) {
        super(name);
    }

    /** Setters for messages.get */

    /**
     * false — to return incoming messages (default)
     * true — to return outgoing messages
     */
    public MessageMethodSetter out(boolean value) {
        put("out", value);
        return this;
    }

    /**
     * Maximum time since a message was sent, in seconds.
     * To return messages without a time limitation, set as 0.
     */
    public MessageMethodSetter timeOffset(int value) {
        put("time_offset", value);
        return this;
    }

    /**
     * Filter to apply:
     * 1 — unread only
     * 2 — not from the chat
     * 4 — messages from friends
     * 4 — messages from friends
     * 8 - important messages
     * <p/>
     * If the 4 flag is set, the 1 and 2 flags are not considered
     */
    public MessageMethodSetter filters(int value) {
        put("filters", value);
        return this;
    }

    /**
     * Number of characters after which to truncate a previewed message.
     * To preview the full message, specify 0.
     */
    public MessageMethodSetter previewLength(int value) {
        put("preview_length", value);
        return this;
    }

    /**
     * ID of the message received before the message,
     * that will be returned last (provided that no more than count messages
     * were received before it; otherwise offset parameter shall be used).
     */
    public MessageMethodSetter lastMessageId(int value) {
        put("last_message_id", value);
        return this;
    }


    /** Setters for messages.getDialogs */

    /**
     * true - to return only conversations which have unread messages
     * false - returns all messages
     * <p/>
     * By default is false
     */
    public MessageMethodSetter unread(boolean value) {
        put("unread", value);
        return this;
    }

    /** Setters for messages.getById */

    /**
     * Message IDs
     */
    public MessageMethodSetter messageIds(int... ids) {
        put("message_ids", ArrayUtil.toString(ids));
        return this;
    }

    /** Setters for messages.search */

    /**
     * Search query string
     */
    public MessageMethodSetter q(String query) {
        put("q", query);
        return this;
    }

    /** Setters for messages.getHistory */

    /**
     * if the value is > 0, then this message ID,
     * starting from which history of correspondence,
     * if the passed value is -1, then value
     * of offset is added number of unread messages at the end of the dialog).
     */
    public MessageMethodSetter startMessageId(int id) {
        put("start_message_id", id);
        return this;
    }

    /**
     * Destination ID
     * <p/>
     * For group chat: 2000000000 + ID of conversation.
     * For community: -community ID
     * <p/>
     */
    public MessageMethodSetter peerId(long value) {
        put("peer_id", value);
        return this;
    }

    /**
     * Sort order:
     * true —  return messages in chronological order (reverse).
     * false — return messages in reverse chronological order (default)
     */
    public MessageMethodSetter rev(boolean value) {
        put("rev", value);
        return this;
    }


    /** Setters for messages.send */

    /**
     * User's short address (for example, durov)
     */
    public MessageMethodSetter domain(String value) {
        put("domain", value);
        return this;
    }

    /**
     * ID of conversation the message will relate to
     */
    public MessageMethodSetter chatId(int value) {
        put("chat_id", value);
        return this;
    }

    /**
     * (Required if attachments is not set.) Text of the message
     */
    public MessageMethodSetter message(String message) {
        put("message", message);
        return this;
    }

    /**
     * Unique ID used to prevent re-sending of the same message
     * (Not necessarily)
     */
    public MessageMethodSetter randomId(int value) {
        put("random_id", value);
        return this;
    }

    /**
     * Geographical latitude of a check-in, in degrees (from -90 to 90).
     */
    public MessageMethodSetter lat(double lat) {
        put("lat", lat);
        return this;
    }

    /**
     * Geographical longitude of a check-in, in degrees (from -180 to 180).
     */
    public MessageMethodSetter longitude(long value) {
        put("LONG", value);
        return this;
    }

    /**
     * List of objects attached to the message, separated by commas
     */
    public final MessageMethodSetter attachment(Collection<String> attachments) {
        put("attachment", ArrayUtil.toString(attachments));
        return this;
    }


    /**
     * List of objects attached to the message, separated by commas
     */
    public final MessageMethodSetter attachment(String... attachments) {
        put("attachment", ArrayUtil.toString(attachments));
        return this;
    }

    /**
     * List of objects attached to the message, separated by commas
     */
    public final MessageMethodSetter forwardMessages(Collection<String> ids) {
        put("forward_messages", ArrayUtil.toString(ids));
        return this;
    }

    /**
     * (Required if message is not set.)
     * List of objects attached to the message, separated by commas
     */
    public final MessageMethodSetter forwardMessages(int... ids) {
        put("forward_messages", ArrayUtil.toString(ids));
        return this;
    }

    /**
     * Sticker ID
     */
    public final MessageMethodSetter stickerId(int value) {
        put("sticker_id", value);
        return this;
    }

    /** Setters for messages.restore */

    /**
     * ID of a previously-deleted message to restore
     */
    public final MessageMethodSetter messageId(int value) {
        put("message_id", value);
        return this;
    }


    /** Setters for messages.markAsImportant */

    /**
     * Sets flag to important message:
     * false — to remove the star
     * true — to add a star (mark as important)
     */
    public final MessageMethodSetter important(boolean value) {
        put("important", value);
        return this;
    }

    /** Setters for messages.getLongPollHistory */

    /**
     * Last value of the ts parameter returned from the Long Poll server
     * or by using messages.getLongPollServer method.
     */
    public final MessageMethodSetter ts(long value) {
        put("ts", value);
        return this;
    }

    /**
     * Last value of the parameter new_pts received from Long Poll server
     * used to receive actions, which are kept always
     */
    public final MessageMethodSetter pts(int value) {
        put("pts", value);
        return this;
    }

    /**
     * Number of messages that need to return
     */
    public final MessageMethodSetter msgsLimit(int limit) {
        put("msgs_limit", limit);
        return this;
    }

    /**
     * True - returns history only from those users who are currently online
     */
    public final MessageMethodSetter onlines(boolean onlines) {
        put("onlines", onlines);
        return this;
    }

    /**
     * Maximum ID of the message among existing ones in the local copy.
     * Both messages received with API methods
     * (for example, messages.getDialogs, messages.getHistory),
     * and data received from a Long Poll server (events with code 4)
     * are taken into account
     */
    public final MessageMethodSetter maxMsgId(int id) {
        put("max_msg_id", id);
        return this;
    }


    /** Setters for messages.messages.getChat */

    /**
     * Chat IDs
     */
    public final MessageMethodSetter chatIds(int... ids) {
        put("max_msg_id", ArrayUtil.toString(ids));
        return this;
    }

    /**
     * Chat IDs
     */
    public final MessageMethodSetter chatIds(Collection<Integer> ids) {
        put("max_msg_id", ArrayUtil.toString(ids));
        return this;
    }


    /** Setters for messages.messages.createChat */

    /**
     * Chat title
     */
    public final MessageMethodSetter title(String title) {
        put("title", title);
        return this;
    }


    /** Setters for messages.setActivity */

    /**
     * typing — user has started to type
     */
    public final MessageMethodSetter type(boolean typing) {
        if (typing) {
            put("type", "typing");
        }
        return this;
    }


    /** Setters for messages.getHistoryAttachments */

    /**
     * Type of media files to return:
     * - photo;
     * - video;
     * - audio;
     * - doc;
     * - link.
     */
    public final MessageMethodSetter mediaType(String type) {
        put("media_type", type);
        return this;
    }

    /**
     * true — to return photo sizes in
     * a special format (https://vk.com/dev/photo_sizes)
     */
    public final MessageMethodSetter photoSizes(boolean value) {
        put("photo_sizes", value);
        return this;
    }
}
