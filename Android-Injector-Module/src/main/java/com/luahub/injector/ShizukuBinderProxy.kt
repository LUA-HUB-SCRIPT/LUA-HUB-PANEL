package com.luahub.injector

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import moe.shizuku.api.Shizuku

object ShizukuBinderProxy {

    fun checkAndRequestShizuku(context: Context): Boolean {
        // Validasi hak kepemilikan aplikasi terlebih dahulu
        if (!OwnershipValidator.verifyAppIntegrity(context)) {
            return false 
        }

        // Jalankan pengecekan binder Shizuku
        if (!Shizuku.pingBinder()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val intent = Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            }
            return false
        }
        return true
    }

    fun safeInject(sourcePath: String, fileName: String): Boolean {
        if (!Shizuku.pingBinder()) return false

        val targetPath = "/sdcard/Android/data/com.dts.freefireth/files/contentcache/Optional/"
        val command = "mkdir -p $targetPath && cp $sourcePath $targetPath$fileName"

        return try {
            val process = Shizuku.newProcess(arrayOf("sh", "-c", command), null, null)
            process.waitFor()
            process.exitValue() == 0
        } catch (e: Exception) {
            false
        }
    }
}
