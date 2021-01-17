package com.clark.accessibility;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShowSceenshotActivity extends AppCompatActivity {
    public static String SCREENSHOT = "screenshot_bitmap";

    private ImageView showScreenshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_sceenshot);

        showScreenshot = findViewById(R.id.show_screenshot);

        showScreenshotInner(getIntent());
    }

    private void showScreenshotInner(Intent intent) {
        if (intent != null) {
            byte[] byteArrayExtra = intent.getByteArrayExtra(SCREENSHOT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArrayExtra, 0, byteArrayExtra.length);
            Log.i("clark", "showScreenshotInner");
            if (showScreenshot != null){
                showScreenshot.setImageBitmap(bitmap);
            }

        }
    }


}