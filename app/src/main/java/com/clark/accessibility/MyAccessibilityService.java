package com.clark.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorSpace;
import android.graphics.Path;
import android.hardware.HardwareBuffer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import androidx.annotation.NonNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MyAccessibilityService extends AccessibilityService {
    private static final String tag = "clark";


    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
        }
    };


    private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 10, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        //Log.i(tag, event.toString());
        //testScreenshot(event);
        //testWindow();
        //testWindow2();
        //testNode();
        testSlide(event);
    }

    private void testSlide(AccessibilityEvent event) {

        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                GestureDescription.Builder builder = new GestureDescription.Builder();
                //builder.setDisplayId(Display.DEFAULT_DISPLAY);
                Path path = new Path();
                path.moveTo(400, 800);
                path.lineTo(400, 300);
                GestureDescription.StrokeDescription strokeDescription = new GestureDescription.StrokeDescription(path, 50, 100);
                builder.addStroke(strokeDescription);
                GestureDescription next = builder.build();
                dispatchGesture(next, null, null);
                Log.i(tag, "下一页");
            }
        }
    }

    private void testNode() {
        AccessibilityNodeInfo rootInActiveWindow = getRootInActiveWindow();
        Log.i(tag, rootInActiveWindow.toString());
        Log.i(tag, " ");
    }

    private void testWindow2() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            SparseArray<List<AccessibilityWindowInfo>> windowsOnAllDisplays = getWindowsOnAllDisplays();
            Log.i(tag, "size " + windowsOnAllDisplays.size());
            for (int i=0; i<windowsOnAllDisplays.size(); i++){
                List<AccessibilityWindowInfo> accessibilityWindowInfoList = windowsOnAllDisplays.valueAt(i);
                for (AccessibilityWindowInfo accessibilityWindowInfo : accessibilityWindowInfoList){
                    Log.i(tag, "" + i + " " + accessibilityWindowInfo.toString());
                }
            }
            Log.i(tag, " ");
        }

    }

    private void testWindow() {
        List<AccessibilityWindowInfo> windows = getWindows();
        Log.i(tag, "windows size " + windows.size());
        for (AccessibilityWindowInfo windowInfo : windows){
            Log.i(tag, windowInfo.toString());
        }
        Log.i(tag, " ");
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void testScreenshot(AccessibilityEvent event){
        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                takeScreenshot(Display.DEFAULT_DISPLAY, threadPoolExecutor, new TakeScreenshotCallback() {
                    @Override
                    public void onSuccess(@NonNull ScreenshotResult screenshot) {
                        Log.i(tag, "take screenshot success");
                        final HardwareBuffer hardwareBuffer = screenshot.getHardwareBuffer();
                        final ColorSpace colorSpace = screenshot.getColorSpace();
                        Bitmap bitmap = Bitmap.wrapHardwareBuffer(hardwareBuffer, colorSpace);
                        showScreenshotOutside(getBaseContext(), bitmap);
                    }

                    @Override
                    public void onFailure(int errorCode) {
                        Log.i(tag, "take screenshot, errorCode = " + errorCode);
                    }
                });
            }
        }
    }

    public  void showScreenshotOutside(Context context, Bitmap bitmap) {
        Intent intent = new Intent(context, ShowSceenshotActivity.class);
        // 这里bitmap大于1M的话回导致无法启动,所以需要进行压缩
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, baos);

        intent.putExtra(ShowSceenshotActivity.SCREENSHOT, baos.toByteArray());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getBaseContext().startActivity(intent);
    }
}