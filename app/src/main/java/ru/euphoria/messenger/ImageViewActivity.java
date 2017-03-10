package ru.euphoria.messenger;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import ru.euphoria.messenger.api.model.VKPhoto;
import ru.euphoria.messenger.util.AndroidUtils;
import ru.euphoria.messenger.view.ZoomImageView;

/**
 * Created by Igor on 07.03.17.
 */

public class ImageViewActivity extends Activity {
    private boolean hidden;
    private ZoomImageView view;
    private VKPhoto photo;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        view = (ZoomImageView) findViewById(R.id.imageView);
        view.setOnTouchListener(view);

        photo = (VKPhoto) getIntent().getSerializableExtra("photo");
        bitmap = AndroidUtils.deserializeImage(getIntent().getByteArrayExtra("bitmap"));

        view.setImageBitmap(bitmap);
    }


    private void toggleSystemUI() {
        if (hidden) {
            showSystemUI();
        } else {
            hideSystemUI();
        }
        hidden = !hidden;
    }

    // This snippet hides the system bars.
    private void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    // This snippet shows the system bars. It does this by removing all the flags
// except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
}
