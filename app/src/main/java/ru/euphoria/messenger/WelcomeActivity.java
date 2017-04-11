package ru.euphoria.messenger;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import ru.euphoria.messenger.api.UserConfig;
import ru.euphoria.messenger.api.VKApi;
import ru.euphoria.messenger.api.model.VKUser;
import ru.euphoria.messenger.concurrent.AsyncCallback;
import ru.euphoria.messenger.database.CacheStorage;
import ru.euphoria.messenger.database.DatabaseHelper;
import ru.euphoria.messenger.util.AndroidUtils;
import ru.euphoria.messenger.util.ArrayUtil;

public class WelcomeActivity extends AppCompatActivity {
    private static final int REQUEST_LOGIN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        findViewById(R.id.buttonLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AndroidUtils.hasConnection()) {
                    Snackbar.make(findViewById(android.R.id.content), R.string.check_connection, Snackbar.LENGTH_LONG)
                            .show();
                    return;
                }

                startActivityForResult(new Intent(WelcomeActivity.this, LoginActivity.class), REQUEST_LOGIN);
            }
        });
    }

    private void preloadCurrentUser(int id) {
        VKApi.users().get().userId(id).fields(VKUser.DEFAULT_FIELDS)
                .execute(VKUser.class, new VKApi.OnResponseListener<VKUser>() {
                    @Override
                    public void onSuccess(ArrayList<VKUser> users) {
                        if (!ArrayUtil.isEmpty(users)) {
                            CacheStorage.insert(DatabaseHelper.USERS_TABLE, users);
                        }
                    }

                    @Override
                    public void onError(Exception ex) {

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUEST_LOGIN) {
            String token = data.getStringExtra("token");
            int id = data.getIntExtra("id", -1);

            UserConfig config = new UserConfig(token, null, id, UserConfig.EUPHORIA_ID);
            config.save();
            preloadCurrentUser(id);

            VKApi.config = config;
            finish();

            startActivity(new Intent(this, MainActivity.class));
        }
    }
}
