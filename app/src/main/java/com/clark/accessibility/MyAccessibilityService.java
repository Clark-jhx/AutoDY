package com.clark.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.accessibility.AccessibilityEvent;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MyAccessibilityService extends AccessibilityService {
    private static final String tag = "clark";

    private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 10, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        //Log.i(tag, event.toString());

        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                takeScreenshot(Display.DEFAULT_DISPLAY, threadPoolExecutor, new TakeScreenshotCallback() {
                    @Override
                    public void onSuccess(@NonNull ScreenshotResult screenshot) { }

                    @Override
                    public void onFailure(int errorCode) {
                        Log.i(tag, "error");
                    }
                });
            }
        }
    }

    @Override
    public void onInterrupt() {

    }
}
