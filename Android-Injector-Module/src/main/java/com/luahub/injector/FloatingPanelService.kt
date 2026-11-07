package com.luahub.injector

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.NotificationCompat
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.luahub.injector.ui.ModernFloatingMenu

class FloatingPanelService : Service() {
    private var windowManager: WindowManager? = null
    private var composeView: ComposeView? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        showNotification()

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        composeView = ComposeView(this).apply {
            setContent {
                ModernFloatingMenu(
                    onHeadlockChanged = { active -> /* Kirim trigger aktif ke Game Client memory writer */ },
                    onEspChanged = { active -> /* Kirim trigger aktif ke Game Client memory writer */ },
                    onMinimizeClick = { stopSelf() }
                )
            }
        }

        // Kebutuhan Lifecycle Jetpack Compose di dalam Service Windows
        composeView?.setViewTreeLifecycleOwner(this as? androidx.lifecycle.LifecycleOwner)
        composeView?.setViewTreeSavedStateRegistryOwner(this as? androidx.savedstate.SavedStateRegistryOwner)

        val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 150
            y = 150
        }

        windowManager?.addView(composeView, params)
    }

    private fun showNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("LUAHUB_CH", "LUAHUB Service", NotificationManager.IMPORTANCE_LOW)
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(this, "LUAHUB_CH")
            .setContentTitle("LUAHUB V1.0.0 Run")
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .build()
        startForeground(2, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        composeView?.let { windowManager?.removeView(it) }
    }
}
