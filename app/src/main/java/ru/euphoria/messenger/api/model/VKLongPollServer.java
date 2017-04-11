package ru.euphoria.messenger.api.model;

import java.io.Serializable;

import ru.euphoria.messenger.json.JsonObject;

public class VKLongPollServer extends VKModel implements Serializable {
    private static final long serialVersionUID = 1L;

    public String key;
    public String server;
    public long ts;

    public VKLongPollServer(JsonObject source) {
        this.key = source.optString("key");
        this.server = source.optString("server").replace("\\", "");
        this.ts = source.optLong("ts");
    }
}