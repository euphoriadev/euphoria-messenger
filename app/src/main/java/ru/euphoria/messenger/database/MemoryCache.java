package ru.euphoria.messenger.database;

import android.util.SparseArray;

import java.util.ArrayList;

import ru.euphoria.messenger.api.model.VKGroup;
import ru.euphoria.messenger.api.model.VKUser;
import ru.euphoria.messenger.util.ArrayUtil;

/**
 * Created by Igor on 09.02.17.
 */

public class MemoryCache {
    private static SparseArray<VKUser> users = new SparseArray<>(20);
    private static SparseArray<VKGroup> groups = new SparseArray<>(20);

    public static VKUser getUser(int id) {
        VKUser user = users.get(id);
        if (user == null) {
            user = CacheStorage.getUser(id);
            if (user != null) {
                append(user);
            }
        }
        return user;
    }

    public static VKGroup getGroup(int id) {
        VKGroup group = groups.get(id);
        if (group == null) {
            group = CacheStorage.getGroup(id);
            if (group != null) {
                append(group);
            }
        }
        return group;
    }

    public static void append(VKGroup value) {
        groups.append(value.id, value);
    }

    public static void append(VKUser value) {
        users.append(value.id, value);
    }

    public static void clear() {
        users.clear();
        groups.clear();
    }
}
