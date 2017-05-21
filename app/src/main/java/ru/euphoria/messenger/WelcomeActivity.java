package ru.euphoria.messenger;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

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
        findViewById(R.id.buttonAccessToken).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccessTokenDialog();
            }
        });
    }

    private void createAccessTokenDialog() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        final EditText etUserId = new AppCompatEditText(this);
        final EditText etAccessToken = new AppCompatEditText(this);

        etUserId.setHint(R.string.label_user_id);
        etAccessToken.setHint(R.string.label_user_token);

        etUserId.setLayoutParams(params);
        etAccessToken.setLayoutParams(params);

        final TextInputLayout inputLayoutUserId = new TextInputLayout(this);
        final TextInputLayout inputLayoutAccessToken = new TextInputLayout(this);

        inputLayoutUserId.addView(etUserId);
        inputLayoutAccessToken.addView(etAccessToken);

        LinearLayout layout = new LinearLayout(this);
        layout.setLayoutParams(params);
        layout.setOrientation(LinearLayout.VERTICAL);

        layout.addView(inputLayoutUserId);
        layout.addView(inputLayoutAccessToken);

        AlertDialog.Builder builder = new AlertDialog.Builder(WelcomeActivity.this);
        builder.setTitle(R.string.login_with_token);
        builder.setView(layout,
                (int) AndroidUtils.px(19),
                (int) AndroidUtils.px(5),
                (int) AndroidUtils.px(19),
                (int) AndroidUtils.px(5));
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (TextUtils.isEmpty(etAccessToken.getText().toString().trim()) ||
                        TextUtils.isEmpty(etAccessToken.getText().toString().trim())) {
                    Toast.makeText(WelcomeActivity.this, "Все поля необходимо заполнить", Toast.LENGTH_LONG).show();
                    return;
                }

                final int id = Integer.parseInt(etUserId.getText().toString().trim());
                final String token = etAccessToken.getText().toString().trim();

                UserConfig config = new UserConfig(token, null, id, UserConfig.EUPHORIA_ID);
                config.save();
                VKApi.config = config;

                preloadCurrentUser(id);
                finish();

            }
        });
        builder.create().show();
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
            VKApi.config = config;

            preloadCurrentUser(id);
            finish();

            startActivity(new Intent(this, MainActivity.class));
        }
    }
}
