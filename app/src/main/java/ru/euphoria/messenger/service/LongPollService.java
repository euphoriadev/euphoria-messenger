package ru.euphoria.messenger.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.HashMap;

import ru.euphoria.messenger.api.VKApi;
import ru.euphoria.messenger.api.model.VKLongPollServer;
import ru.euphoria.messenger.concurrent.LowThread;
import ru.euphoria.messenger.json.JsonArray;
import ru.euphoria.messenger.json.JsonException;
import ru.euphoria.messenger.json.JsonObject;
import ru.euphoria.messenger.net.HttpRequest;
import ru.euphoria.messenger.util.AndroidUtils;

public class LongPollService extends Service {
    public static final String MOBILE_USER_AGENT = "Mozilla/5.0 (Linux; Android 4.4; Nexus 5 Build/_BuildID_) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36";
    private static final String TAG = "LongPollService";
    private Thread updateThread;
    private boolean isRunning;

    public LongPollService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();

        isRunning = true;
        updateThread = new LowThread(new MessageUpdater());
        updateThread.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        isRunning = false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    private class MessageUpdater implements Runnable {
        @Override
        public void run() {
            VKLongPollServer server = null;
            while (isRunning) {
                if (!AndroidUtils.hasConnection()) {
                    // user do not have Internet connection
                    try {
                        Thread.sleep(5_000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }
                try {
                    if (server == null) {
                        server = VKApi.messages().getLongPollServer()
                                .execute(VKLongPollServer.class).get(0);
                    };

                    JsonObject response = getResponse(server);
                    if (response == null || response.has("failed")) {
                        // failed get response, try again
                        Log.w(TAG, "Failed get response from");
                        Thread.sleep(1_000);
                        server = null;
                        continue;
                    }

                    long tsResponse = response.optLong("ts");
                    JsonArray updates = response.getJsonArray("updates");
                    Log.i(TAG, "response = " + updates);

                    server.ts = tsResponse;
                    if (updates.length() != 0) {
                        // success! parse updates
                        process(updates);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }

            }
        }

        public JsonObject getResponse(VKLongPollServer server) throws JsonException {
            StringBuilder buffer = new StringBuilder();

            HashMap<String, String> params = new HashMap<>();
            params.put("act", "a_check");
            params.put("key", server.key);
            params.put("ts", String.valueOf(server.ts));
            params.put("wait", "25");
            params.put("mode", "2");;

            HttpRequest.get("https://" + server.server, params, false)
                    .acceptGzipEncoding()
                    .uncompress(true)
                    .userAgent(MOBILE_USER_AGENT)
                    .receive(buffer)
                    .disconnect();

            Log.w(TAG, "server: " + buffer);
            return new JsonObject(buffer.toString());
        }

        private void process(JsonArray updates) {

        }

    }
}
