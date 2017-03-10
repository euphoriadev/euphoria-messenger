package ru.euphoria.messenger;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import ru.euphoria.messenger.api.Auth;
import ru.euphoria.messenger.api.Scopes;
import ru.euphoria.messenger.api.UserConfig;

public class LoginActivity extends Activity {
    WebView webView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        webView = new WebView(this);
        webView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(webView);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.setWebViewClient(new VKWebViewClient());
        ViewCompat.setLayerType(webView, ViewCompat.LAYER_TYPE_HARDWARE, null);

        webView.loadUrl(Auth.getUrl(UserConfig.EUPHORIA_ID, Scopes.all()));
    }

    private void parseUrl(String url) {
        if (TextUtils.isEmpty(url)) return;

        try {
            if (url.startsWith(Auth.REDIRECT_URL) && !url.contains("error=")) {
                String[] auth = Auth.parseRedirectUrl(url);
                Intent intent = new Intent();
                intent.putExtra("token", auth[0]);
                intent.putExtra("id", Integer.parseInt(auth[1]));
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (webView != null) {
            webView.removeAllViews();
            webView.clearCache(true);
            webView.destroy();
            webView = null;
        }

    }

    private class VKWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            parseUrl(url);
        }
    }
}