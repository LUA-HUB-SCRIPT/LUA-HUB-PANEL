package com.luahub.injector;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import androidx.core.app.NotificationCompat;

public class FloatingPanelService extends Service {
    private WindowManager windowManager;
    private View floatingView;

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onCreate() {
        super.onCreate();
        
        // 1. Tampilkan Notifikasi Debug Nirkabel & Shizuku
        createNotificationChannel();
        Notification notification = new NotificationCompat.Builder(this, "LUAHUB_DEBUG")
                .setContentTitle("LUAHUB V1.0.0 Active")
                .setContentText("Debugging via Shizuku & Bypass Running...")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .build();
        startForeground(1, notification);

        // 2. Setup Panel Melayang
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        
        int layoutFlag;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutFlag = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutFlag = WindowManager.LayoutParams.TYPE_PHONE;
        }

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                layoutFlag,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 100;
        params.y = 100;

        // Inflate layout panel melayang kamu (misal dari layout XML)
        floatingView = LayoutInflater.from(this).inflate(android.R.layout.simple_list_item_1, null);
        // Di sini kamu bisa menambahkan tombol untuk mengaktifkan HEADLOCK dan ESP
        
        windowManager.addView(floatingView, params);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "LUAHUB_DEBUG", "LUAHUB Debugging", NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floatingView != null) windowManager.removeView(floatingView);
    }
}
