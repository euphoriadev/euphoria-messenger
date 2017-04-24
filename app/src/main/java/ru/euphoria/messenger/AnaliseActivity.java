package ru.euphoria.messenger;

import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaCodec;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import ru.euphoria.messenger.api.VKApi;
import ru.euphoria.messenger.api.model.VKAudio;
import ru.euphoria.messenger.api.model.VKDoc;
import ru.euphoria.messenger.api.model.VKGift;
import ru.euphoria.messenger.api.model.VKGroup;
import ru.euphoria.messenger.api.model.VKLink;
import ru.euphoria.messenger.api.model.VKMessage;
import ru.euphoria.messenger.api.model.VKModel;
import ru.euphoria.messenger.api.model.VKPhoto;
import ru.euphoria.messenger.api.model.VKSticker;
import ru.euphoria.messenger.api.model.VKUser;
import ru.euphoria.messenger.api.model.VKVideo;
import ru.euphoria.messenger.common.AppGlobal;
import ru.euphoria.messenger.common.ThemeManager;
import ru.euphoria.messenger.concurrent.ThreadExecutor;
import ru.euphoria.messenger.database.MemoryCache;
import ru.euphoria.messenger.util.AndroidUtils;
import ru.euphoria.messenger.util.ArrayUtil;

/**
 * Created by Igor on 17.04.17.
 */

public class AnaliseActivity extends BaseActivity {
    private String title, avatar;
    private long peerId;
    private int total, out, in;
    private int photos, videos, audios, docs, links;
    private int stickers, gifts, words;
    private int chars;
    private int offset;

    private TextView textInfo;
    private ProgressBar progressBar;
    private SpannableStringBuilder builder;
    private HashMap<String, Integer> mapWords;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!ThemeManager.isNightMode()) {
            getWindow().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.md_grey_200)));
        }
        setContentView(R.layout.activity_analise);
        getIntentData();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.analise);

        ImageView avatar = (ImageView) findViewById(R.id.analiseAvatar);
        TextView title = (TextView) findViewById(R.id.analiseTitle);
        TextView body = (TextView) findViewById(R.id.analiseBody);
        textInfo = (TextView) findViewById(R.id.analiseInfo);
        progressBar = (ProgressBar) findViewById(R.id.analiseProgress);

        title.setText(this.title);
        body.setText("ID: " + peerId);

        Picasso.with(this)
                .load(this.avatar)
                .config(Bitmap.Config.RGB_565)
                .into(avatar);

        startAnalise();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private void startAnalise() {
        if (!AndroidUtils.hasConnection()) {
            Snackbar.make(findViewById(android.R.id.content), R.string.check_connection, Snackbar.LENGTH_LONG)
                    .show();

            if (progressBar.getVisibility() == View.VISIBLE) {
                progressBar.setVisibility(View.GONE);
            }
            return;
        }

        mapWords = new HashMap<>();
        ThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        final ArrayList<VKMessage> messages = VKApi.messages()
                                .getHistory()
                                .peerId(peerId)
                                .offset(offset)
                                .count(200).execute(VKMessage.class);

                        offset += messages.size();
                        process(messages);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textInfo.setText(builder);

                                float current = (float) total / VKMessage.lastHistoryCount * 100f;
                                getSupportActionBar().setTitle(getString(R.string.analise) + ", " + Math.round(current) + "%");
                            }
                        });

                        if (isFinishing()
                                || ArrayUtil.isEmpty(messages)
                                || messages.size() < 200) {
                            break;
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getSupportActionBar().setTitle(getString(R.string.analise) + ", 100%");

                            if (progressBar.getVisibility() == View.VISIBLE) {
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });

                    ArrayList<Map.Entry<String, Integer>> entries = new ArrayList<>(mapWords.entrySet());
                    Collections.sort(entries, new Comparator<Map.Entry<String, Integer>>() {
                        @Override
                        public int compare(Map.Entry<String, Integer> a, Map.Entry<String, Integer> b) {
                            return a.getValue().compareTo(b.getValue());
                        }
                    });
                    Collections.reverse(entries);

                    for (int i = 0; i < 25; i++) {
                        Map.Entry<String, Integer> entry = entries.get(i);
                        System.out.println(entry.getKey() + ": " + entry.getValue());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void process(ArrayList<VKMessage> messages) {
        total += messages.size();
        for (int i = 0; i < messages.size(); i++) {
            VKMessage msg = messages.get(i);
            if (msg.is_out) {
                out++;
            } else {
                in++;
            }
            words += countWords(msg.body);
            chars += countChars(msg.body);

            if (!TextUtils.isEmpty(msg.body)) {
                String[] split = msg.body.split("\\W+");
                if (!ArrayUtil.isEmpty(split)) {
                    for (String word : split) {
                        if (mapWords.containsKey(word)) {
                            mapWords.put(word, mapWords.get(word) + 1);
                        } else {
                            mapWords.put(word, 0);
                        }
                    }
                }
            }

            if (!ArrayUtil.isEmpty(msg.attachments)) {
                for (int j = 0; j < msg.attachments.size(); j++) {
                    VKModel attach = msg.attachments.get(j);
                    if (attach instanceof VKPhoto) {
                        photos++;
                    } else if (attach instanceof VKVideo) {
                        videos++;
                    } else if (attach instanceof VKAudio) {
                        audios++;
                    } else if (attach instanceof VKDoc) {
                        docs++;
                    } else if (attach instanceof VKLink) {
                        links++;
                    } else if (attach instanceof VKSticker) {
                        stickers++;
                    } else if (attach instanceof VKGift) {
                        gifts++;
                    }
                }
            }
        }

        builder = new SpannableStringBuilder();
        appendLine(builder, "Всего сообщений: ", total + " / " + VKMessage.lastHistoryCount);
        appendLine(builder, "Исходящих: ", String.format(AppGlobal.locale, "%,d", out));
        appendLine(builder, "Входящих: ", String.format(AppGlobal.locale, "%,d", in));
        appendLine(builder, "Кол-во слов: ", String.format(AppGlobal.locale, "%,d", words));
        appendLine(builder, "Кол-во символов: ", String.format(AppGlobal.locale, "%,d", chars));

        builder.append('\n');
        appendLine(builder, "Изображений: ", String.valueOf(photos));
        appendLine(builder, "Видео: ", String.valueOf(videos));
        appendLine(builder, "Аудио: ", String.valueOf(audios));
        appendLine(builder, "Документов: ", String.valueOf(docs));
        appendLine(builder, "Ссылок: ", String.valueOf(links));
        appendLine(builder, "Стикеров: ", String.valueOf(stickers));
        appendLine(builder, "Подарков: ", String.valueOf(gifts));

        builder.delete(builder.length() - 1, builder.length());
    }

    private int countChars(String text) {
        if (TextUtils.isEmpty(text)) {
            return 0;
        }
        return text.length();
    }

    private int countWords(String text) {
        if (TextUtils.isEmpty(text)) {
            return 0;
        }

        int count = 1;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == ' ') {
                count++;
            }
        }
        return count;
    }

    private void appendLine(SpannableStringBuilder builder, String title, String body) {
        builder.append(title);
        builder.append(body);
        builder.append('\n');

        int start = builder.toString().indexOf(title);
        builder.setSpan(new StyleSpan(Typeface.BOLD), start, start + title.length(), 0);
    }

    private void getIntentData() {
        this.peerId = getIntent().getLongExtra("peer_id", -1);
        this.title = getIntent().getStringExtra("title");
        this.avatar = getIntent().getStringExtra("photo");
    }
}
