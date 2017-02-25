package ru.euphoria.messenger.database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import ru.euphoria.messenger.common.AppGlobal;

/**
 * Created by Igor on 05.02.17.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String LOG_TAG = "Euphoria.Database";

    private static final int DATABASE_VERSION = 5;
    private static final String DATABASE_NAME = "cache.db";

    /** Tables */
    public static final String USERS_TABLE = "users";
    public static final String FRIENDS_TABLE = "friends";
    public static final String DIALOGS_TABLE = "dialogs";
    public static final String MESSAGES_TABLE = "messages";
    public static final String SAVED_MESSAGES_TABLE = "saved_messages";
    public static final String AUDIOS_TABLE = "audios";
    public static final String DOCS_TABLE = "docs";
    public static final String PHOTOS_TABLE = "photos";
    public static final String GROUPS_TABLE = "groups";
    public static final String USER_GROUP_TABLE = "user_group";
    public static final String STATS_MESSAGES_TABLE = "stats_messages";
    public static final String FAILED_MESSAGES_TABLE = "failed_messages";

    /** Columns */
    public static final String _ID = "_id";
    public static final String USER_ID = "user_id";
    public static final String OWNER_ID = "owner_id";
    public static final String GROUP_ID = "group_id";
    public static final String FRIEND_ID = "friend_id";
    public static final String AUDIO_ID = "audio_id";
    public static final String CHAT_ID = "chat_id";
    public static final String DOC_ID = "doc_id";
    public static final String ALBUM_ID = "album_id";
    public static final String LYRICS_ID = "lyrics_id";
    public static final String MESSAGE_ID = "message_id";
    public static final String TITLE = "title";
    public static final String BODY = "body";
    public static final String UNREAD_COUNT = "unread_count";
    public static final String TOTAL_COUNT = "total_count";
    public static final String INCOMING_COUNT = "incoming_count";
    public static final String OUTGOING_COUNT = "outgoing_count";
    public static final String IMPORTANT = "important";
    public static final String ATTACHMENTS = "attachments";
    public static final String FWD_MESSAGES = "fwd_messages";
    public static final String USERS_COUNT = "users_count";
    public static final String IS_OUT = "is_out";
    public static final String READ_STATE = "read_state";
    public static final String DATE = "date";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String NICKNAME = "nickname";
    public static final String ONLINE = "online_status";
    public static final String ONLINE_MOBILE = "online_mobile";
    public static final String ONLINE_APP = "online_app";
    public static final String STATUS = "status";
    public static final String IS_FRIEND = "is_friend";
    public static final String TEXT = "text";
    public static final String PHOTO_50 = "photo_50";
    public static final String PHOTO_75 = "photo_75";
    public static final String PHOTO_100 = "photo_100";
    public static final String PHOTO_130 = "photo_130";
    public static final String PHOTO_200 = "photo_200";
    public static final String PHOTO_400 = "photo_400";
    public static final String PHOTO_604 = "photo_604";
    public static final String PHOTO_807 = "photo_807";
    public static final String PHOTO_1280 = "photo_1280";
    public static final String PHOTO_2560 = "photo_2560";
    public static final String PHOTO_MAX = "photo_max";
    public static final String WIDTH = "width";
    public static final String HEIGHT = "height";
    public static final String POSITION_DECIMAL = "position";
    public static final String LAST_SEEN = "last_seen";
    public static final String ARTIST = "artist";
    public static final String DURATION = "duration";
    public static final String URL = "url";
    public static final String SIZE = "size";
    public static final String EXT = "ext";
    public static final String NAME = "name";
    public static final String SCREEN_NAME = "screen_name";
    public static final String IS_CLOSED = "is_closed";
    public static final String IS_ADMIN = "is_admin";
    public static final String ADMIN_LEVER = "admin_level";
    public static final String TYPE = "type";
    public static final String DESCRIPTION = "description";
    public static final String MEMBERS_COUNT = "members_count";

    private static final String SQL_CREATE_TABLE_USERS = "CREATE TABLE " + USERS_TABLE +
            " (" + USER_ID + " INTEGER PRIMARY KEY ON CONFLICT REPLACE, " +
            " [" + FIRST_NAME + "] VARCHAR(255), " +
            " [" + LAST_NAME + "] VARCHAR(255), " +
            " [" + SCREEN_NAME + "] VARCHAR(255), " +
            " [" + NICKNAME + "] VARCHAR(255), " +
            " [" + ONLINE + "] INTEGER, " +
            " [" + ONLINE_MOBILE + "] INTEGER, " +
            " [" + ONLINE_APP + "] INTEGER, " +
            " [" + STATUS + "] VARCHAR(255), " +
            " [" + IS_FRIEND + "] VARCHAR(255), " +
            " [" + LAST_SEEN + "] INTEGER, " +
            " [" + PHOTO_50 + "] VARCHAR(255), " +
            " [" + PHOTO_100 + "] VARCHAR(255), " +
            " [" + PHOTO_200 + "] VARCHAR(255), " +
            " [" + PHOTO_400 + "] VARCHAR(255), " +
            " [" + PHOTO_MAX + "] VARCHAR(255), " +
            " [" + POSITION_DECIMAL + "] DECIMAL" +
            ");";

    private static final String SQL_CREATE_TABLE_FRIENDS = "CREATE TABLE " + FRIENDS_TABLE +
            " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            " [" + USER_ID + "] INTEGER, " +
            " [" + FRIEND_ID + "] INTEGER " +
            ");";

    private static final String SQL_CREATE_TABLE_DIALOGS = "CREATE TABLE " + DIALOGS_TABLE +
            " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            " [" + MESSAGE_ID + "] INTEGER, " +
            " [" + USER_ID + "] INTEGER, " +
            " [" + CHAT_ID + "] INTEGER, " +
            " [" + TITLE + "] VARCHAR(255), " +
            " [" + BODY + "] VARCHAR(255), " +
            " [" + IS_OUT + "] INTEGER, " +
            " [" + READ_STATE + "] INTEGER, " +
            " [" + USERS_COUNT + "] INTEGER, " +
            " [" + UNREAD_COUNT + "] INTEGER, " +
            " [" + DATE + "] INTEGER , " +
            " [" + PHOTO_50 + "] VARCHAR(255), " +
            " [" + PHOTO_100 + "] VARCHAR(255)" +
            ");";

    private final static String SQL_CREATE_TABLE_MESSAGES = "CREATE TABLE " + MESSAGES_TABLE +
            " (" + _ID + " INTEGER PRIMARY KEY, " +
            " [" + MESSAGE_ID + "] INTEGER, " +
            " [" + USER_ID + "] INTEGER, " +
            " [" + CHAT_ID + "] INTEGER, " +
            " [" + BODY + "] VARCHAR(255), " +
            " [" + DATE + "] INTEGER, " +
            " [" + READ_STATE + "] INTEGER, " +
            " [" + IS_OUT + "] INTEGER, " +
            " [" + IMPORTANT + "] INTEGER, " +
            " [" + ATTACHMENTS + "] BLOB, " +
            " [" + FWD_MESSAGES + "] BLOB" +
            ");";


    private final static String SQL_CREATE_TABLE_STATS_MESSAGES = "CREATE TABLE " + STATS_MESSAGES_TABLE +
            " (" + _ID + " INTEGER PRIMARY KEY, " +
            " [" + USER_ID + "] INTEGER, " +
            " [" + CHAT_ID + "] INTEGER, " +
            " [" + TOTAL_COUNT + "] INTEGER, " +
            " [" + INCOMING_COUNT + "] INTEGER, " +
            " [" + OUTGOING_COUNT + "] INTEGER" +
            ");";

    private final static String SQL_CREATE_TABLE_AUDIOS = "CREATE TABLE " + AUDIOS_TABLE +
            " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            " [" + AUDIO_ID + "] INTEGER, " +
            " [" + OWNER_ID + "] INTEGER, " +
            " [" + ARTIST + "] VARCHAR(255), " +
            " [" + TITLE + "] VARCHAR(255), " +
            " [" + DURATION + "] INTEGER, " +
            " [" + URL + "] VARCHAR(255), " +
            " [" + LYRICS_ID + "] INTEGER " +
            ");";

    private final static String SQK_CREATE_TABLE_PHOTOS = "CREATE TABLE " + PHOTOS_TABLE +
            " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            " [" + ALBUM_ID + "] INTEGER, " +
            " [" + OWNER_ID + "] INTEGER, " +
            " [" + WIDTH + "] INTEGER, " +
            " [" + HEIGHT + "] INTEGER, " +
            " [" + DATE + "] INTEGER, " +
            " [" + TEXT + "] VARCHAR(255), " +
            " [" + PHOTO_75 + "] VARCHAR(255), " +
            " [" + PHOTO_130 + "] VARCHAR(255), " +
            " [" + PHOTO_604 + "] VARCHAR(255), " +
            " [" + PHOTO_807 + "] VARCHAR(255), " +
            " [" + PHOTO_1280 + "] VARCHAR(255), " +
            " [" + PHOTO_2560 + "] VARCHAR(255)" +
            ");";

    private final static String SQL_CREATE_TABLE_DOCS = "CREATE TABLE " + DOCS_TABLE +
            " (" + _ID + " INTEGER PRIMARY KEY, " +
            " [" + DOC_ID + "] INTEGER, " +
            " [" + OWNER_ID + "] INTEGER, " +
            " [" + TITLE + "] VARCHAR(255), " +
            " [" + SIZE + "] INTEGER, " +
            " [" + TYPE + "] INTEGER, " +
            " [" + EXT + "] VARCHAR(255), " +
            " [" + URL + "] VARCHAR(255), " +
            " [" + PHOTO_100 + "] VARCHAR(255), " +
            " [" + PHOTO_130 + "] VARCHAR(255)" +
            ");";

    private final static String SQL_CREATE_TABLE_GROUPS = "CREATE TABLE " + GROUPS_TABLE +
            " (" + GROUP_ID + " INTEGER PRIMARY KEY ON CONFLICT REPLACE, " +
            " [" + NAME + "] VARCHAR(255), " +
            " [" + SCREEN_NAME + "] VARCHAR(255), " +
            " [" + DESCRIPTION + "] VARCHAR(255), " +
            " [" + STATUS + "] VARCHAR(255), " +
            " [" + TYPE + "] INTEGER, " +
            " [" + IS_CLOSED + "] INTEGER, " +
            " [" + IS_ADMIN + "] INTEGER, " +
            " [" + ADMIN_LEVER + "] INTEGER, " +
            " [" + PHOTO_50 + "] VARCHAR(255), " +
            " [" + PHOTO_100 + "] VARCHAR(255), " +
            " [" + MEMBERS_COUNT + "] INTEGER " +
            ");";

    private final static String SQL_CREATE_TABLE_USER_GROUP = "CREATE TABLE " + USER_GROUP_TABLE +
            " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            " [" + GROUP_ID + "] INTEGER, " +
            " [" + USER_ID + "] INTEGER " +
            ");";

    private final static String SQL_CREATE_TABLE_FAILED_MESSAGES = "CREATE TABLE " + FAILED_MESSAGES_TABLE +
            " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            " [" + USER_ID + "] INTEGER, " +
            " [" + CHAT_ID + "] INTEGER, " +
            " [" + BODY + "] VARCHAR(255)" +
            ");";

    private static final String SQL_DELETE_DOCS = "DROP TABLE IF EXISTS " + DOCS_TABLE;
    private static final String SQL_DELETE_USERS = "DROP TABLE IF EXISTS " + USERS_TABLE;
    private static final String SQL_DELETE_AUDIOS = "DROP TABLE IF EXISTS " + AUDIOS_TABLE;
    private static final String SQL_DELETE_PHOTOS = "DROP TABLE IF EXISTS " + PHOTOS_TABLE;
    private static final String SQL_DELETE_GROUPS = "DROP TABLE IF EXISTS " + GROUPS_TABLE;
    private static final String SQL_DELETE_FRIENDS = "DROP TABLE IF EXISTS " + FRIENDS_TABLE;
    private static final String SQL_DELETE_DIALOGS = "DROP TABLE IF EXISTS " + DIALOGS_TABLE;
    private static final String SQL_DELETE_MESSAGES = "DROP TABLE IF EXISTS " + MESSAGES_TABLE;
    private static final String SQL_DELETE_USER_GROUPS = "DROP TABLE IF EXISTS " + USER_GROUP_TABLE;
    private static final String SQL_DELETE_SAVED_MESSAGES = "DROP TABLE IF EXISTS " + SAVED_MESSAGES_TABLE;
    private static final String SQL_DElETE_STATS_MESSAGES = "DROP TABLE IF EXISTS " + STATS_MESSAGES_TABLE;
    private static final String SQL_DELETE_FAILED_MESSAGES = "DROP TABLE IF EXISTS " + FAILED_MESSAGES_TABLE;

    private static DatabaseHelper instance;

    public synchronized static DatabaseHelper getInstance() {
        if (instance == null) {
            instance = new DatabaseHelper();
        }

        return instance;
    }

    private DatabaseHelper() {
        super(AppGlobal.appContext, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_DOCS);
        db.execSQL(SQL_CREATE_TABLE_USERS);
        db.execSQL(SQL_CREATE_TABLE_GROUPS);
        db.execSQL(SQL_CREATE_TABLE_AUDIOS);
        db.execSQL(SQK_CREATE_TABLE_PHOTOS);
        db.execSQL(SQL_CREATE_TABLE_DIALOGS);
        db.execSQL(SQL_CREATE_TABLE_FRIENDS);
        db.execSQL(SQL_CREATE_TABLE_MESSAGES);
        db.execSQL(SQL_CREATE_TABLE_USER_GROUP);
        db.execSQL(SQL_CREATE_TABLE_STATS_MESSAGES);
        db.execSQL(SQL_CREATE_TABLE_FAILED_MESSAGES);

        Log.w(LOG_TAG, "Database created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(LOG_TAG, "Database upgraded from " + oldVersion + " to " + newVersion + " version");

        dropTables(db);
        onCreate(db);
    }

    public void dropTables(SQLiteDatabase db) {
        db.execSQL(SQL_DELETE_DOCS);
        db.execSQL(SQL_DELETE_USERS);
        db.execSQL(SQL_DELETE_AUDIOS);
        db.execSQL(SQL_DELETE_PHOTOS);
        db.execSQL(SQL_DELETE_GROUPS);
        db.execSQL(SQL_DELETE_FRIENDS);
        db.execSQL(SQL_DELETE_DIALOGS);
        db.execSQL(SQL_DELETE_MESSAGES);
        db.execSQL(SQL_DELETE_USER_GROUPS);
        db.execSQL(SQL_DELETE_SAVED_MESSAGES);
        db.execSQL(SQL_DElETE_STATS_MESSAGES);
        db.execSQL(SQL_DELETE_FAILED_MESSAGES);
    }
}
