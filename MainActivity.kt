
package com.luckyzuby.breaker

import android.app.*
import android.content.*
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.*
import android.provider.Settings
import android.view.*
import android.widget.*
import android.graphics.drawable.GradientDrawable

class MainActivity : Activity() {

    private var panelActive = false
    private var sensitivity = 150
    private lateinit var status: TextView
    private lateinit var sensitivityText: TextView

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    private fun background(color: Int): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(16).toFloat()
        }
    }

    private fun makeButton(label: String): Button {
        return Button(this).apply {
            text = label
            setTextColor(Color.WHITE)
            background = background(Color.rgb(75, 35, 130))
            textSize = 13f
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.rgb(12, 8, 22)
        window.navigationBarColor = Color.rgb(12, 8, 22)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(20), dp(20), dp(20))
            setBackgroundColor(Color.rgb(12, 8, 22))
        }

        val scroll = ScrollView(this)
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        val title = TextView(this).apply {
            text = "LUCKY ZUBY"
            textSize = 27f
            setTextColor(Color.rgb(190, 110, 255))
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }

        val subtitle = TextView(this).apply {
            text = "AI BREAKER // GAMING COMPANION"
            textSize = 11f
            setTextColor(Color.LTGRAY)
        }

        status = TextView(this).apply {
            text = "SYSTEM: INITIALIZING"
            textSize = 14f
            setTextColor(Color.GREEN)
            setPadding(0, dp(20), 0, dp(15))
        }

        content.addView(title)
        content.addView(subtitle)
        content.addView(status)

        content.addView(section("GAME PROFILE"))

        content.addView(makeButton("FREE FIRE").apply {
            setOnClickListener {
                status.text = "PROFILE: FREE FIRE SELECTED"
            }
        })

        content.addView(makeButton("FREE FIRE MAX").apply {
            setOnClickListener {
                status.text = "PROFILE: FREE FIRE MAX SELECTED"
            }
        })

        content.addView(section("SENSITIVITY SETTINGS"))

        sensitivityText = TextView(this).apply {
            text = "Sensitivity: $sensitivity"
            setTextColor(Color.WHITE)
            textSize = 17f
        }

        content.addView(sensitivityText)

        val slider = SeekBar(this).apply {
            max = 200
            progress = sensitivity

            setOnSeekBarChangeListener(
                object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(
                        seekBar: SeekBar?,
                        value: Int,
                        fromUser: Boolean
                    ) {
                        sensitivity = value
                        sensitivityText.text =
                            "Sensitivity: $value"
                    }

                    override fun onStartTrackingTouch(
                        seekBar: SeekBar?
                    ) {}

                    override fun onStopTrackingTouch(
                        seekBar: SeekBar?
                    ) {}
                }
            )
        }

        content.addView(slider)

        content.addView(section("SHIZUKU CONNECTION"))

        content.addView(makeButton("CHECK SHIZUKU").apply {
            setOnClickListener {
                val connected = try {
                    val clazz = Class.forName(
                        "rikka.shizuku.Shizuku"
                    )
                    val method = clazz.getMethod(
                        "pingBinder"
                    )
                    method.invoke(null) as Boolean
                } catch (e: Exception) {
                    false
                }

                status.text = if (connected)
                    "SHIZUKU: CONNECTED"
                else
                    "SHIZUKU: NOT CONNECTED"
            }
        })

        content.addView(section("FLOATING ASSISTANT"))

        content.addView(makeButton("ENABLE FLOATING PANEL").apply {
            setOnClickListener {
                if (Settings.canDrawOverlays(this@MainActivity)) {
                    showFloatingPanel()
                } else {
                    startActivity(
                        Intent(
                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            android.net.Uri.parse(
                                "package:$packageName"
                            )
                        )
                    )
                }
            }
        })

        content.addView(makeButton("SEND NOTIFICATION").apply {
            setOnClickListener {
                sendNotification()
            }
        })

        content.addView(makeButton("STOP PANEL").apply {
            setOnClickListener {
                panelActive = false
                status.text = "PANEL: STOPPED"
            }
        })

        scroll.addView(content)
        root.addView(scroll)
        setContentView(root)
    }

    private fun section(name: String): TextView {
        return TextView(this).apply {
            text = name
            textSize = 14f
            setTextColor(Color.rgb(190, 110, 255))
            setPadding(0, dp(24), 0, dp(12))
        }
    }

    private fun showFloatingPanel() {
        if (panelActive) return

        val wm = getSystemService(WINDOW_SERVICE)
                as WindowManager

        val floating = TextView(this).apply {
            text = "LZB  |  GAMING PANEL"
            textSize = 14f
            setTextColor(Color.WHITE)
            setPadding(dp(18), dp(14), dp(18), dp(14))
            background = background(Color.rgb(65, 25, 110))
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP or Gravity.END
        params.x = dp(12)
        params.y = dp(150)

        floating.setOnTouchListener(
            object : View.OnTouchListener {
                var startX = 0
                var startY = 0
                var touchX = 0f
                var touchY = 0f

                override fun onTouch(
                    view: View,
                    event: android.view.MotionEvent
                ): Boolean {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            startX = params.x
                            startY = params.y
                            touchX = event.rawX
                            touchY = event.rawY
                            return true
                        }

                        MotionEvent.ACTION_MOVE -> {
                            params.x =
                                startX + (touchX - event.rawX).toInt()
                            params.y =
                                startY + (event.rawY - touchY).toInt()

                            wm.updateViewLayout(floating, params)
                            return true
                        }

                        MotionEvent.ACTION_UP -> return true
                    }
                    return false
                }
            }
        )

        wm.addView(floating, params)
        panelActive = true
        status.text = "FLOATING PANEL: ACTIVE"
    }

    private fun sendNotification() {
        val channelId = "lzb_status"

        val manager = getSystemService(
            NOTIFICATION_SERVICE
        ) as NotificationManager

        if (Build.VERSION.SDK_INT >= 26) {
            manager.createNotificationChannel(
                NotificationChannel(
                    channelId,
                    "Lucky Zuby Status",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )
        }

        val notification = Notification.Builder(
            this,
            channelId
        )
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Lucky Zuby AI Breaker")
            .setContentText("Gaming Companion is running.")
            .setAutoCancel(true)
            .build()

        manager.notify(1001, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        panelActive = false
    }
}
